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
    private Long userId;
    private String email;
    private String password;
    private String nickname;
    private VerificationStatus isVerified;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
