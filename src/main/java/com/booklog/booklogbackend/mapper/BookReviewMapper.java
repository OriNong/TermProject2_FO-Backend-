package com.booklog.booklogbackend.mapper;

import com.booklog.booklogbackend.Model.response.BookReviewDetailResponse;
import com.booklog.booklogbackend.Model.response.BookReviewResponse;
import com.booklog.booklogbackend.Model.response.MyReviewResponse;
import com.booklog.booklogbackend.Model.vo.BookReviewVO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface BookReviewMapper {

    // 리뷰 id로 리뷰 조회
    BookReviewVO selectByReviewId(Long reviewId);

    // 리뷰 등록
    void insertReview(BookReviewVO review);

    // 리뷰 수정
    void updateReview(BookReviewVO review);

    // 리뷰 삭제
    void deleteReview(Long reviewId);

    // userId와 bookId로 이미 작성된 리뷰가 있는지 조회
    boolean isReviewExist(@Param("userId") Long userId, @Param("bookId") Long bookId);

    // 특정 도서에 작성된 리뷰 목록 조회
    List<BookReviewResponse> selectReviewByBookId(@Param("bookId") Long bookId, @Param("userId") Long userId);

    // 리뷰 상세 조회
    BookReviewDetailResponse selectReviewDetailById(@Param("reviewId") Long reviewId, @Param("userId") Long userId);

    // 사용자가 작성한 리뷰 목록 조회
    List<MyReviewResponse> selectMyReviews(Long userId);

}
