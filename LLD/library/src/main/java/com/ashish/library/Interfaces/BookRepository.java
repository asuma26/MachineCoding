package com.ashish.library.Interfaces;

import com.ashish.library.Models.Book;

import java.time.LocalDate;
import java.util.List;

public interface BookRepository {
    List<Book> getBooksByTitle(String bookTitle);
    List<Book> getBooksByAuthorName(String authorName);
    List<Book> getBooksBySubject(String Subject);
    List<Book> getBooksByPublishDate(LocalDate date);
    boolean AddBookToStorage(Book book);
    boolean removeBookFromStorage(Book book);
    boolean modifyBookInStorage(Book book);
}


// library (multiple library)
// member, admin
// Reservation , users
//
