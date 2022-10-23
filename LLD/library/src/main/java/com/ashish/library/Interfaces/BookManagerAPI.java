package com.ashish.library.Interfaces;

import com.ashish.library.Models.Book;

public abstract class BookManagerAPI {
    private BookInventoryAPI inventory;
    public abstract Book issueBook(Book book);
    public abstract Book renewBook(Book book);
    public abstract Book returnBook(Book book);
}
