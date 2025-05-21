package com.booklog.booklogbackend.Model.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class EmailVerificationRequest {

    @Email
    @NotBlank(message = "Email cannot be blank")
    private String email;   // 이메일

    private String code;    // 인증 코드
}