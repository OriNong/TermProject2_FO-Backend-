package com.booklog.booklogbackend.controller.book;

import com.booklog.booklogbackend.Model.CustomUserDetails;
import com.booklog.booklogbackend.Model.request.BookRegisterRequest;
import com.booklog.booklogbackend.Model.request.UpdateBookcaseStatusRequest;
import com.booklog.booklogbackend.Model.response.BookReviewResponse;
import com.booklog.booklogbackend.Model.response.BookcaseResponse;
import com.booklog.booklogbackend.Model.response.ReviewIdByBookIdResponse;
import com.booklog.booklogbackend.Model.vo.BookcaseVO;
import com.booklog.booklogbackend.service.BookReviewService;
import com.booklog.booklogbackend.service.BookcaseService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/bookcase")
@RequiredArgsConstructor
public class BookcaseController {

    private final BookcaseService bookcaseService;
    private final BookReviewService bookReviewService;

    /**
     * 로그인 사용자의 서재 정보 전체 조회 후 반환
     * @param userDetails : 로그인 사용자 정보
     * @return : 서재에 등록된 도서 정보를 리스트로 반환
     */
    @GetMapping("/user")
    public ResponseEntity<?> getUserBookcase(@AuthenticationPrincipal CustomUserDetails userDetails) {
        Long userId = userDetails.getUserId();
        List<BookcaseResponse> userBookcase = bookcaseService.getBookcaseByUser(userId);
        return ResponseEntity.ok(userBookcase);
    }

    /**
     * 사용자 서재에 도서 등록 (읽을 목록에 추가)
     * @param userDetails : 로그인 사용자 정보
     * @param request : BookcaseRegisterRequest
     * @return
     */
    @PostMapping("/register")
    public ResponseEntity<Void> registerBookToBookcase(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @RequestBody BookRegisterRequest request
    ) {
        Long userId = userDetails.getUserId();
        bookcaseService.registerToBookcase(userId, request);
        return ResponseEntity.ok().build();
    }

    /**
     * 독서 시작 시 해당 도서 독서 상태 'READING'으로 처리
     */
    @PostMapping("/startReading")
    public ResponseEntity<Void> readBookStart(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @RequestParam Long bookId
    ) {
        Long userId = userDetails.getUserId();
        bookcaseService.startReading(userId, bookId);
        return ResponseEntity.ok().build();
    }

    /**
     * 독서 완료 시 해당 도서 독서 상태 'COMPLETED'로 처리
     */
    @PostMapping("/readingComplete")
    public ResponseEntity<Void> readBookComplete(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @RequestParam Long bookId
    ){
        Long userId = userDetails.getUserId();
        bookcaseService.finishReading(userId, bookId);
        return ResponseEntity.ok().build();
    }

    /**
     * 서재 읽기 상태 되돌리기 처리
     */
    @PutMapping("/rollbackStatus")
    public ResponseEntity<?> updateReadingStatus(@AuthenticationPrincipal CustomUserDetails userDetails,
                                                 @RequestBody UpdateBookcaseStatusRequest request) {
        bookcaseService.updateReadingStatus(userDetails.getUserId(), request.getBookcaseId());
        return ResponseEntity.ok().build();
    }

    /**
     * 서재 등록 도서 삭제
     */
    @DeleteMapping("/delete/{bookcaseId}")
    public ResponseEntity<?> deleteBookcase(@AuthenticationPrincipal CustomUserDetails userDetails,
                                            @PathVariable Long bookcaseId) {
        bookcaseService.deleteBookcase(userDetails.getUserId(), bookcaseId);
        return ResponseEntity.ok().build();
    }

    /**
     * 도서 서재 상태 등록 또는 변경
     * @param userDetails 현재 로그인 사용자
     * @param bookId
     * @return
     */
    @GetMapping("/{bookId}")
    public ResponseEntity<BookcaseVO> getBookcaseStatus(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @PathVariable Long bookId
    ) {
        Long userId = userDetails.getUserId();
        BookcaseVO status = bookcaseService.getBookcaseStatus(userId, bookId);
        return ResponseEntity.ok(status);
    }

    /**
     * 내 서재 페이지에서 리뷰 작성 또는 수정을 표시하기 위해
     * 특정 도서에 대한 나의 리뷰 조회
     */
    @GetMapping("/review/bookId")
    public ResponseEntity<ReviewIdByBookIdResponse> getMyReviewForBookId(
            @RequestParam Long bookId,
            @AuthenticationPrincipal(expression = "userId") Long userId
    ) {
        return ResponseEntity.ok(bookcaseService.getReviewByUserAndBook(userId, bookId));
    }
}
