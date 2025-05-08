package com.booklog.booklogbackend.controller.book;

import com.booklog.booklogbackend.Model.request.BookReviewRequest;
import com.booklog.booklogbackend.Model.response.BookForNewReviewResponse;
import com.booklog.booklogbackend.service.BookReviewService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/reviews")
@RequiredArgsConstructor
public class BookReviewController {

    private final BookReviewService bookReviewService;

    /**
     * 리뷰 작성하려는 도서의 정보 조회(도서 이미지, 도서 제목, 도서 저자, 도서 출판사)
     * @param bookId : 도서 id
     * @return : 페이지 표시에 필요한 정보만 return
     */
    @GetMapping("book/{bookId}")
    public ResponseEntity<BookForNewReviewResponse> getReviewRequestBook(@PathVariable("bookId") Long bookId) {
        BookForNewReviewResponse book = bookReviewService.getReviewRequestBook(bookId);
        return ResponseEntity.ok(book);
    }

    /**
     * 도서 리뷰 신규 등록
     * @param userId : 로그인 사용자 id
     * @param bookReviewRequest : 작성된 리뷰 form
     * @return
     */
    @PostMapping("/register")
    public ResponseEntity<Void> registerReview(
            @AuthenticationPrincipal(expression = "userId") Long userId,
            @Valid @RequestBody BookReviewRequest bookReviewRequest
            ) {
        bookReviewService.registerReview(userId, bookReviewRequest);
        return ResponseEntity.ok().build();
    }
}
