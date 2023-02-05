package com.ashish.ParkingLot.controller;

import com.ashish.ParkingLot.models.ParkingSpot.ParkingSpot;
import com.ashish.ParkingLot.models.ParkingSpotType;
import com.ashish.ParkingLot.models.Vehicle;
import com.ashish.ParkingLot.models.VehicleType;
import lombok.Getter;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

@Getter
@Setter
public class ParkingFloor {
  private int floorNumber;
  private Map<ParkingSpotType, Integer>  freeParkingSpots;
  private Map<ParkingSpotType, Integer> occupiedParkingSpots;
  private Map<ParkingSpotType, Map<String, ParkingSpot>> parkingSpots;
  private ParkingFloorDisplayBoard parkingFloorDisplayBoard;

  @Autowired
  private TicketProcessor ticketProcessor;

  public void addParkingSpot(ParkingSpot spot){
    parkingSpots.getOrDefault(spot.getSpotType(), new HashMap<>()).put(spot.getParkingSpotId(), spot);
  }

  public void assignVehicleToSpot(Vehicle vehicle, ParkingSpot spot) {
            if(!spot.isFree()) {
              System.out.println("Failed assigning vehiche = "+ vehicle + "to spot = " + spot + " Error: spot is not free");
              return;
            }
            // add a step to validate spot
           spot.assignVehicle(vehicle);
            freeParkingSpots.put(spot.getSpotType(), freeParkingSpots.get(spot.getSpotType())-1);
            occupiedParkingSpots.put(spot.getSpotType(), occupiedParkingSpots.get(spot.getSpotType())+1);
  }

  public void freeParkingSpot(ParkingSpot spot) {
    spot.removeVehicle();
    freeParkingSpots.put(spot.getSpotType(), freeParkingSpots.get(spot.getSpotType())+d1);
    occupiedParkingSpots.put(spot.getSpotType(), occupiedParkingSpots.get(spot.getSpotType())-1);
  }

  public boolean isFull(){
    for(ParkingSpotType type: freeParkingSpots.keySet()){
            if(freeParkingSpots.getOrDefault(type,0)>0)
              return false;
    }
    return true;
  }

  public boolean isSpotAvailable(VehicleType type) {
   return freeParkingSpots.getOrDefault(type,0)>0;
  }







}
