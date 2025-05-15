package com.booklog.booklogbackend.config;

import com.booklog.booklogbackend.Model.CustomUserDetails;
import com.booklog.booklogbackend.Model.VerificationStatus;
import com.booklog.booklogbackend.exception.JwtAuthenticationException;
import com.booklog.booklogbackend.service.auth.CustomUserDetailService;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.util.AntPathMatcher;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {
    private static final Logger logger = LoggerFactory.getLogger(JwtAuthenticationFilter.class);
    private final ObjectMapper objectMapper = new ObjectMapper();

    private final JwtTokenProvider jwtTokenProvider;
    private final CustomUserDetailService customUserDetailService;

    // 인증이 필요 없는 경로 목록
    private final List<String> excludedPaths = Arrays.asList(
            "/api/auth/login",
            "/api/auth/refresh",
            "/api/auth/register",
            "/api/auth/check-email",
            "/api/auth/check-nickname",
            "/api/auth/send-email",
            "/api/auth/verify-email",
            "/api/books/public/**"
    );

    // 이메일 인증 완료 여부가 필요 없는 경로 목록 (인증은 필요하지만 이메일 검증은 불필요)
    private final List<String> emailVerificationExcludedPaths = Arrays.asList(
            "/api/auth/verify-email",
            "/api/auth/resend-verification"
    );

    public JwtAuthenticationFilter(JwtTokenProvider jwtTokenProvider,
                                   CustomUserDetailService customUserDetailService) {
        this.jwtTokenProvider = jwtTokenProvider;
        this.customUserDetailService = customUserDetailService;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        String requestURI = request.getRequestURI();

        // 인증이 필요 없는 경로는 통과
        if (isPathExcluded(requestURI)) {
            filterChain.doFilter(request, response);
            return;
        }

        try {
            String token = getJwtFromRequest(request);
            if (token == null) {
                throw JwtAuthenticationException.missing();
            }

            // 토큰 검증 - 예외가 발생하면 JwtAuthenticationException으로 처리됨
            jwtTokenProvider.validateToken(token);

            // 검증 통과 시 인증 처리
            String email = jwtTokenProvider.getEmailFromToken(token);
            var userDetails = (CustomUserDetails) customUserDetailService.loadUserByUsername(email);

            // 이메일 인증 상태 확인
            // 인증이 되지 않은 사용자이고, 이메일 인증이 필요한 경로인 경우
            if (userDetails.getVerificationStatus() == VerificationStatus.UNVERIFIED
                    && !isEmailVerificationExcluded(requestURI)) {
                handleEmailNotVerified(response);
                return;
            }

            // 프론트 오피스에서는 권한 체크를 하지 않으므로, 빈 authorities 리스트 사용
            var authentication = new UsernamePasswordAuthenticationToken(
                    userDetails, null, userDetails.getAuthorities()
            );
            authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
            SecurityContextHolder.getContext().setAuthentication(authentication);

            filterChain.doFilter(request, response);
        } catch (JwtAuthenticationException e) {
            // JWT 인증 예외 처리
            handleAuthenticationException(response, e);
        } catch (Exception e) {
            // 기타 예외 처리
            logger.error("인증 처리 중 오류 발생: {}", e.getMessage());
            SecurityContextHolder.clearContext();
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            response.setContentType("application/json;charset=UTF-8");

            Map<String, Object> errorDetails = new HashMap<>();
            errorDetails.put("message", "서버 내부 오류가 발생했습니다.");
            errorDetails.put("code", "SERVER_ERROR");

            response.getWriter().write(objectMapper.writeValueAsString(errorDetails));
        }
    }

    // 이메일 미인증 처리 메서드
    private void handleEmailNotVerified(HttpServletResponse response) throws IOException {
        SecurityContextHolder.clearContext();
        response.setStatus(HttpServletResponse.SC_FORBIDDEN);
        response.setContentType("application/json;charset=UTF-8");

        Map<String, Object> errorDetails = new HashMap<>();
        errorDetails.put("message", "이메일 인증이 필요합니다");
        errorDetails.put("code", "EMAIL_VERIFICATION_REQUIRED");

        response.getWriter().write(objectMapper.writeValueAsString(errorDetails));
    }

    // 인증 예외 처리 메서드
    private void handleAuthenticationException(HttpServletResponse response, JwtAuthenticationException e) throws IOException {
        SecurityContextHolder.clearContext();
        response.setStatus(e.getStatus().value());
        response.setContentType("application/json;charset=UTF-8");

        Map<String, Object> errorDetails = new HashMap<>();
        errorDetails.put("message", e.getMessage());
        errorDetails.put("code", e.getCode());

        response.getWriter().write(objectMapper.writeValueAsString(errorDetails));
    }

    // 인증에서 제외된 경로인지 확인
    private boolean isPathExcluded(String requestURI) {
        AntPathMatcher pathMatcher = new AntPathMatcher();
        return excludedPaths.stream()
                .anyMatch(pattern -> pathMatcher.match(pattern, requestURI));
    }

    // 이메일 인증 확인에서 제외된 경로인지 확인
    private boolean isEmailVerificationExcluded(String requestURI) {
        AntPathMatcher pathMatcher = new AntPathMatcher();
        return emailVerificationExcludedPaths.stream()
                .anyMatch(pattern -> pathMatcher.match(pattern, requestURI));
    }

    // 요청에서 JWT 토큰 추출
    private String getJwtFromRequest(HttpServletRequest request) {
        String bearerToken = request.getHeader("Authorization");
        if (bearerToken != null && bearerToken.startsWith("Bearer ")) {
            return bearerToken.substring(7);
        }
        return null;
    }
}