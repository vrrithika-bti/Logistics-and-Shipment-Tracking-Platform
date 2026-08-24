import { useEffect, useState } from "react";
import Navbar from "../../components/Navbar";

import {
  getShipmentByTrackingNumber,
} from "../../services/shipmentService";

import {
  getShipmentEta,
} from "../../services/etaService";

function TrackShipment() {
  const [trackingNumber, setTrackingNumber] =
    useState("");

  const [shipment, setShipment] =
    useState(null);

  const [eta, setEta] =
    useState(null);

  const [error, setError] =
    useState("");

  const [etaError, setEtaError] =
    useState("");

  const [loading, setLoading] =
    useState(false);

  const [etaLoading, setEtaLoading] =
    useState(false);

  async function handleSubmit(event) {
    event.preventDefault();

    setShipment(null);
    setEta(null);
    setError("");
    setEtaError("");

    try {
      setLoading(true);

      // Step 1: Get shipment using tracking number
      const shipmentResponse =
        await getShipmentByTrackingNumber(
          trackingNumber.trim()
        );

      console.log(
        "Shipment returned by tracking API:",
        shipmentResponse
      );

      console.log(
        "Shipment ID used for ETA:",
        shipmentResponse.id
      );

      setShipment(shipmentResponse);

      // Step 2: Get ETA using shipment ID
      try {
        setEtaLoading(true);

        console.log(
          "Requesting ETA for shipment ID:",
          shipmentResponse.id
        );

        const etaResponse =
          await getShipmentEta(
            shipmentResponse.id
          );

        console.log(
          "ETA response:",
          etaResponse
        );

        setEta(etaResponse);
      } catch (etaRequestError) {
        console.error(
          "ETA request failed:",
          etaRequestError
        );

        if (
          etaRequestError.response?.status === 404
        ) {
          setEtaError(
            "ETA is not available yet. At least two location updates are required to calculate the live ETA."
          );
        } else {
          setEtaError(
            "Unable to load ETA information."
          );
        }
      } finally {
        setEtaLoading(false);
      }
    } catch (shipmentRequestError) {
      console.error(
        "Error tracking shipment:",
        shipmentRequestError
      );

      setError(
        shipmentRequestError.response?.data?.message ||
        "Shipment not found. Please check the tracking number."
      );
    } finally {
      setLoading(false);
    }
  }

  // Auto-refresh ETA every 10 seconds
  useEffect(() => {
    if (!shipment?.id) {
      return;
    }

    const intervalId = setInterval(async () => {
      try {
        setEtaLoading(true);

        const etaResponse =
          await getShipmentEta(shipment.id);

        setEta(etaResponse);
        setEtaError("");
      } catch (refreshError) {
        console.log(
          "Auto-refresh ETA failed:",
          refreshError
        );

        if (
          refreshError.response?.status === 404
        ) {
          setEta(null);
          setEtaError(
            "ETA is not available yet. At least two location updates are required to calculate the live ETA."
          );
        }
      } finally {
        setEtaLoading(false);
      }
    }, 10000);

    return () => {
      clearInterval(intervalId);
    };
  }, [shipment?.id]);

  function formatDate(dateValue) {
    if (!dateValue) {
      return "Not available";
    }

    return new Date(
      dateValue
    ).toLocaleString();
  }

  function formatNumber(value) {
    if (
      value === null ||
      value === undefined
    ) {
      return "Not available";
    }

    return Number(value).toFixed(2);
  }

  return (
    <>
      <Navbar />

      <main className="dashboard-container">
        <h1>Track Shipment</h1>

        <p className="dashboard-subtitle">
          Enter your tracking number to view the latest
          shipment details and live ETA.
        </p>

        <div className="form-card">
          <form onSubmit={handleSubmit}>
            <label>Tracking Number</label>

            <input
              type="text"
              placeholder="Example: SHP-96C5371628E9"
              value={trackingNumber}
              onChange={(event) =>
                setTrackingNumber(
                  event.target.value
                )
              }
              required
            />

            <button
              type="submit"
              className="login-button"
              disabled={loading}
            >
              {loading
                ? "Tracking..."
                : "Track Shipment"}
            </button>
          </form>

          {error && (
            <div className="error-message">
              {error}
            </div>
          )}

          {shipment && (
            <>
              {/* Shipment Details */}
              <div className="shipment-result">
                <h2>Shipment Details</h2>

                <p>
                  <strong>Tracking Number:</strong>{" "}
                  {shipment.trackingNumber}
                </p>

                <p>
                  <strong>Shipment ID:</strong>{" "}
                  {shipment.id}
                </p>

                <p>
                  <strong>Origin:</strong>{" "}
                  {shipment.origin}
                </p>

                <p>
                  <strong>Destination:</strong>{" "}
                  {shipment.destination}
                </p>

                <p>
                  <strong>Current Status:</strong>{" "}
                  {shipment.status}
                </p>

                <p>
                  <strong>Estimated Delivery:</strong>{" "}
                  {formatDate(
                    shipment.estimatedDelivery
                  )}
                </p>

                <p>
                  <strong>Created At:</strong>{" "}
                  {formatDate(
                    shipment.createdAt
                  )}
                </p>

                <p>
                  <strong>Last Updated:</strong>{" "}
                  {formatDate(
                    shipment.updatedAt
                  )}
                </p>
              </div>

              {/* Live ETA Details */}
              <div className="shipment-result eta-section">
                <div className="eta-header">
                  <h2>Live ETA Information</h2>

                  {eta && (
                    <span className="live-badge">
                      <span className="live-dot"></span>
                      LIVE
                    </span>
                  )}
                </div>

                {etaLoading && !eta && (
                  <div className="eta-loading">
                    Calculating live ETA...
                  </div>
                )}

                {etaError && !eta && (
                  <div className="error-message">
                    {etaError}
                  </div>
                )}

                {eta && (
                  <>
                    {etaLoading && (
                      <p className="refreshing-text">
                        Refreshing live ETA...
                      </p>
                    )}

                    <div className="eta-grid">
                      <div className="eta-card">
                        <span className="eta-icon">
                          📍
                        </span>

                        <div>
                          <p className="eta-label">
                            Current Location
                          </p>

                          <h3>
                            {eta.currentLocation ||
                              "Not available"}
                          </h3>
                        </div>
                      </div>

                      <div className="eta-card">
                        <span className="eta-icon">
                          🕒
                        </span>

                        <div>
                          <p className="eta-label">
                            Estimated Arrival
                          </p>

                          <h3>
                            {formatDate(
                              eta.estimatedArrival
                            )}
                          </h3>
                        </div>
                      </div>

                      <div className="eta-card">
                        <span className="eta-icon">
                          🛣️
                        </span>

                        <div>
                          <p className="eta-label">
                            Remaining Distance
                          </p>

                          <h3>
                            {formatNumber(
                              eta.remainingDistanceKm
                            )} km
                          </h3>
                        </div>
                      </div>

                      <div className="eta-card">
                        <span className="eta-icon">
                          🚚
                        </span>

                        <div>
                          <p className="eta-label">
                            Average Speed
                          </p>

                          <h3>
                            {formatNumber(
                              eta.averageSpeedKmh
                            )} km/h
                          </h3>
                        </div>
                      </div>

                      <div className="eta-card">
                        <span className="eta-icon">
                          ⏱️
                        </span>

                        <div>
                          <p className="eta-label">
                            Delay
                          </p>

                          <h3>
                            {formatNumber(
                              eta.delayMinutes
                            )} minutes
                          </h3>
                        </div>
                      </div>
                    </div>
                  </>
                )}
              </div>
            </>
          )}
        </div>
      </main>
    </>
  );
}

export default TrackShipment;