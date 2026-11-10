import { useState, type ReactNode } from "react";
import { AuthContext } from "./AuthContext";
import type { PageUserType } from "./PageUserType";

type PanelProps = { children: ReactNode };

function AuthProvider({ children }: PanelProps) {
  const [user, setUser] = useState<PageUserType | null>(null);

  function login(name: string) {
    setUser(() => ({ name: name }));
  }

  function logout() {
    setUser(() => null);
  }

  return (
    <>
      <AuthContext
        value={{
          user: user,
          login: login,
          logout: logout,
        }}
      >
        {children}
      </AuthContext>
    </>
  );
}

export default AuthProvider;
