from datetime import timedelta

from app.models import EtaResponse, LocationUpdate


def haversine_distance_km(
    latitude1: float,
    longitude1: float,
    latitude2: float,
    longitude2: float
) -> float:

    from math import radians, sin, cos, sqrt, atan2

    earth_radius_km = 6371.0

    lat1 = radians(latitude1)
    lon1 = radians(longitude1)
    lat2 = radians(latitude2)
    lon2 = radians(longitude2)

    delta_lat = lat2 - lat1
    delta_lon = lon2 - lon1

    a = (
        sin(delta_lat / 2) ** 2
        + cos(lat1)
        * cos(lat2)
        * sin(delta_lon / 2) ** 2
    )

    c = 2 * atan2(
        sqrt(a),
        sqrt(1 - a)
    )

    return earth_radius_km * c


def calculate_eta(
    previous: LocationUpdate,
    current: LocationUpdate,
    destination_latitude: float,
    destination_longitude: float
) -> EtaResponse:

    distance_travelled_km = haversine_distance_km(
        previous.latitude,
        previous.longitude,
        current.latitude,
        current.longitude
    )

    time_difference_hours = (
        current.timestamp
        - previous.timestamp
    ).total_seconds() / 3600

    if time_difference_hours <= 0:
        average_speed_kmh = 0.0
    else:
        average_speed_kmh = (
            distance_travelled_km
            / time_difference_hours
        )

    remaining_distance_km = haversine_distance_km(
        current.latitude,
        current.longitude,
        destination_latitude,
        destination_longitude
    )

    if average_speed_kmh <= 0:
        estimated_arrival = None
    else:
        remaining_hours = (
            remaining_distance_km
            / average_speed_kmh
        )

        estimated_arrival = (
            current.timestamp
            + timedelta(hours=remaining_hours)
        )

    return EtaResponse(
        shipmentId=current.shipmentId,
        currentLocation=current.location,
        estimatedArrival=estimated_arrival,
        remainingDistanceKm=round(
            remaining_distance_km,
            2
        ),
        averageSpeedKmh=round(
            average_speed_kmh,
            2
        ),
        delayMinutes=0
    )