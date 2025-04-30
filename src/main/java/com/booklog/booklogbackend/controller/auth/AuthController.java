package com.booklog.booklogbackend.controller.auth;

import com.booklog.booklogbackend.Model.request.LoginRequest;
import com.booklog.booklogbackend.Model.request.TokenRefreshRequest;
import com.booklog.booklogbackend.Model.response.ApiResponse;
import com.booklog.booklogbackend.Model.vo.UserVO;
import com.booklog.booklogbackend.service.auth.AuthService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

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
     *
     * @param loginRequest
     * @return : JWT 토큰 반환
     */
    @PostMapping("/login")
    public ResponseEntity<Map<String, String>> login(@RequestBody LoginRequest loginRequest) {
        Map<String, String> tokens = authService.login(loginRequest.getEmail(), loginRequest.getPassword());
        return ResponseEntity.ok(tokens);
    }

    /**
     * AccessToken 만료 시 RefreshToken으로 AccessToken 재발급
     * @param request
     * @return
     */
    @PostMapping("/refresh")
    public ResponseEntity<String> refresh(@RequestBody TokenRefreshRequest request) {
        String newAccessToken = authService.refreshToken(request.getRefreshToken());
        return ResponseEntity.ok(newAccessToken);
    }

    /**
     * 회원 로그 아웃 처리 (토큰 삭제)
     */
    @PostMapping("/logout")
    public ResponseEntity<String> logout(@RequestBody Map<String, Long> request) {
        authService.logout(request.get("userId"));
        return ResponseEntity.ok("User logged out successfully");
    }
}
