package com.booklog.booklogbackend.service.impl;

import com.booklog.booklogbackend.Model.response.ReviewLikeResponse;
import com.booklog.booklogbackend.mapper.ReviewLikeMapper;
import com.booklog.booklogbackend.service.ReviewLikeService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class ReviewLikeServiceImpl implements ReviewLikeService {

    private final ReviewLikeMapper reviewLikeMapper;

    /**
     * 사용자가 리뷰에 좋아요를 클릭 시
     *      1. 이미 좋아요를 누른 게시물 -> 좋아요 취소
     *      2. 좋아요를 누르지 않은 게시물 -> 좋아요 추가
     * @return : 추가/취소 상태, 총 좋아요 수
     */
    @Override
    @Transactional(rollbackFor = Exception.class) // RuntimeException 뿐 아니라 모든 Exception 발생 시 ROLLBACK
    public ReviewLikeResponse toggleLike(Long userId, Long reviewId) {
        boolean exists = reviewLikeMapper.existsByUserIdAndReviewId(userId, reviewId);
        if (exists) {
            // 좋아요를 이미 누른 게시물
            reviewLikeMapper.deleteLike(userId, reviewId); // 좋아요 취소
            reviewLikeMapper.decrementLikeCount(reviewId); // 좋아요 취소 시 -1
        } else {
            // 좋아요를 누르지 않은 게시물
            reviewLikeMapper.insertLike(userId, reviewId); // 좋아요 추가
            reviewLikeMapper.incrementLikeCount(reviewId); // 좋아요 추가 시 +1
        }

        // 취소 or 추가 이후 총 좋아요 수
        int updatedLikeCount = reviewLikeMapper.countByReviewId(reviewId);

        return ReviewLikeResponse.builder()
                .liked(!exists)
                .likeCount(updatedLikeCount)
                .build();
    }
}
