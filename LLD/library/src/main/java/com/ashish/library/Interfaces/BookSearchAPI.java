package com.ashish.library.Interfaces;

import com.ashish.library.Models.Book;

import java.time.LocalDate;
import java.util.List;

public interface BookSearchAPI {
    List<Book> searchByBookTitle(String bookTitle);
    List<Book> searchByAuthorName(String authorName);
    List<Book> searchBySubject(String Subject);
    List<Book> searchByPublicationDate(LocalDate date);
}
