package com.booklog.booklogbackend.mapper;

import com.booklog.booklogbackend.Model.vo.BookVO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface BookMapper {

    // 시스템 books 테이블에 신규 도서 등록(사용자 서재 등록 도서, 리뷰 작성된 도서)
    void insertBook(BookVO bookVO);

    // 서재에서 readingStatus만 조회
    String selectBookcaseReadingStatus(@Param("userId") Long userId, @Param("bookId") Long bookId);

    // 도서 상세보기 시 시스템에 등록된 도서 중 isbn으로 일치 도서 조회
    BookVO findByIsbn(String isbn);

    void existByIsbn(String isbn);

    BookVO findByBookId(Long bookId);

    BookVO findFromBookshelfByIsbn(String isbn);
}
