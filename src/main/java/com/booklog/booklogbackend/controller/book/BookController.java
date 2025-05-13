package com.booklog.booklogbackend.controller.book;

import com.booklog.booklogbackend.Model.CustomUserDetails;
import com.booklog.booklogbackend.Model.request.BookSearchRequest;
import com.booklog.booklogbackend.Model.response.BookSearchResponse;
import com.booklog.booklogbackend.Model.response.BookWithReviewStaticsResponse;
import com.booklog.booklogbackend.Model.response.BookcaseStatsResponse;
import com.booklog.booklogbackend.Model.vo.BookVO;
import com.booklog.booklogbackend.service.BookService;
import com.booklog.booklogbackend.service.BookcaseService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/books")
@RequiredArgsConstructor
@Validated
public class BookController {

    private final BookService bookService;
    private final BookcaseService bookcaseService;

    /**
     * 도서 검색 API
     * @param request: 아래로 구성
     * query 검색 키워드 (필수)
     * sort 정렬 기준 (선택, 기본값 "accuracy")
     * page 페이지 번호 (선택, 기본값 1)
     * limit 페이지당 결과 수 (선택, 기본값 10)
     * @return BookVO 리스트
     */
    @GetMapping("/search")
    public ResponseEntity<BookSearchResponse> searchBooks(
            @Valid @ModelAttribute BookSearchRequest request
            ) {
        BookSearchResponse bookSearchResult = bookService.searchBooks(
                request.getQuery(),
                request.getSort(),
                request.getPage(),
                request.getLimit()
        );
        return ResponseEntity.ok(bookSearchResult);
    }

    /**
     * 특정 도서의 서재 등록 통계 조회
     * @param bookId : 도서 고유 id
     * @return : BookcaseStatsResponse{
     *     서재에 등록한 총 사용자 수
     *     독서 중인 사용자 수
     *     독서 완료 사용자 수
     * }
     */
    @GetMapping("/bookcase/stats/{bookId}")
    public ResponseEntity<BookcaseStatsResponse> getBookcaseStats(
            @PathVariable Long bookId
    ) {
        BookcaseStatsResponse stats = bookService.getTotalBookcaseStats(bookId);
        return ResponseEntity.ok(stats);
    }

    /**
     * 사용자의 도서 읽기 상태(TO_READ, READING, COMPLETED) 조회
     * @param userDetails : 로그인 사용자
     * @param bookId : 도서 id
     * @return : String {TO_READ, READING, COMPLETED}
     */
    @GetMapping("/readingStatus/{bookId}")
    public ResponseEntity<String> getReadingStatus(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @PathVariable Long bookId
    ) {
        Long userId = userDetails.getUser().getUserId();
        String status = bookService.getReadingStatus(userId, bookId);

        if (status == null) {
            return ResponseEntity.notFound().build();
        }

        return ResponseEntity.ok(status);
    }

    /**
     * ISBN으로 도서 단건 조회
     *
     * @param isbn ISBN 코드
     * @return BookVO (없을 경우 404 반환)
     */
    @GetMapping("/{isbn}")
    public ResponseEntity<BookVO> getBookByIsbn(@PathVariable("isbn") String isbn) {
        BookVO book = bookService.getBookByIsbn(isbn);
        return ResponseEntity.ok(book);
    }

    /**
     * 리뷰가 1건 이상 등록되어 있는 도서 목록 조회
     * 메인페이지 진입 시 사용
     * @return : 도서 정보 + 해당 도서의 리뷰 통계 정보(평균 평점, 등록 리뷰 수)
     */
    @GetMapping("/reviewed")
    public ResponseEntity<List<BookWithReviewStaticsResponse>> getBooksWithReview(){
        return ResponseEntity.ok(bookService.getBooksWithReviewSummary());
    }
}

