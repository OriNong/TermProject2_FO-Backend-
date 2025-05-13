package com.booklog.booklogbackend.mapper;

import com.booklog.booklogbackend.Model.response.BookForNewReviewResponse;
import com.booklog.booklogbackend.Model.response.BookWithReviewStaticsResponse;
import com.booklog.booklogbackend.Model.vo.BookVO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface BookMapper {

    // 시스템 books 테이블에 신규 도서 등록(사용자 서재 등록 도서, 리뷰 작성된 도서)
    void insertBook(BookVO bookVO);

    // 리뷰가 1건 이상 등록되어 있는 도서만 조회
    List<BookWithReviewStaticsResponse> selectBooksWithReviewStatics();

    // 도서를 서재에 등록한 사용자의 total count
    int countAllBookcaseStatsByBookId(@Param("bookId") Long bookId);

    // 도서가 서재에 등록된 읽기 상태 별 count
    int countBookcaseStatsByBookIdAndStatus(@Param("bookId") Long bookId, @Param("status") String status);

    // 서재에서 readingStatus만 조회
    String selectBookcaseReadingStatus(@Param("userId") Long userId, @Param("bookId") Long bookId);

    // 도서 상세보기 시 시스템에 등록된 도서 중 isbn으로 일치 도서 조회
    BookVO findByIsbn(String isbn);

    // bookId로 도서 조회
    BookVO getBookByBookId(Long bookId);
    // 리뷰 작성 페이지 초기 도서 정보 세팅에 특화된 조회 로직
    BookForNewReviewResponse getReviewRequestBookByBookId(Long bookId);
}
