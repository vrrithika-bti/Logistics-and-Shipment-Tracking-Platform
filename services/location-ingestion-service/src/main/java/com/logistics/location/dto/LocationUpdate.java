package com.logistics.location.dto;

import java.time.LocalDateTime;
import java.util.UUID;

import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record LocationUpdate(

        @NotNull(message = "Shipment ID is required")
        UUID shipmentId,

        @NotNull(message = "Latitude is required")
        @DecimalMin(value = "-90.0", message = "Latitude must be between -90 and 90")
        @DecimalMax(value = "90.0", message = "Latitude must be between -90 and 90")
        Double latitude,

        @NotNull(message = "Longitude is required")
        @DecimalMin(value = "-180.0", message = "Longitude must be between -180 and 180")
        @DecimalMax(value = "180.0", message = "Longitude must be between -180 and 180")
        Double longitude,

        @NotBlank(message = "Location is required")
        String location,

        @NotNull(message = "Timestamp is required")
        LocalDateTime timestamp,

        @NotNull(message = "Destination latitude is required")
        @DecimalMin(value = "-90.0", message = "Destination latitude must be between -90 and 90")
        @DecimalMax(value = "90.0", message = "Destination latitude must be between -90 and 90")
        Double destinationLatitude,

        @NotNull(message = "Destination longitude is required")
        @DecimalMin(value = "-180.0", message = "Destination longitude must be between -180 and 180")
        @DecimalMax(value = "180.0", message = "Destination longitude must be between -180 and 180")
        Double destinationLongitude

) {
}