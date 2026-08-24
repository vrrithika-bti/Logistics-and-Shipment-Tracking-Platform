import json
import os
import threading

from kafka import KafkaConsumer

from app.destination_resolver import resolve_destination
from app.eta_calculator import calculate_eta
from app.models import LocationUpdate
from app.shipment_client import get_shipment
from app.storage import eta_results, latest_locations


def consume_location_updates():

    try:
        bootstrap_servers = os.getenv(
            "KAFKA_BOOTSTRAP_SERVERS",
            "kafka:9092"
        )

        print(
            f"ETA Kafka consumer connecting to: "
            f"{bootstrap_servers}",
            flush=True
        )

        consumer = KafkaConsumer(
            "location-updates",
            bootstrap_servers=bootstrap_servers,
            group_id="eta-service",
            auto_offset_reset="latest",
            enable_auto_commit=True,
            value_deserializer=lambda value: json.loads(
                value.decode("utf-8")
            )
        )

        print(
            "ETA Kafka consumer started",
            flush=True
        )

        print(
            "Listening to topic: location-updates",
            flush=True
        )

        for message in consumer:

            try:
                location_update = LocationUpdate(
                    **message.value
                )

                shipment_id = (
                    location_update.shipmentId
                )

                previous_location = (
                    latest_locations.get(
                        shipment_id
                    )
                )

                latest_locations[
                    shipment_id
                ] = location_update

                print(
                    "Received location update: "
                    f"shipment={shipment_id}, "
                    f"location={location_update.location}",
                    flush=True
                )

                if previous_location is None:

                    print(
                        "Waiting for another location update "
                        "to calculate ETA for "
                        f"shipment={shipment_id}",
                        flush=True
                    )

                    continue

                shipment = get_shipment(
                    shipment_id
                )

                destination = shipment.get(
                    "destination"
                )

                if not destination:
                    raise ValueError(
                        "Shipment destination is missing "
                        f"for shipment={shipment_id}"
                    )

                (
                    destination_latitude,
                    destination_longitude
                ) = resolve_destination(
                    destination
                )

                print(
                    "Shipment destination resolved: "
                    f"shipment={shipment_id}, "
                    f"destination={destination}, "
                    f"latitude={destination_latitude}, "
                    f"longitude={destination_longitude}",
                    flush=True
                )

                eta = calculate_eta(
                    previous_location,
                    location_update,
                    destination_latitude,
                    destination_longitude
                )

                eta_results[
                    shipment_id
                ] = eta

                print(
                    "ETA calculated: "
                    f"shipment={shipment_id}, "
                    f"remainingDistanceKm="
                    f"{eta.remainingDistanceKm}, "
                    f"averageSpeedKmh="
                    f"{eta.averageSpeedKmh}, "
                    f"estimatedArrival="
                    f"{eta.estimatedArrival}",
                    flush=True
                )

            except Exception as error:

                print(
                    "Error processing Kafka message: "
                    f"{error}",
                    flush=True
                )

    except Exception as error:

        print(
            f"ETA Kafka consumer failed: {error}",
            flush=True
        )


def start_kafka_consumer():

    print(
        "Creating ETA Kafka consumer thread",
        flush=True
    )

    consumer_thread = threading.Thread(
        target=consume_location_updates,
        daemon=True
    )

    consumer_thread.start()