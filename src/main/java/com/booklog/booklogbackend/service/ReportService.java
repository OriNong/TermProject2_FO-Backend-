package com.booklog.booklogbackend.service;

import com.booklog.booklogbackend.Model.request.ReviewReportRequest;
import com.booklog.booklogbackend.Model.response.ReviewReportResponse;

import java.util.List;

public interface ReportService {

    void createReport(Long userId, ReviewReportRequest request);

    List<ReviewReportResponse> getMyReports(Long userId);

    void updateMyReport(Long userId, Long reportId, ReviewReportRequest request);

    void deleteMyReport(Long userId, Long reportId);
}
