import models.*;
import services.*;
import java.time.Instant;
import java.util.Map;


public class ParkingLotTests {
    private static int passed = 0;

    private static ParkingLotServices newService() {
        ParkingLot lot = new ParkingLot("Test Lot");
        ParkingFloor floor = new ParkingFloor(0);
        floor.addSlot(new ParkingSlot("B1", VehicleType.BIKE));
        floor.addSlot(new ParkingSlot("C1", VehicleType.CAR));
        floor.addSlot(new ParkingSlot("T1", VehicleType.TRUCK));
        lot.addFloor(floor);
        return new ParkingLotServices(lot, new FirstAvailableSlotStrategy(),
                new DefaultFeeStrategy(Map.of(VehicleType.BIKE, 10L, VehicleType.CAR, 30L, VehicleType.TRUCK, 80L)));
    }

    private static void check(String name, Runnable test) {
        try {
            test.run();
            passed++;
            System.out.println("PASS: " + name);
        } catch (Throwable t) {
            System.out.println("FAIL: " + name + " -> " + t);
            throw t;
        }
    }

    private static void expectThrows(Class<? extends Throwable> type, Runnable action) {
        try { action.run(); }
        catch (Throwable ex) {
            if (type.isInstance(ex)) return;
            throw new AssertionError("Expected " + type.getSimpleName() + " but got " + ex, ex);
        }
        throw new AssertionError("Expected exception " + type.getSimpleName());
    }

    public static void main(String[] args) {
        check("park car and create ticket", () -> {
            Ticket t = newService().parkVehicle(new Car("car-1"));
            assert t.getTicketId() != null;
            assert t.getParkingSlot().getSlotId().equals("C1");
        });
        check("reject duplicate active vehicle case-insensitively", () -> {
            ParkingLotServices s = newService();
            s.parkVehicle(new Car("car-1"));
            expectThrows(IllegalStateException.class, () -> s.parkVehicle(new Car("CAR-1")));
        });
        check("reject full vehicle-type capacity", () -> {
            ParkingLotServices s = newService();
            s.parkVehicle(new Bike("b1"));
            expectThrows(IllegalStateException.class, () -> s.parkVehicle(new Bike("b2")));
        });
        check("reject invalid ticket", () ->
                expectThrows(IllegalArgumentException.class, () -> newService().unparkVehicle("bad-ticket")));
        check("exit releases slot and closes ticket", () -> {
            ParkingLotServices s = newService();
            Ticket t = s.parkVehicle(new Car("c1"));
            s.unparkVehicle(t.getTicketId());
            assert t.getParkingSlot().isAvailable();
            assert t.getStatus() == Ticket.Status.CLOSED;
        });
        check("reject repeated exit", () -> {
            ParkingLotServices s = newService();
            Ticket t = s.parkVehicle(new Car("c1"));
            s.unparkVehicle(t.getTicketId());
            expectThrows(IllegalStateException.class, () -> s.unparkVehicle(t.getTicketId()));
        });
        check("filter free slots by type", () -> {
            ParkingLotServices s = newService();
            assert s.viewFreeSlots(VehicleType.TRUCK).size() == 1;
            s.parkVehicle(new Truck("t1"));
            assert s.viewFreeSlots(VehicleType.TRUCK).isEmpty();
        });
        check("occupancy report", () -> {
            ParkingLotServices s = newService();
            s.parkVehicle(new Bike("b1"));
            assert s.displayOccupancy().equals("Occupancy: 1/3 slots (33%)");
        });
        check("fee rounds partial hour up", () -> {
            FeeStrategy fee = new DefaultFeeStrategy(Map.of(VehicleType.BIKE, 10L, VehicleType.CAR, 30L, VehicleType.TRUCK, 80L));
            assert fee.calculateFee(VehicleType.CAR, Instant.parse("2026-01-01T10:00:00Z"),
                    Instant.parse("2026-01-01T11:01:00Z")) == 60;
        });
        check("reject duplicate floor", () -> {
            ParkingLot lot = new ParkingLot("Lot");
            lot.addFloor(new ParkingFloor(0));
            expectThrows(IllegalArgumentException.class, () -> lot.addFloor(new ParkingFloor(0)));
        });
        check("factory vehicle subtype constructors", () -> {
            assert new Bike("b").getVehicleType() == VehicleType.BIKE;
            assert new Car("c").getVehicleType() == VehicleType.CAR;
            assert new Truck("t").getVehicleType() == VehicleType.TRUCK;
        });
        System.out.println("Tests passed: " + passed + "/11");
    }
}
