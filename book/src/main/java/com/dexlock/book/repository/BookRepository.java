package com.dexlock.book.repository;

import com.dexlock.book.model.Book;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import java.util.List;
import java.util.Optional;


public interface BookRepository extends JpaRepository<Book, Long> {
@Query(value = "Select * from books where title like %?1%", nativeQuery = true)
    List<Book> findByTitle(String title);

    @Query(value = "SELECT * FROM books where genre like %?1% OR author like %?2%", nativeQuery = true)
    public Optional<List<Book>> getTheBooksWithGenreAndAuthor(String genre, String author);


}
