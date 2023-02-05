package com.ashish.ParkingLot.controller;

import com.ashish.ParkingLot.models.*;
import com.ashish.ParkingLot.models.ParkingSpot.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.lang.invoke.SwitchPoint;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.Period;

@Component
public class TicketProcessor {

    @Autowired
    private ParkingRate parkingRate;

    @Autowired
    private PaymentService paymentService;

    public ParkingTicket createTicket(Vehicle vehicle) {
        ParkingSpot spot;
    }

    public ParkingTicket processTicketForEntry(ParkingTicket ticket, Vehicle vehicle) {
              ticket.setEntryTime(LocalDateTime.now());
              ticket.setStatus(ParkingTicketStatus.ACTIVE);
              ticket.setVehicleNumber(vehicle.getVehicleNumber());
              vehicle.setParkingTicket(ticket);
              return ticket;
    }

    public ParkingTicket processTicketForExit(ParkingTicket ticket, Vehicle vehicle) {
        ticket.setExitTime(LocalDateTime.now());
        ticket.setFare(calculateFare(ticket));
        // call for payment process

    }

    private ParkingSpot getParkingSpot(VehicleType type) {
        switch (type) {
            case BUS: return new BusParkingSpot();
            case CAR: return new CarParkingSpot();
            case BIKE: return new BikeParkingSpot();
            case TRUCK: return  new TruckParkingSpot();
            case VAN: return new VanParkingSpot();
        }
        return null;
    }

    private double calculateFare(ParkingTicket ticket) {
        Duration duration = Duration.between(ticket.getEntryTime(), ticket.getExitTime());
        return (Double)((duration.toHours()*parkingRate.parkingRatePerHour) + (duration.toMinutes() - duration.toHours()*60)>0?1.0:0.0);
    }
}
