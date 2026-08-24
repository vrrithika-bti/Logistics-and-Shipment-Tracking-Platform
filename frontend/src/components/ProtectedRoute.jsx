import { Navigate } from "react-router-dom";
import { getLoggedInUser } from "../services/authService";

function ProtectedRoute({ children, allowedRole }) {
  const user = getLoggedInUser();

  // User is not logged in
  if (!user) {
    return <Navigate to="/" replace />;
  }

  // User does not have permission
  if (user.role !== allowedRole) {
    if (user.role === "ADMIN") {
      return (
        <Navigate
          to="/admin/dashboard"
          replace
        />
      );
    }

    return (
      <Navigate
        to="/user/dashboard"
        replace
      />
    );
  }

  return children;
}

export default ProtectedRoute;