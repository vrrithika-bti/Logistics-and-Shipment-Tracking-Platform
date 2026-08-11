package com.logistics.shipment.service;

import com.logistics.shipment.dto.CreateShipmentRequest;
import com.logistics.shipment.dto.ShipmentResponse;
import com.logistics.shipment.entity.Shipment;
import com.logistics.shipment.exception.ShipmentNotFoundException;
import com.logistics.shipment.model.ShipmentStatus;
import com.logistics.shipment.repository.ShipmentRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class ShipmentService {

    private final ShipmentRepository shipmentRepository;

    public ShipmentService(ShipmentRepository shipmentRepository) {
        this.shipmentRepository = shipmentRepository;
    }

    @Transactional
    public ShipmentResponse createShipment(CreateShipmentRequest request) {

        Shipment shipment = new Shipment();

        shipment.setTrackingNumber(generateTrackingNumber());
        shipment.setCustomerId(request.getCustomerId());
        shipment.setOrigin(request.getOrigin());
        shipment.setDestination(request.getDestination());
        shipment.setStatus(ShipmentStatus.CREATED);

        Shipment savedShipment = shipmentRepository.save(shipment);

        return ShipmentResponse.fromEntity(savedShipment);
    }

    @Transactional(readOnly = true)
    public ShipmentResponse getShipment(UUID id) {

        Shipment shipment = shipmentRepository.findById(id)
                .orElseThrow(() -> new ShipmentNotFoundException(id));

        return ShipmentResponse.fromEntity(shipment);
    }

    @Transactional(readOnly = true)
    public ShipmentResponse getShipmentByTrackingNumber(String trackingNumber) {

        Shipment shipment = shipmentRepository
                .findByTrackingNumber(trackingNumber)
                .orElseThrow(() -> new ShipmentNotFoundException(trackingNumber));

        return ShipmentResponse.fromEntity(shipment);
    }

    @Transactional(readOnly = true)
    public List<ShipmentResponse> getShipmentsByCustomer(UUID customerId) {

        return shipmentRepository.findByCustomerId(customerId)
                .stream()
                .map(ShipmentResponse::fromEntity)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<ShipmentResponse> getAllShipments() {

        return shipmentRepository.findAll()
                .stream()
                .map(ShipmentResponse::fromEntity)
                .collect(Collectors.toList());
    }

    @Transactional
    public ShipmentResponse updateStatus(
            UUID id,
            ShipmentStatus newStatus
    ) {

        Shipment shipment = shipmentRepository.findById(id)
                .orElseThrow(() -> new ShipmentNotFoundException(id));

        shipment.setStatus(newStatus);

        Shipment updatedShipment = shipmentRepository.save(shipment);

        return ShipmentResponse.fromEntity(updatedShipment);
    }

    private String generateTrackingNumber() {

        return "SHP-" + UUID.randomUUID()
                .toString()
                .replace("-", "")
                .substring(0, 12)
                .toUpperCase();
    }
}