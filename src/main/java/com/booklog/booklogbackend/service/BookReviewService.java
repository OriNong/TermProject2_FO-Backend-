package com.booklog.booklogbackend.service;

import com.booklog.booklogbackend.Model.request.BookReviewRequest;
import com.booklog.booklogbackend.Model.request.ReviewCommentRequest;
import com.booklog.booklogbackend.Model.response.BookForNewReviewResponse;
import com.booklog.booklogbackend.Model.response.BookReviewDetailResponse;
import com.booklog.booklogbackend.Model.response.BookReviewResponse;
import com.booklog.booklogbackend.Model.response.BookWithReviewStaticsResponse;

import java.util.List;

public interface BookReviewService {

    // 리뷰를 작성하려는 도서의 도서 정보 조회
    BookForNewReviewResponse getReviewRequestBook(Long bookId);

    // 신규 도서 리뷰 등록
    void registerReview(Long userId, BookReviewRequest bookReviewRequest);

    // 특정 도서에 작성된 리뷰 목록 조회
    List<BookReviewResponse> getReviewsByBookId(Long bookId, Long userId);

    // 리뷰 상세 조회
    BookReviewDetailResponse getReviewDetail(Long reviewId, Long userId);
}
