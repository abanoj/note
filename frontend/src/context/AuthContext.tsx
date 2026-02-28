import {
  createContext,
  useContext,
  useState,
  useEffect,
  useCallback,
  type ReactNode,
} from "react";
import { Loader2 } from "lucide-react";
import * as authApi from "../api/auth";
import type { AuthenticationRequest, RegisterRequest } from "../types";

interface AuthContextType {
  isAuthenticated: boolean;
  loading: boolean;
  loginUser: (data: AuthenticationRequest) => Promise<void>;
  registerUser: (data: RegisterRequest) => Promise<void>;
  logoutUser: () => Promise<void>;
}

const AuthContext = createContext<AuthContextType | null>(null);

export function AuthProvider({ children }: { children: ReactNode }) {
  const [isAuthenticated, setIsAuthenticated] = useState(false);
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    const token = localStorage.getItem("access_token");
    if (!token) {
      setLoading(false);
      return;
    }
    authApi
      .validateSession()
      .then(() => setIsAuthenticated(true))
      .catch(() => {
        localStorage.removeItem("access_token");
        localStorage.removeItem("refresh_token");
      })
      .finally(() => setLoading(false));
  }, []);

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

  if (loading) {
    return (
      <div className="min-h-screen flex items-center justify-center bg-gray-50">
        <Loader2 className="w-8 h-8 animate-spin text-violet-600" />
      </div>
    );
  }

  return (
    <AuthContext.Provider
      value={{ isAuthenticated, loading, loginUser, registerUser, logoutUser }}
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
