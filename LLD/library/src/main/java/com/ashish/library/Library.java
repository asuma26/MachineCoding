package com.ashish.library;

import com.ashish.library.Controllers.BookService;
import com.ashish.library.Controllers.Librarian;
import com.ashish.library.Interfaces.BookSearchAPI;
import com.ashish.library.Models.BookItem;
import org.springframework.beans.factory.annotation.Autowired;

import java.time.LocalDate;
import java.util.List;

public class Library {

    @Autowired
    BookSearchAPI bookSearchAPI;

    @Autowired
    BookService bookService;

    @Autowired
    Librarian librarian;


    public List<BookItem> searchByBookTitle(String bookTitle) {
        return bookSearchAPI.searchByBookTitle(bookTitle);
    }


    public List<BookItem> searchByAuthorName(String authorName) {
        return bookSearchAPI.searchByAuthorName(authorName);
    }


    public List<BookItem> searchBySubject(String subject) {
        return bookSearchAPI.searchBySubject(subject);
    }

    public List<BookItem> searchByPublicationDate(LocalDate date) {
        return bookSearchAPI.searchByPublicationDate(date);
    }


    public void AddBookToLibrary(BookItem book) {
        librarian.addBookItem(book);
    }

    public void removeBook(String bookId) {
        bookService.removeBook(bookId);
    }





}
