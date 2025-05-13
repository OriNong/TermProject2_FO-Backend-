package com.booklog.booklogbackend.service;

import com.booklog.booklogbackend.Model.response.BookSearchResponse;
import com.booklog.booklogbackend.Model.response.BookWithReviewStaticsResponse;
import com.booklog.booklogbackend.Model.response.BookcaseStatsResponse;
import com.booklog.booklogbackend.Model.vo.BookVO;

import java.util.List;

public interface BookService {

    // 키워드(도서명 or 저자명)에 대한 도서 검색 결과 반환
    BookSearchResponse searchBooks(String query, String sort, int page, int limit);

    // 도서에 대한 전체 사용자의 서재 상태 통계 조회
    BookcaseStatsResponse getTotalBookcaseStats(Long bookId);

    // 사용자 서재에서 도서 readingStatus 조회
    String getReadingStatus(Long userId, Long bookId);

    BookVO getBookByIsbn(String isbn);

    // 리뷰가 1건 이상 등록된 도서 조회
    List<BookWithReviewStaticsResponse> getBooksWithReviewSummary();
}
