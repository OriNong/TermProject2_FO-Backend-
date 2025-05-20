package com.booklog.booklogbackend.service.impl;

import com.booklog.booklogbackend.Model.request.ReviewReportRequest;
import com.booklog.booklogbackend.Model.response.ReviewReportResponse;
import com.booklog.booklogbackend.exception.BadRequestException;
import com.booklog.booklogbackend.mapper.ReportMapper;
import com.booklog.booklogbackend.service.ReportService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ReportServiceImpl implements ReportService {

    private final ReportMapper reportMapper;

    @Override
    @Transactional
    public void createReport(Long userId, ReviewReportRequest request) {
        // 중복 신고 여부 확인
        if (reportMapper.existsByUserAndReview(userId, request.getReviewId())) {
            throw new BadRequestException("동일한 리뷰를 중복 신고할 수 없습니다.");
        }

        reportMapper.insertReport(userId, request);
    }

    @Override
    public List<ReviewReportResponse> getMyReports(Long userId) {
        return reportMapper.selectMyReports(userId);
    }

    @Override
    @Transactional
    public void updateMyReport(Long userId, Long reportId, ReviewReportRequest request) {
        if (!reportMapper.isEditable(userId, reportId)) {
            throw new BadRequestException("이미 관리자가 처리 완료하여 수정할 수 없습니다.");
        }

        reportMapper.updateReport(userId, reportId, request);
    }

    @Override
    @Transactional
    public void deleteMyReport(Long userId, Long reportId) {
        if (!reportMapper.isEditable(userId, reportId)) {
            throw new BadRequestException("이미 관리자가 처리 완료하여 삭제할 수 없습니다.");
        }

        reportMapper.deleteReport(userId, reportId);
    }
}
