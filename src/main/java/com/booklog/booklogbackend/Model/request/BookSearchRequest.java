package com.booklog.booklogbackend.Model.request;

import jakarta.validation.constraints.*;
import lombok.Getter;
import lombok.Setter;

/**
 * /api/books/search 요청 파라미터 전용 Request
 */
@Getter
@Setter
public class BookSearchRequest {

    @NotBlank(message = "검색어(query)는 필수입니다.")
    @Size(max = 100, message = "검색어는 최대 100자까지 가능합니다.")
    private String query;

    /** accuracy(정확도순) | pubdate(출간일순) */
    @Pattern(
            regexp = "accuracy|pubdate",
            message = "sort는 accuracy 또는 pubdate만 허용됩니다."
    )
    private String sort = "accuracy"; // 필드에 기본값 할당하면 파라미터 생략 시 기본값 적용

    @Min(value = 1,  message = "page는 최소 1부터 시작합니다.")
    @Max(value = 100, message = "page는 최대 100까지 조회할 수 있습니다.")
    private int page = 100;

    @Min(value = 1,  message = "limit은 최소 1입니다.")
    @Max(value = 100, message = "limit은 최대 100까지 가능합니다.")
    private int limit = 100;
}

