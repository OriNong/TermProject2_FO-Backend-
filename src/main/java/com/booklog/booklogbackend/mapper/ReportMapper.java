package com.booklog.booklogbackend.mapper;

import com.booklog.booklogbackend.Model.request.ReportUpdateRequest;
import com.booklog.booklogbackend.Model.request.ReviewReportRequest;
import com.booklog.booklogbackend.Model.response.ReviewReportResponse;
import jakarta.validation.Valid;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface ReportMapper {

    // 이미 신고를 했었는지 여부 조회
    boolean existsByUserAndReview(@Param("userId") Long userId, @Param("reviewId") Long reviewId);

    // 리뷰 신고
    void insertReport(@Param("userId") Long userId, @Param("request") ReviewReportRequest request);

    // 자신의 신고 내역 조회
    List<ReviewReportResponse> selectMyReports(@Param("userId") Long userId);

    // 수정/삭제 가능 여부 조회 (관리자 처리 여부)
    boolean isEditable(@Param("userId") Long userId, @Param("reportId") Long reportId);

    // 신고 수정
    void updateReport(@Param("userId") Long userId, @Param("reportId") Long reportId, @Param("request") @Valid ReportUpdateRequest request);

    // 신고 취소 (Hard Delete)
    void deleteReport(@Param("userId") Long userId, @Param("reportId") Long reportId);
}
