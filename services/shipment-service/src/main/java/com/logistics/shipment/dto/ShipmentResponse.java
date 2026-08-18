package com.logistics.shipment.dto;

import java.time.LocalDateTime;
import java.util.UUID;

import com.logistics.shipment.entity.Shipment;
import com.logistics.shipment.model.ShipmentStatus;

public record ShipmentResponse(
        UUID id,
        String trackingNumber,
        UUID customerId,
        String origin,
        String destination,
        Double destinationLatitude,
        Double destinationLongitude,
        ShipmentStatus status,
        LocalDateTime estimatedDelivery,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {

    public static ShipmentResponse fromEntity(Shipment shipment) {
        return new ShipmentResponse(
                shipment.getId(),
                shipment.getTrackingNumber(),
                shipment.getCustomerId(),
                shipment.getOrigin(),
                shipment.getDestination(),
                shipment.getDestinationLatitude(),
                shipment.getDestinationLongitude(),
                shipment.getStatus(),
                shipment.getEstimatedDelivery(),
                shipment.getCreatedAt(),
                shipment.getUpdatedAt()
            
        );
    }
}