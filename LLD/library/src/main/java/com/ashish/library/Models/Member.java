package com.ashish.library.Models;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

@Getter
@Setter
public class Member {
    private String name;
    private LocalDate createDate;
    private int totalBooksCheckout;
    private String memberId;
    private AccountStatus status;
    private Set<String> bookItemLoaned = new HashSet<>();
}
