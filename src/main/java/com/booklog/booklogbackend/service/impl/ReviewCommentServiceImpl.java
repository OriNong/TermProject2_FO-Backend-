package com.booklog.booklogbackend.service.impl;

import com.booklog.booklogbackend.Model.request.ReviewCommentRequest;
import com.booklog.booklogbackend.Model.response.ReviewCommentResponse;
import com.booklog.booklogbackend.Model.vo.ReviewCommentVO;
import com.booklog.booklogbackend.mapper.ReviewCommentMapper;
import com.booklog.booklogbackend.service.ReviewCommentService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class ReviewCommentServiceImpl implements ReviewCommentService {

    private final ReviewCommentMapper reviewCommentMapper;

    /**
     * 리뷰에 댓글 등록
     */
    @Override
    public void registerCommentOnReview(Long userId, Long reviewId, ReviewCommentRequest request) {
        ReviewCommentVO comment = ReviewCommentVO.builder()
                .userId(userId)
                .reviewId(reviewId)
                .content(request.getContent())
                .parentCommentId(request.getParentCommentId()) // null or 대댓글 ID
                .build();

        reviewCommentMapper.insertComment(comment);
    }

    /**
     * 특정 리뷰의 댓글 조회하여 2계층 트리 구조로 구성
     */
    @Override
    public List<ReviewCommentResponse> getCommentsWithReplies(Long reviewId) {
        List<ReviewCommentVO> flatList = reviewCommentMapper.selectCommentsByReviewId(reviewId);

        Map<Long, ReviewCommentResponse> map = new LinkedHashMap<>();
        List<ReviewCommentResponse> roots = new ArrayList<>();

        for (ReviewCommentVO vo : flatList) {
            ReviewCommentResponse response = map.computeIfAbsent(vo.getCommentId(), id ->
                    ReviewCommentResponse.builder()
                            .commentId(vo.getCommentId())
                            .parentCommentId(vo.getParentCommentId())
                            .userNickname(vo.getUserNickname())
                            .content(vo.getContent())
                            .createdAt(vo.getCreatedAt())
                            .replies(new ArrayList<>())
                            .build()
            );

            if (vo.getParentCommentId() == null) {
                roots.add(response);
            } else {
                ReviewCommentResponse parent = map.get(vo.getParentCommentId());
                if (parent != null) {
                    parent.getReplies().add(response);
                }
            }
        }

        return roots;
    }

}
