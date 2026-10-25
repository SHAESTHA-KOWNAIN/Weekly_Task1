package services;

import models.*;
import java.time.Instant;
import java.util.*;
import java.util.concurrent.atomic.AtomicLong;

public class ParkingLotServices {
    private final ParkingLot parkingLot;
    private final SlotAllocationStrategy allocationStrategy;
    private final FeeStrategy feeStrategy;
    private final Map<String, Ticket> tickets = new LinkedHashMap<>();
    private final Map<String, String> activeTicketByVehicle = new HashMap<>();
    private final AtomicLong ticketSequence = new AtomicLong(1000);

    public ParkingLotServices(ParkingLot parkingLot,
                              SlotAllocationStrategy allocationStrategy,
                              FeeStrategy feeStrategy) {
        this.parkingLot = Objects.requireNonNull(parkingLot);
        this.allocationStrategy = Objects.requireNonNull(allocationStrategy);
        this.feeStrategy = Objects.requireNonNull(feeStrategy);
    }

    public synchronized Ticket parkVehicle(Vehicle vehicle) {
        Objects.requireNonNull(vehicle, "Vehicle is required.");
        String registration = vehicle.getVehicleNumber();
        if (activeTicketByVehicle.containsKey(registration)) {
            throw new IllegalStateException("Vehicle is already parked: " + registration);
        }
        ParkingSlot slot = allocationStrategy.findSlot(parkingLot, vehicle)
                .orElseThrow(() -> new IllegalStateException("No available slot for " + vehicle.getVehicleType()));
        slot.parkVehicle(vehicle);
        Ticket ticket = new Ticket("T-" + ticketSequence.incrementAndGet(), vehicle, slot, Instant.now());
        tickets.put(ticket.getTicketId(), ticket);
        activeTicketByVehicle.put(registration, ticket.getTicketId());
        return ticket;
    }

    public synchronized long unparkVehicle(String ticketId) {
        Ticket ticket = tickets.get(ticketId);
        if (ticket == null) throw new IllegalArgumentException("Invalid ticket: " + ticketId);
        if (ticket.getStatus() != Ticket.Status.ACTIVE) {
            throw new IllegalStateException("Ticket has already been used: " + ticketId);
        }
        long fee = feeStrategy.calculateFee(ticket.getVehicle().getVehicleType(),
                ticket.getEntryTime(), Instant.now());
        ticket.getParkingSlot().removeVehicle();
        activeTicketByVehicle.remove(ticket.getVehicle().getVehicleNumber());
        ticket.close(fee);
        return fee;
    }

    public List<ParkingSlot> viewFreeSlots(VehicleType type) {
        return parkingLot.getAvailableSlots(type);
    }

    public String displayOccupancy() {
        return parkingLot.getOccupancyReport();
    }

    public Optional<Ticket> findTicket(String ticketId) {
        return Optional.ofNullable(tickets.get(ticketId));
    }

    public ParkingLot getParkingLot() { return parkingLot; }
}
