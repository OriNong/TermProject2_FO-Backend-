package com.booklog.booklogbackend.controller.auth;

import com.booklog.booklogbackend.Model.request.EmailVerificationRequest;
import com.booklog.booklogbackend.Model.response.ApiResponse;
import com.booklog.booklogbackend.service.auth.EmailVerificationService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("api/auth/")
@RequiredArgsConstructor
public class EmailVerificationController {

    private final EmailVerificationService emailVerificationService;

    /**
     * 회원가입 시 이메일 인증 코드 발송
     * @param request: 이메일
     * @return
     */
    @PostMapping("/send-email")
    public ResponseEntity<ApiResponse> sendVerificationCode(@Valid @RequestBody EmailVerificationRequest request) {
        emailVerificationService.sendVerificationCode(request.getEmail());
        return ResponseEntity.ok(new ApiResponse(true, "인증 코드 발송"));
    }

    /**
     * 발송된 인증 코드와 사용자 입력 인증 코드 일치 여부 확인
     * @param request: 사용자 이메일과 사용자 입력 인증 코드
     * @return : 인증 성공 여부 반환
     */
    @PostMapping("/verify-email")
    public ResponseEntity<Map<String, Object>> verifyEmail(@Valid @RequestBody EmailVerificationRequest request) {
        boolean success = emailVerificationService.verifyCode(request.getEmail(), request.getCode());
        Map<String, Object> response = new HashMap<>();
        response.put("verified", success);
        response.put("message", success ? "인증 성공" : "잘못된 코드");
        return ResponseEntity.ok(response);
        // ExceptionHandler에서 ApiResponse 활용하여 Success와 Message 반환
    }
}
