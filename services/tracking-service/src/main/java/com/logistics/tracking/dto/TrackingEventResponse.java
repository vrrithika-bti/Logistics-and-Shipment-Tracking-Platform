package com.logistics.tracking.dto;

import com.logistics.tracking.entity.TrackingEvent;
import com.logistics.tracking.enums.TrackingStatus;

import java.time.LocalDateTime;
import java.util.UUID;

public class TrackingEventResponse {

    private UUID id;
    private UUID shipmentId;
    private TrackingStatus status;
    private String location;
    private LocalDateTime eventTime;
    private LocalDateTime createdAt;

    public static TrackingEventResponse fromEntity(TrackingEvent event) {

        TrackingEventResponse response = new TrackingEventResponse();

        response.id = event.getId();
        response.shipmentId = event.getShipmentId();
        response.status = event.getStatus();
        response.location = event.getLocation();
        response.eventTime = event.getEventTime();
        response.createdAt = event.getCreatedAt();

        return response;
    }

    public UUID getId() {
        return id;
    }

    public UUID getShipmentId() {
        return shipmentId;
    }

    public TrackingStatus getStatus() {
        return status;
    }

    public String getLocation() {
        return location;
    }

    public LocalDateTime getEventTime() {
        return eventTime;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }
}