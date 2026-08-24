import {
  BrowserRouter,
  Routes,
  Route,
} from "react-router-dom";

import LoginPage from "./pages/LoginPage";

import UserDashboard from "./pages/user/UserDashboard";
import CreateShipment from "./pages/user/CreateShipment";
import MyShipments from "./pages/user/MyShipments";
import TrackShipment from "./pages/user/TrackShipment";

import AdminDashboard from "./pages/admin/AdminDashboard";

import ProtectedRoute from "./components/ProtectedRoute";

function App() {
  return (
    <BrowserRouter>
      <Routes>
        {/* Login */}
        <Route
          path="/"
          element={<LoginPage />}
        />

        {/* User Routes */}
        <Route
          path="/user/dashboard"
          element={
            <ProtectedRoute allowedRole="USER">
              <UserDashboard />
            </ProtectedRoute>
          }
        />

        <Route
          path="/user/create-shipment"
          element={
            <ProtectedRoute allowedRole="USER">
              <CreateShipment />
            </ProtectedRoute>
          }
        />

        <Route
          path="/user/shipments"
          element={
            <ProtectedRoute allowedRole="USER">
              <MyShipments />
            </ProtectedRoute>
          }
        />

        <Route
          path="/user/track"
          element={
            <ProtectedRoute allowedRole="USER">
              <TrackShipment />
            </ProtectedRoute>
          }
        />

        {/* Admin Routes */}
        <Route
          path="/admin/dashboard"
          element={
            <ProtectedRoute allowedRole="ADMIN">
              <AdminDashboard />
            </ProtectedRoute>
          }
        />

        {/* Unknown URL */}
        <Route
          path="*"
          element={<LoginPage />}
        />
      </Routes>
    </BrowserRouter>
  );
}

export default App;