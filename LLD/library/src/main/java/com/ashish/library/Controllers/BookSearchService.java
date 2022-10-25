package com.ashish.library.Controllers;

import com.ashish.library.DataBase.BooksDatabase;
import com.ashish.library.Interfaces.BookSearchAPI;
import com.ashish.library.Models.BookItem;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
public class BookSearchService implements BookSearchAPI {

    private BooksDatabase booksDatabase = BooksDatabase.getBooksDatabase();

    @Override
    public List<BookItem> searchByBookTitle(String bookTitle) {
        return booksDatabase.getBooksByTitle(bookTitle);
    }

    @Override
    public List<BookItem> searchByAuthorName(String authorName) {
        return booksDatabase.getBooksByAuthorName(authorName);
    }

    @Override
    public List<BookItem> searchBySubject(String subject) {
        return booksDatabase.getBooksBySubject(subject);
    }

    @Override
    public List<BookItem> searchByPublicationDate(LocalDate date) {
        return booksDatabase.getBooksByPublishDate(date);
    }
}
