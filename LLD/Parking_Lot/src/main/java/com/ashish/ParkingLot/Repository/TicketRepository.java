package com.ashish.ParkingLot.Repository;

import com.ashish.ParkingLot.models.ParkingTicket;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Component
public class TicketRepository {

    private Map<String, ParkingTicket> ticketMap;

    public ParkingTicket getParkingTicketByTicketNumber(String ticketNumber){
        return ticketMap.get(ticketNumber);
    }

    public void storeParkingTicket(ParkingTicket ticket) {
        ticketMap.put(ticket.getTicketNumber(),ticket);
    }

    public List<ParkingTicket> getParkingTicketByVehicleNumber(String vehicleNumber) {
        List<ParkingTicket> listOfTickets = new ArrayList<>();
       listOfTickets = ticketMap.values().stream().filter(ticket -> ticket.getVehicleNumber() == vehicleNumber).collect(Collectors.toList());
       return listOfTickets;
    }
}
