package com.booklog.booklogbackend.service;

import com.booklog.booklogbackend.Model.response.ReviewLikeResponse;

public interface ReviewLikeService {
    // 리뷰 좋아요 처리
    ReviewLikeResponse toggleLike(Long userId, Long reviewId);
}
