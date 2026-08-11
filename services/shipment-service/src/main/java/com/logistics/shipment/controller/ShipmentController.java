package com.logistics.shipment.controller;

import com.logistics.shipment.dto.CreateShipmentRequest;
import com.logistics.shipment.dto.ShipmentResponse;
import com.logistics.shipment.dto.ShipmentStatusHistoryResponse;
import com.logistics.shipment.model.ShipmentStatus;
import com.logistics.shipment.service.ShipmentService;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/shipments")
public class ShipmentController {

    private final ShipmentService shipmentService;

    public ShipmentController(
            ShipmentService shipmentService
    ) {
        this.shipmentService = shipmentService;
    }

    @PostMapping
    public ResponseEntity<ShipmentResponse> createShipment(
            @Valid @RequestBody CreateShipmentRequest request
    ) {

        ShipmentResponse response =
                shipmentService.createShipment(request);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(response);
    }

    @GetMapping
    public ResponseEntity<Page<ShipmentResponse>> getShipments(

            @RequestParam(required = false)
            ShipmentStatus status,

            @RequestParam(defaultValue = "0")
            int page,

            @RequestParam(defaultValue = "20")
            int size
    ) {

        if (size > 100) {
            size = 100;
        }

        Pageable pageable =
                PageRequest.of(
                        page,
                        size,
                        Sort.by(
                            Sort.Direction.DESC,
                            "createdAt"
                        )
                );

        return ResponseEntity.ok(
                shipmentService.getShipments(
                        status,
                        pageable
                )
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
    public ResponseEntity<ShipmentResponse>
    getShipmentByTrackingNumber(
            @PathVariable String trackingNumber
    ) {

        return ResponseEntity.ok(
                shipmentService
                        .getShipmentByTrackingNumber(
                                trackingNumber
                        )
        );
    }

    @GetMapping("/customer/{customerId}")
    public ResponseEntity<List<ShipmentResponse>>
    getShipmentsByCustomer(
            @PathVariable UUID customerId
    ) {

        return ResponseEntity.ok(
                shipmentService
                        .getShipmentsByCustomer(customerId)
        );
    }

    @GetMapping("/{id}/history")
    public ResponseEntity<List<ShipmentStatusHistoryResponse>>
    getShipmentHistory(
            @PathVariable UUID id
    ) {

        return ResponseEntity.ok(
                shipmentService.getHistory(id)
        );
    }

    @PatchMapping("/{id}/status")
    public ResponseEntity<ShipmentResponse> updateStatus(
            @PathVariable UUID id,

            @RequestParam ShipmentStatus status
    ) {

        return ResponseEntity.ok(
                shipmentService.updateStatus(
                        id,
                        status
                )
        );
    }
}