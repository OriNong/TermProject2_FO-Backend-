package com.booklog.booklogbackend.service;

import com.booklog.booklogbackend.Model.request.BookRegisterRequest;
import com.booklog.booklogbackend.Model.response.BookcaseResponse;
import com.booklog.booklogbackend.Model.vo.BookcaseVO;

import java.util.List;

public interface BookcaseService {

    // 사용자 서재에 신규 도서 등록
    void registerToBookcase(Long userId, BookRegisterRequest request);

    // 서재에 읽을 예정으로 등록된 도서 '독서 중'으로 상태 변경
    void startReading(Long userId, Long bookId);

    // 서재에 '독서 중'으로 등록되어 있는 도서 독서 완료 처리
    void finishReading(Long userId, Long bookId);

    // 사용자 서재 전체 정보 조회
    public List<BookcaseResponse> getBookcaseByUser(Long userId);

    // 사용자 서재에 등록된 특정 도서에 대한 독서 상태 조회
    BookcaseVO getBookcaseStatus(Long userId, Long bookId);

    void upsertBookcase(BookcaseVO bookcaseVO);
}