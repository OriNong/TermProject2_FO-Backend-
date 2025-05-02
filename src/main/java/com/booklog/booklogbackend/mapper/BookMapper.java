package com.booklog.booklogbackend.mapper;

import com.booklog.booklogbackend.Model.vo.BookVO;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface BookMapper {
    BookVO findByBookId(Long bookId);
    BookVO findFromBooksByIsbn(String isbn);
    BookVO findFromBookshelfByIsbn(String isbn);
}
