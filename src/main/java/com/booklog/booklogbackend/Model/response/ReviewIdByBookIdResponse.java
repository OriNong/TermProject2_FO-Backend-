package com.booklog.booklogbackend.Model.response;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ReviewIdByBookIdResponse {
    private Long reviewId;           // 리뷰 고유 ID
    private Long bookId;             // 도서 ID
    private String exists;          // 존재 여부
}
