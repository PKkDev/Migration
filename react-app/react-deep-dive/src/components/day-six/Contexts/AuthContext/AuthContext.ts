import { createContext } from "react";
import type { PageUserType } from "./PageUserType";

export type AuthContextValue = {
  user: PageUserType | null;
  login(name: string): void;
  logout(): void;
};

export const AuthContext = createContext<AuthContextValue | null>(null);
