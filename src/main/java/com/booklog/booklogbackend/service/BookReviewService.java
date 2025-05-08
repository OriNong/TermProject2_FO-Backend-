package com.booklog.booklogbackend.service;

import com.booklog.booklogbackend.Model.request.BookReviewRequest;
import com.booklog.booklogbackend.Model.response.BookForNewReviewResponse;

public interface BookReviewService {

    BookForNewReviewResponse getReviewRequestBook(Long bookId);

    void registerReview(Long userId, BookReviewRequest bookReviewRequest);
}
