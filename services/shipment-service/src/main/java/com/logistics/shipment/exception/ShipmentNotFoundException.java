package com.logistics.shipment.exception;

import java.util.UUID;

public class ShipmentNotFoundException extends RuntimeException {

    public ShipmentNotFoundException(UUID id) {
        super("Shipment not found with id: " + id);
    }

    public ShipmentNotFoundException(String trackingNumber) {
        super("Shipment not found with tracking number: " + trackingNumber);
    }
}