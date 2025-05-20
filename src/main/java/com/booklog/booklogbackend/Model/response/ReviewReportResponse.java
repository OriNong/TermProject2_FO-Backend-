package com.booklog.booklogbackend.Model.response;

import com.booklog.booklogbackend.Model.ReviewReportReason;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
public class ReviewReportResponse {

    private Long reportId;
    private Long reviewId;
    private String reviewTitle;   // 선택적으로 포함 가능
    private ReviewReportReason reasonCode;
    private String reason;
    private String status;
    private LocalDateTime reportedAt;
    private LocalDateTime processedAt;
}