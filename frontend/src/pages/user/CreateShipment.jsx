import { useState } from "react";
import Navbar from "../../components/Navbar";
import { createShipment } from "../../services/shipmentService";
import { getCustomerId } from "../../services/authService";

const CITIES = [
  "Chennai",
  "Bengaluru",
  "Hyderabad",
  "Mumbai",
  "Pune",
  "Delhi",
  "Kolkata",
  "Coimbatore",
  "Kochi",
  "Visakhapatnam",
];

function CreateShipment() {
  const customerId = getCustomerId();
  const [origin, setOrigin] = useState("");
  const [destination, setDestination] = useState("");

  const [message, setMessage] = useState("");
  const [error, setError] = useState("");
  const [createdShipment, setCreatedShipment] = useState(null);
  const [loading, setLoading] = useState(false);

  async function handleSubmit(event) {
    event.preventDefault();

    setMessage("");
    setError("");
    setCreatedShipment(null);

    if (origin === destination) {
      setError("Origin and destination cannot be the same.");
      return;
    }

    try {
      setLoading(true);

      const shipment = await createShipment({
        customerId,
        origin,
        destination,
      });

      setCreatedShipment(shipment);
      setMessage("Shipment created successfully!");

      
      setOrigin("");
      setDestination("");
    } catch (error) {
      console.error("Error creating shipment:", error);

      if (error.response?.data?.errors) {
        const errors = error.response.data.errors;

        setError(
          Object.values(errors).join(", ")
        );
      } else {
        setError(
          error.response?.data?.message ||
          "Failed to create shipment. Please try again."
        );
      }
    } finally {
      setLoading(false);
    }
  }

  return (
    <>
      <Navbar />

      <main className="dashboard-container">
        <h1>Create Shipment</h1>

        <p className="dashboard-subtitle">
          Create a new shipment by selecting the origin and destination.
        </p>

        <div className="form-card">
          <form onSubmit={handleSubmit}>
            

            <label>Origin</label>

            <select
              value={origin}
              onChange={(event) =>
                setOrigin(event.target.value)
              }
              required
            >
              <option value="">
                Select origin
              </option>

              {CITIES.map((city) => (
                <option
                  key={city}
                  value={city}
                >
                  {city}
                </option>
              ))}
            </select>

            <label>Destination</label>

            <select
              value={destination}
              onChange={(event) =>
                setDestination(event.target.value)
              }
              required
            >
              <option value="">
                Select destination
              </option>

              {CITIES.map((city) => (
                <option
                  key={city}
                  value={city}
                >
                  {city}
                </option>
              ))}
            </select>

            <button
              type="submit"
              className="login-button"
              disabled={loading}
            >
              {loading
                ? "Creating Shipment..."
                : "Create Shipment"}
            </button>
          </form>

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

          {createdShipment && (
            <div className="shipment-result">
              <h2>Shipment Created</h2>

              <p>
                <strong>Tracking Number:</strong>{" "}
                {createdShipment.trackingNumber}
              </p>

              <p>
                <strong>Shipment ID:</strong>{" "}
                {createdShipment.id}
              </p>

              <p>
                <strong>Status:</strong>{" "}
                {createdShipment.status}
              </p>

              <p>
                <strong>Route:</strong>{" "}
                {createdShipment.origin} →{" "}
                {createdShipment.destination}
              </p>
            </div>
          )}
        </div>
      </main>
    </>
  );
}

export default CreateShipment;