import { createContext, useState } from "react";
import { getToken } from "../services/auth";
import { login as saveToken } from "../services/auth";
import { logout as removeToken } from "../services/auth";

const AuthContext = createContext();

export function AuthProvider({ children }) {
  const [token, setToken] = useState(getToken());
  const isAuthenticated = !!token;
  
  function login(newToken) {
    setToken(newToken);
    saveToken(newToken);
  }
  
  function logout() {
    setToken(null);
    removeToken();
  }
  
  return (
    <AuthContext.Provider value={{ token, login, logout, isAuthenticated }}>
      {children}
    </AuthContext.Provider>
  );
}

export default AuthContext;