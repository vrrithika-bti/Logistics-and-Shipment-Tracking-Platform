import os

import requests


def get_shipment(shipment_id):

    shipment_service_url = os.getenv(
        "SHIPMENT_SERVICE_URL",
        "http://shipment-service:8081"
    )

    url = (
        f"{shipment_service_url}"
        f"/api/v1/shipments/{shipment_id}"
    )

    response = requests.get(
        url,
        timeout=5
    )

    response.raise_for_status()

    return response.json()