import api from "./axios";
import type {
  AuthenticationRequest,
  AuthenticationResponse,
  RegisterRequest,
} from "../types";

export const login = (data: AuthenticationRequest) =>
  api.post<AuthenticationResponse>("/auth/authenticate", data);

export const register = (data: RegisterRequest) =>
  api.post<AuthenticationResponse>("/auth/register", data);

export const logout = () => api.post("/auth/logout");

export const validateSession = () =>
  api.get("/checklists", { params: { page: 0, size: 1 } });
