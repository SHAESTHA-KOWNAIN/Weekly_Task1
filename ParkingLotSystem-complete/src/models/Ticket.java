package models;

import java.time.Instant;
import java.util.Objects;
import java.util.OptionalLong;

public class Ticket {
    public enum Status { ACTIVE, CLOSED }

    private final String ticketId;
    private final Vehicle vehicle;
    private final ParkingSlot parkingSlot;
    private final Instant entryTime;
    private Status status = Status.ACTIVE;
    private Long fee;

    public Ticket(String ticketId, Vehicle vehicle, ParkingSlot parkingSlot, Instant entryTime) {
        this.ticketId = Objects.requireNonNull(ticketId);
        this.vehicle = Objects.requireNonNull(vehicle);
        this.parkingSlot = Objects.requireNonNull(parkingSlot);
        this.entryTime = Objects.requireNonNull(entryTime);
    }

    public String getTicketId() { return ticketId; }
    public Vehicle getVehicle() { return vehicle; }
    public ParkingSlot getParkingSlot() { return parkingSlot; }
    public Instant getEntryTime() { return entryTime; }
    public Status getStatus() { return status; }
    public OptionalLong getFee() { return fee == null ? OptionalLong.empty() : OptionalLong.of(fee); }

    public void close(long finalFee) {
        if (status != Status.ACTIVE) throw new IllegalStateException("Ticket is already closed: " + ticketId);
        if (finalFee < 0) throw new IllegalArgumentException("Fee cannot be negative.");
        this.fee = finalFee;
        this.status = Status.CLOSED;
    }
}
