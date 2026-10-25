package services;

import models.ParkingLot;
import models.ParkingSlot;
import models.Vehicle;
import java.util.Optional;

public class FirstAvailableSlotStrategy implements SlotAllocationStrategy {
    @Override
    public Optional<ParkingSlot> findSlot(ParkingLot parkingLot, Vehicle vehicle) {
        return parkingLot.findAvailableSlot(vehicle);
    }
}
