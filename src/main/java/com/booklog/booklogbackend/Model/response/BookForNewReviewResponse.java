package com.booklog.booklogbackend.Model.response;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BookForNewReviewResponse {
    private Long bookId;                // 도서 고유 id
    private String isbn;                // 도서 isbn
    private String bookTitle;           // 도서 이름
    private String bookImg;             // 도서 이미지 URL
    private String bookAuthor;          // 도서 저자
    private String bookPublisher;       // 도서 출판사
}
