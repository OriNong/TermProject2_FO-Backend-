package com.booklog.booklogbackend.mapper;

import com.booklog.booklogbackend.Model.vo.ReviewCommentVO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface ReviewCommentMapper {
    // 댓글 등록
    void insertComment(ReviewCommentVO reviewCommentVO);

    // 특정 리뷰의 댓글 조회
    List<ReviewCommentVO> selectCommentsByReviewId(@Param("reviewId") Long reviewId);

}
