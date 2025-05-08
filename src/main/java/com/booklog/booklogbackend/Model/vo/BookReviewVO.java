package com.booklog.booklogbackend.Model.vo;

import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BookReviewVO {
    private Long reviewId;          // 리뷰 고유 ID
    private Long userId;            // 작성자 ID
    private Long bookId;            // 도서 ID
    private String reviewTitle;     // 리뷰 제목
    private String reviewContent;   // 리뷰 본문
    private int rating;             // 평점 (1~5)
    private int likesCount;         // 좋아요 수
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
