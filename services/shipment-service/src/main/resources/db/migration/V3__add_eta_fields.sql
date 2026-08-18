ALTER TABLE shipments
    ADD COLUMN current_location VARCHAR(255),
    ADD COLUMN remaining_distance_km DOUBLE PRECISION,
    ADD COLUMN average_speed_kmh DOUBLE PRECISION,
    ADD COLUMN delay_minutes DOUBLE PRECISION NOT NULL DEFAULT 0;