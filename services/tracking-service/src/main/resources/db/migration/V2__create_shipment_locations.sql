CREATE TABLE shipment_locations (
    id UUID PRIMARY KEY,
    shipment_id UUID NOT NULL,
    latitude DOUBLE PRECISION NOT NULL,
    longitude DOUBLE PRECISION NOT NULL,
    location VARCHAR(255) NOT NULL,
    timestamp TIMESTAMP NOT NULL
);