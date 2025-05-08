package com.booklog.booklogbackend.mapper;

import com.booklog.booklogbackend.Model.vo.BookVO;
import com.booklog.booklogbackend.Model.vo.BookcaseVO;
import com.booklog.booklogbackend.Model.vo.BookcaseWithBookVO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface BookcaseMapper {

    BookVO findByIsbn(String isbn);

    void insertBook(BookVO bookVO);

    // 읽기 상태 조회
    BookcaseVO selectBookcaseStatus(@Param("userId") Long userId, @Param("bookId") Long bookId);

    // 읽기 상태 변경
    int updateBookcaseStatus(BookcaseVO bookcaseVO);

    // 신규 등록
    int insertBookcase(BookcaseVO bookcaseVO);

    // 사용자의 서재 전체 정보 조회
    List<BookcaseWithBookVO> selectBookcaseByUserId(Long userId);

    // 리뷰 작성 시 도서 읽기 상태가 COMPLETED인지 조회
        // true: COMPLETED, false: TO_READ || READING
    boolean isBookReadCompleted(@Param("userId") Long userId, @Param("bookId") Long bookId, @Param("status") String status);
}
