package com.booklog.booklogbackend.controller.book;

import com.booklog.booklogbackend.Model.request.BookSearchRequest;
import com.booklog.booklogbackend.Model.vo.BookVO;
import com.booklog.booklogbackend.service.BookService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
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
    public ResponseEntity<List<BookVO>> searchBooks(
            @Valid @ModelAttribute BookSearchRequest request
            ) {
        List<BookVO> books = bookService.searchBooks(
                request.getQuery(),
                request.getSort(),
                request.getPage(),
                request.getLimit()
        );
        return ResponseEntity.ok(books);
    }
}

