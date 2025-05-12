package com.booklog.booklogbackend.controller.review;

import com.booklog.booklogbackend.Model.request.ReviewCommentRequest;
import com.booklog.booklogbackend.Model.response.ReviewCommentResponse;
import com.booklog.booklogbackend.service.ReviewCommentService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/api/review/comments")
@RequiredArgsConstructor
public class ReviewCommentController {

    private final ReviewCommentService reviewCommentService;

    /**
     * 리뷰에 댓글 등록
     * @param reviewId : 리뷰 고유 id
     * @param request : 작성된 댓글 request
     * @param userId : 로그인 사용자 id
     */
    @PostMapping("/{reviewId}/insert")
    public ResponseEntity<Void> createComment(
            @PathVariable Long reviewId,
            @Valid @RequestBody ReviewCommentRequest request,
            @AuthenticationPrincipal(expression = "userId") Long userId
    ) {
        log.info("댓글 등록 진입 - reviewId: " + reviewId);
        log.info("댓글 내용: " + request.getContent());
        log.info("parentCommentId: " + request.getParentCommentId());
        log.info("로그인 유저 ID: " + userId);
        reviewCommentService.registerCommentOnReview(userId, reviewId, request);
        return ResponseEntity.ok().build();
    }

    /**
     * 리뷰에 등록된 댓글 조회 (대댓글을 포함한 2계층 트리 구조)
     * @param reviewId : 댓글 조회 리뷰 고유 id
     */
    @GetMapping("/{reviewId}/select")
    public ResponseEntity<List<ReviewCommentResponse>> getReviewComments(@PathVariable Long reviewId) {
        List<ReviewCommentResponse> comments = reviewCommentService.getCommentsWithReplies(reviewId);
        return ResponseEntity.ok(comments);
    }

}
