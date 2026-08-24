import { Link, useNavigate } from "react-router-dom";
import {
  getLoggedInUser,
  logout,
} from "../services/authService";

function Navbar() {
  const navigate = useNavigate();
  const user = getLoggedInUser();

  function handleLogout() {
    logout();
    navigate("/");
  }

  return (
    <nav className="navbar">
      <div className="navbar-brand">
        Logistics Platform
      </div>

      <div className="navbar-links">
        {user && user.role === "USER" && (
          <>
            <Link to="/user/dashboard">
              Dashboard
            </Link>

            <Link to="/user/create-shipment">
              Create Shipment
            </Link>

            <Link to="/user/shipments">
              My Shipments
            </Link>

            <Link to="/user/track">
              Track Shipment
            </Link>
          </>
        )}

        {user && user.role === "ADMIN" && (
          <Link to="/admin/dashboard">
            Admin Dashboard
          </Link>
        )}

        <button
          type="button"
          onClick={handleLogout}
          className="logout-button"
        >
          Logout
        </button>
      </div>
    </nav>
  );
}

export default Navbar;