package com.ashish.ParkingLot.controller;

import com.ashish.ParkingLot.models.ParkingSpot.ParkingSpot;
import com.ashish.ParkingLot.models.ParkingTicket;
import com.ashish.ParkingLot.models.Vehicle;
import com.ashish.ParkingLot.models.VehicleType;

import java.util.HashMap;
import java.util.Map;

public class ParkingLot {

    private Map<Integer, ParkingFloor> parkingFloors = new HashMap();


    private ParkingLot(){}
    private static ParkingLot parkingLot;

    public static ParkingLot getParkingLot() {
        if(parkingLot == null){
            parkingLot = new ParkingLot();
        }
        return parkingLot;
    }

    public void addParkingFloor(ParkingFloor floor) {
        parkingFloors.put(floor.getFloorNumber(), floor);
    }

    public void removeParkingFloor(int floorNumber) {
        parkingFloors.remove(floorNumber);
    }

    public boolean isFull() {
        for(int key: parkingFloors.keySet()){
           if(!parkingFloors.get(key).isFull())
               return false;
        }
        return true;
    }

    public boolean isSpotAvailable(VehicleType type) {
        for(int key: parkingFloors.keySet()){
            if(parkingFloors.get(key).isSpotAvailable(type))
                return true;
        }
        return false;
    }

    public synchronized ParkingTicket createParkingTicket(Vehicle vehicle) {
        if(isFull()){
            System.out.println("PARKING IS FULL NO SPOT AVAILABLE");
        }

        if(!isSpotAvailable(vehicle.getType())) {
            System.out.println("No spot available for this vehicle type");
        }

       ParkingFloor floor = getFloorForFreeParkingSpot(vehicle.getType());





    }

    private ParkingFloor getFloorForFreeParkingSpot(VehicleType type) {
        for(ParkingFloor floor  : parkingFloors.values()) {
            if(floor.getFreeParkingSpots().get(type)>0){
                return floor;
            }
        }
        return null;
    }


}
