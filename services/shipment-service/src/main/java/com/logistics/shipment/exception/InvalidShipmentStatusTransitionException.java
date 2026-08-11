package com.logistics.shipment.exception;

import com.logistics.shipment.model.ShipmentStatus;

public class InvalidShipmentStatusTransitionException
        extends RuntimeException {

    public InvalidShipmentStatusTransitionException(
            ShipmentStatus currentStatus,
            ShipmentStatus requestedStatus
    ) {
        super(
            "Cannot change shipment status from "
            + currentStatus
            + " to "
            + requestedStatus
        );
    }
}