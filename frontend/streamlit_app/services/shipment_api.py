import requests


BASE_URL = "http://localhost:8081/api/v1/shipments"


def get_shipments(page=0, size=20, status=None):

    params = {
        "page": page,
        "size": size
    }

    if status and status != "ALL":
        params["status"] = status

    response = requests.get(
        BASE_URL,
        params=params,
        timeout=5
    )

    response.raise_for_status()

    return response.json()


def create_shipment(customer_id, origin, destination):

    payload = {
        "customerId": customer_id,
        "origin": origin,
        "destination": destination
    }

    response = requests.post(
        BASE_URL,
        json=payload,
        timeout=5
    )

    response.raise_for_status()

    return response.json()


def get_shipment(shipment_id):

    response = requests.get(
        f"{BASE_URL}/{shipment_id}",
        timeout=5
    )

    response.raise_for_status()

    return response.json()


def get_shipment_by_tracking_number(tracking_number):

    response = requests.get(
        f"{BASE_URL}/tracking/{tracking_number}",
        timeout=5
    )

    response.raise_for_status()

    return response.json()


def get_shipments_by_customer(customer_id):

    response = requests.get(
        f"{BASE_URL}/customer/{customer_id}",
        timeout=5
    )

    response.raise_for_status()

    return response.json()


def update_shipment_status(shipment_id, status):

    response = requests.patch(
        f"{BASE_URL}/{shipment_id}/status",
        params={
            "status": status
        },
        timeout=5
    )

    response.raise_for_status()

    return response.json()


def get_shipment_history(shipment_id):

    response = requests.get(
        f"{BASE_URL}/{shipment_id}/history",
        timeout=5
    )

    response.raise_for_status()

    return response.json()