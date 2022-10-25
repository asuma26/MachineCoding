package com.ashish.library.Models;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
public class BookLending {
    private LocalDate creationDate;
    private LocalDate dueDate;
    private LocalDate returnDate;
    private String bookItemId;
    private String memberId;
    private int renewCount = 0;
}


