package services;

import models.VehicleType;
import java.time.Instant;

public interface FeeStrategy {
    long calculateFee(VehicleType type, Instant entryTime, Instant exitTime);
}
