package com.booklog.booklogbackend.Model.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PasswordVerifyRequest {
    @NotBlank(message = "비밀번호는 필수입니다.")
    private String password;
}
