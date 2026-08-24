import { useEffect, useState } from "react";
import Navbar from "../../components/Navbar";

import {
  getShipmentsByCustomer,
  getShipmentHistory,
} from "../../services/shipmentService";

import { getCustomerId } from "../../services/authService";

function MyShipments() {
  const [shipments, setShipments] = useState([]);
  const [error, setError] = useState("");
  const [loading, setLoading] = useState(true);

  const [history, setHistory] = useState([]);
  const [historyShipment, setHistoryShipment] =
    useState(null);

  const [historyLoading, setHistoryLoading] =
    useState(false);

  const customerId = getCustomerId();

  useEffect(() => {
    loadMyShipments();
  }, []);

  async function loadMyShipments() {
    try {
      setLoading(true);
      setError("");

      if (!customerId) {
        setError(
          "Customer information not found. Please login again."
        );
        return;
      }

      const response =
        await getShipmentsByCustomer(customerId);

      setShipments(response || []);
    } catch (error) {
      console.error(
        "Error loading shipments:",
        error
      );

      setError(
        error.response?.data?.message ||
        "Failed to load shipments. Please try again."
      );
    } finally {
      setLoading(false);
    }
  }

  async function handleViewHistory(shipment) {
    try {
      setHistoryLoading(true);
      setError("");
      setHistory([]);
      setHistoryShipment(shipment);

      const response =
        await getShipmentHistory(shipment.id);

      setHistory(response || []);
    } catch (error) {
      console.error(
        "Error loading shipment history:",
        error
      );

      setError(
        error.response?.data?.message ||
        "Failed to load shipment history."
      );

      setHistoryShipment(null);
    } finally {
      setHistoryLoading(false);
    }
  }

  function formatDate(dateValue) {
    if (!dateValue) {
      return "Not available";
    }

    return new Date(
      dateValue
    ).toLocaleString();
  }

  return (
    <>
      <Navbar />

      <main className="dashboard-container">
        <h1>My Shipments</h1>

        <p className="dashboard-subtitle">
          View all shipments created from your account.
        </p>

        {error && (
          <div className="error-message">
            {error}
          </div>
        )}

        {loading && (
          <div className="form-card">
            <p>Loading your shipments...</p>
          </div>
        )}

        {!loading && shipments.length === 0 && !error && (
          <div className="form-card">
            <p>No shipments found.</p>
          </div>
        )}

        {!loading && shipments.length > 0 && (
          <div className="shipment-result">
            <div className="dashboard-header-row">
              <h2>
                My Shipments ({shipments.length})
              </h2>

              <button
                className="login-button"
                onClick={loadMyShipments}
              >
                Refresh
              </button>
            </div>

            <div className="table-container">
              <table>
                <thead>
                  <tr>
                    <th>Tracking Number</th>
                    <th>Origin</th>
                    <th>Destination</th>
                    <th>Status</th>
                    <th>Estimated Delivery</th>
                    <th>Created At</th>
                    <th>History</th>
                  </tr>
                </thead>

                <tbody>
                  {shipments.map((shipment) => (
                    <tr key={shipment.id}>
                      <td>
                        {shipment.trackingNumber}
                      </td>

                      <td>
                        {shipment.origin}
                      </td>

                      <td>
                        {shipment.destination}
                      </td>

                      <td>
                        {shipment.status}
                      </td>

                      <td>
                        {formatDate(
                          shipment.estimatedDelivery
                        )}
                      </td>

                      <td>
                        {formatDate(
                          shipment.createdAt
                        )}
                      </td>

                      <td>
                        <button
                          className="login-button"
                          onClick={() =>
                            handleViewHistory(shipment)
                          }
                        >
                          View History
                        </button>
                      </td>
                    </tr>
                  ))}
                </tbody>
              </table>
            </div>
          </div>
        )}

        {historyShipment && (
          <div className="shipment-result">
            <div className="dashboard-header-row">
              <h2>Shipment Status History</h2>

              <button
                className="login-button"
                onClick={() => {
                  setHistoryShipment(null);
                  setHistory([]);
                }}
              >
                Close
              </button>
            </div>

            <p>
              <strong>Tracking Number:</strong>{" "}
              {historyShipment.trackingNumber}
            </p>

            <p>
              <strong>Route:</strong>{" "}
              {historyShipment.origin} →{" "}
              {historyShipment.destination}
            </p>

            {historyLoading && (
              <p>Loading history...</p>
            )}

            {!historyLoading &&
              history.length === 0 && (
                <p>
                  No shipment history found.
                </p>
              )}

            {!historyLoading &&
              history.length > 0 && (
                <div className="table-container">
                  <table>
                    <thead>
                      <tr>
                        <th>Status Change</th>
                        <th>Changed At</th>
                      </tr>
                    </thead>

                    <tbody>
                      {history.map((item) => (
                        <tr key={item.id}>
                          <td>
                            {item.oldStatus || "None"} →{" "}
                            {item.newStatus}
                          </td>

                          <td>
                            {formatDate(
                              item.changedAt
                            )}
                          </td>
                        </tr>
                      ))}
                    </tbody>
                  </table>
                </div>
              )}
          </div>
        )}
      </main>
    </>
  );
}

export default MyShipments;