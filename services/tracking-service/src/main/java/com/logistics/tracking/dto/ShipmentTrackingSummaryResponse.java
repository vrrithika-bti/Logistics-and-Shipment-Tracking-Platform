package com.logistics.tracking.dto;

import com.logistics.tracking.entity.TrackingEvent;

import java.time.LocalDateTime;
import java.util.UUID;

public class ShipmentTrackingSummaryResponse {

    private UUID shipmentId;
    private String currentStatus;
    private String currentLocation;
    private LocalDateTime latestEventTime;
    private int totalEvents;

    public static ShipmentTrackingSummaryResponse from(
            TrackingEvent latestEvent,
            int totalEvents
    ) {

        ShipmentTrackingSummaryResponse response =
                new ShipmentTrackingSummaryResponse();

        response.shipmentId = latestEvent.getShipmentId();
        response.currentStatus = latestEvent.getStatus().name();
        response.currentLocation = latestEvent.getLocation();
        response.latestEventTime = latestEvent.getEventTime();
        response.totalEvents = totalEvents;

        return response;
    }

    public UUID getShipmentId() {
        return shipmentId;
    }

    public String getCurrentStatus() {
        return currentStatus;
    }

    public String getCurrentLocation() {
        return currentLocation;
    }

    public LocalDateTime getLatestEventTime() {
        return latestEventTime;
    }

    public int getTotalEvents() {
        return totalEvents;
    }
}