package com.ashish.ParkingLot.models.ParkingSpot;


import com.ashish.ParkingLot.models.ParkingSpotStatus;
import com.ashish.ParkingLot.models.ParkingSpotType;
import com.ashish.ParkingLot.models.Vehicle;
import lombok.Getter;
import lombok.Setter;


@Getter
@Setter
public abstract class ParkingSpot {
    private final ParkingSpotType spotType;
    private int floorNumber;
    private String parkingSpotId;
    private ParkingSpotStatus status;
    private Vehicle vehicle;
    private boolean free = false;

    protected ParkingSpot(ParkingSpotType spotType) {
        this.spotType = spotType;
    }

    public boolean isFree() {
        return free;
    }

    public void assignVehicle(Vehicle vehicle) {
        this.vehicle = vehicle;
        this.status = ParkingSpotStatus.OCCUPIED;
        free = false;
    }

    public void removeVehicle() {
        this.vehicle = null;
        free = true;
        this.status = ParkingSpotStatus.AVAILABLE;
    }


}
