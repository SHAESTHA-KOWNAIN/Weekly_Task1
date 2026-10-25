package models;

import java.util.*;

public class ParkingFloor {
    private final int floorNumber;
    private final Map<String, ParkingSlot> slots = new LinkedHashMap<>();

    public ParkingFloor(int floorNumber) {
        if (floorNumber < 0) throw new IllegalArgumentException("Floor number cannot be negative.");
        this.floorNumber = floorNumber;
    }

    public int getFloorNumber() { return floorNumber; }
    public Collection<ParkingSlot> getSlots() {
        return Collections.unmodifiableCollection(slots.values());
    }

    public void addSlot(ParkingSlot slot) {
        Objects.requireNonNull(slot, "Slot is required.");
        if (slots.putIfAbsent(slot.getSlotId(), slot) != null) {
            throw new IllegalArgumentException("Duplicate slot ID on floor: " + slot.getSlotId());
        }
    }

    public Optional<ParkingSlot> findAvailableSlot(Vehicle vehicle) {
        return slots.values().stream().filter(slot -> slot.canFit(vehicle)).findFirst();
    }

    public long getAvailableCount(VehicleType type) {
        return slots.values().stream()
                .filter(ParkingSlot::isAvailable)
                .filter(slot -> slot.getSupportedType() == type).count();
    }

    public long getOccupiedCount() {
        return slots.values().stream().filter(slot -> !slot.isAvailable()).count();
    }
}
