package com.booklog.booklogbackend.service;

import com.booklog.booklogbackend.Model.request.BookReviewRequest;

public interface BookReviewService {
    void createReview(Long userId, BookReviewRequest bookReviewRequest);
}
