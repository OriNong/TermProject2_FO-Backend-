package com.booklog.booklogbackend.Model.response;

import lombok.*;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ReviewCommentResponse {
    private Long commentId;
    private Long userId;
    private Long parentCommentId;
    private String content;
    private String userNickname;
    private LocalDateTime createdAt;

    // 대댓글 목록 (2depth용)
    private List<ReviewCommentResponse> replies;
}

