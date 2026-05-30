import { createContext, useContext, useState } from "react";

const AuthContext = createContext(null);

function decodeEmail(jwt) {
  try {
    const payload = JSON.parse(atob(jwt.split(".")[1]));
    return payload.sub || payload.email || "";
  } catch {
    return "";
  }
}

export function AuthProvider({ children }) {
  const [token, setToken] = useState(() => localStorage.getItem("token"));

  const login = (jwt) => {
    localStorage.setItem("token", jwt);
    setToken(jwt);
  };

  const logout = () => {
    localStorage.removeItem("token");
    setToken(null);
  };

  const userEmail = token ? decodeEmail(token) : "";

  return (
    <AuthContext.Provider value={{ token, isAuthenticated: !!token, login, logout, userEmail }}>
      {children}
    </AuthContext.Provider>
  );
}

export function useAuth() {
  return useContext(AuthContext);
}
