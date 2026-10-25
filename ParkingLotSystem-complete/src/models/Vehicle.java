package models;

import java.util.Locale;
import java.util.Objects;

public abstract class Vehicle {
    private final String vehicleNumber;
    private final VehicleType vehicleType;

    protected Vehicle(String vehicleNumber, VehicleType vehicleType) {
        if (vehicleNumber == null || vehicleNumber.isBlank()) {
            throw new IllegalArgumentException("Vehicle number cannot be empty.");
        }
        this.vehicleNumber = vehicleNumber.trim().toUpperCase(Locale.ROOT);
        this.vehicleType = Objects.requireNonNull(vehicleType, "Vehicle type is required.");
    }

    public String getVehicleNumber() { return vehicleNumber; }
    public VehicleType getVehicleType() { return vehicleType; }

    @Override
    public String toString() {
        return vehicleType + " (" + vehicleNumber + ")";
    }
}
