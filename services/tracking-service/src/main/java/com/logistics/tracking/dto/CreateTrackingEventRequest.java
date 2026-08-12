package com.logistics.tracking.dto;

import com.logistics.tracking.enums.TrackingStatus;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.time.LocalDateTime;
import java.util.UUID;

public class CreateTrackingEventRequest {

    @NotNull(message = "Shipment ID is required")
    private UUID shipmentId;

    @NotNull(message = "Status is required")
    private TrackingStatus status;

    @Size(max = 255, message = "Location must not exceed 255 characters")
    private String location;

    private LocalDateTime eventTime;

    public UUID getShipmentId() {
        return shipmentId;
    }

    public void setShipmentId(UUID shipmentId) {
        this.shipmentId = shipmentId;
    }

    public TrackingStatus getStatus() {
        return status;
    }

    public void setStatus(TrackingStatus status) {
        this.status = status;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public LocalDateTime getEventTime() {
        return eventTime;
    }

    public void setEventTime(LocalDateTime eventTime) {
        this.eventTime = eventTime;
    }
}