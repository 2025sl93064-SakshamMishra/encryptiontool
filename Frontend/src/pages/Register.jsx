import { useState } from "react";
import { Link, useNavigate } from "react-router-dom";
import API from "../api/axios";
import Loader from "../components/Loader";

function Register() {
  const [step, setStep] = useState("signup"); // "signup" | "otp"
  const [form, setForm] = useState({ name: "", email: "", password: "", confirm: "" });
  const [otp, setOtp] = useState("");
  const [error, setError] = useState("");
  const [info, setInfo] = useState("");
  const [loading, setLoading] = useState(false);
  const navigate = useNavigate();

  const handleChange = (e) => setForm({ ...form, [e.target.name]: e.target.value });

  const handleSignup = async (e) => {
    e.preventDefault();
    setError("");
    if (form.password !== form.confirm) {
      setError("Passwords do not match.");
      return;
    }
    setLoading(true);
    try {
      await API.post("/auth/signup", {
        name: form.name,
        email: form.email,
        password: form.password,
      });
      setInfo(`A 6-digit OTP has been sent to ${form.email}`);
      setStep("otp");
    } catch (err) {
      console.error(err);
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

  const handleVerifyOtp = async (e) => {
    e.preventDefault();
    setError("");
    setLoading(true);
    try {
      await API.post("/auth/verify-otp", { email: form.email, otp });
      navigate("/login");
    } catch (err) {
      console.error(err);
      if (!err.response) {
        setError(`Network error: ${err.message}.`);
      } else {
        const data = err.response?.data;
        const msg = typeof data === "string" ? data : data?.message || data?.error || JSON.stringify(data);
        setError(msg || "Invalid OTP. Please try again.");
      }
    } finally {
      setLoading(false);
    }
  };

  const handleResendOtp = async () => {
    setError(""); setInfo("");
    setLoading(true);
    try {
      await API.post("/auth/resend-otp", { email: form.email });
      setInfo("OTP resent! Check your email.");
    } catch (err) {
      setError("Failed to resend OTP.");
    } finally {
      setLoading(false);
    }
  };

  return (
    <div className="auth-page">
      {loading && <Loader />}
      <div className="auth-card">

        {step === "signup" && (
          <>
            <div className="auth-header">
              <span className="auth-icon">🛡️</span>
              <h2>Create Account</h2>
              <p>Join CipherVault to secure your data</p>
            </div>
            <form onSubmit={handleSignup} className="auth-form">
              <div className="form-group">
                <label>Full Name</label>
                <input
                  type="text"
                  name="name"
                  placeholder="John Doe"
                  value={form.name}
                  onChange={handleChange}
                  required
                />
              </div>
              <div className="form-group">
                <label>Email</label>
                <input
                  type="email"
                  name="email"
                  placeholder="you@example.com"
                  value={form.email}
                  onChange={handleChange}
                  required
                />
              </div>
              <div className="form-group">
                <label>Password</label>
                <input
                  type="password"
                  name="password"
                  placeholder="Create a strong password"
                  value={form.password}
                  onChange={handleChange}
                  required
                />
              </div>
              <div className="form-group">
                <label>Confirm Password</label>
                <input
                  type="password"
                  name="confirm"
                  placeholder="Repeat your password"
                  value={form.confirm}
                  onChange={handleChange}
                  required
                />
              </div>
              {error && <div className="alert alert-error">{error}</div>}
              <button type="submit" className="btn btn-primary btn-full" disabled={loading}>
                {loading ? "Creating account…" : "Create Account"}
              </button>
            </form>
            <p className="auth-footer">
              Already have an account? <Link to="/login">Sign In</Link>
            </p>
          </>
        )}

        {step === "otp" && (
          <>
            <div className="auth-header">
              <span className="auth-icon">📧</span>
              <h2>Verify Your Email</h2>
              <p>Enter the 6-digit code sent to<br /><strong style={{ color: "var(--text-h)" }}>{form.email}</strong></p>
            </div>
            {info && <div className="alert alert-success">{info}</div>}
            <form onSubmit={handleVerifyOtp} className="auth-form">
              <div className="form-group">
                <label>OTP Code</label>
                <input
                  type="text"
                  placeholder="Enter 6-digit code"
                  value={otp}
                  onChange={(e) => setOtp(e.target.value)}
                  maxLength={6}
                  required
                  className="otp-input"
                />
              </div>
              {error && <div className="alert alert-error">{error}</div>}
              <button type="submit" className="btn btn-primary btn-full" disabled={loading}>
                {loading ? "Verifying…" : "Verify & Activate"}
              </button>
            </form>
            <p className="auth-footer">
              Didn&apos;t receive the code?{" "}
              <button className="btn-link" onClick={handleResendOtp} disabled={loading}>
                Resend OTP
              </button>
            </p>
            <p className="auth-footer" style={{ marginTop: 8 }}>
              <button className="btn-link" onClick={() => { setStep("signup"); setError(""); setInfo(""); }}>
                ← Back to signup
              </button>
            </p>
          </>
        )}

      </div>
    </div>
  );
}

export default Register;
