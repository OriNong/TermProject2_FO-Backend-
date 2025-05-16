package com.booklog.booklogbackend.mapper;

import com.booklog.booklogbackend.Model.vo.ReviewReportVO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface ReviewReportMapper {

    // 신고 등록
    void insertReport(ReviewReportVO report);

    // 상태별 신고 목록 조회 (BO용)
    List<ReviewReportVO> selectReportsByStatus(String status);

    // 신고 상태 변경 (PENDING → APPROVED / REJECTED)
    int updateReportStatus(ReviewReportVO report);

    // 특정 리뷰의 신고 건수 조회
    int countReportsByReviewId(@Param("reviewId") Long reviewId);
}
