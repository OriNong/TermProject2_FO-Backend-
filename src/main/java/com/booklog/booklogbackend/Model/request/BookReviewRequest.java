package com.booklog.booklogbackend.Model.request;

import jakarta.validation.constraints.*;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class BookReviewRequest {

    @NotNull(message = "도서 ID는 필수입니다.")
    private Long bookId;

    @NotBlank(message = "제목은 비워둘 수 없습니다.")
    @Size(max = 255, message = "제목은 255자 이하로 입력해주세요.") // 추후 제목 최대값 다시 조정
    private String reviewTitle;

    @NotBlank(message = "본문은 비워둘 수 없습니다.")
    private String reviewContent;

    @Min(value = 1, message = "평점은 최소 1점이어야 합니다.")
    @Max(value = 5, message = "평점은 최대 5점이어야 합니다.")
    private int rating;
}