package com.ashish.library.Controllers;

import com.ashish.library.Interfaces.BookInventoryAPI;
import com.ashish.library.Interfaces.BookManagerAPI;
import com.ashish.library.Interfaces.BookRepository;
import com.ashish.library.Models.BookItem;
import com.ashish.library.Models.BookLending;
import com.ashish.library.Models.BookStatus;
import com.ashish.library.Models.Member;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class BookService implements BookInventoryAPI {

    @Autowired
    private BookRepository bookRepository;


    @Override
    public void AddBook(BookItem book) {
        bookRepository.AddBookToStorage(book);
    }

    @Override
    public void removeBook(String bookId) {
        BookItem book =  bookRepository.getBookById(bookId);
        if(book==null || book.getStatus()==BookStatus.Loaned) {
            System.out.println("Book is loaned or not found in the system");
        } else {
            bookRepository.removeBookFromStorage(bookId);
            System.out.println("Book is removed from the system");
        }
    }

    @Override
    public void modifyBook(BookItem book) {
     bookRepository.modifyBookInStorage(book);
    }

}
