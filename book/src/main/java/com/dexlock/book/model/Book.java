package com.dexlock.book.model;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

@Entity
@Table(name = "books")
public class Book {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long bookId;

    @NotBlank(message = "Title is mandatory")
    @Size(max = 40)
    @Column
    private  String title;


    @NotBlank(message = "Genre is mandatory")
    @Size(max = 40)
    @Column
    private  String genre;

    @NotBlank(message = "Synopsis is mandatory")
    @Size(max = 40)
    @Column
    private  String synopsis;

    @NotBlank(message = "Author is mandatory")
    @Size(max = 40)
    @Column
    private  String author;

    public Book(Long bookId,  String genre, String synopsis, String author) {
        this.bookId = bookId;
        this.genre = genre;
        this.synopsis = synopsis;
        this.author = author;
    }

    public Book(Long bookId, String title,  String genre, String synopsis,  String author) {
        this.bookId = bookId;
        this.title = title;
        this.genre = genre;
        this.synopsis = synopsis;
        this.author = author;
    }

    public Book() {
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public Long getBookId() {
        return bookId;
    }

    public void setBookId(Long bookId) {
        this.bookId = bookId;
    }

    public String getGenre() {
        return genre;
    }

    public void setGenre(String genre) {
        this.genre = genre;
    }

    public String getSynopsis() {
        return synopsis;
    }

    public void setSynopsis(String synopsis) {
        this.synopsis = synopsis;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }
}

