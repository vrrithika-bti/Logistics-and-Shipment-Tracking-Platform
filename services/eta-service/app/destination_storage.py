from typing import Dict
from uuid import UUID


shipment_destinations: Dict[UUID, dict] = {
    UUID("33333333-3333-3333-3333-333333333333"): {
        "latitude": 13.1500,
        "longitude": 80.3200,
        "location": "Chennai Destination"
    },
    UUID("44444444-4444-4444-4444-444444444444"): {
        "latitude": 13.1500,
        "longitude": 80.3200,
        "location": "Chennai Destination"
    }
}