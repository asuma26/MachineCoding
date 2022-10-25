package com.ashish.library.Models;

import java.time.LocalDate;

public class BookReservation {
    private LocalDate creationDate;
    private ReservationStatus status;
    private String bookItemBarcode;
    private String memberId;

    public static BookReservation fetchReservationDetails(String barcode) {
        return null;
    }

}
