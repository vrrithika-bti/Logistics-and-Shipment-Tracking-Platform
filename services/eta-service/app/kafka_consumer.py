import json
import os
import threading
from urllib.request import Request, urlopen
from urllib.error import HTTPError, URLError

from kafka import KafkaConsumer

from app.eta_calculator import calculate_eta
from app.models import LocationUpdate
from app.storage import latest_locations, eta_results


KAFKA_BOOTSTRAP_SERVERS = os.getenv(
    "KAFKA_BOOTSTRAP_SERVERS",
    "kafka:9092"
)

KAFKA_TOPIC = "location-updates"
KAFKA_GROUP_ID = "eta-service"

SHIPMENT_SERVICE_URL = os.getenv(
    "SHIPMENT_SERVICE_URL",
    "http://shipment-service:8081"
)


def get_shipment_destination(shipment_id):
    """
    Get destination coordinates from the shipment service.
    """

    url = (
        f"{SHIPMENT_SERVICE_URL}"
        f"/api/v1/shipments/{shipment_id}"
    )

    request = Request(
        url,
        method="GET"
    )

    try:
        with urlopen(request, timeout=5) as response:
            data = json.loads(
                response.read().decode("utf-8")
            )

            return (
                data.get("destinationLatitude"),
                data.get("destinationLongitude")
            )

    except HTTPError as exc:
        print(
            f"Shipment service returned HTTP "
            f"{exc.code} for shipment {shipment_id}"
        )

    except URLError as exc:
        print(
            f"Could not connect to shipment service: {exc}"
        )

    except Exception as exc:
        print(
            f"Failed to get shipment destination: {exc}"
        )

    return None, None


def process_location_update(
    location_update: LocationUpdate
):
    shipment_id = location_update.shipmentId

    previous = latest_locations.get(shipment_id)

    if previous is not None:

        destination_latitude, destination_longitude = (
            get_shipment_destination(shipment_id)
        )

        if (
            destination_latitude is None
            or destination_longitude is None
        ):
            print(
                f"Destination coordinates not available "
                f"for shipment {shipment_id}"
            )

            latest_locations[shipment_id] = location_update
            return

        eta = calculate_eta(
            previous=previous,
            current=location_update,
            destination_latitude=destination_latitude,
            destination_longitude=destination_longitude
        )

        if eta is not None:
            eta_results[shipment_id] = eta

            print(
                f"ETA calculated for shipment "
                f"{shipment_id}: "
                f"{eta.estimatedArrival}"
            )

    latest_locations[shipment_id] = location_update


def start_kafka_consumer():

    consumer = KafkaConsumer(
        KAFKA_TOPIC,
        bootstrap_servers=KAFKA_BOOTSTRAP_SERVERS,
        group_id=KAFKA_GROUP_ID,
        auto_offset_reset="earliest",
        enable_auto_commit=True,
        value_deserializer=lambda value: json.loads(
            value.decode("utf-8")
        )
    )

    print(
        f"ETA service consuming Kafka topic: "
        f"{KAFKA_TOPIC}"
    )

    for message in consumer:

        try:
            location_update = (
                LocationUpdate.model_validate(
                    message.value
                )
            )

            process_location_update(
                location_update
            )

            print(
                f"Processed location update for shipment "
                f"{location_update.shipmentId}"
            )

        except Exception as exc:

            print(
                f"Failed to process location update: "
                f"{exc}"
            )


def run_consumer_in_background():

    thread = threading.Thread(
        target=start_kafka_consumer,
        daemon=True
    )

    thread.start()