package com.ashish.library.Interfaces;

import com.ashish.library.Models.BookItem;

public interface BookInventoryAPI {
    void AddBook(BookItem book);
    void removeBook(String bookId);
    void modifyBook(BookItem book);
}
