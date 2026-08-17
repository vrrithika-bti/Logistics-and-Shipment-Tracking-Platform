package com.logistics.tracking.consumer;

import com.logistics.tracking.dto.LocationUpdate;
import com.logistics.tracking.entity.ShipmentLocation;
import com.logistics.tracking.repository.ShipmentLocationRepository;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Service
public class LocationUpdateConsumer {

    private final ShipmentLocationRepository shipmentLocationRepository;

    public LocationUpdateConsumer(
            ShipmentLocationRepository shipmentLocationRepository
    ) {
        this.shipmentLocationRepository = shipmentLocationRepository;
    }

    @KafkaListener(
            topics = "location-updates",
            groupId = "tracking-service"
    )
    public void consume(LocationUpdate locationUpdate) {

        System.out.println(
                "Received location update: " + locationUpdate
        );

        ShipmentLocation shipmentLocation =
                new ShipmentLocation(
                        locationUpdate.shipmentId(),
                        locationUpdate.latitude(),
                        locationUpdate.longitude(),
                        locationUpdate.location(),
                        locationUpdate.timestamp()
                );

        shipmentLocationRepository.save(shipmentLocation);

        System.out.println(
                "Location saved successfully for shipment: "
                        + locationUpdate.shipmentId()
        );
    }
}