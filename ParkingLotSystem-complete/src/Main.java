import models.*;
import services.*;

import java.util.*;

public class Main {
    public static void main(String[] args) {
        ParkingLot lot = new ParkingLot("City Centre Parking");

        ParkingFloor ground = new ParkingFloor(0);
        ground.addSlot(new ParkingSlot("G-B1", VehicleType.BIKE));
        ground.addSlot(new ParkingSlot("G-C1", VehicleType.CAR));
        ground.addSlot(new ParkingSlot("G-T1", VehicleType.TRUCK));

        ParkingFloor first = new ParkingFloor(1);
        first.addSlot(new ParkingSlot("F1-B1", VehicleType.BIKE));
        first.addSlot(new ParkingSlot("F1-C1", VehicleType.CAR));

        lot.addFloor(ground);
        lot.addFloor(first);

        ParkingLotServices service = new ParkingLotServices(
                lot, new FirstAvailableSlotStrategy(), new DefaultFeeStrategy());

        System.out.println("=== Multi-Floor Parking Lot ===");

        Ticket carTicket = service.parkVehicle(new Car("TS09AB1234")); // 1
        System.out.println("1. Car parked at " + carTicket.getParkingSlot().getSlotId()
                + ", ticket: " + carTicket.getTicketId());

        try { // 2
            service.parkVehicle(new Car("TS09AB1234"));
        } catch (IllegalStateException ex) {
            System.out.println("2. Duplicate parking rejected: " + ex.getMessage());
        }

        Ticket bikeTicket = service.parkVehicle(new Bike("TS10XY5678")); // 3
        System.out.println("3. Bike parked at " + bikeTicket.getParkingSlot().getSlotId());

        System.out.println("4. Free truck slots: " + service.viewFreeSlots(VehicleType.TRUCK).size());

        System.out.println("5. " + service.displayOccupancy());

        try { // 6
            service.unparkVehicle("T-INVALID");
        } catch (IllegalArgumentException ex) {
            System.out.println("6. Invalid ticket rejected: " + ex.getMessage());
        }

        long fee = service.unparkVehicle(carTicket.getTicketId()); // 7
        System.out.println("7. Car exited; fee: Rs." + fee
                + ", slot free: " + carTicket.getParkingSlot().isAvailable());

        try { // 8
            service.unparkVehicle(carTicket.getTicketId());
        } catch (IllegalStateException ex) {
            System.out.println("8. Repeated exit rejected: " + ex.getMessage());
        }

        Ticket truckTicket = service.parkVehicle(new Truck("TS11TR9999")); // 9
        System.out.println("9. Truck parked at " + truckTicket.getParkingSlot().getSlotId());

        System.out.println("10. " + service.displayOccupancy());
        System.out.println("11. Free car slots: " + service.viewFreeSlots(VehicleType.CAR).size());
    }
}
