package com.booklog.booklogbackend.Model.vo;

import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
@Builder
public class ReviewReportVO {
    private Long reportId;
    private Long reviewId;
    private Long reporterId;
    private String reason;
    private String reportStatus; // PENDING, APPROVED, REJECTED
    private LocalDateTime reportedAt;
    private LocalDateTime processedAt;
}
