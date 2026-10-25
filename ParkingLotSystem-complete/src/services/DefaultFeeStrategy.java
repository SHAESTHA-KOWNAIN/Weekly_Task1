package services;

import models.VehicleType;
import java.time.Duration;
import java.time.Instant;
import java.util.Map;

public class DefaultFeeStrategy implements FeeStrategy {
    private final Map<VehicleType, Long> hourlyRates;

    public DefaultFeeStrategy() {
        this(Map.of(VehicleType.BIKE, 20L, VehicleType.CAR, 50L, VehicleType.TRUCK, 100L));
    }

    public DefaultFeeStrategy(Map<VehicleType, Long> hourlyRates) {
        this.hourlyRates = Map.copyOf(hourlyRates);
        for (VehicleType type : VehicleType.values()) {
            Long rate = this.hourlyRates.get(type);
            if (rate == null || rate < 0) throw new IllegalArgumentException("Missing/invalid rate for " + type);
        }
    }

    @Override
    public long calculateFee(VehicleType type, Instant entryTime, Instant exitTime) {
        if (exitTime.isBefore(entryTime)) throw new IllegalArgumentException("Exit time cannot be before entry time.");
        long minutes = Duration.between(entryTime, exitTime).toMinutes();
        long hours = Math.max(1, (minutes + 59) / 60);
        return Math.multiplyExact(hours, hourlyRates.get(type));
    }
}
