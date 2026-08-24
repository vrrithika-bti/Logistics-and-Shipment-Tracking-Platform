import { useState } from "react";
import { useNavigate } from "react-router-dom";

import {
  loginUser,
  loginAdmin,
} from "../services/authService";

import "./LoginPage.css";

function LoginPage() {
  const [role, setRole] = useState("USER");
  const [customerId, setCustomerId] = useState("");
  const [error, setError] = useState("");

  const navigate = useNavigate();

  function handleLogin(event) {
    event.preventDefault();

    setError("");

    if (role === "USER") {
      if (!customerId.trim()) {
        setError("Please enter your Customer ID.");
        return;
      }

      loginUser(customerId.trim());
      navigate("/user/dashboard");
      return; 
    }

    if (role === "ADMIN") {
      loginAdmin();
      navigate("/admin/dashboard");
    }
  }

  return (
    <main className="login-page">
      <div className="background-overlay" />

      <section className="login-wrapper">
        <div className="login-card">
          {/* Logo */}
          <div className="login-logo">
            <span>🚚</span>
          </div>

          <h1>Logistics Platform</h1>

          <p className="login-subtitle">
            Login to manage and track your shipments.
          </p>

          <div className="login-divider">
            <span>◇</span>
          </div>

          <form onSubmit={handleLogin}>
            {/* Role */}
            <div className="input-group">
              <label htmlFor="role">
                Login As
              </label>

              <div className="select-wrapper">
                <span className="input-icon">
                  👤
                </span>

                <select
                  id="role"
                  value={role}
                  onChange={(event) =>
                    setRole(event.target.value)
                  }
                >
                  <option value="USER">
                    User
                  </option>

                  <option value="ADMIN">
                    Admin
                  </option>
                </select>
              </div>
            </div>

            {/* Customer ID only for User */}
            {role === "USER" && (
              <div className="input-group">
                <label htmlFor="customerId">
                  Customer ID
                </label>

                <div className="input-wrapper">
                  <span className="input-icon">
                    🪪
                  </span>

                  <input
                    id="customerId"
                    type="text"
                    placeholder="Enter your Customer UUID"
                    value={customerId}
                    onChange={(event) =>
                      setCustomerId(
                        event.target.value
                      )
                    }
                  />
                </div>
              </div>
            )}

            {role === "ADMIN" && (
              <div className="admin-message">
                <span>🔐</span>
                <p>
                  Login as an administrator to manage
                  all shipments.
                </p>
              </div>
            )}

            {error && (
              <div className="login-error">
                {error}
              </div>
            )}

            <button
              type="submit"
              className="modern-login-button"
            >
              <span>⇥</span>

              {role === "USER"
                ? "Login as User"
                : "Login as Admin"}
            </button>
          </form>

          {/* Features */}
          <div className="login-features">
            <div className="feature">
              <div className="feature-icon">
                🛡
              </div>

              <div>
                <h3>Secure</h3>
                <p>Your data is safe with us</p>
              </div>
            </div>

            <div className="feature-divider" />

            <div className="feature">
              <div className="feature-icon">
                🚚
              </div>

              <div>
                <h3>Real-time</h3>
                <p>Track shipments in real-time</p>
              </div>
            </div>

            <div className="feature-divider" />

            <div className="feature">
              <div className="feature-icon">
                ◔
              </div>

              <div>
                <h3>Reliable</h3>
                <p>Updates you can trust</p>
              </div>
            </div>
          </div>
        </div>

        <p className="login-footer">
          © 2026 Logistics Platform. All rights reserved.
        </p>
      </section>
    </main>
  );
}

export default LoginPage;