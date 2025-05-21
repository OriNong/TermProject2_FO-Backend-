package com.booklog.booklogbackend.Model.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class EmailVerificationRequest {

    @Email(message = "이메일 형식이 아닙니다")
    @NotNull(message = "이메일은 필수입니다")
    private String email;   // 이메일

    private String code;    // 인증 코드
}