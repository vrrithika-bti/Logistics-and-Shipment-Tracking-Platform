package com.logistics.tracking.dto;

import com.logistics.tracking.entity.ShipmentLocation;
import com.logistics.tracking.entity.TrackingEvent;

import java.time.LocalDateTime;
import java.util.UUID;

public class ShipmentTrackingSummaryResponse {

    private UUID shipmentId;
    private String currentStatus;
    private String currentLocation;
    private LocalDateTime latestEventTime;
    private int totalEvents;

    private Double latitude;
    private Double longitude;
    private String latestLocation;
    private LocalDateTime latestLocationTime;

    public static ShipmentTrackingSummaryResponse from(
            TrackingEvent latestEvent,
            int totalEvents,
            ShipmentLocation latestLocation
    ) {

        ShipmentTrackingSummaryResponse response =
                new ShipmentTrackingSummaryResponse();

        response.shipmentId = latestEvent.getShipmentId();
        response.currentStatus = latestEvent.getStatus().name();
        response.currentLocation = latestEvent.getLocation();
        response.latestEventTime = latestEvent.getEventTime();
        response.totalEvents = totalEvents;

        if (latestLocation != null) {
            response.latitude = latestLocation.getLatitude();
            response.longitude = latestLocation.getLongitude();
            response.latestLocation = latestLocation.getLocation();
            response.latestLocationTime = latestLocation.getTimestamp();
        }

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

    public Double getLatitude() {
        return latitude;
    }

    public Double getLongitude() {
        return longitude;
    }

    public String getLatestLocation() {
        return latestLocation;
    }

    public LocalDateTime getLatestLocationTime() {
        return latestLocationTime;
    }
}