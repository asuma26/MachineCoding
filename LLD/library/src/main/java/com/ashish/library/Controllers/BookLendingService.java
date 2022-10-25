package com.ashish.library.Controllers;

import com.ashish.library.Models.BookItem;
import com.ashish.library.Models.BookLending;
import com.ashish.library.Models.Constants;
import com.ashish.library.Models.Member;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.HashMap;

@Component
public class BookLendingService {

    private HashMap<String ,BookLending> bookLendingMap = new HashMap<>();

    @Autowired
    private MemberService memberService;


    public boolean lendBookItem(BookItem book, String memberId) {
        BookLending bookLending = new BookLending();
        bookLending.setBookItemId(book.getBookId());
        bookLending.setCreationDate(LocalDate.now());
        bookLending.setMemberId(memberId);
        bookLending.setDueDate(LocalDate.now().plus(Constants.MAX_LENDING_DAYS, ChronoUnit.DAYS));
        book.setBorrowedDate(LocalDate.now());
        bookLendingMap.put(book.getBookId(),bookLending);
        return true;
    }

    public BookLending fetchBookLending(String  bookItemId) {
        return bookLendingMap.get(bookItemId);
    }

    public boolean renewBookItem(String bookItemId, String memberId) {
        BookLending bookLending = bookLendingMap.get(bookItemId);
        if(bookLending != null && bookLending.getMemberId()==memberId) {
            if(canRenewBookItem(bookLending)) {
               bookLending.setRenewCount(bookLending.getRenewCount()+1);
               bookLending.setDueDate(LocalDate.now().plus(Constants.MAX_LENDING_DAYS, ChronoUnit.DAYS));
               bookLendingMap.put(bookItemId, bookLending);
               return true;
            } else {
                System.out.println("ERROR: Max renewal limit reached, can not be rewnewed more!! please return the book");
                return  false;
            }
        } else {
            System.out.println("NO Book was issued to the user= ${} with given bookItemId");
            return false;
        }
    }

    public boolean returnBookItem(String bookItemId, String memberId) {
        BookLending bookLending = bookLendingMap.get(bookItemId);
        if(bookLending != null && bookLending.getMemberId()==memberId) {
            bookLendingMap.remove(bookItemId);
            return true;
        } else {
            System.out.println("NO Book was issued to the user= ${} with given bookItemId");
            return false;
        }
    }

    private boolean canRenewBookItem(BookLending lending) {
        return lending.getRenewCount()<Constants.MAX_RENEW_COUNT;// && lending.getDueDate().isBefore(LocalDate.now());
    }
}




