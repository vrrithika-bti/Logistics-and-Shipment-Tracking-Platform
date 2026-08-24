import axios from "axios";

const ETA_API_URL =
  "http://localhost:8084/api/v1/eta";

export async function getShipmentEta(shipmentId) {
  const response = await axios.get(
    `${ETA_API_URL}/${shipmentId}`
  );

  return response.data;
}