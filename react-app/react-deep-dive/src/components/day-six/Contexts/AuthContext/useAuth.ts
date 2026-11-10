import { useContext } from "react";
import { MissingContextError } from "../MissingContextError";
import { AuthContext, type AuthContextValue } from "./AuthContext";

export const useAuth = (): AuthContextValue => {
  const authContext = useContext(AuthContext);
  if (authContext === null) {
    throw new MissingContextError("useAuth", "AuthProvider");
  }
  return authContext;
};
