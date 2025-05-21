package com.booklog.booklogbackend.controller.auth;

import com.booklog.booklogbackend.Model.CustomUserDetails;
import com.booklog.booklogbackend.Model.VerificationStatus;
import com.booklog.booklogbackend.Model.request.EmailVerificationRequest;
import com.booklog.booklogbackend.Model.request.LoginRequest;
import com.booklog.booklogbackend.Model.request.ResetPasswordRequest;
import com.booklog.booklogbackend.Model.request.TokenRefreshRequest;
import com.booklog.booklogbackend.Model.response.AccessTokenRefreshResponse;
import com.booklog.booklogbackend.Model.response.ApiResponse;
import com.booklog.booklogbackend.Model.response.LoginSuccessResponse;
import com.booklog.booklogbackend.Model.response.UserProfileResponse;
import com.booklog.booklogbackend.Model.vo.UserVO;
import com.booklog.booklogbackend.service.auth.AuthService;
import com.booklog.booklogbackend.service.auth.EmailVerificationService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/auth")
public class AuthController {
    private final AuthService authService;
    private final EmailVerificationService emailVerificationService;



    /**
     * 이메일 중복 확인
     * @param email
     * @return
     */
    @PostMapping("/check-email")
    public ResponseEntity<ApiResponse> checkEmailInUse(@RequestParam("email") String email) {

        boolean emailExists = authService.emailAlreadyExists(email);
        if (emailExists) {
            return ResponseEntity.ok(new ApiResponse(false, "이미 사용중인 이메일입니다"));
        }else {
            return ResponseEntity.ok(new ApiResponse(true, "사용 가능한 이메일입니다"));
        }
    }

    /**
     * 닉네임 중복 확인
     * @param nickname
     * @return
     */
    @PostMapping("/check-nickname")
    public ResponseEntity<ApiResponse> checkNicknameInUse(@RequestParam("nickname") String nickname) {

        boolean nicknameExists = authService.nicknameAlreadyExists(nickname);
        if (nicknameExists) {
            return ResponseEntity.ok(new ApiResponse(false, "이미 사용중인 닉네임입니다"));
        } else {
            return ResponseEntity.ok(new ApiResponse(true, "사용 가능한 닉네임입니다"));
        }
    }

    /**
     * 사용자 회원 가입
     * @param user : Frontend에서 사용자 Form 입력 정보
     * @return : 회원가입 성공 여부 반환
     */
    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody UserVO user) {
        try {
            // 이메일 인증 상태 초기값 설정 (컨트롤러에서도 설정해 두기)
            user.setIsVerified(VerificationStatus.UNVERIFIED);

            authService.register(user);
            return ResponseEntity.ok(new ApiResponse(true, "사용자 등록 성공"));
        } catch (IllegalArgumentException e) {
            if (e.getMessage().contains("Email verification required")) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body(new ApiResponse(false, "이메일 인증이 필요합니다"));
            }
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new ApiResponse(false, e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ApiResponse(false, "회원가입 처리 중 오류가 발생했습니다"));
        }
    }

    /**
     * 사용자 로그인
     * 성공 시 토큰과 초기 사용자 정보 세팅을 위한 닉네임 반환
     * @param loginRequest : email, password
     * @return : LoginSuccessResponse{accessToken, refreshToken, nickname}
     */
    @PostMapping("/login")
    public ResponseEntity<LoginSuccessResponse> login(@Valid @RequestBody LoginRequest loginRequest) {
        LoginSuccessResponse loginSuccess = authService.login(loginRequest.getEmail(), loginRequest.getPassword());
        return ResponseEntity.ok(loginSuccess);
    }

    /**
     * 사용자 프로필 리턴
     * @param userDetails : 로그인 사용자 정보
     * @return : UserProfileResponse{email, nickname, createdAt, updatedAt}
     */
    @GetMapping("/user")
    public ResponseEntity<UserProfileResponse> getUser(
            @AuthenticationPrincipal CustomUserDetails userDetails
    ) {
        Long userId = userDetails.getUserId();
        return ResponseEntity.ok(authService.getUserProfile(userId));
    }

    /**
     * AccessToken 만료 시 RefreshToken으로 AccessToken 재발급
     * @param request
     * @return
     */
    @PostMapping("/refresh")
    public ResponseEntity<AccessTokenRefreshResponse> refresh(@Valid @RequestBody TokenRefreshRequest request) {
        if (request.getRefreshToken() == null) {
            throw new IllegalArgumentException("refresh token is null");
        }
        log.info("Refresh request received");
        try {
            String newAccessToken = authService.refreshToken(request.getRefreshToken());
            log.debug("New access token: {}", newAccessToken);
            return ResponseEntity.ok(new AccessTokenRefreshResponse(newAccessToken));
        } catch (IllegalArgumentException e) {
            // 유효하지 않은 토큰일 경우 로그아웃 처리
            log.warn("Refresh token invalid, logging out");
            authService.logoutByToken(request.getRefreshToken());  // 여기 추가됨
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
    }

    /**
     * 회원 로그 아웃 처리 (토큰 삭제)
     */
    @PostMapping("/logout")
    public ResponseEntity<String> logout(@AuthenticationPrincipal CustomUserDetails userDetails) {
        Long userId = userDetails.getUserId();
        authService.logout(userId);
        return ResponseEntity.ok("User logged out successfully");
    }

    // 비밀번호 찾기 로직 총 3단계로 구성

    /**
     * 비밀번호 재설정을 위한 인증 코드 발송
     * @param email : 사용자 가입 이메일
     */
    @PostMapping("/forgot-password/send-code")
    public ResponseEntity<String> sendPasswordResetCode(@RequestParam String email) {
        emailVerificationService.sendPasswordResetCode(email); // 이메일 발송 및 Redis 저장
        return ResponseEntity.ok("비밀번호 재설정 코드가 전송되었습니다.");
    }

    /**
     * 비밀번호 재설정 인증 코드 인증 확인
     * @param request : EmailVerificationRequest.java
     */
    @PostMapping("/forgot-password/verify-code")
    public ResponseEntity<String> verifyPasswordResetCode(@Valid @RequestBody EmailVerificationRequest request) {
        emailVerificationService.verifyPasswordResetCode(request.getEmail(), request.getCode()); // Redis에서 코드 검증
        return ResponseEntity.ok("인증되었습니다.");
    }

    /**
     * 비밀번호 재설정
     * @param request : ResetPasswordRequest.java
     */
    @PostMapping("/forgot-password/reset")
    public ResponseEntity<String> resetPassword(@Valid @RequestBody ResetPasswordRequest request) {
        authService.resetPassword(request.getEmail(), request.getNewPassword()); // 비밀번호 변경
        return ResponseEntity.ok("비밀번호가 성공적으로 재설정되었습니다.");
    }

}
