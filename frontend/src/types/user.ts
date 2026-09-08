export type Role = "USER" | "ADMIN";

export interface User {
  id: string;
  username: string;
  email: string;
  roles: Role[];
}

export interface AuthTokens {
  accessToken: string;
  refreshToken: string;
}

export interface LoginRequest {
  username: string;
  password: string;
}

export interface RegisterRequest {
  username: string;
  email: string;
  password: string;
}
