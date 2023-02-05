package com.ashish.ParkingLot.models;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Vehicle {
private VehicleType type;
private String vehicleNumber;
private ParkingTicket parkingTicket;
private ParkingSpotType spotType;

}
