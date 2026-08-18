import math
from datetime import timedelta

from app.models import LocationUpdate, EtaResponse


EARTH_RADIUS_KM = 6371.0


def calculate_distance_km(
    latitude1: float,
    longitude1: float,
    latitude2: float,
    longitude2: float
) -> float:
    """
    Calculate the distance between two GPS coordinates
    using the Haversine formula.
    """

    lat1 = math.radians(latitude1)
    lat2 = math.radians(latitude2)

    delta_lat = math.radians(latitude2 - latitude1)
    delta_lon = math.radians(longitude2 - longitude1)

    a = (
        math.sin(delta_lat / 2) ** 2
        + math.cos(lat1)
        * math.cos(lat2)
        * math.sin(delta_lon / 2) ** 2
    )

    c = 2 * math.atan2(
        math.sqrt(a),
        math.sqrt(1 - a)
    )

    return EARTH_RADIUS_KM * c


def calculate_eta(
    previous: LocationUpdate,
    current: LocationUpdate,
    destination_latitude: float,
    destination_longitude: float
) -> EtaResponse | None:

    # Calculate distance travelled since previous update
    distance_travelled_km = calculate_distance_km(
        previous.latitude,
        previous.longitude,
        current.latitude,
        current.longitude
    )

    # Calculate elapsed time in hours
    elapsed_seconds = (
        current.timestamp - previous.timestamp
    ).total_seconds()

    if elapsed_seconds <= 0:
        return None

    elapsed_hours = elapsed_seconds / 3600.0

    # Calculate current average speed
    average_speed_kmh = (
        distance_travelled_km / elapsed_hours
    )

    # Cannot calculate ETA if the shipment has not moved
    if average_speed_kmh <= 0:
        return EtaResponse(
            shipmentId=current.shipmentId,
            currentLocation=current.location,
            estimatedArrival=None,
            remainingDistanceKm=None,
            averageSpeedKmh=0.0,
            delayMinutes=0.0
        )

    # Calculate remaining distance to destination
    remaining_distance_km = calculate_distance_km(
        current.latitude,
        current.longitude,
        destination_latitude,
        destination_longitude
    )

    # Calculate travel time remaining
    remaining_hours = (
        remaining_distance_km / average_speed_kmh
    )

    remaining_seconds = remaining_hours * 3600

    estimated_arrival = (
        current.timestamp
        + timedelta(seconds=remaining_seconds)
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
        delayMinutes=0.0
    )