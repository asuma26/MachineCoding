package com.ashish.library.Interfaces;

import com.ashish.library.Models.BookItem;
import com.ashish.library.Models.Member;

public abstract class BookManagerAPI {
    public abstract boolean issueBook(Member member, BookItem book);
    public abstract boolean renewBook(String memberId, String bookItemId);
    public abstract boolean returnBook(Member member, BookItem book);
}
