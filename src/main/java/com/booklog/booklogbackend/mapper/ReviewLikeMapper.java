package com.booklog.booklogbackend.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface ReviewLikeMapper {

    // 이미 리뷰에 사용자가 좋아요를 눌렀는지 확인
    boolean existsByUserIdAndReviewId(@Param("userId") Long userId, @Param("reviewId") Long reviewId);

    // 좋아요 추가
    void insertLike(@Param("userId") Long userId, @Param("reviewId") Long reviewId);
    // 좋아요 추가 시 book_review 테이블의 likes_count 컬럼 업데이트
    void incrementLikeCount(@Param("reviewId") Long reviewId);

    // 좋아요 취소
    void deleteLike(@Param("userId") Long userId, @Param("reviewId") Long reviewId);
    // 좋아요 취소 시 book_review 테이블의 likes_count 컬럼 업데이트
    void decrementLikeCount(@Param("reviewId") Long reviewId);

    // 특정 리뷰의 총 좋아요 수 조회
    int countByReviewId(@Param("reviewId") Long reviewId);

}

