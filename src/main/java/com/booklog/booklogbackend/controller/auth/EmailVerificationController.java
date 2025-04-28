package com.booklog.booklogbackend.controller.auth;

import com.booklog.booklogbackend.Model.request.EmailVerificationRequest;
import com.booklog.booklogbackend.Model.response.ApiResponse;
import com.booklog.booklogbackend.service.auth.EmailVerificationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("api/auth/")
public class EmailVerificationController {

    private final EmailVerificationService emailVerificationService;

    public EmailVerificationController(EmailVerificationService emailVerificationService) {
        this.emailVerificationService = emailVerificationService;
    }

    /**
     * 회원가입 시 이메일 인증 코드 발송
     * @param request: 이메일
     * @return
     */
    @PostMapping("/send-email")
    public ResponseEntity<ApiResponse> sendVerificationCode(@RequestBody EmailVerificationRequest request) {
        emailVerificationService.sendVerificationCode(request.getEmail());
        return ResponseEntity.ok(new ApiResponse(true, "인증 코드 발송"));
    }

    @PostMapping("/verify-email")
    public ResponseEntity<Map<String, Object>> verifyEmail(@RequestBody EmailVerificationRequest request) {
        boolean verified = emailVerificationService.verifyCode(request.getEmail(), request.getCode());
        Map<String, Object> response = new HashMap<>();
        response.put("verified", verified);
        response.put("message", verified ? "인증 성공" : "잘못된 코드");
        return ResponseEntity.ok(response);
    }
}
