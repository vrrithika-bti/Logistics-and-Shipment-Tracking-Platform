package com.logistics.location.controller;

import com.logistics.location.dto.LocationUpdate;
import com.logistics.location.producer.LocationUpdateProducer;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/locations")
@Tag(
        name = "Location Updates",
        description = "APIs for receiving shipment GPS location updates and publishing them to Kafka"
)
public class LocationUpdateController {

    private final LocationUpdateProducer locationUpdateProducer;

    public LocationUpdateController(
            LocationUpdateProducer locationUpdateProducer
    ) {
        this.locationUpdateProducer = locationUpdateProducer;
    }

    @Operation(
            summary = "Ingest a shipment location update",
            description = "Receives a shipment GPS location update and publishes it to the location-updates Kafka topic"
    )
    @PostMapping
    public ResponseEntity<LocationUpdate> ingestLocation(
            @Valid @RequestBody LocationUpdate locationUpdate
    ) {

        locationUpdateProducer.publish(locationUpdate);

        return ResponseEntity
                .status(HttpStatus.ACCEPTED)
                .body(locationUpdate);
    }
}
