package com.logistics.tracking.controller;

import com.logistics.tracking.entity.TrackingEvent;
import com.logistics.tracking.enums.TrackingStatus;
import com.logistics.tracking.exception.ShipmentNotFoundException;
import com.logistics.tracking.service.TrackingEventService;
import com.logistics.tracking.exception.InvalidStatusTransitionException;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(TrackingEventController.class)
class TrackingEventControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private TrackingEventService trackingEventService;

    @Test
    void shouldCreateTrackingEvent() throws Exception {

        UUID shipmentId =
                UUID.fromString("11111111-1111-1111-1111-111111111111");

        TrackingEvent event = new TrackingEvent();

        event.setShipmentId(shipmentId);
        event.setStatus(TrackingStatus.CREATED);
        event.setLocation("Munich");
        event.setEventTime(
                LocalDateTime.parse("2026-08-12T10:30:00")
        );

        when(trackingEventService.createTrackingEvent(
                eq(shipmentId),
                eq(TrackingStatus.CREATED),
                eq("Munich"),
                any(LocalDateTime.class)
        )).thenReturn(event);

        String requestBody = """
                {
                  "shipmentId": "11111111-1111-1111-1111-111111111111",
                  "status": "CREATED",
                  "location": "Munich",
                  "eventTime": "2026-08-12T10:30:00"
                }
                """;

        mockMvc.perform(
                        post("/api/v1/tracking-events")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(requestBody)
                )
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.shipmentId")
                        .value(shipmentId.toString()))
                .andExpect(jsonPath("$.status")
                        .value("CREATED"))
                .andExpect(jsonPath("$.location")
                        .value("Munich"));
    }

    @Test
    void shouldGetTrackingEventsByShipmentId() throws Exception {

        UUID shipmentId =
                UUID.fromString("11111111-1111-1111-1111-111111111111");

        TrackingEvent event1 = new TrackingEvent();
        event1.setShipmentId(shipmentId);
        event1.setStatus(TrackingStatus.CREATED);
        event1.setLocation("Munich");
        event1.setEventTime(
                LocalDateTime.parse("2026-08-12T10:30:00")
        );

        TrackingEvent event2 = new TrackingEvent();
        event2.setShipmentId(shipmentId);
        event2.setStatus(TrackingStatus.PICKED_UP);
        event2.setLocation("Frankfurt");
        event2.setEventTime(
                LocalDateTime.parse("2026-08-12T12:30:00")
        );

        when(trackingEventService.getTrackingEvents(shipmentId))
                .thenReturn(List.of(event1, event2));

        mockMvc.perform(
                        get(
                                "/api/v1/tracking-events/shipment/{shipmentId}",
                                shipmentId
                        )
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].shipmentId")
                        .value(shipmentId.toString()))
                .andExpect(jsonPath("$[0].status")
                        .value("CREATED"))
                .andExpect(jsonPath("$[0].location")
                        .value("Munich"))
                .andExpect(jsonPath("$[1].status")
                        .value("PICKED_UP"))
                .andExpect(jsonPath("$[1].location")
                        .value("Frankfurt"));
    }

    @Test
    void shouldGetCurrentTrackingEvent() throws Exception {

        UUID shipmentId =
                UUID.fromString("55555555-5555-5555-5555-555555555555");

        TrackingEvent event = new TrackingEvent();

        event.setShipmentId(shipmentId);
        event.setStatus(TrackingStatus.DELIVERED);
        event.setLocation("Chennai");
        event.setEventTime(
                LocalDateTime.parse("2026-08-14T10:00:00")
        );

        when(trackingEventService.getCurrentTrackingEvent(shipmentId))
                .thenReturn(event);

        mockMvc.perform(
                        get(
                                "/api/v1/tracking-events/shipment/{shipmentId}/current",
                                shipmentId
                        )
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.shipmentId")
                        .value(shipmentId.toString()))
                .andExpect(jsonPath("$.status")
                        .value("DELIVERED"))
                .andExpect(jsonPath("$.location")
                        .value("Chennai"))
                .andExpect(jsonPath("$.eventTime")
                        .value("2026-08-14T10:00:00"));
    }

    @Test
    void shouldGetShipmentSummary() throws Exception {

        UUID shipmentId =
                UUID.fromString("55555555-5555-5555-5555-555555555555");

        TrackingEvent event1 = new TrackingEvent();
        event1.setShipmentId(shipmentId);
        event1.setStatus(TrackingStatus.CREATED);
        event1.setLocation("Munich");
        event1.setEventTime(
                LocalDateTime.parse("2026-08-12T17:00:00")
        );

        TrackingEvent event2 = new TrackingEvent();
        event2.setShipmentId(shipmentId);
        event2.setStatus(TrackingStatus.PICKED_UP);
        event2.setLocation("Munich");
        event2.setEventTime(
                LocalDateTime.parse("2026-08-12T18:00:00")
        );

        TrackingEvent event3 = new TrackingEvent();
        event3.setShipmentId(shipmentId);
        event3.setStatus(TrackingStatus.DELIVERED);
        event3.setLocation("Chennai");
        event3.setEventTime(
                LocalDateTime.parse("2026-08-14T10:00:00")
        );

        when(trackingEventService.getTrackingEventsForShipment(shipmentId))
                .thenReturn(List.of(event1, event2, event3));

        mockMvc.perform(
                        get(
                                "/api/v1/tracking-events/shipment/{shipmentId}/summary",
                                shipmentId
                        )
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.shipmentId")
                        .value(shipmentId.toString()))
                .andExpect(jsonPath("$.currentStatus")
                        .value("DELIVERED"))
                .andExpect(jsonPath("$.currentLocation")
                        .value("Chennai"))
                .andExpect(jsonPath("$.latestEventTime")
                        .value("2026-08-14T10:00:00"))
                .andExpect(jsonPath("$.totalEvents")
                        .value(3));
    }



    @Test
