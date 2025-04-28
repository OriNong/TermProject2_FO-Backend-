package com.booklog.booklogbackend.controller.book;

import com.booklog.booklogbackend.Model.vo.BookVO;
import com.booklog.booklogbackend.service.BookService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/books")
@RequiredArgsConstructor
public class BookController {

    private final BookService bookService;

    /**
     * 도서 검색 API
     *
     * @param query 검색 키워드 (필수)
     * @param sort 정렬 기준 (선택, 기본값 "accuracy")
     * @param page 페이지 번호 (선택, 기본값 1)
     * @param limit 페이지당 결과 수 (선택, 기본값 10)
     * @return BookVO 리스트
     */
    @GetMapping("/search")
    public ResponseEntity<List<BookVO>> searchBooks(
            @RequestParam("query") String query,
            @RequestParam(value = "sort", defaultValue = "accuracy") String sort,
            @RequestParam(value = "page", defaultValue = "1") int page,
            @RequestParam(value = "limit", defaultValue = "10") int limit
    ) {
        List<BookVO> books = bookService.searchBooks(query, sort, page, limit);
        return ResponseEntity.ok(books);
    }
}

