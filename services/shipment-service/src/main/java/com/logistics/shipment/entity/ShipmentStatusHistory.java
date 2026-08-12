package com.logistics.shipment.entity;

import com.logistics.shipment.model.ShipmentStatus;
import jakarta.persistence.*;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(
    name = "shipment_status_history",
    indexes = {
        @Index(
            name = "idx_history_shipment_id",
            columnList = "shipment_id"
        ),
        @Index(
            name = "idx_history_changed_at",
            columnList = "changed_at"
        )
    }
)
public class ShipmentStatusHistory {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(name = "shipment_id", nullable = false)
    private UUID shipmentId;

    @Enumerated(EnumType.STRING)
    @Column(name = "old_status", length = 30)
    private ShipmentStatus oldStatus;

    @Enumerated(EnumType.STRING)
    @Column(name = "new_status", nullable = false, length = 30)
    private ShipmentStatus newStatus;

    @Column(name = "changed_at", nullable = false)
    private LocalDateTime changedAt;

    public ShipmentStatusHistory() {
    }

    public UUID getId() {
        return id;
    }

    public UUID getShipmentId() {
        return shipmentId;
    }

    public void setShipmentId(UUID shipmentId) {
        this.shipmentId = shipmentId;
    }

    public ShipmentStatus getOldStatus() {
        return oldStatus;
    }

    public void setOldStatus(ShipmentStatus oldStatus) {
        this.oldStatus = oldStatus;
    }

    public ShipmentStatus getNewStatus() {
        return newStatus;
    }

    public void setNewStatus(ShipmentStatus newStatus) {
        this.newStatus = newStatus;
    }

    public LocalDateTime getChangedAt() {
        return changedAt;
    }

    public void setChangedAt(LocalDateTime changedAt) {
        this.changedAt = changedAt;
    }

    @PrePersist
    protected void onCreate() {
        if (changedAt == null) {
            changedAt = LocalDateTime.now();
        }
    }
}