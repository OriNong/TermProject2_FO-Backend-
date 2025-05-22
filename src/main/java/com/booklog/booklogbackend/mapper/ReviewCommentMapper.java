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

    // 댓글 존재 여부 확인 및 자신의 댓글인지 권한 체크
    boolean existsByCommentIdAndUserId(@Param("commentId") Long commentId, @Param("userId") Long userId);

    // 댓글 삭제
    void deleteComment(@Param("commentId") Long commentId);

    // 댓글 삭제 전 이미 삭제된 댓글인지 확인
    boolean isCommentAlreadyDeleted(@Param("commentId") Long commentId, @Param("userId") Long userId);

    // 댓글 수정
    void updateComment(@Param("commentId") Long commentId, @Param("content") String content);

    // 삭제된 댓글을 수정하는 경우 -> 댓글 수정 + is_deleted(삭제 여부)를 false로 변경
    void updateDeletedComment(@Param("commentId") Long commentId, @Param("content") String content);


}
