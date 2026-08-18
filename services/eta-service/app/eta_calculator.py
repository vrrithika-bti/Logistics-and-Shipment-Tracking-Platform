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

    print(
        f"Calculating ETA for shipment "
        f"{current.shipmentId}"
    )

    print(
        f"Previous location: "
        f"{previous.latitude}, {previous.longitude}"
    )

    print(
        f"Current location: "
        f"{current.latitude}, {current.longitude}"
    )

    print(
        f"Previous timestamp: {previous.timestamp}"
    )

    print(
        f"Current timestamp: {current.timestamp}"
    )

    # --------------------------------------------------
    # 1. Calculate distance travelled
    # --------------------------------------------------

    distance_travelled_km = calculate_distance_km(
        previous.latitude,
        previous.longitude,
        current.latitude,
        current.longitude
    )

    print(
        f"Distance travelled: "
        f"{distance_travelled_km:.2f} km"
    )

    # --------------------------------------------------
    # 2. Calculate elapsed time
    # --------------------------------------------------

    elapsed_seconds = (
        current.timestamp - previous.timestamp
    ).total_seconds()

    print(
        f"Elapsed time: "
        f"{elapsed_seconds:.2f} seconds"
    )

    if elapsed_seconds <= 0:
        print(
            "Cannot calculate ETA: "
            "current timestamp is not after previous timestamp"
        )
        return None

    elapsed_hours = elapsed_seconds / 3600.0

    # --------------------------------------------------
    # 3. Calculate average speed
    # --------------------------------------------------

    average_speed_kmh = (
        distance_travelled_km / elapsed_hours
    )

    print(
        f"Average speed: "
        f"{average_speed_kmh:.2f} km/h"
    )

    # --------------------------------------------------
    # 4. Handle zero movement
    # --------------------------------------------------

    if average_speed_kmh <= 0:
        print(
            "Shipment has not moved. "
            "Cannot calculate a normal ETA."
        )

        return EtaResponse(
            shipmentId=current.shipmentId,
            currentLocation=current.location,
            estimatedArrival=None,
            remainingDistanceKm=None,
            averageSpeedKmh=0.0,
            delayMinutes=0.0
        )

    # --------------------------------------------------
    # 5. Calculate remaining distance
    # --------------------------------------------------

    remaining_distance_km = calculate_distance_km(
        current.latitude,
        current.longitude,
        destination_latitude,
        destination_longitude
    )

    print(
        f"Remaining distance: "
        f"{remaining_distance_km:.2f} km"
    )

    # --------------------------------------------------
    # 6. Calculate remaining travel time
    # --------------------------------------------------

    remaining_hours = (
        remaining_distance_km / average_speed_kmh
    )

    remaining_seconds = (
        remaining_hours * 3600
    )

    # --------------------------------------------------
    # 7. Calculate estimated arrival
    # --------------------------------------------------

    estimated_arrival = (
        current.timestamp
        + timedelta(seconds=remaining_seconds)
    )

    print(
        f"Estimated arrival: "
        f"{estimated_arrival}"
    )

    # --------------------------------------------------
    # 8. Return ETA response
    # --------------------------------------------------

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