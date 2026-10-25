package services;

import models.ParkingLot;
import models.ParkingSlot;
import models.Vehicle;
import java.util.Optional;

public interface SlotAllocationStrategy {
    Optional<ParkingSlot> findSlot(ParkingLot parkingLot, Vehicle vehicle);
}
