package com.ashish.library.Interfaces;

import com.ashish.library.Models.Book;

public interface BookInventoryAPI {
    void AddBook(Book book);
    void removeBook(Book book);
    void modifyBook(Book book);
}
