package com.dexlock.book.repository;

import com.dexlock.book.model.Book;

import java.util.List;

public interface IBookService {
    List<Book> findPaginated(int pageNo, int pageSize);
}
