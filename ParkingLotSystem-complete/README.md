# ParkingLotSystem — Multi-Floor Parking Lot LLD

## Project structure

```text
ParkingLotSystem/
├── .vscode/settings.json
├── bin/             
│   ├── models/
│   └── services/
├── lib/                        
├── src/
│   ├── models/
│   │   ├── Bike.java
│   │   ├── Car.java
│   │   ├── ParkingFloor.java
│   │   ├── ParkingLot.java
│   │   ├── ParkingSlot.java
│   │   ├── Ticket.java
│   │   ├── Truck.java
│   │   ├── Vehicle.java
│   │   └── VehicleType.java
│   ├── services/
│   │   ├── DefaultFeeStrategy.java
│   │   ├── FeeStrategy.java
│   │   ├── FirstAvailableSlotStrategy.java
│   │   ├── ParkingLotServices.java
│   │   └── SlotAllocationStrategy.java
│   └── Main.java
├── test/ParkingLotTests.java
```

## Design

- **Strategy pattern:** `SlotAllocationStrategy` allows slot selection algorithms to be swapped; `FirstAvailableSlotStrategy` is the default. `FeeStrategy` allows pricing algorithms to be swapped; `DefaultFeeStrategy` charges per started hour.
- **Factory principle:** `Bike`, `Car`, and `Truck` constructors centralize each subtype's type invariant. Callers can create the needed subtype directly. If a dedicated `VehicleFactory` is desired, add it as a separate class without changing the service.
- **Single responsibility:** models store and validate domain state; `ParkingLotServices` coordinates parking, tickets, fee calculation, and exit; `Main` is the console driver.
- **Enums:** `VehicleType` avoids fragile string comparisons. Ticket lifecycle is represented by `Ticket.Status`.
- **Validation:** normalized registration numbers detect duplicates regardless of case; unknown ticket IDs, full type-specific capacity, duplicate floors/slots, and repeated exit are rejected.
- **Extensibility:** add a pricing implementation by implementing `FeeStrategy`; add an allocation policy by implementing `SlotAllocationStrategy`. A new vehicle type requires a new `Vehicle` subtype and enum value, plus a matching slot type value (here represented by `VehicleType`).
