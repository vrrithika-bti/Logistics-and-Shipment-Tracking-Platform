package com.logistics.tracking.service;

import com.logistics.tracking.entity.TrackingEvent;
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
            String status,
            String location,
            LocalDateTime eventTime
    ) {

        TrackingEvent event = new TrackingEvent();

        event.setShipmentId(shipmentId);
        event.setStatus(status);
        event.setLocation(location);

        if (eventTime != null) {
            event.setEventTime(eventTime);
        }

        return trackingEventRepository.save(event);
    }

    @Transactional(readOnly = true)
    public List<TrackingEvent> getTrackingEvents(UUID shipmentId) {

        return trackingEventRepository
                .findByShipmentIdOrderByEventTimeAsc(shipmentId);
    }
}