package com.ashish.library.Controllers;

import com.ashish.library.Interfaces.BookInventoryAPI;
import com.ashish.library.Interfaces.BookManagerAPI;
import com.ashish.library.Interfaces.BookRepository;
import com.ashish.library.Models.Book;
import org.springframework.beans.factory.annotation.Autowired;

public class BookService extends BookManagerAPI implements BookInventoryAPI {

    @Autowired
    private BookRepository bookRepository;

    @Override
    public void AddBook(Book book) {
        bookRepository.AddBookToStorage(book);
    }

    @Override
    public void removeBook(Book book) {
     bookRepository.removeBookFromStorage(book);
    }

    @Override
    public void modifyBook(Book book) {
     bookRepository.modifyBookInStorage(book);
    }

    @Override
    public Book issueBook(Book book) {
        book.setIsIssued(true);
        bookRepository.modifyBookInStorage(book);
    }

    @Override
    public Book renewBook(Book book) {

    }

    @Override
    public Book returnBook(Book book) {
        return null;
    }
}
