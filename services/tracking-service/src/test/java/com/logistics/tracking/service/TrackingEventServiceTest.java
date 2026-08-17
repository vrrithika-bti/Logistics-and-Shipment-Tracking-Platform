package com.logistics.tracking.service;

import com.logistics.tracking.entity.TrackingEvent;
import com.logistics.tracking.enums.TrackingStatus;
import com.logistics.tracking.exception.InvalidStatusTransitionException;
import com.logistics.tracking.exception.ShipmentNotFoundException;
import com.logistics.tracking.repository.ShipmentLocationRepository;
import com.logistics.tracking.repository.TrackingEventRepository;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class TrackingEventServiceTest {

    @Test
    void shouldCreateFirstEventWhenStatusIsCreated() {

        TrackingEventRepository repository =
                mock(TrackingEventRepository.class);

        ShipmentLocationRepository locationRepository =
                mock(ShipmentLocationRepository.class);

        TrackingEventService service =
                new TrackingEventService(
                        repository,
                        locationRepository
                );

        UUID shipmentId = UUID.randomUUID();

        when(repository.findByShipmentIdOrderByEventTimeAsc(shipmentId))
                .thenReturn(Collections.emptyList());

        when(repository.save(any(TrackingEvent.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        assertDoesNotThrow(() ->
                service.createTrackingEvent(
                        shipmentId,
                        TrackingStatus.CREATED,
                        "Munich",
                        LocalDateTime.now()
                )
        );

        verify(repository, times(1)).save(any(TrackingEvent.class));
    }

    @Test
    void shouldRejectFirstEventWhenStatusIsNotCreated() {

        TrackingEventRepository repository =
                mock(TrackingEventRepository.class);

        ShipmentLocationRepository locationRepository =
                mock(ShipmentLocationRepository.class);

        TrackingEventService service =
                new TrackingEventService(
                        repository,
                        locationRepository
                );

        UUID shipmentId = UUID.randomUUID();

        when(repository.findByShipmentIdOrderByEventTimeAsc(shipmentId))
                .thenReturn(Collections.emptyList());

        assertThrows(
                InvalidStatusTransitionException.class,
                () -> service.createTrackingEvent(
                        shipmentId,
                        TrackingStatus.IN_TRANSIT,
                        "Frankfurt",
                        LocalDateTime.now()
                )
        );

        verify(repository, never()).save(any());
    }

    @Test
    void shouldAllowCreatedToPickedUp() {

        TrackingEventRepository repository =
                mock(TrackingEventRepository.class);

        ShipmentLocationRepository locationRepository =
                mock(ShipmentLocationRepository.class);

        TrackingEventService service =
                new TrackingEventService(
                        repository,
                        locationRepository
                );

        UUID shipmentId = UUID.randomUUID();

        TrackingEvent existingEvent = new TrackingEvent();
        existingEvent.setShipmentId(shipmentId);
        existingEvent.setStatus(TrackingStatus.CREATED);
        existingEvent.setEventTime(LocalDateTime.now());

        when(repository.findByShipmentIdOrderByEventTimeAsc(shipmentId))
                .thenReturn(List.of(existingEvent));

        when(repository.save(any(TrackingEvent.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        assertDoesNotThrow(() ->
                service.createTrackingEvent(
                        shipmentId,
                        TrackingStatus.PICKED_UP,
                        "Munich",
                        LocalDateTime.now().plusHours(1)
                )
        );

        verify(repository, times(1)).save(any(TrackingEvent.class));
    }

    @Test
    void shouldRejectInvalidTransitionFromPickedUpToDelivered() {

        TrackingEventRepository repository =
                mock(TrackingEventRepository.class);

        ShipmentLocationRepository locationRepository =
                mock(ShipmentLocationRepository.class);

        TrackingEventService service =
                new TrackingEventService(
                        repository,
                        locationRepository
                );

        UUID shipmentId = UUID.randomUUID();

        TrackingEvent existingEvent = new TrackingEvent();
        existingEvent.setShipmentId(shipmentId);
        existingEvent.setStatus(TrackingStatus.PICKED_UP);
        existingEvent.setEventTime(LocalDateTime.now());

        when(repository.findByShipmentIdOrderByEventTimeAsc(shipmentId))
                .thenReturn(List.of(existingEvent));

        assertThrows(
                InvalidStatusTransitionException.class,
                () -> service.createTrackingEvent(
                        shipmentId,
                        TrackingStatus.DELIVERED,
                        "Chennai",
                        LocalDateTime.now().plusHours(1)
                )
        );

        verify(repository, never()).save(any());
    }

    @Test
    void shouldAllowCancellation() {

        TrackingEventRepository repository =
                mock(TrackingEventRepository.class);

        ShipmentLocationRepository locationRepository =
                mock(ShipmentLocationRepository.class);

        TrackingEventService service =
                new TrackingEventService(
                        repository,
                        locationRepository
                );

        UUID shipmentId = UUID.randomUUID();

        TrackingEvent existingEvent = new TrackingEvent();
        existingEvent.setShipmentId(shipmentId);
        existingEvent.setStatus(TrackingStatus.IN_TRANSIT);
        existingEvent.setEventTime(LocalDateTime.now());

        when(repository.findByShipmentIdOrderByEventTimeAsc(shipmentId))
                .thenReturn(List.of(existingEvent));

        when(repository.save(any(TrackingEvent.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        assertDoesNotThrow(() ->
                service.createTrackingEvent(
                        shipmentId,
                        TrackingStatus.CANCELLED,
                        "Frankfurt",
                        LocalDateTime.now().plusHours(1)
                )
        );

        verify(repository, times(1)).save(any(TrackingEvent.class));
    }

    @Test
    void shouldRejectStatusChangeAfterDelivered() {

        TrackingEventRepository repository =
                mock(TrackingEventRepository.class);

        ShipmentLocationRepository locationRepository =
                mock(ShipmentLocationRepository.class);

        TrackingEventService service =
                new TrackingEventService(
                        repository,
                        locationRepository
                );

        UUID shipmentId = UUID.randomUUID();

        TrackingEvent existingEvent = new TrackingEvent();
        existingEvent.setShipmentId(shipmentId);
        existingEvent.setStatus(TrackingStatus.DELIVERED);
        existingEvent.setEventTime(LocalDateTime.now());

        when(repository.findByShipmentIdOrderByEventTimeAsc(shipmentId))
                .thenReturn(List.of(existingEvent));

        assertThrows(
                InvalidStatusTransitionException.class,
                () -> service.createTrackingEvent(
                        shipmentId,
                        TrackingStatus.IN_TRANSIT,
                        "Chennai",
                        LocalDateTime.now().plusHours(1)
                )
        );

        verify(repository, never()).save(any());
    }

    @Test
    void shouldAllowPickedUpToInTransit() {

        TrackingEventRepository repository =
                mock(TrackingEventRepository.class);

        ShipmentLocationRepository locationRepository =
                mock(ShipmentLocationRepository.class);

        TrackingEventService service =
                new TrackingEventService(
                        repository,
                        locationRepository
                );

        UUID shipmentId = UUID.randomUUID();

        TrackingEvent existingEvent = new TrackingEvent();
        existingEvent.setShipmentId(shipmentId);
        existingEvent.setStatus(TrackingStatus.PICKED_UP);
        existingEvent.setEventTime(LocalDateTime.now());

        when(repository.findByShipmentIdOrderByEventTimeAsc(shipmentId))
                .thenReturn(List.of(existingEvent));

        when(repository.save(any(TrackingEvent.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        assertDoesNotThrow(() ->
                service.createTrackingEvent(
                        shipmentId,
                        TrackingStatus.IN_TRANSIT,
                        "Frankfurt",
                        LocalDateTime.now().plusHours(1)
                )
        );

        verify(repository, times(1)).save(any(TrackingEvent.class));
    }

    @Test
    void shouldAllowInTransitToArrived() {

        TrackingEventRepository repository =
                mock(TrackingEventRepository.class);

        ShipmentLocationRepository locationRepository =
                mock(ShipmentLocationRepository.class);

        TrackingEventService service =
                new TrackingEventService(
                        repository,
                        locationRepository
                );

        UUID shipmentId = UUID.randomUUID();

        TrackingEvent existingEvent = new TrackingEvent();
        existingEvent.setShipmentId(shipmentId);
        existingEvent.setStatus(TrackingStatus.IN_TRANSIT);
        existingEvent.setEventTime(LocalDateTime.now());

        when(repository.findByShipmentIdOrderByEventTimeAsc(shipmentId))
                .thenReturn(List.of(existingEvent));

        when(repository.save(any(TrackingEvent.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        assertDoesNotThrow(() ->
                service.createTrackingEvent(
                        shipmentId,
                        TrackingStatus.ARRIVED,
                        "Dubai",
                        LocalDateTime.now().plusHours(1)
                )
        );

        verify(repository, times(1)).save(any(TrackingEvent.class));
    }

    @Test
    void shouldAllowArrivedToDelivered() {

        TrackingEventRepository repository =
                mock(TrackingEventRepository.class);

        ShipmentLocationRepository locationRepository =
                mock(ShipmentLocationRepository.class);

        TrackingEventService service =
                new TrackingEventService(
                        repository,
                        locationRepository
                );

        UUID shipmentId = UUID.randomUUID();

        TrackingEvent existingEvent = new TrackingEvent();
        existingEvent.setShipmentId(shipmentId);
        existingEvent.setStatus(TrackingStatus.ARRIVED);
        existingEvent.setEventTime(LocalDateTime.now());

        when(repository.findByShipmentIdOrderByEventTimeAsc(shipmentId))
                .thenReturn(List.of(existingEvent));

        when(repository.save(any(TrackingEvent.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        assertDoesNotThrow(() ->
                service.createTrackingEvent(
                        shipmentId,
                        TrackingStatus.DELIVERED,
                        "Chennai",
                        LocalDateTime.now().plusHours(1)
                )
        );

        verify(repository, times(1)).save(any(TrackingEvent.class));
    }

    @Test
    void shouldReturnCurrentTrackingEvent() {

        TrackingEventRepository repository =
                mock(TrackingEventRepository.class);

        ShipmentLocationRepository locationRepository =
                mock(ShipmentLocationRepository.class);

        TrackingEventService service =
                new TrackingEventService(
                        repository,
                        locationRepository
                );

        UUID shipmentId = UUID.randomUUID();

        TrackingEvent event = new TrackingEvent();
        event.setShipmentId(shipmentId);
        event.setStatus(TrackingStatus.IN_TRANSIT);
        event.setLocation("Frankfurt");
        event.setEventTime(LocalDateTime.now());

        when(repository.findTopByShipmentIdOrderByEventTimeDesc(shipmentId))
                .thenReturn(java.util.Optional.of(event));

        TrackingEvent result =
                service.getCurrentTrackingEvent(shipmentId);

        org.junit.jupiter.api.Assertions.assertEquals(
                TrackingStatus.IN_TRANSIT,
                result.getStatus()
        );

        verify(repository, times(1))
                .findTopByShipmentIdOrderByEventTimeDesc(shipmentId);
    }

    @Test
    void shouldThrowExceptionWhenCurrentTrackingEventNotFound() {

        TrackingEventRepository repository =
                mock(TrackingEventRepository.class);

        ShipmentLocationRepository locationRepository =
                mock(ShipmentLocationRepository.class);

        TrackingEventService service =
                new TrackingEventService(
                        repository,
                        locationRepository
                );

        UUID shipmentId = UUID.randomUUID();

        when(repository.findTopByShipmentIdOrderByEventTimeDesc(shipmentId))
                .thenReturn(java.util.Optional.empty());

        assertThrows(
                ShipmentNotFoundException.class,
                () -> service.getCurrentTrackingEvent(shipmentId)
        );
    }

    @Test
    void shouldReturnTrackingEventsForShipment() {

        TrackingEventRepository repository =
                mock(TrackingEventRepository.class);

        ShipmentLocationRepository locationRepository =
                mock(ShipmentLocationRepository.class);

        TrackingEventService service =
                new TrackingEventService(
                        repository,
                        locationRepository
                );

        UUID shipmentId = UUID.randomUUID();

        TrackingEvent event1 = new TrackingEvent();
        event1.setShipmentId(shipmentId);
        event1.setStatus(TrackingStatus.CREATED);

        TrackingEvent event2 = new TrackingEvent();
        event2.setShipmentId(shipmentId);
        event2.setStatus(TrackingStatus.PICKED_UP);

        when(repository.findByShipmentIdOrderByEventTimeAsc(shipmentId))
                .thenReturn(List.of(event1, event2));

        List<TrackingEvent> result =
                service.getTrackingEventsForShipment(shipmentId);

        org.junit.jupiter.api.Assertions.assertEquals(
                2,
                result.size()
        );

        verify(repository, times(1))
                .findByShipmentIdOrderByEventTimeAsc(shipmentId);
    }

    @Test
    void shouldThrowExceptionWhenTrackingEventsNotFound() {

        TrackingEventRepository repository =
                mock(TrackingEventRepository.class);

        ShipmentLocationRepository locationRepository =
                mock(ShipmentLocationRepository.class);

        TrackingEventService service =
                new TrackingEventService(
                        repository,
                        locationRepository
                );

        UUID shipmentId = UUID.randomUUID();

        when(repository.findByShipmentIdOrderByEventTimeAsc(shipmentId))
                .thenReturn(Collections.emptyList());

        assertThrows(
                ShipmentNotFoundException.class,
                () -> service.getTrackingEventsForShipment(shipmentId)
        );
    }
}