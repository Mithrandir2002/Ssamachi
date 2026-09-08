import { create } from "zustand";
import type { AuthTokens, User } from "../types/user";

export interface AuthState {
  accessToken: string | null;
  refreshToken: string | null;
  user: User | null;
  login: (tokens: AuthTokens, user: User) => void;
  logout: () => void;
}

// TODO (decide together): whether to wire up zustand's `persist` middleware
// so accessToken/refreshToken/user survive a page refresh (localStorage vs.
// in-memory only). Left unimplemented on purpose.

export const useAuthStore = create<AuthState>((set) => ({
  accessToken: null,
  refreshToken: null,
  user: null,
  login: (_tokens, _user) => {
    // TODO: implement login — store tokens + user in state
  },
  logout: () => {
    // TODO: implement logout — clear tokens + user from state
  },
}));
