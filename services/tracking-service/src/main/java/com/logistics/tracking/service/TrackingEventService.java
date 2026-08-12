package com.logistics.tracking.service;

import com.logistics.tracking.entity.TrackingEvent;
import com.logistics.tracking.enums.TrackingStatus;
import com.logistics.tracking.exception.InvalidStatusTransitionException;
import com.logistics.tracking.exception.ShipmentNotFoundException;
import com.logistics.tracking.repository.TrackingEventRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
public class TrackingEventService {

    private final TrackingEventRepository trackingEventRepository;

    public TrackingEventService(
            TrackingEventRepository trackingEventRepository
    ) {
        this.trackingEventRepository = trackingEventRepository;
    }

    @Transactional
    public TrackingEvent createTrackingEvent(
            UUID shipmentId,
            TrackingStatus status,
            String location,
            LocalDateTime eventTime
    ) {

        List<TrackingEvent> existingEvents =
                trackingEventRepository
                        .findByShipmentIdOrderByEventTimeAsc(shipmentId);

        if (!existingEvents.isEmpty()) {

            TrackingEvent latestEvent =
                    existingEvents.get(existingEvents.size() - 1);

            validateEventTime(
                    latestEvent.getEventTime(),
                    eventTime
            );

            validateStatusTransition(
                    latestEvent.getStatus(),
                    status
            );

        } else if (status != TrackingStatus.CREATED) {

            throw new InvalidStatusTransitionException(
                    "First tracking event must have status CREATED"
            );
        }

        TrackingEvent event = new TrackingEvent();

        event.setShipmentId(shipmentId);
        event.setStatus(status);
        event.setLocation(location);

        if (eventTime != null) {
            event.setEventTime(eventTime);
        }

        return trackingEventRepository.save(event);
    }

    private void validateEventTime(
            LocalDateTime latestEventTime,
            LocalDateTime newEventTime
    ) {

        if (newEventTime != null
                && latestEventTime != null
                && newEventTime.isBefore(latestEventTime)) {

            throw new IllegalArgumentException(
                    "Tracking event time cannot be earlier than the latest event"
            );
        }
    }

    private void validateStatusTransition(
            TrackingStatus currentStatus,
            TrackingStatus newStatus
    ) {

        if (currentStatus == TrackingStatus.DELIVERED) {
            throw new InvalidStatusTransitionException(
                    "Cannot change status after DELIVERED"
            );
        }

        if (newStatus == TrackingStatus.CANCELLED) {
            return;
        }

        boolean valid = switch (currentStatus) {
            case CREATED ->
                    newStatus == TrackingStatus.PICKED_UP;

            case PICKED_UP ->
                    newStatus == TrackingStatus.IN_TRANSIT;

            case IN_TRANSIT ->
                    newStatus == TrackingStatus.ARRIVED;

            case ARRIVED ->
                    newStatus == TrackingStatus.DELIVERED;

            case CANCELLED ->
                    false;

            case DELIVERED ->
                    false;
        };

        if (!valid) {
            throw new InvalidStatusTransitionException(
                    "Invalid status transition from "
                            + currentStatus
                            + " to "
                            + newStatus
            );
        }
    }

    @Transactional(readOnly = true)
    public List<TrackingEvent> getTrackingEvents(UUID shipmentId) {

        return trackingEventRepository
                .findByShipmentIdOrderByEventTimeAsc(shipmentId);
    }

    @Transactional(readOnly = true)
    public TrackingEvent getCurrentTrackingEvent(UUID shipmentId) {

        return trackingEventRepository
                .findTopByShipmentIdOrderByEventTimeDesc(shipmentId)
                .orElseThrow(() -> new ShipmentNotFoundException(
                        "Shipment not found: " + shipmentId
                ));
    }

    @Transactional(readOnly = true)
    public List<TrackingEvent> getTrackingEventsForShipment(UUID shipmentId) {

        List<TrackingEvent> events =
                trackingEventRepository
                        .findByShipmentIdOrderByEventTimeAsc(shipmentId);

        if (events.isEmpty()) {
            throw new ShipmentNotFoundException(
                    "Shipment not found: " + shipmentId
            );
        }

        return events;
    }
}