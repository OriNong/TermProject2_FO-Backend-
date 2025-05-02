package com.booklog.booklogbackend.service;

import com.booklog.booklogbackend.Model.vo.BookVO;
import java.util.List;

public interface BookService {

    List<BookVO> searchBooks(String query, String sort, int page, int limit);

    BookVO getBookByIsbn(String isbn);
}
