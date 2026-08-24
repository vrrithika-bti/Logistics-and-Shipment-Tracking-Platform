import axios from "axios";

const SHIPMENT_API_URL =
  "http://localhost:8081/api/v1/shipments";

export async function createShipment(shipmentData) {
  const response = await axios.post(
    SHIPMENT_API_URL,
    shipmentData
  );

  return response.data;
}

export async function getShipmentByTrackingNumber(
  trackingNumber
) {
  const response = await axios.get(
    `${SHIPMENT_API_URL}/tracking/${trackingNumber}`
  );

  return response.data;
}

export async function getShipmentsByCustomer(customerId) {
  const response = await axios.get(
    `${SHIPMENT_API_URL}/customer/${customerId}`
  );

  return response.data;
}

export async function getAllShipments() {
  const response = await axios.get(
    SHIPMENT_API_URL
  );

  return response.data;
}

export async function updateShipmentStatus(
  shipmentId,
  newStatus
) {
  const response = await axios.patch(
    `${SHIPMENT_API_URL}/${shipmentId}/status`,
    null,
    {
      params: {
        status: newStatus,
      },
    }
  );

  return response.data;
}

export async function getShipmentHistory(shipmentId) {
  const response = await axios.get(
    `${SHIPMENT_API_URL}/${shipmentId}/history`
  );

  return response.data;
}