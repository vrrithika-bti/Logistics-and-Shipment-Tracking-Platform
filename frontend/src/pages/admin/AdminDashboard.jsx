import { useEffect, useState } from "react";

import {
  getAllShipments,
  getShipmentHistory,
  updateShipmentStatus,
} from "../../services/shipmentService";

import {
  sendLocationUpdate,
} from "../../services/locationService";

import Navbar from "../../components/Navbar";

const STATUS_OPTIONS = [
  "CREATED",
  "PICKED_UP",
  "IN_TRANSIT",
  "OUT_FOR_DELIVERY",
  "DELIVERED",
  "CANCELLED",
];

function AdminDashboard() {
  const [shipments, setShipments] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState("");
  const [message, setMessage] = useState("");
  const [updatingId, setUpdatingId] = useState(null);

  // Shipment history states
  const [history, setHistory] = useState([]);
  const [historyShipment, setHistoryShipment] =
    useState(null);
  const [historyLoading, setHistoryLoading] =
    useState(false);

  // Location update states
  const [locationShipment, setLocationShipment] =
    useState(null);

  const [locationName, setLocationName] =
    useState("");

  const [latitude, setLatitude] =
    useState("");

  const [longitude, setLongitude] =
    useState("");

  const [locationLoading, setLocationLoading] =
    useState(false);

  useEffect(() => {
    loadShipments();
  }, []);

  async function loadShipments() {
    try {
      setLoading(true);
      setError("");

      const response = await getAllShipments();

      setShipments(response.content || []);
    } catch (error) {
      console.error(
        "Error loading all shipments:",
        error
      );

      setError(
        error.response?.data?.message ||
        "Failed to load shipments."
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

  async function handleStatusUpdate(
    shipmentId,
    newStatus
  ) {
    try {
      setUpdatingId(shipmentId);
      setError("");
      setMessage("");

      const updatedShipment =
        await updateShipmentStatus(
          shipmentId,
          newStatus
        );

      setShipments((currentShipments) =>
        currentShipments.map((shipment) =>
          shipment.id === shipmentId
            ? updatedShipment
            : shipment
        )
      );

      setMessage(
        `Shipment ${updatedShipment.trackingNumber} updated to ${updatedShipment.status}`
      );
    } catch (error) {
      console.error(
        "Error updating shipment status:",
        error
      );

      setError(
        error.response?.data?.message ||
        "Failed to update shipment status."
      );
    } finally {
      setUpdatingId(null);
    }
  }

  // Open location update form
  function handleOpenLocationUpdate(shipment) {
    setLocationShipment(shipment);

    setLocationName("");
    setLatitude("");
    setLongitude("");

    setError("");
    setMessage("");
  }

  // Close location update form
  function handleCloseLocationUpdate() {
    setLocationShipment(null);

    setLocationName("");
    setLatitude("");
    setLongitude("");
  }

  // Send location update to Location Service
  async function handleLocationUpdate(event) {
    event.preventDefault();

    if (!locationShipment) {
      return;
    }

    try {
      setLocationLoading(true);
      setError("");
      setMessage("");

      const locationData = {
        shipmentId: locationShipment.id,
        latitude: Number(latitude),
        longitude: Number(longitude),
        location: locationName.trim(),
        timestamp: new Date()
          .toISOString()
          .slice(0, 19),
      };

      console.log(
        "Sending location update:",
        locationData
      );

      await sendLocationUpdate(locationData);

      setMessage(
        `Location update sent successfully for shipment ${locationShipment.trackingNumber}.`
      );

      setLocationName("");
      setLatitude("");
      setLongitude("");

    } catch (error) {
      console.error(
        "Error sending location update:",
        error
      );

      setError(
        error.response?.data?.message ||
        "Failed to send location update."
      );
    } finally {
      setLocationLoading(false);
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
        <h1>Admin Dashboard</h1>

        <p className="dashboard-subtitle">
          View and manage all shipments in the system.
        </p>

        {message && (
          <div className="success-message">
            {message}
          </div>
        )}

        {error && (
          <div className="error-message">
            {error}
          </div>
        )}

        {loading && (
          <div className="form-card">
            <p>Loading shipments...</p>
          </div>
        )}

        {!loading && (
          <div className="shipment-result">
            <div className="dashboard-header-row">
              <h2>
                All Shipments ({shipments.length})
              </h2>

              <button
                className="login-button"
                onClick={loadShipments}
              >
                Refresh
              </button>
            </div>

            {shipments.length === 0 ? (
              <p>No shipments found.</p>
            ) : (
              <div className="table-container">
                <table>
                  <thead>
                    <tr>
                      <th>Tracking Number</th>
                      <th>Customer ID</th>
                      <th>Route</th>
                      <th>Current Status</th>
                      <th>New Status</th>
                      <th>Update</th>
                      <th>History</th>
                      <th>Location</th>
                      <th>Created At</th>
                    </tr>
                  </thead>

                  <tbody>
                    {shipments.map((shipment) => (
                      <ShipmentRow
                        key={shipment.id}
                        shipment={shipment}
                        updatingId={updatingId}
                        onUpdate={handleStatusUpdate}
                        onViewHistory={
                          handleViewHistory
                        }
                        onOpenLocationUpdate={
                          handleOpenLocationUpdate
                        }
                        formatDate={formatDate}
                      />
                    ))}
                  </tbody>
                </table>
              </div>
            )}
          </div>
        )}

        {/* LOCATION UPDATE FORM */}
        {locationShipment && (
          <div className="shipment-result">
            <div className="dashboard-header-row">
              <h2>
                Update Shipment Location
              </h2>

              <button
                className="login-button"
                onClick={
                  handleCloseLocationUpdate
                }
                disabled={locationLoading}
              >
                Close
              </button>
            </div>

            <p>
              <strong>Tracking Number:</strong>{" "}
              {locationShipment.trackingNumber}
            </p>

            <p>
              <strong>Shipment ID:</strong>{" "}
              {locationShipment.id}
            </p>

            <p>
              Enter the current GPS coordinates for
              this shipment.
            </p>

            <form
              onSubmit={handleLocationUpdate}
            >
              <label>
                Current Location
              </label>

              <input
                type="text"
                placeholder="Example: Chennai"
                value={locationName}
                onChange={(event) =>
                  setLocationName(
                    event.target.value
                  )
                }
                required
              />

              <label>
                Latitude
              </label>

              <input
                type="number"
                step="any"
                min="-90"
                max="90"
                placeholder="Example: 13.0827"
                value={latitude}
                onChange={(event) =>
                  setLatitude(
                    event.target.value
                  )
                }
                required
              />

              <label>
                Longitude
              </label>

              <input
                type="number"
                step="any"
                min="-180"
                max="180"
                placeholder="Example: 80.2707"
                value={longitude}
                onChange={(event) =>
                  setLongitude(
                    event.target.value
                  )
                }
                required
              />

              <button
                type="submit"
                className="login-button"
                disabled={locationLoading}
              >
                {locationLoading
                  ? "Sending Location..."
                  : "Send Location Update"}
              </button>
            </form>
          </div>
        )}

        {/* SHIPMENT HISTORY */}
        {historyShipment && (
          <div className="shipment-result">
            <div className="dashboard-header-row">
              <h2>
                Shipment History
              </h2>

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
              <strong>Shipment ID:</strong>{" "}
              {historyShipment.id}
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
                            {item.oldStatus || "None"}
                            {" → "}
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

function ShipmentRow({
  shipment,
  updatingId,
  onUpdate,
  onViewHistory,
  onOpenLocationUpdate,
  formatDate,
}) {
  const [selectedStatus, setSelectedStatus] =
    useState(shipment.status);

  const isUpdating =
    updatingId === shipment.id;

  return (
    <tr>
      <td>
        {shipment.trackingNumber}
      </td>

      <td>
        {shipment.customerId}
      </td>

      <td>
        {shipment.origin}
        {" → "}
        {shipment.destination}
      </td>

      <td>
        {shipment.status}
      </td>

      <td>
        <select
          value={selectedStatus}
          onChange={(event) =>
            setSelectedStatus(
              event.target.value
            )
          }
          disabled={isUpdating}
        >
          {STATUS_OPTIONS.map((status) => (
            <option
              key={status}
              value={status}
            >
              {status}
            </option>
          ))}
        </select>
      </td>

      <td>
        <button
          className="login-button"
          onClick={() =>
            onUpdate(
              shipment.id,
              selectedStatus
            )
          }
          disabled={
            isUpdating ||
            selectedStatus === shipment.status
          }
        >
          {isUpdating
            ? "Updating..."
            : "Update"}
        </button>
      </td>

      <td>
        <button
          className="login-button"
          onClick={() =>
            onViewHistory(shipment)
          }
        >
          View History
        </button>
      </td>

      <td>
        <button
          className="login-button"
          onClick={() =>
            onOpenLocationUpdate(shipment)
          }
        >
          Update Location
        </button>
      </td>

      <td>
        {formatDate(shipment.createdAt)}
      </td>
    </tr>
  );
}

export default AdminDashboard;