void shouldReturnNotFoundForUnknownCurrentTrackingEvent() throws Exception {

    UUID shipmentId =
            UUID.fromString("99999999-9999-9999-9999-999999999999");

    when(trackingEventService.getCurrentTrackingEvent(shipmentId))
            .thenThrow(new ShipmentNotFoundException(
                    "Shipment not found: " + shipmentId
            ));

    mockMvc.perform(
                    get(
                            "/api/v1/tracking-events/shipment/{shipmentId}/current",
                            shipmentId
                    )
            )
            .andExpect(status().isNotFound())
            .andExpect(jsonPath("$.status").value(404))
            .andExpect(jsonPath("$.message").value("Shipment not found"));
}


@Test
void shouldReturnBadRequestWhenStatusIsMissing() throws Exception {

    String requestBody = """
            {
              "shipmentId": "11111111-1111-1111-1111-111111111111",
              "location": "Munich",
              "eventTime": "2026-08-12T10:30:00"
            }
            """;

    mockMvc.perform(
                    post("/api/v1/tracking-events")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(requestBody)
            )
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.status").value(400))
            .andExpect(jsonPath("$.message").value("Validation failed"))
            .andExpect(jsonPath("$.errors.status")
                    .value("Status is required"));
}

@Test
void shouldReturnBadRequestForInvalidTrackingStatus() throws Exception {

    String requestBody = """
            {
              "shipmentId": "11111111-1111-1111-1111-111111111111",
              "status": "HELLO",
              "location": "Munich",
              "eventTime": "2026-08-12T10:30:00"
            }
            """;

    mockMvc.perform(
                    post("/api/v1/tracking-events")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(requestBody)
            )
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.status").value(400))
            .andExpect(jsonPath("$.message").value("Invalid request data"))
            .andExpect(jsonPath("$.error").value(
                    "Invalid tracking status. Allowed values: " +
                    "CREATED, PICKED_UP, IN_TRANSIT, ARRIVED, " +
                    "DELIVERED, CANCELLED"
            ));
}

@Test
void shouldReturnBadRequestForInvalidStatusTransition() throws Exception {

    UUID shipmentId =
            UUID.fromString("11111111-1111-1111-1111-111111111111");

    when(trackingEventService.createTrackingEvent(
            eq(shipmentId),
            eq(TrackingStatus.DELIVERED),
            eq("Munich"),
            any(LocalDateTime.class)
    )).thenThrow(new InvalidStatusTransitionException(
            "Invalid status transition from CREATED to DELIVERED"
    ));

    String requestBody = """
            {
              "shipmentId": "11111111-1111-1111-1111-111111111111",
              "status": "DELIVERED",
              "location": "Munich",
              "eventTime": "2026-08-12T12:30:00"
            }
            """;

    mockMvc.perform(
                    post("/api/v1/tracking-events")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(requestBody)
            )
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.status").value(400))
            .andExpect(jsonPath("$.message")
                    .value("Invalid status transition"))
            .andExpect(jsonPath("$.error")
                    .value("Invalid status transition from CREATED to DELIVERED"));
}


@Test
void shouldReturnNotFoundForUnknownShipmentSummary() throws Exception {

    UUID shipmentId =
            UUID.fromString("99999999-9999-9999-9999-999999999999");

    when(trackingEventService.getTrackingEventsForShipment(shipmentId))
            .thenThrow(new ShipmentNotFoundException(
                    "Shipment not found: " + shipmentId
            ));

    mockMvc.perform(
                    get(
                            "/api/v1/tracking-events/shipment/{shipmentId}/summary",
                            shipmentId
                    )
            )
            .andExpect(status().isNotFound())
            .andExpect(jsonPath("$.status").value(404))
            .andExpect(jsonPath("$.message").value("Shipment not found"));
}

@Test
void shouldReturnEmptyListWhenNoTrackingEventsExist() throws Exception {

    UUID shipmentId =
            UUID.fromString("88888888-8888-8888-8888-888888888888");

    when(trackingEventService.getTrackingEvents(shipmentId))
            .thenReturn(List.of());

    mockMvc.perform(
                    get(
                            "/api/v1/tracking-events/shipment/{shipmentId}",
                            shipmentId
                    )
            )
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.length()").value(0));
}


}