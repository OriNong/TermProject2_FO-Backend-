package com.booklog.booklogbackend.Model.vo;

import lombok.*;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ReviewCommentVO {

    private Long commentId;
    private Long reviewId;
    private Long userId;
    private String userNickname;
    private String content;
    private Long parentCommentId; // null이면 일반 댓글, 있으면 대댓글
    private LocalDateTime createdAt;

    // 대댓글 목록 (2depth)
    private List<ReviewCommentVO> replies;
}

