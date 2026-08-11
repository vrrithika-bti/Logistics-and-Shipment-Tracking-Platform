package com.logistics.shipment.controller;

import com.logistics.shipment.dto.CreateShipmentRequest;
import com.logistics.shipment.dto.ShipmentResponse;
import com.logistics.shipment.model.ShipmentStatus;
import com.logistics.shipment.service.ShipmentService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/shipments")
public class ShipmentController {

    private final ShipmentService shipmentService;

    public ShipmentController(ShipmentService shipmentService) {
        this.shipmentService = shipmentService;
    }

    @PostMapping
    public ResponseEntity<ShipmentResponse> createShipment(
            @Valid @RequestBody CreateShipmentRequest request
    ) {

        ShipmentResponse response = shipmentService.createShipment(request);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(response);
    }

    @GetMapping
    public ResponseEntity<List<ShipmentResponse>> getAllShipments() {

        return ResponseEntity.ok(
                shipmentService.getAllShipments()
        );
    }

    @GetMapping("/{id}")
    public ResponseEntity<ShipmentResponse> getShipment(
            @PathVariable UUID id
    ) {

        return ResponseEntity.ok(
                shipmentService.getShipment(id)
        );
    }

    @GetMapping("/tracking/{trackingNumber}")
    public ResponseEntity<ShipmentResponse> getShipmentByTrackingNumber(
            @PathVariable String trackingNumber
    ) {

        return ResponseEntity.ok(
                shipmentService.getShipmentByTrackingNumber(trackingNumber)
        );
    }

    @GetMapping("/customer/{customerId}")
    public ResponseEntity<List<ShipmentResponse>> getShipmentsByCustomer(
            @PathVariable UUID customerId
    ) {

        return ResponseEntity.ok(
                shipmentService.getShipmentsByCustomer(customerId)
        );
    }

    @PatchMapping("/{id}/status")
    public ResponseEntity<ShipmentResponse> updateStatus(
            @PathVariable UUID id,
            @RequestParam ShipmentStatus status
    ) {

        return ResponseEntity.ok(
                shipmentService.updateStatus(id, status)
        );
    }
}