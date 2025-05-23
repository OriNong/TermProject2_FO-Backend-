package com.booklog.booklogbackend.mapper;

import com.booklog.booklogbackend.Model.vo.BookVO;
import com.booklog.booklogbackend.Model.vo.BookcaseVO;
import com.booklog.booklogbackend.Model.vo.BookcaseWithBookVO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface BookcaseMapper {

    // 읽기 상태 조회
    BookcaseVO selectBookcaseStatus(@Param("userId") Long userId, @Param("bookId") Long bookId);

    // 읽기 상태 변경
    int updateBookcaseStatus(BookcaseVO bookcaseVO);

    // 신규 등록
    int insertBookcase(BookcaseVO bookcaseVO);

    // 사용자의 서재 전체 정보 조회
    List<BookcaseWithBookVO> selectBookcaseByUserId(@Param("userId") Long userId);

    // 리뷰 작성 시 도서 읽기 상태가 COMPLETED인지 조회
        // true: COMPLETED, false: TO_READ || READING
    boolean isBookReadCompleted(@Param("userId") Long userId, @Param("bookId") Long bookId, @Param("status") String status);
    // 서재 고유 id로 서재 조회
    BookcaseVO selectBookcaseById(@Param("bookcaseId") Long bookcaseId);

    // 읽기 상태 되돌리기
    void updateReadingStatusWithDates(@Param("bookcaseId") Long bookcaseId, @Param("readingStatus") String readingStatus);

    // 서재 등록 도서 삭제
    void deleteBookcaseById(@Param("bookcaseId") Long bookcaseId);
}
