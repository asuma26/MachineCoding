package com.ashish.ParkingLot.models;

import lombok.Getter;
import org.springframework.stereotype.Component;


@Getter
@Component
public class ParkingRate {
    public final double parkingRatePerHour = 10.0;
}
