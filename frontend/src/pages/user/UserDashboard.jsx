import Navbar from "../../components/Navbar";

function UserDashboard() {
  return (
    <>
      <Navbar />

      <main className="dashboard-container">
        <h1>Welcome, User</h1>

        <p className="dashboard-subtitle">
          Manage and track your shipments from one place.
        </p>

        <div className="dashboard-grid">
          <div className="dashboard-card">
            <h2>Create Shipment</h2>
            <p>Create a new shipment with origin and destination.</p>
          </div>

          <div className="dashboard-card">
            <h2>My Shipments</h2>
            <p>View all your created shipments and their status.</p>
          </div>

          <div className="dashboard-card">
            <h2>Track Shipment</h2>
            <p>Track your shipment and view its latest location.</p>
          </div>

          <div className="dashboard-card">
            <h2>Shipment History</h2>
            <p>View shipment status history and estimated arrival.</p>
          </div>
        </div>
      </main>
    </>
  );
}

export default UserDashboard;