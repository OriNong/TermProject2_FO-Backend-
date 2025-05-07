package com.booklog.booklogbackend.Model.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class LoginSuccessResponse {
    private String accessToken;
    private String refreshToken;
    private String nickname;
}
