CREATE TABLE shipments (
    id UUID PRIMARY KEY,

    tracking_number VARCHAR(50) NOT NULL UNIQUE,

    customer_id UUID NOT NULL,

    origin VARCHAR(255) NOT NULL,

    destination VARCHAR(255) NOT NULL,

    status VARCHAR(30) NOT NULL,

    estimated_delivery TIMESTAMP,

    created_at TIMESTAMP NOT NULL,

    updated_at TIMESTAMP NOT NULL
);

CREATE INDEX idx_shipments_tracking_number
    ON shipments(tracking_number);

CREATE INDEX idx_shipments_customer_id
    ON shipments(customer_id);

CREATE INDEX idx_shipments_status
    ON shipments(status);