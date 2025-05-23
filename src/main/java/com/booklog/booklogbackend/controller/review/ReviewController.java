package com.booklog.booklogbackend.controller.review;

import com.booklog.booklogbackend.Model.request.BookReviewCreateRequest;
import com.booklog.booklogbackend.Model.request.BookReviewUpdateRequest;
import com.booklog.booklogbackend.Model.response.*;
import com.booklog.booklogbackend.service.BookReviewService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/reviews")
@RequiredArgsConstructor
public class ReviewController {

    private final BookReviewService bookReviewService;

    /**
     * 리뷰 작성하려는 도서의 정보 조회(도서 이미지, 도서 제목, 도서 저자, 도서 출판사)
     * @param bookId : 도서 id
     * @return : 페이지 표시에 필요한 정보만 return
     */
    @GetMapping("book/{bookId}")
    public ResponseEntity<BookForNewReviewResponse> getReviewRequestBook(@PathVariable("bookId") Long bookId) {
        BookForNewReviewResponse book = bookReviewService.getReviewRequestBook(bookId);
        return ResponseEntity.ok(book);
    }

    /**
     * 도서 리뷰 신규 등록
     * @param userId : 로그인 사용자 id
     * @param bookReviewCreateRequest : 작성된 리뷰 form
     * @return
     */
    @PostMapping("/register")
    public ResponseEntity<Void> registerReview(
            @AuthenticationPrincipal(expression = "userId") Long userId,
            @Valid @RequestBody BookReviewCreateRequest bookReviewCreateRequest
            ) {
        bookReviewService.registerReview(userId, bookReviewCreateRequest);
        return ResponseEntity.ok().build();
    }

    /**
     * 작성된 도서 리뷰 수정
     * @param reviewId : 기존 리뷰 고유 id
     * @param userId : 로그인 사용자 id
     * @param request : 리뷰 수정 내용
     * @return
     */
    @PutMapping("/update/{reviewId}")
    public ResponseEntity<Void> updateReview(
            @PathVariable Long reviewId,
            @AuthenticationPrincipal(expression = "userId") Long userId,
            @Valid @RequestBody BookReviewUpdateRequest request
    ) {
        bookReviewService.updateReview(reviewId, userId, request);
        return ResponseEntity.ok().build();
    }

    /**
     * 작성된 리뷰 삭제
     * @param reviewId : 기존 리뷰 고유 id
     * @param userId : 로그인 사용자 id
     * @return
     */
    @DeleteMapping("/delete/{reviewId}")
    public ResponseEntity<Void> deleteReview(
            @PathVariable Long reviewId,
            @AuthenticationPrincipal(expression = "userId") Long userId
    ) {
        bookReviewService.deleteReview(reviewId, userId);
        return ResponseEntity.ok().build();
    }

    /**
     * 특정 도서에 등록된 리뷰 목록 조회
     * @param bookId : 도서 id
     * @param userId : 로그인 사용자 id
     * @return 리뷰 데이터 리스트
     */
    @GetMapping("/list/{bookId}")
    public ResponseEntity<List<BookReviewResponse>> getReviewListByBook(
            @PathVariable Long bookId,
            @AuthenticationPrincipal(expression = "userId") Long userId
    ){
        List<BookReviewResponse> reviews = bookReviewService.getReviewsByBookId(bookId, userId);
        return ResponseEntity.ok(reviews);
    }

    /**
     * 리뷰 상세 조회
     * @param reviewId : 리뷰 고유 id
     * @param userId : 로그인 사용자 id
     * @return : 개별 리뷰 상세정보
     */
    @GetMapping("/{reviewId}")
    public ResponseEntity<BookReviewDetailResponse> getReviewDetail(
            @PathVariable Long reviewId,
            @AuthenticationPrincipal(expression = "userId") Long userId
    ) {
        BookReviewDetailResponse response = bookReviewService.getReviewDetail(reviewId, userId);
        return ResponseEntity.ok(response);
    }

    /**
     * 로그인 사용자가 작성한 모든 리뷰 목록 조회
     * @param userId : 로그인 사용자 ID
     * @return 사용자가 작성한 리뷰 목록
     */
    @GetMapping("/my")
    public ResponseEntity<List<MyReviewResponse>> getMyReviews(
            @AuthenticationPrincipal(expression = "userId") Long userId
    ) {
        List<MyReviewResponse> myReviews = bookReviewService.getMyReviews(userId);
        return ResponseEntity.ok(myReviews);
    }

    /**
     * 사용자가 작성한 리뷰 중 관리자가 신고 내역을 접수하고 승인하여 삭제된 리뷰 목록 조회
     * @param userId : 로그인 사용자 id
     * @return : 삭제된 리뷰 정보 + 관리자가 승인한 신고 사유 및 최초 승인 일자
     */
    @GetMapping("/my/admin/deleted")
    public ResponseEntity<List<MyReviewDeletedByAdminResponse>> getMyReviewsAdminDeleted(
            @AuthenticationPrincipal(expression = "userId") Long userId
    ){
        List<MyReviewDeletedByAdminResponse> myReviewsAdminDeleted = bookReviewService.getMyReviewsAdminDeleted(userId);
        return ResponseEntity.ok(myReviewsAdminDeleted);
    }

}
