package com.ashish.library.Interfaces;

import com.ashish.library.Models.BookItem;

import java.time.LocalDate;
import java.util.List;

public interface BookSearchAPI {
    List<BookItem> searchByBookTitle(String bookTitle);
    List<BookItem> searchByAuthorName(String authorName);
    List<BookItem> searchBySubject(String subject);
    List<BookItem> searchByPublicationDate(LocalDate date);
}
