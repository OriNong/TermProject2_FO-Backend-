package com.booklog.booklogbackend.Model.response;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BookWithReviewStaticsResponse {
    private Long bookId;
    private String isbn;
    private String title;
    private String author;
    private String bookImg;
    private double rating;       // 평균 평점
    private int reviewCount;     // 리뷰 수
}
