package com.booklog.booklogbackend.controller.auth;

import com.booklog.booklogbackend.Model.request.EmailVerificationRequest;
import com.booklog.booklogbackend.service.auth.EmailVerificationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("api/verification/")
public class EmailVerificationController {

    private final EmailVerificationService emailVerificationService;

    public EmailVerificationController(EmailVerificationService emailVerificationService) {
        this.emailVerificationService = emailVerificationService;
    }

//    @PostMapping("email-send")
//    public ResponseEntity<String> sendVerificationCode(@RequestBody EmailVerificationRequest request){
//
//    }
}
