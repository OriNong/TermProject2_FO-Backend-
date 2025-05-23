package com.booklog.booklogbackend.Model.response;

import lombok.*;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString
public class ReviewIdByBookIdResponse {
    private Long reviewId;    // 리뷰 ID (nullable 가능)
    private Boolean isDeleted; // 논리 삭제 여부 (true: 삭제됨, false: 존재함)
}
