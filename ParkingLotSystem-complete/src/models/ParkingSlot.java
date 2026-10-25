package models;

import java.util.Objects;
import java.util.Optional;

public class ParkingSlot {
    private final String slotId;
    private final VehicleType supportedType;
    private Vehicle parkedVehicle;

    public ParkingSlot(String slotId, VehicleType supportedType) {
        if (slotId == null || slotId.isBlank()) {
            throw new IllegalArgumentException("Slot ID cannot be empty.");
        }
        this.slotId = slotId.trim();
        this.supportedType = Objects.requireNonNull(supportedType);
    }

    public String getSlotId() { return slotId; }
    public VehicleType getSupportedType() { return supportedType; }
    public boolean isAvailable() { return parkedVehicle == null; }
    public Optional<Vehicle> getParkedVehicle() { return Optional.ofNullable(parkedVehicle); }

    public boolean canFit(Vehicle vehicle) {
        return isAvailable() && vehicle != null && supportedType == vehicle.getVehicleType();
    }

    public void parkVehicle(Vehicle vehicle) {
        if (!canFit(vehicle)) {
            throw new IllegalStateException("Slot " + slotId + " is occupied or does not fit " +
                    (vehicle == null ? "the vehicle" : vehicle.getVehicleType()) + ".");
        }
        parkedVehicle = vehicle;
    }

    public void removeVehicle() {
        if (isAvailable()) {
            throw new IllegalStateException("Slot " + slotId + " is already empty.");
        }
        parkedVehicle = null;
    }

    @Override
    public String toString() {
        return slotId + "[" + supportedType + ", " + (isAvailable() ? "FREE" : "OCCUPIED") + "]";
    }
}
