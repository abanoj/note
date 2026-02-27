import {
  createContext,
  useContext,
  useState,
  useCallback,
  type ReactNode,
} from "react";
import * as authApi from "../api/auth";
import type { AuthenticationRequest, RegisterRequest } from "../types";

interface AuthContextType {
  isAuthenticated: boolean;
  loginUser: (data: AuthenticationRequest) => Promise<void>;
  registerUser: (data: RegisterRequest) => Promise<void>;
  logoutUser: () => Promise<void>;
}

const AuthContext = createContext<AuthContextType | null>(null);

export function AuthProvider({ children }: { children: ReactNode }) {
  const [isAuthenticated, setIsAuthenticated] = useState(
    () => !!localStorage.getItem("access_token")
  );

  const loginUser = useCallback(async (data: AuthenticationRequest) => {
    const res = await authApi.login(data);
    localStorage.setItem("access_token", res.data.access_token);
    localStorage.setItem("refresh_token", res.data.refresh_token);
    setIsAuthenticated(true);
  }, []);

  const registerUser = useCallback(async (data: RegisterRequest) => {
    const res = await authApi.register(data);
    localStorage.setItem("access_token", res.data.access_token);
    localStorage.setItem("refresh_token", res.data.refresh_token);
    setIsAuthenticated(true);
  }, []);

  const logoutUser = useCallback(async () => {
    try {
      await authApi.logout();
    } finally {
      localStorage.removeItem("access_token");
      localStorage.removeItem("refresh_token");
      setIsAuthenticated(false);
    }
  }, []);

  return (
    <AuthContext.Provider
      value={{ isAuthenticated, loginUser, registerUser, logoutUser }}
    >
      {children}
    </AuthContext.Provider>
  );
}

export function useAuth() {
  const context = useContext(AuthContext);
  if (!context) {
    throw new Error("useAuth must be used within an AuthProvider");
  }
  return context;
}
