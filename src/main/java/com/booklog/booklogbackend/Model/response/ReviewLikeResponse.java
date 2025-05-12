package com.booklog.booklogbackend.Model.response;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ReviewLikeResponse {
    private boolean liked;     // true: 좋아요 추가, false: 좋아요 취소
    private int likeCount;     // 현재 총 좋아요 수
}
