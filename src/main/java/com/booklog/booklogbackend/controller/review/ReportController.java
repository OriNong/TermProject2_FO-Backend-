package com.booklog.booklogbackend.controller.review;

import com.booklog.booklogbackend.Model.request.ReportUpdateRequest;
import com.booklog.booklogbackend.Model.request.ReviewReportRequest;
import com.booklog.booklogbackend.Model.response.ReviewReportResponse;
import com.booklog.booklogbackend.service.ReportService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("api/reports")
@RequiredArgsConstructor
public class ReportController {

    private final ReportService reportService;

    /**
     * 리뷰 신고 등록
     */
    @PostMapping("/register")
    public ResponseEntity<Void> reportReview(
            @AuthenticationPrincipal(expression = "userId") Long userId,
            @Valid @RequestBody ReviewReportRequest request
    ) {
        reportService.createReport(userId, request);
        return ResponseEntity.ok().build();
    }

    /**
     * 내가 신고한 리뷰 목록 조회
     */
    @GetMapping("/my")
    public ResponseEntity<List<ReviewReportResponse>> getMyReports(
            @AuthenticationPrincipal(expression = "userId") Long userId
    ) {
        return ResponseEntity.ok(reportService.getMyReports(userId));
    }

    /**
     * 내가 신고한 리뷰 수정
     */
    @PutMapping("/update/{reportId}")
    public ResponseEntity<Void> updateReport(
            @PathVariable Long reportId,
            @AuthenticationPrincipal(expression = "userId") Long userId,
            @Valid @RequestBody ReportUpdateRequest request
    ) {
        reportService.updateMyReport(userId, reportId, request);
        return ResponseEntity.ok().build();
    }

    /**
     * 내가 신고한 리뷰 신고 취소 (Hard delete)
     */
    @DeleteMapping("/delete/{reportId}")
    public ResponseEntity<Void> deleteReport(
            @PathVariable Long reportId,
            @AuthenticationPrincipal(expression = "userId") Long userId
    ) {
        reportService.deleteMyReport(userId, reportId);
        return ResponseEntity.ok().build();
    }
}
