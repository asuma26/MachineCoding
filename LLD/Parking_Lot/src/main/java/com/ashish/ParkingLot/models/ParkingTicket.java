package com.ashish.ParkingLot.models;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@AllArgsConstructor
@Getter
@Setter
public class ParkingTicket {
  private LocalDateTime entryTime;
  private String ticketNumber;
  private LocalDateTime exitTime;
  private double fare;
  private ParkingTicketStatus status;
  private String vehicleNumber;
  private String parkingSpotNumber;
  private String UserId;
}
