CREATE TABLE tracking_events (
    id UUID PRIMARY KEY,
    shipment_id UUID NOT NULL,
    status VARCHAR(30) NOT NULL,
    location VARCHAR(255),
    event_time TIMESTAMP NOT NULL,
    created_at TIMESTAMP NOT NULL
);

CREATE INDEX idx_tracking_events_shipment_id
ON tracking_events(shipment_id);

CREATE INDEX idx_tracking_events_event_time
ON tracking_events(event_time);
