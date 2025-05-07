package com.booklog.booklogbackend.controller.book;

import com.booklog.booklogbackend.Model.CustomUserDetails;
import com.booklog.booklogbackend.Model.request.BookSearchRequest;
import com.booklog.booklogbackend.Model.response.BookSearchResponse;
import com.booklog.booklogbackend.Model.vo.BookVO;
import com.booklog.booklogbackend.service.BookService;
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

    @GetMapping("/readingStatus")
    public ResponseEntity<String> getReadingStatus(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @RequestParam Long bookId
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
}

