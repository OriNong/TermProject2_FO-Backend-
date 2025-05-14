package com.booklog.booklogbackend.controller.auth;

import com.booklog.booklogbackend.Model.CustomUserDetails;
import com.booklog.booklogbackend.Model.request.LoginRequest;
import com.booklog.booklogbackend.Model.request.TokenRefreshRequest;
import com.booklog.booklogbackend.Model.response.AccessTokenRefreshResponse;
import com.booklog.booklogbackend.Model.response.ApiResponse;
import com.booklog.booklogbackend.Model.response.LoginSuccessResponse;
import com.booklog.booklogbackend.Model.response.UserProfileResponse;
import com.booklog.booklogbackend.Model.vo.UserVO;
import com.booklog.booklogbackend.service.auth.AuthService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/api/auth")
public class AuthController {
    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

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
     * @param userVO : Frontend에서 사용자 Form 입력 정보
     * @return : 회원가입 성공 여부 반환
     */
    @PostMapping("/register")
    public ResponseEntity<String> register(@RequestBody UserVO userVO) {
        authService.register(userVO);
        return ResponseEntity.ok("User registered successfully");
    }

    /**
     * 사용자 로그인
     * 성공 시 토큰과 초기 사용자 정보 세팅을 위한 닉네임 반환
     * @param loginRequest : email, password
     * @return : LoginSuccessResponse{accessToken, refreshToken, nickname}
     */
    @PostMapping("/login")
    public ResponseEntity<LoginSuccessResponse> login(@RequestBody LoginRequest loginRequest) {
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
    public ResponseEntity<AccessTokenRefreshResponse> refresh(@RequestBody TokenRefreshRequest request) {
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
}
