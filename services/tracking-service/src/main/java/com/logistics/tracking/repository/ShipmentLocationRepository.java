package com.logistics.tracking.repository;

import com.logistics.tracking.entity.ShipmentLocation;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface ShipmentLocationRepository
        extends JpaRepository<ShipmentLocation, UUID> {

    List<ShipmentLocation> findByShipmentIdOrderByTimestampAsc(
            UUID shipmentId
    );

    Optional<ShipmentLocation>
    findTopByShipmentIdOrderByTimestampDesc(UUID shipmentId);
}