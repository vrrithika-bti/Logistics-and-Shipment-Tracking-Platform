package com.logistics.tracking.controller;

import com.logistics.tracking.dto.CreateTrackingEventRequest;
import com.logistics.tracking.dto.ShipmentTrackingSummaryResponse;
import com.logistics.tracking.dto.TrackingEventResponse;
import com.logistics.tracking.entity.ShipmentLocation;
import com.logistics.tracking.entity.TrackingEvent;
import com.logistics.tracking.service.TrackingEventService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/v1/tracking-events")
@Tag(
        name = "Tracking Events",
        description = "APIs for managing shipment tracking events and locations"
)       
public class TrackingEventController {

    private final TrackingEventService trackingEventService;

    public TrackingEventController(
            TrackingEventService trackingEventService
    ) {
        this.trackingEventService = trackingEventService;
    }


    @Operation(
        summary = "Create a tracking event",
        description = "Creates a new tracking event and validates the shipment status transition"
    )
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


    @Operation(
        summary = "Get shipment tracking history",
        description = "Returns all tracking events for a shipment ordered by event time"
)
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

    @Operation(
        summary = "Get current tracking event",
        description = "Returns the latest tracking event for a shipment"
)
    @GetMapping("/shipment/{shipmentId}/current")
    public ResponseEntity<TrackingEventResponse> getCurrentTrackingEvent(
            @PathVariable UUID shipmentId
    ) {

        TrackingEvent event =
                trackingEventService.getCurrentTrackingEvent(shipmentId);

        return ResponseEntity.ok(
                TrackingEventResponse.fromEntity(event)
        );
    }

    @Operation(
        summary = "Get shipment tracking summary",
        description = "Returns the current tracking status together with the latest location information"
)
    @GetMapping("/shipment/{shipmentId}/summary")
    public ResponseEntity<ShipmentTrackingSummaryResponse> getShipmentSummary(
            @PathVariable UUID shipmentId
    ) {

        List<TrackingEvent> events =
                trackingEventService.getTrackingEventsForShipment(shipmentId);

        TrackingEvent latestEvent =
                events.get(events.size() - 1);

        ShipmentLocation latestLocation =
                trackingEventService.getLatestShipmentLocation(shipmentId);

        ShipmentTrackingSummaryResponse response =
                ShipmentTrackingSummaryResponse.from(
                        latestEvent,
                        events.size(),
                        latestLocation
                );

        return ResponseEntity.ok(response);
    }

    @Operation(
        summary = "Get shipment location history",
        description = "Returns all recorded GPS locations for a shipment ordered by timestamp"
)
    @GetMapping("/shipment/{shipmentId}/locations")
    public ResponseEntity<List<ShipmentLocation>> getShipmentLocations(
            @PathVariable UUID shipmentId
    ) {

        List<ShipmentLocation> locations =
                trackingEventService.getShipmentLocations(shipmentId);

        return ResponseEntity.ok(locations);
    }
}

