package com.logistics.tracking.controller;

import com.logistics.tracking.dto.CreateTrackingEventRequest;
import com.logistics.tracking.dto.TrackingEventResponse;
import com.logistics.tracking.entity.TrackingEvent;
import com.logistics.tracking.service.TrackingEventService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/v1/tracking-events")
public class TrackingEventController {

    private final TrackingEventService trackingEventService;

    public TrackingEventController(
            TrackingEventService trackingEventService
    ) {
        this.trackingEventService = trackingEventService;
    }

    @PostMapping
    public ResponseEntity<TrackingEventResponse> createTrackingEvent(
            @Valid @RequestBody CreateTrackingEventRequest request
    ) {

        TrackingEvent event = trackingEventService.createTrackingEvent(
                request.getShipmentId(),
                request.getStatus(),
                request.getLocation(),
                request.getEventTime()
        );

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(TrackingEventResponse.fromEntity(event));
    }

    @GetMapping("/shipment/{shipmentId}")
    public ResponseEntity<List<TrackingEventResponse>> getTrackingEvents(
            @PathVariable UUID shipmentId
    ) {

        List<TrackingEventResponse> response =
                trackingEventService.getTrackingEvents(shipmentId)
                        .stream()
                        .map(TrackingEventResponse::fromEntity)
                        .collect(Collectors.toList());

        return ResponseEntity.ok(response);
    }
}