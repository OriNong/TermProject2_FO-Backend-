package com.booklog.booklogbackend.mapper;

import com.booklog.booklogbackend.Model.response.*;
import com.booklog.booklogbackend.Model.vo.BookReviewVO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface BookReviewMapper {

    // 리뷰 id로 리뷰 조회
    BookReviewVO selectByReviewId(@Param("reviewId") Long reviewId);

    // 리뷰 등록
    void insertReview(BookReviewVO review);

    // 리뷰 수정
    void updateReview(BookReviewVO review);

    // 리뷰 논리적 삭제
    void deleteReview(@Param("reviewId") Long reviewId);

    // 리뷰 물리적 삭제
    void reviewPhysicalDeletion(@Param("reviewId") Long reviewId);

    // userId와 bookId로 이미 작성된 리뷰가 있는지 조회
    boolean isReviewExist(@Param("userId") Long userId, @Param("bookId") Long bookId);

    // 특정 도서에 작성된 리뷰 목록 조회
    List<BookReviewResponse> selectReviewByBookId(@Param("bookId") Long bookId, @Param("userId") Long userId);

    // 리뷰 상세 조회
    BookReviewDetailResponse selectReviewDetailById(@Param("reviewId") Long reviewId, @Param("userId") Long userId);

    // 사용자가 작성한 리뷰 목록 조회
    List<MyReviewResponse> selectMyReviews(@Param("userId") Long userId);

    // 사용자가 작성한 리뷰 중 관리자가 삭제한 리뷰 목록 조회
    List<MyReviewDeletedByAdminResponse> selectMyReviewDeletedByAdmin(@Param("userId") Long userId);

    // 사용자 id와 도서 id로 리뷰 id 조회 -> 사용자 서재에서 독서 완료 도서의 리뷰 작성 여부 판단
    ReviewIdByBookIdResponse getReviewIdByBookAndUserId(@Param("userId") Long userId, @Param("bookId") Long bookId);

}
