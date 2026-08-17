package com.logistics.tracking.entity;

import jakarta.persistence.*;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "shipment_locations")
public class ShipmentLocation {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(name = "shipment_id", nullable = false)
    private UUID shipmentId;

    @Column(nullable = false)
    private Double latitude;

    @Column(nullable = false)
    private Double longitude;

    @Column(nullable = false)
    private String location;

    @Column(nullable = false)
    private LocalDateTime timestamp;

    public ShipmentLocation() {
    }

    public ShipmentLocation(
            UUID shipmentId,
            Double latitude,
            Double longitude,
            String location,
            LocalDateTime timestamp
    ) {
        this.shipmentId = shipmentId;
        this.latitude = latitude;
        this.longitude = longitude;
        this.location = location;
        this.timestamp = timestamp;
    }

    public UUID getId() {
        return id;
    }

    public UUID getShipmentId() {
        return shipmentId;
    }

    public Double getLatitude() {
        return latitude;
    }

    public Double getLongitude() {
        return longitude;
    }

    public String getLocation() {
        return location;
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }
}