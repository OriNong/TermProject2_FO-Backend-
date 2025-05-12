package com.booklog.booklogbackend.Model.response;

import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BookReviewDetailResponse {
    private Long reviewId;              // 리뷰 고유 id
    private Long bookId;                // 도서 고유 id
    private String nickname;            // 작성자 닉네임
    private String reviewTitle;         // 리뷰 제목
    private String reviewContent;       // 리뷰 본문
    private int rating;                 // 리뷰에 등록된 평점
    private int likeCount;              // 좋아요 수
    private LocalDateTime createdAt;    // 작성일자
    private boolean likedByUser;        // 사용자가 해당 리뷰에 좋아요를 눌렀는지 여부
}
