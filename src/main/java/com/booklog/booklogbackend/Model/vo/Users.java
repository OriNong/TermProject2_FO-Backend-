package com.booklog.booklogbackend.Model.vo;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Users {
    private Long id;
    private String email;
    private String password;
    private String nickname;
}
