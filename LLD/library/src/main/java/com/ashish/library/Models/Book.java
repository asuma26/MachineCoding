package com.ashish.library.Models;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.util.List;

@Getter
@Setter
public abstract class Book {
    private String bookId;
    private String bookTitle;
    private String subject;
    private String publisher;
    private String language;
    private int numberOfPages;
    private LocalDate publicationDate;
    private List<String> authors;
}
