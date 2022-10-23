package com.ashish.library.Controllers;

import com.ashish.library.DataBase.BooksDatabase;
import com.ashish.library.Interfaces.BookSearchAPI;
import com.ashish.library.Models.Book;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
public class BookSearchService implements BookSearchAPI {

    private BooksDatabase booksDatabase = BooksDatabase.getBooksDatabase();

    @Override
    public List<Book> searchByBookTitle(String bookTitle) {
        return booksDatabase.getBooksByTitle(bookTitle);
    }

    @Override
    public List<Book> searchByAuthorName(String authorName) {
        return booksDatabase.getBooksByAuthorName(authorName);
    }

    @Override
    public List<Book> searchBySubject(String Subject) {
        return booksDatabase.getBooksBySubject(Subject);
    }

    @Override
    public List<Book> searchByPublicationDate(LocalDate date) {
        return booksDatabase.getBooksByPublishDate(date);
    }
}
