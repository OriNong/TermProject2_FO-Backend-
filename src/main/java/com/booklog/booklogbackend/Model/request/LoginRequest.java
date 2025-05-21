package com.booklog.booklogbackend.Model.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class LoginRequest {

    @Email(message = "로그인 정보를 올바르게 입력하세요")
    @NotBlank(message = "로그인 정보를 모두 입력하세요")
    private String email;

    @NotBlank(message = "로그인 정보를 모두 입력하세요")
    private String password;
}
