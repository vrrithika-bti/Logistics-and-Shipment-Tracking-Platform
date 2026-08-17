package com.logistics.location.producer;

import com.logistics.location.dto.LocationUpdate;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
public class LocationUpdateProducer {

    private static final String TOPIC = "location-updates";

    private final KafkaTemplate<String, LocationUpdate> kafkaTemplate;

    public LocationUpdateProducer(
            KafkaTemplate<String, LocationUpdate> kafkaTemplate
    ) {
        this.kafkaTemplate = kafkaTemplate;
    }

    public void publish(LocationUpdate locationUpdate) {

        kafkaTemplate.send(
                TOPIC,
                locationUpdate.shipmentId().toString(),
                locationUpdate
        );
    }
}