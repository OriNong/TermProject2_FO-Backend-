package com.booklog.booklogbackend.service;

import com.booklog.booklogbackend.Model.request.BookReviewCreateRequest;
import com.booklog.booklogbackend.Model.request.BookReviewUpdateRequest;
import com.booklog.booklogbackend.Model.response.*;

import java.util.List;

public interface BookReviewService {

    // 리뷰를 작성하려는 도서의 도서 정보 조회
    BookForNewReviewResponse getReviewRequestBook(Long bookId);

    // 신규 도서 리뷰 등록
    void registerReview(Long userId, BookReviewCreateRequest bookReviewCreateRequest);

    // 작성된 리뷰 수정
    void updateReview(Long reviewId, Long userId, BookReviewUpdateRequest request);

    // 작성된 리뷰 삭제
    void deleteReview(Long reviewId, Long userId);

    // 특정 도서에 작성된 리뷰 목록 조회
    List<BookReviewResponse> getReviewsByBookId(Long bookId, Long userId);

    // 리뷰 상세 조회
    BookReviewDetailResponse getReviewDetail(Long reviewId, Long userId);

    // 사용자가 작성한 리뷰 목록 조회
    List<MyReviewResponse> getMyReviews(Long userId);

    // 사용자가 작성한 리뷰 중 관리자가 삭제한 리뷰 목록 조회
    List<MyReviewDeletedByAdminResponse> getMyReviewsAdminDeleted(Long userId);
}
