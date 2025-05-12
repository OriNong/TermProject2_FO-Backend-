package com.booklog.booklogbackend.Model.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ReviewCommentRequest {
    @NotBlank(message = "내용을 입력해주세요.")
    private String content;         //댓글 내용
    private Long parentCommentId;   // null이면 일반 댓글, 있으면 대댓글
}

