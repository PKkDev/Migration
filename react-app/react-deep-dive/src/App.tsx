import { NavLink, Outlet, type NavLinkRenderProps } from "react-router";
import styles from "./App.module.css";
import AuthProvider from "./components/day-six/Contexts/AuthContext/AuthProvider";
import NotifProvider from "./components/day-six/Contexts/NotifContext/NotifProvider";

function activeClass(param: NavLinkRenderProps) {
  return param.isActive ? styles.active : param.isPending ? styles.pending : "";
}

function App() {
  return (
    <>
      <nav className={styles.nav}>
        <NavLink to="/" className={activeClass} end>
          Текущий
        </NavLink>
        <NavLink to="/day-one" className={activeClass} end>
          День 1
        </NavLink>
        <NavLink to="/day-two" className={activeClass} end>
          День 2
        </NavLink>
        <NavLink to="/day-three" className={activeClass} end>
          День 3
        </NavLink>
        <NavLink to="/day-four" className={activeClass} end>
          День 4
        </NavLink>
        <NavLink to="/day-five" className={activeClass} end>
          День 5
        </NavLink>
        <NavLink to="/day-six" className={activeClass} end>
          День 6
        </NavLink>
        <NavLink to="/day-seven" className={activeClass} end>
          День 7
        </NavLink>
      </nav>

      <h2>MAIN</h2>

      <AuthProvider>
        <NotifProvider>
          <Outlet />
        </NotifProvider>
      </AuthProvider>
    </>
  );
}

export default App;
