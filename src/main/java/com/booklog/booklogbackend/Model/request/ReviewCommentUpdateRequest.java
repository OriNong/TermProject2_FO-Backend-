package com.booklog.booklogbackend.Model.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ReviewCommentUpdateRequest {

    @NotBlank(message = "댓글 내용은 비워둘 수 없습니다.")
    private String content;
}
