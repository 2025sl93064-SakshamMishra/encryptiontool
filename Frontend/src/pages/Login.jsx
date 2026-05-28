import { useState } from "react";
import { Link, useNavigate } from "react-router-dom";
import API from "../api/axios";
import { useAuth } from "../context/AuthContext";
import Loader from "../components/Loader";

function Login() {
  const [email, setEmail] = useState("");
  const [password, setPassword] = useState("");
  const [error, setError] = useState("");
  const [loading, setLoading] = useState(false);
  const { login } = useAuth();
  const navigate = useNavigate();

  const handleLogin = async (e) => {
    e.preventDefault();
    setError("");
    setLoading(true);
    try {
      const response = await API.post("/auth/login", { email, password });
      if (!response.data.token) {
        setError(response.data.message || "Login failed.");
        return;
      }
      login(response.data.token);
      navigate("/dashboard");
    } catch (err) {
      console.error("Login error:", err);
      if (!err.response) {
        setError(`Network error: ${err.message}. Is the backend running on port 8333?`);
      } else {
        const data = err.response?.data;
        const msg = typeof data === "string" ? data : data?.message || data?.error || JSON.stringify(data);
        setError(msg || `Server error ${err.response.status}`);
      }
    } finally {
      setLoading(false);
    }
  };

  return (
    <div className="auth-page">
      {loading && <Loader />}
      <div className="auth-card">
        <div className="auth-header">
          <span className="auth-icon">🔐</span>
          <h2>Welcome Back</h2>
          <p>Sign in to your CipherVault account</p>
        </div>
        <form onSubmit={handleLogin} className="auth-form">
          <div className="form-group">
            <label>Email</label>
            <input
              type="email"
              placeholder="you@example.com"
              value={email}
              onChange={(e) => setEmail(e.target.value)}
              required
            />
          </div>
          <div className="form-group">
            <label>Password</label>
            <input
              type="password"
              placeholder="Enter your password"
              value={password}
              onChange={(e) => setPassword(e.target.value)}
              required
            />
          </div>
          {error && <div className="alert alert-error">{error}</div>}
          <button type="submit" className="btn btn-primary btn-full" disabled={loading}>
            {loading ? "Signing in…" : "Sign In"}
          </button>
        </form>
        <p className="auth-footer">
          Don&apos;t have an account? <Link to="/register">Register</Link>
        </p>
      </div>
    </div>
  );
}

export default Login;
