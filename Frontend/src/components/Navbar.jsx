import { Link, useNavigate, useLocation } from "react-router-dom";
import { useAuth } from "../context/AuthContext";

const NAV_TABS = [
  { label: "Dashboard", section: null },
  { label: "Text", section: "text" },
  { label: "Email", section: "email" },
  { label: "Export", section: "export" },
  { label: "Reports", section: "reports" },
];

function Navbar() {
  const { isAuthenticated, logout, userEmail } = useAuth();
  const navigate = useNavigate();
  const location = useLocation();

  const handleLogout = () => {
    logout();
    navigate("/login");
  };

  const params = new URLSearchParams(location.search);
  const activeSection = params.get("section");

  const handleTab = (section) => {
    if (section === null) {
      navigate("/dashboard");
    } else {
      navigate(`/dashboard?section=${section}`);
    }
  };

  return (
    <nav className="navbar">
      <Link to={isAuthenticated ? "/dashboard" : "/"} className="navbar-brand">
        <span className="navbar-logo">🔐</span>
        <span>CipherVault</span>
      </Link>

      {isAuthenticated && location.pathname === "/dashboard" && (
        <div className="navbar-tabs">
          {NAV_TABS.map((t) => {
            const isActive =
              t.section === null
                ? !activeSection
                : activeSection === t.section;
            return (
              <button
                key={t.label}
                className={`navbar-tab${isActive ? " active" : ""}`}
                onClick={() => handleTab(t.section)}
              >
                {t.label}
              </button>
            );
          })}
        </div>
      )}

      <div className="navbar-actions">
        {isAuthenticated ? (
          <>
            {userEmail && <span className="navbar-email">{userEmail}</span>}
            <button className="btn btn-outline" onClick={handleLogout}>
              Logout
            </button>
          </>
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
