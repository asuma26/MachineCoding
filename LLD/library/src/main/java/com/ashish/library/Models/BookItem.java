package com.ashish.library.Models;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
public class BookItem extends Book{
  private boolean isReferenceOnly;
  private LocalDate borrowedDate;
  private double price;
  private BookStatus status;
  private BookShelf placedAt;
  private LocalDate dateOfPurchase;
}