package models;

import java.util.*;

public class ParkingLot {
    private final String name;
    private final Map<Integer, ParkingFloor> floors = new TreeMap<>();

    public ParkingLot(String name) {
        if (name == null || name.isBlank()) throw new IllegalArgumentException("Parking lot name is required.");
        this.name = name;
    }

    public String getName() { return name; }
    public Collection<ParkingFloor> getFloors() {
        return Collections.unmodifiableCollection(floors.values());
    }

    public void addFloor(ParkingFloor floor) {
        Objects.requireNonNull(floor, "Floor is required.");
        if (floors.containsKey(floor.getFloorNumber())) {
            throw new IllegalArgumentException("Floor already exists: " + floor.getFloorNumber());
        }
        Set<String> existingIds = new HashSet<>();
        for (ParkingFloor existing : floors.values()) {
            for (ParkingSlot slot : existing.getSlots()) existingIds.add(slot.getSlotId());
        }
        for (ParkingSlot slot : floor.getSlots()) {
            if (existingIds.contains(slot.getSlotId())) {
                throw new IllegalArgumentException("Slot IDs must be unique across the lot: " + slot.getSlotId());
            }
        }
        floors.put(floor.getFloorNumber(), floor);
    }

    public Optional<ParkingSlot> findAvailableSlot(Vehicle vehicle) {
        return floors.values().stream()
                .map(floor -> floor.findAvailableSlot(vehicle))
                .filter(Optional::isPresent)
                .map(Optional::get)
                .findFirst();
    }

    public List<ParkingSlot> getAvailableSlots(VehicleType type) {
        List<ParkingSlot> result = new ArrayList<>();
        for (ParkingFloor floor : floors.values()) {
            for (ParkingSlot slot : floor.getSlots()) {
                if (slot.isAvailable() && slot.getSupportedType() == type) result.add(slot);
            }
        }
        return Collections.unmodifiableList(result);
    }

    public long getTotalSlotCount() {
        return floors.values().stream().mapToLong(f -> f.getSlots().size()).sum();
    }

    public long getOccupiedSlotCount() {
        return floors.values().stream().mapToLong(ParkingFloor::getOccupiedCount).sum();
    }

    public String getOccupancyReport() {
        long total = getTotalSlotCount(), occupied = getOccupiedSlotCount();
        long percentage = total == 0 ? 0 : occupied * 100 / total;
        return "Occupancy: " + occupied + "/" + total + " slots (" + percentage + "%)";
    }
}
