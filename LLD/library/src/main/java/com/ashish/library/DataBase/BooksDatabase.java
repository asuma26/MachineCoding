package com.ashish.library.DataBase;

import com.ashish.library.Interfaces.BookRepository;
import com.ashish.library.Models.Book;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class BooksDatabase implements BookRepository {

    private Map<String, Book> bookStorage;
    private Map<String, Integer> countOfBooks;
    private BooksDatabase(){}

    private static BooksDatabase booksDatabase;

    public static BooksDatabase getBooksDatabase(){
        if(booksDatabase == null){
            booksDatabase = new BooksDatabase();
        }
        return booksDatabase;
    }


    @Override
    public List<Book> getBooksByTitle(String bookTitle) {
        List<Book> listOfBooks;
       listOfBooks = bookStorage.values().stream().filter(book -> book.getBookTitle()==bookTitle&&isPresentInInventory(book)).collect(Collectors.toList());
       return  listOfBooks;
    }

    @Override
    public List<Book> getBooksByAuthorName(String authorName) {
        List<Book> listOfBooks;
        listOfBooks = bookStorage.values().stream().filter(book -> book.getAuthorName()==authorName&&isPresentInInventory(book)).collect(Collectors.toList());
        return  listOfBooks;
    }

    @Override
    public List<Book> getBooksBySubject(String Subject) {
        List<Book> listOfBooks;
        listOfBooks = bookStorage.values().stream().filter(book -> book.getSubject()==Subject&&isPresentInInventory(book)).collect(Collectors.toList());
        return  listOfBooks;
    }

    @Override
    public List<Book> getBooksByPublishDate(LocalDate date) {
        List<Book> listOfBooks;
        listOfBooks = bookStorage.values().stream().filter(book -> book.getPublicationDate().isEqual(date)&&isPresentInInventory(book)).collect(Collectors.toList());
        return  listOfBooks;
    }

    @Override
    public boolean AddBookToStorage(Book book) {
        bookStorage.put(book.getBookId(),book);
        countOfBooks.put(book.getBookId(),countOfBooks.getOrDefault(book.getBookId(),0)+1);
        return true;
    }

    @Override
    public boolean removeBookFromStorage(Book book) {
        if(doesBookExist(book)==false|| !isPresentInInventory(book)){
            return false;
        }
        bookStorage.remove(book.getBookId());
        countOfBooks.put(book.getBookId(),countOfBooks.get(book.getBookId())-1);
        return true;
    }

    @Override
    public boolean modifyBookInStorage(Book book) {
        if(doesBookExist(book)==false || !isPresentInInventory(book)){
            return false;
        }
        bookStorage.put(book.getBookId(),book);
        return  true;
    }

    private boolean doesBookExist(Book book){
        if(bookStorage.containsKey(book.getBookId())==false){
            return false;
        }
        return true;
    }

    private boolean isPresentInInventory(Book book){
        return countOfBooks.getOrDefault(book.getBookId(),0)==0?false:true;
    }
}
