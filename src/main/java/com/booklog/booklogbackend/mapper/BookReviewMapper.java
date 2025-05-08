package com.booklog.booklogbackend.mapper;

import com.booklog.booklogbackend.Model.vo.BookReviewVO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface BookReviewMapper {

    void insertReview(BookReviewVO review);

    boolean isReviewExist(@Param("userId") Long userId, @Param("bookId") Long bookId);
}
