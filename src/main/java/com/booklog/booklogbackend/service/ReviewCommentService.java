package com.booklog.booklogbackend.service;


import com.booklog.booklogbackend.Model.request.ReviewCommentRequest;
import com.booklog.booklogbackend.Model.response.ReviewCommentResponse;

import java.util.List;

public interface ReviewCommentService {
    // 댓글 등록
    void registerCommentOnReview(Long userId, Long reviewId, ReviewCommentRequest request);

    // 특정 리뷰의 댓글 조회(대댓글 포함 2depth 구조)
    List<ReviewCommentResponse> getCommentsWithReplies(Long reviewId);
}
