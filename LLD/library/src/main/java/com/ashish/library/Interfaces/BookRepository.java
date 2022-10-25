package com.ashish.library.Interfaces;

import com.ashish.library.Models.BookItem;
import com.ashish.library.Models.Member;
import com.sun.tools.corba.se.idl.constExpr.BooleanOr;

import java.time.LocalDate;
import java.util.List;

public interface BookRepository {
    List<BookItem> getBooksByTitle(String bookTitle);
    List<BookItem> getBooksByAuthorName(String authorName);
    List<BookItem> getBooksBySubject(String Subject);
    List<BookItem> getBooksByPublishDate(LocalDate date);
    BookItem getBookById(String bookId);
    boolean AddBookToStorage(BookItem book);
    boolean removeBookFromStorage(String bookId);
    boolean modifyBookInStorage(BookItem book);
}



// library (multiple library)
// member, admin
// Reservation , users
//
