package com.booklog.booklogbackend.controller.auth;

import com.booklog.booklogbackend.Model.vo.UserVO;
import com.booklog.booklogbackend.service.auth.AuthService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
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

//    @PostMapping("/check-email")
//    public ResponseEntity<?> checkEmailInUse(@RequestParam("email") String email) {
//        // 이메일 중복 체크 로직 구현
//        return ResponseEntity.ok(new ApiResponse(true, emailExists ? "이미 사용중인 이메일입니다." : ""));
//    }
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
     * @param userVO
     * @return : JWT 토큰 반환
     */
    @PostMapping("/login")
    public ResponseEntity<Map<String, String>> login(@RequestBody UserVO userVO) {
        Map<String, String> tokens = authService.login(userVO.getEmail(), userVO.getPassword());
        return ResponseEntity.ok(tokens);
    }


    @PostMapping("/refresh")
    public ResponseEntity<String> refresh(@RequestBody Map<String, String> request) {
        String newAccessToken = authService.refreshToken(request.get("refresh_token"));
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
