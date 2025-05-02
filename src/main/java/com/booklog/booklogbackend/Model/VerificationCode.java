package com.booklog.booklogbackend.Model;

import lombok.Getter;
import lombok.Setter;

import java.sql.Date;

@Getter
@Setter
// 삭제 예정
public class VerificationCode {
    private Long id;
    private String email;
    private String code;
    private Date createdAt;
    private Date expiresAt;
    private boolean isVerified;
}
