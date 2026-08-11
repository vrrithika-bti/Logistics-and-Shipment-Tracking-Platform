package com.logistics.shipment.repository;

import com.logistics.shipment.entity.Shipment;
import com.logistics.shipment.model.ShipmentStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface ShipmentRepository extends JpaRepository<Shipment, UUID> {

    Optional<Shipment> findByTrackingNumber(String trackingNumber);

    boolean existsByTrackingNumber(String trackingNumber);

    List<Shipment> findByCustomerId(UUID customerId);

    List<Shipment> findByStatus(ShipmentStatus status);
}