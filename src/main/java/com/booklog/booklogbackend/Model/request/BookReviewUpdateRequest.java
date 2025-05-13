package com.booklog.booklogbackend.Model.request;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class BookReviewUpdateRequest {

    @NotBlank(message = "제목은 비워둘 수 없습니다.")
    @Size(max = 255, message = "제목은 255자 이하로 입력해주세요.")
    private String reviewTitle;

    @NotBlank(message = "본문은 비워둘 수 없습니다.")
    private String reviewContent;

    @Min(value = 1, message = "평점은 최소 1점이어야 합니다.")
    @Max(value = 5, message = "평점은 최대 5점이어야 합니다.")
    private int rating;
}
