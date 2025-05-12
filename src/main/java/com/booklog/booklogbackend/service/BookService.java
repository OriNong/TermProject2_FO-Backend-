package com.booklog.booklogbackend.service;

import com.booklog.booklogbackend.Model.response.BookSearchResponse;
import com.booklog.booklogbackend.Model.response.BookWithReviewStaticsResponse;
import com.booklog.booklogbackend.Model.vo.BookVO;

import java.util.List;

public interface BookService {

    BookSearchResponse searchBooks(String query, String sort, int page, int limit);

    // 사용자 서재에서 도서 readingStatus 조회
    String getReadingStatus(Long userId, Long bookId);

    BookVO getBookByIsbn(String isbn);

    // 리뷰가 1건 이상 등록된 도서 조회
    List<BookWithReviewStaticsResponse> getBooksWithReviewSummary();
}
