package com.booklog.booklogbackend.Model.vo;

import com.booklog.booklogbackend.Model.BookReadingStatus;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BookcaseVO {
    private Long bookcaseId;
    private Long userId;
    private Long bookId;
    private BookReadingStatus readingStatus; // TO_READ, READING, COMPLETED
    private LocalDateTime readingStartedAt;
    private LocalDateTime readingFinishedAt;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
