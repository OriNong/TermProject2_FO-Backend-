package com.booklog.booklogbackend.controller.book;

import com.booklog.booklogbackend.Model.request.BookReviewRequest;
import com.booklog.booklogbackend.service.BookReviewService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/reviews")
@RequiredArgsConstructor
public class BookReviewController {

    private final BookReviewService bookReviewService;

    @PostMapping("/register")
    public ResponseEntity<Void> registerReview(
            @AuthenticationPrincipal(expression = "userId") Long userId,
            @Valid @RequestBody BookReviewRequest bookReviewRequest
            ) {
        bookReviewService.createReview(userId, bookReviewRequest);
        return ResponseEntity.ok().build();
    }
}
