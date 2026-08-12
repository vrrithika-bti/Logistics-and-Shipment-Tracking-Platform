package com.logistics.shipment.dto;

import com.logistics.shipment.entity.ShipmentStatusHistory;
import com.logistics.shipment.model.ShipmentStatus;

import java.time.LocalDateTime;
import java.util.UUID;

public record ShipmentStatusHistoryResponse(
        UUID id,
        UUID shipmentId,
        ShipmentStatus oldStatus,
        ShipmentStatus newStatus,
        LocalDateTime changedAt
) {

    public static ShipmentStatusHistoryResponse fromEntity(
            ShipmentStatusHistory history
    ) {
        return new ShipmentStatusHistoryResponse(
                history.getId(),
                history.getShipmentId(),
                history.getOldStatus(),
                history.getNewStatus(),
                history.getChangedAt()
        );
    }
}