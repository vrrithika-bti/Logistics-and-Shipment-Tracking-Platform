package com.logistics.shipment.service;

import java.util.List;
import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.logistics.shipment.dto.CreateShipmentRequest;
import com.logistics.shipment.dto.ShipmentResponse;
import com.logistics.shipment.dto.ShipmentStatusHistoryResponse;
import com.logistics.shipment.entity.Shipment;
import com.logistics.shipment.entity.ShipmentStatusHistory;
import com.logistics.shipment.exception.InvalidShipmentStatusTransitionException;
import com.logistics.shipment.exception.ShipmentNotFoundException;
import com.logistics.shipment.model.ShipmentStatus;
import com.logistics.shipment.repository.ShipmentRepository;
import com.logistics.shipment.repository.ShipmentStatusHistoryRepository;

@Service
public class ShipmentService {

    private final ShipmentRepository shipmentRepository;
    private final ShipmentStatusHistoryRepository historyRepository;

    public ShipmentService(
            ShipmentRepository shipmentRepository,
            ShipmentStatusHistoryRepository historyRepository
    ) {
        this.shipmentRepository = shipmentRepository;
        this.historyRepository = historyRepository;
    }

    @Transactional
    public ShipmentResponse createShipment(
            CreateShipmentRequest request
    ) {

        Shipment shipment = new Shipment();

        shipment.setTrackingNumber(generateTrackingNumber());
        shipment.setCustomerId(request.getCustomerId());
        shipment.setOrigin(request.getOrigin());
        shipment.setDestination(request.getDestination());

        shipment.setDestinationLatitude(request.getDestinationLatitude());

        shipment.setDestinationLongitude(request.getDestinationLongitude());

        shipment.setStatus(ShipmentStatus.CREATED);

        Shipment savedShipment =
                shipmentRepository.save(shipment);

        ShipmentStatusHistory history =
                new ShipmentStatusHistory();

        history.setShipmentId(savedShipment.getId());
        history.setOldStatus(null);
        history.setNewStatus(ShipmentStatus.CREATED);

        historyRepository.save(history);

        return ShipmentResponse.fromEntity(savedShipment);
    }

    @Transactional(readOnly = true)
    public ShipmentResponse getShipment(UUID id) {

        Shipment shipment =
                shipmentRepository.findById(id)
                        .orElseThrow(
                            () -> new ShipmentNotFoundException(id)
                        );

        return ShipmentResponse.fromEntity(shipment);
    }

    @Transactional(readOnly = true)
    public ShipmentResponse getShipmentByTrackingNumber(
            String trackingNumber
    ) {

        Shipment shipment =
                shipmentRepository
                        .findByTrackingNumber(trackingNumber)
                        .orElseThrow(
                            () -> new ShipmentNotFoundException(
                                    trackingNumber
                            )
                        );

        return ShipmentResponse.fromEntity(shipment);
    }

    @Transactional(readOnly = true)
    public List<ShipmentResponse> getShipmentsByCustomer(
            UUID customerId
    ) {

        return shipmentRepository
                .findByCustomerId(customerId)
                .stream()
                .map(ShipmentResponse::fromEntity)
                .toList();
    }

    @Transactional(readOnly = true)
    public Page<ShipmentResponse> getShipments(
            ShipmentStatus status,
            Pageable pageable
    ) {

        Page<Shipment> shipments;

        if (status != null) {
            shipments =
                    shipmentRepository
                            .findByStatus(status, pageable);
        } else {
            shipments =
                    shipmentRepository.findAll(pageable);
        }

        return shipments.map(ShipmentResponse::fromEntity);
    }

    @Transactional
    public ShipmentResponse updateStatus(
            UUID id,
            ShipmentStatus newStatus
    ) {

        Shipment shipment =
                shipmentRepository.findById(id)
                        .orElseThrow(
                            () -> new ShipmentNotFoundException(id)
                        );

        ShipmentStatus currentStatus =
                shipment.getStatus();

        if (currentStatus == newStatus) {
            return ShipmentResponse.fromEntity(shipment);
        }

        if (!currentStatus.canTransitionTo(newStatus)) {

            throw new InvalidShipmentStatusTransitionException(
                    currentStatus,
                    newStatus
            );
        }

        shipment.setStatus(newStatus);

        Shipment updatedShipment =
                shipmentRepository.save(shipment);

        ShipmentStatusHistory history =
                new ShipmentStatusHistory();

        history.setShipmentId(updatedShipment.getId());
        history.setOldStatus(currentStatus);
        history.setNewStatus(newStatus);

        historyRepository.save(history);

        return ShipmentResponse.fromEntity(updatedShipment);
    }

    @Transactional(readOnly = true)
    public List<ShipmentStatusHistoryResponse> getHistory(
            UUID shipmentId
    ) {

        if (!shipmentRepository.existsById(shipmentId)) {
            throw new ShipmentNotFoundException(shipmentId);
        }

        return historyRepository
                .findByShipmentIdOrderByChangedAtAsc(shipmentId)
                .stream()
                .map(ShipmentStatusHistoryResponse::fromEntity)
                .toList();
    }

    private String generateTrackingNumber() {

        return "SHP-"
                + UUID.randomUUID()
                    .toString()
                    .replace("-", "")
                    .substring(0, 12)
                    .toUpperCase();
    }
}