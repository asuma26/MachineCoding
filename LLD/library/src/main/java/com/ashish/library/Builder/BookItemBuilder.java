package com.ashish.library.Builder;

import com.ashish.library.Models.BookItem;
import com.ashish.library.Models.BookShelf;
import com.ashish.library.Models.BookStatus;
import lombok.Setter;

import java.time.LocalDate;
import java.util.List;

@Setter
public class BookItemBuilder {

    private String bookId;
    private String bookTitle;
    private String subject;
    private String publisher;
    private String language;
    private int numberOfPages;
    private List<String> authors;

    private String barCode;
    private boolean isReferenceOnly;
    private LocalDate borrowedDate;
    private LocalDate dueDate;
    private double price;
    private BookStatus status;
    private LocalDate publicationDate;
    private BookShelf placedAt;
    private LocalDate dateOfPurchase;

    public BookItemBuilder(){}

    public BookItem build() {
        BookItem book =  new BookItem();
      return  book;
    }


}
