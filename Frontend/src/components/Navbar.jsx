import { Link, useNavigate } from "react-router-dom";
import { useAuth } from "../context/AuthContext";

function Navbar() {
  const { isAuthenticated, logout } = useAuth();
  const navigate = useNavigate();

  const handleLogout = () => {
    logout();
    navigate("/login");
  };

  return (
    <nav className="navbar">
      <Link to={isAuthenticated ? "/dashboard" : "/"} className="navbar-brand">
        <span className="navbar-logo">🔐</span>
        <span>CipherVault</span>
      </Link>
      <div className="navbar-actions">
        {isAuthenticated ? (
          <button className="btn btn-outline" onClick={handleLogout}>
            Logout
          </button>
        ) : (
          <>
            <Link to="/login" className="btn btn-ghost">Login</Link>
            <Link to="/register" className="btn btn-primary">Register</Link>
          </>
        )}
      </div>
    </nav>
  );
}

export default Navbar;
