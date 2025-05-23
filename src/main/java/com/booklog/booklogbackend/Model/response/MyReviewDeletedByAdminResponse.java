package com.booklog.booklogbackend.Model.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MyReviewDeletedByAdminResponse {
    private Long reviewId;            // 리뷰 고유 ID
    private Long bookId;              // 도서 고유 ID
    private String bookTitle;         // 도서 제목
    private String bookImg;           // 도서 이미지
    private String reviewTitle;       // 리뷰 제목
    private String reviewContent;     // 리뷰 본문
    private int rating;               // 평점
    private int likeCount;            // 좋아요 수
    private LocalDateTime createdAt;  // 작성일시

    private String reportReason;      // 리뷰 신고 사유(중복 제거하여 모든 사유 표시)
    private String processedAt;       // 관리자 처리 일자
}
