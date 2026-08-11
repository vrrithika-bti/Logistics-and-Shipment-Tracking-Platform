CREATE TABLE shipment_status_history (
    id UUID PRIMARY KEY,

    shipment_id UUID NOT NULL,

    old_status VARCHAR(30),

    new_status VARCHAR(30) NOT NULL,

    changed_at TIMESTAMP NOT NULL,

    CONSTRAINT fk_history_shipment
        FOREIGN KEY (shipment_id)
        REFERENCES shipments(id)
        ON DELETE CASCADE
);

CREATE INDEX idx_history_shipment_id
    ON shipment_status_history(shipment_id);

CREATE INDEX idx_history_changed_at
    ON shipment_status_history(changed_at);