DESTINATION_COORDINATES = {
    "chennai": (
        13.0827,
        80.2707
    ),
    "bengaluru": (
        12.9716,
        77.5946
    ),
    "bangalore": (
        12.9716,
        77.5946
    ),
    "hyderabad": (
        17.3850,
        78.4867
    ),
    "mumbai": (
        19.0760,
        72.8777
    ),
    "pune": (
        18.5204,
        73.8567
    ),
    "delhi": (
        28.6139,
        77.2090
    ),
    "new delhi": (
        28.6139,
        77.2090
    ),
    "kolkata": (
        22.5726,
        88.3639
    ),
    "coimbatore": (
        11.0168,
        76.9558
    ),
    "kochi": (
        9.9312,
        76.2673
    ),
    "cochin": (
        9.9312,
        76.2673
    ),
    "visakhapatnam": (
        17.6868,
        83.2185
    ),
    "berlin": (
        52.5200, 
        13.4050
    ),
    "vizag": (
        17.6868,
        83.2185
    )
}


def resolve_destination(destination: str):

    normalized_destination = (
        destination
        .strip()
        .lower()
    )

    coordinates = DESTINATION_COORDINATES.get(
        normalized_destination
    )

    if coordinates is None:
        raise ValueError(
            f"Coordinates not found for destination: "
            f"{destination}"
        )

    latitude, longitude = coordinates

    return latitude, longitude