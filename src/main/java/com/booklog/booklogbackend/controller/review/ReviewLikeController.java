package com.booklog.booklogbackend.controller.review;

import com.booklog.booklogbackend.Model.response.ReviewLikeResponse;
import com.booklog.booklogbackend.service.ReviewLikeService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/reviews/like")
@RequiredArgsConstructor
public class ReviewLikeController {

    private final ReviewLikeService reviewLikeService;

    /**
     * 리뷰에 좋아요 시 리뷰에 대한 사용자의
     * 이전 좋아요 상태( [이미 좋아요 한 리뷰] or [아직 좋아요 하지 않은 리뷰] )에 따라
     * [좋아요 등록] or [좋아요 취소] 처리
     * @param userId : 로그인 사용자 고유 id
     * @param reviewId : 좋아요를 누른 리뷰 고유 id
     * @return : 좋아요(등록 or 취소) 상태 + 처리 이후 총 좋아요 수
     */
    @PostMapping("/{reviewId}")
    public ResponseEntity<ReviewLikeResponse> toggleReviewLike(
            @AuthenticationPrincipal(expression = "userId") Long userId,
            @PathVariable Long reviewId) {
        ReviewLikeResponse response = reviewLikeService.toggleLike(userId, reviewId);
        return ResponseEntity.ok(response);
    }
}
