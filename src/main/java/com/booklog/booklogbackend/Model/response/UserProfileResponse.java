package com.booklog.booklogbackend.Model.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class UserProfileResponse {
    private Long userId;
    private String email; // 사용자 이메일
    private String nickname; // 사용자 닉네임
    private LocalDateTime createAt; // 가입 일자
    private LocalDateTime updatedAt; // 회원 정보 수정 일자
}
