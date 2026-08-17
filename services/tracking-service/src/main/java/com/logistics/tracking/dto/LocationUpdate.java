package com.logistics.tracking.dto;

import java.time.LocalDateTime;
import java.util.UUID;

public record LocationUpdate(

        UUID shipmentId,

        Double latitude,

        Double longitude,

        String location,

        LocalDateTime timestamp

) {
}