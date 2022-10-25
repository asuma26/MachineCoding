package com.ashish.library.DataBase;

import com.ashish.library.Interfaces.BookRepository;
import com.ashish.library.Models.BookItem;
import com.ashish.library.Models.Member;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class BooksDatabase implements BookRepository {

    private Map<String, BookItem> bookStorage = new HashMap<>();;
//    private Map<String, Integer> countOfBooks;
    // Todo: do we need a reference storage to display books and then check for its storage in inventory storage ??
    private BooksDatabase(){}

    private static BooksDatabase booksDatabase;

    public static BooksDatabase getBooksDatabase(){
        if(booksDatabase == null){
            booksDatabase = new BooksDatabase();
        }
        return booksDatabase;
    }


    @Override
    public List<BookItem> getBooksByTitle(String bookTitle) {
        List<BookItem> listOfBooks;
       listOfBooks = bookStorage.values().stream().filter(book -> book.getBookTitle()==bookTitle).collect(Collectors.toList());
       return  listOfBooks;
    }

    @Override
    public List<BookItem> getBooksByAuthorName(String authorName) {
        List<BookItem> listOfBooks;
        listOfBooks = bookStorage.values().stream().filter(book -> book.getAuthors().contains(authorName)).collect(Collectors.toList());
        return  listOfBooks;
    }

    @Override
    public List<BookItem> getBooksBySubject(String Subject) {
        List<BookItem> listOfBooks;
        listOfBooks = bookStorage.values().stream().filter(book -> book.getSubject()==Subject).collect(Collectors.toList());
        return  listOfBooks;
    }

    @Override
    public List<BookItem> getBooksByPublishDate(LocalDate date) {
        List<BookItem> listOfBooks;
        listOfBooks = bookStorage.values().stream().filter(book -> book.getPublicationDate().isEqual(date)).collect(Collectors.toList());
        return  listOfBooks;
    }

    @Override
    public BookItem getBookById(String bookId) {
        return bookStorage.get(bookId);
    }

    @Override
    public boolean AddBookToStorage(BookItem book) {
        bookStorage.put(book.getBookId(),book);
//        countOfBooks.put(book.getBookId(),countOfBooks.getOrDefault(book.getBookId(),0)+1);
        return true;
    }

    @Override
    public boolean removeBookFromStorage(String bookId) {
        if(doesBookExist(bookId)==false){
            return false;
        }
        bookStorage.remove(bookId);
//        countOfBooks.put(book.getBookId(),countOfBooks.get(book.getBookId())-1);
        return true;
    }

    @Override
    public boolean modifyBookInStorage(BookItem book) {
        if(doesBookExist(book.getBookId())==false){
            return false;
        }
        bookStorage.put(book.getBookId(),book);
        return  true;
    }

    private boolean doesBookExist(String bookId){
        if(bookStorage.containsKey(bookId)==false){
            return false;
        }
        return true;
    }

//    private boolean isPresentInInventory(BookItem book){
//        return countOfBooks.getOrDefault(book.getBookId(),0)==0?false:true;
//    }
}
