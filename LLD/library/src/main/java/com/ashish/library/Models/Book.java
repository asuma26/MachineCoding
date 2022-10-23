package com.ashish.library.Models;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
public class Book {
    private String bookId;
    private String bookTitle;
    private String subject;
    private String authorName;
    private Boolean isIssued;
    private LocalDate publicationDate;
}
