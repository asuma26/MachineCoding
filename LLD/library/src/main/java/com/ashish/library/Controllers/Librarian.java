package com.ashish.library.Controllers;

import com.ashish.library.Controllers.BookService;
import com.ashish.library.Controllers.MemberService;
import com.ashish.library.Interfaces.BookManagerAPI;
import com.ashish.library.Models.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Set;

@Component
public class Librarian extends BookManagerAPI {

    @Autowired
    private BookService bookService;

    @Autowired
    private MemberService memberService;

    @Autowired
    private BookLendingService bookLendingService;

    public boolean addBookItem(BookItem bookItem) {
        bookService.AddBook(bookItem);
        return true;
    }

    public boolean removeBook(String  bookId) {
        bookService.removeBook(bookId);
        return true;
    }


    public boolean blockMember(String memberId) {
      Member member = memberService.getMember(memberId);
      if(member != null) {
          member.setStatus(AccountStatus.BLOCKED);
          memberService.modifyMember(member);
          return true;
      } else {
          System.out.println("No member was found with this memberID");
          return  false;
      }
    }

    public Member getMember(String memberId) {
        return memberService.getMember(memberId);
    }

    @Override
    public boolean issueBook(Member member, BookItem book) {
        if(!isBookItemAvailable(book)) {
            System.out.println("Book can not be issued, it is not available! book="+ book);
            return false;
        }
        if(!isValidMember(member)) {
            System.out.println("Member is not eligible to issue a Book, member =" + member);
        }

        bookLendingService.lendBookItem(book, member.getMemberId());
        // update member and update book
        book.setStatus(BookStatus.Loaned);

        member.setTotalBooksCheckout(member.getTotalBooksCheckout()+1);
        Set<String> loanedItems = member.getBookItemLoaned();
        loanedItems.add(book.getBookId());
        member.setBookItemLoaned(loanedItems);


        bookService.modifyBook(book);
        memberService.modifyMember(member);
        return true;
    }

    @Override
    public boolean renewBook(String memberId, String bookItemId) {
       if(bookLendingService.renewBookItem(bookItemId, memberId) ==  false) {
           System.out.println("Could not renew the book");
           return  false;
       } else {
           System.out.println("BookItem renewed with details =" + bookItemId + " " + memberId);
           return true;
       }
    }

    @Override
    public boolean returnBook(Member member, BookItem book) {
       if(bookLendingService.returnBookItem(book.getBookId(), member.getMemberId())) {
           member.setTotalBooksCheckout(member.getTotalBooksCheckout()-1);
           book.setBorrowedDate(null);
           book.setStatus(BookStatus.Available);
           member.getBookItemLoaned().remove(book.getBookId());

           bookService.modifyBook(book);
           memberService.modifyMember(member);
           return true;
       } else {
           System.out.println("Can not return this book, encountered error while returning book");
           return false;
       }
    }

    private boolean isBookItemAvailable(BookItem bookItem) {
        return bookItem.getStatus() == BookStatus.Available && bookItem.isReferenceOnly() == false;
    }


    private boolean isValidMember(Member member) {
        return member.getStatus() == AccountStatus.ACTIVE && member.getTotalBooksCheckout() < Constants.MAX_BOOKS_ISSUED_TO_A_USER;
    }



}
