package com.booklog.booklogbackend.Model.request;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BookRegisterRequest {
    private String isbn;            // 도서 고유 식별자
    private String bookTitle;       // 제목
    private String bookLink;        // 네이버 도서 상세 페이지
    private String bookImg;         // 썸네일 이미지
    private String bookAuthor;      // 저자
    private Long bookDiscount;      // 도서 가격
    private String bookPublisher;   // 도서 출판사
    private String bookPubDate;     // 도서 출판일
    private String bookDescription; // 네이버 도서 요약 줄거리
}
