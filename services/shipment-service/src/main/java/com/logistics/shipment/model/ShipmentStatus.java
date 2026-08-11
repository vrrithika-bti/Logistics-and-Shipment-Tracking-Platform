package com.logistics.shipment.model;

import java.util.EnumSet;
import java.util.Map;
import java.util.Set;

public enum ShipmentStatus {

    CREATED,
    PICKED_UP,
    IN_TRANSIT,
    OUT_FOR_DELIVERY,
    DELIVERED,
    CANCELLED;

    private static final Map<ShipmentStatus, Set<ShipmentStatus>>
            ALLOWED_TRANSITIONS = Map.of(
                CREATED,
                    EnumSet.of(PICKED_UP, CANCELLED),

                PICKED_UP,
                    EnumSet.of(IN_TRANSIT, CANCELLED),

                IN_TRANSIT,
                    EnumSet.of(OUT_FOR_DELIVERY, CANCELLED),

                OUT_FOR_DELIVERY,
                    EnumSet.of(DELIVERED),

                DELIVERED,
                    EnumSet.noneOf(ShipmentStatus.class),

                CANCELLED,
                    EnumSet.noneOf(ShipmentStatus.class)
            );

    public boolean canTransitionTo(ShipmentStatus newStatus) {
        return ALLOWED_TRANSITIONS
                .getOrDefault(this, Set.of())
                .contains(newStatus);
    }
}