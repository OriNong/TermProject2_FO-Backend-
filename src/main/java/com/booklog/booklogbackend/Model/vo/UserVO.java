package com.booklog.booklogbackend.Model.vo;

import com.booklog.booklogbackend.Model.VerificationStatus;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserVO {
    private Long userId;                        // 사용자 내부 고유 id
    private String email;                       // 사용자 이메일
    private String password;                    // 사용자 비밀번호
    private String nickname;                    // 사용자 닉네임
    private VerificationStatus isVerified;      // 이메일 인증 여부
    private LocalDateTime createdAt;            // 사용자 회원 가입 일자
    private LocalDateTime updatedAt;            // 사용자 정보 수정 일자
}
