import axios from "axios";

const LOCATION_API_URL =
  "http://localhost:8083/api/v1/locations";

export async function sendLocationUpdate(locationData) {
  console.log(
    "Calling Location API:",
    LOCATION_API_URL,
    locationData
  );

  const response = await axios.post(
    LOCATION_API_URL,
    locationData,
    {
      headers: {
        "Content-Type": "application/json",
      },
    }
  );

  console.log(
    "Location API response:",
    response.data
  );

  return response.data;
}