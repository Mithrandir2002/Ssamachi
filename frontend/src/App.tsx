import { BrowserRouter, Link, useLocation } from "react-router-dom";
import AppRouter from "./routes/AppRouter";

const AUTH_ROUTES = ["/login", "/register"];

export default function App() {
  return (
    <BrowserRouter>
      <Shell />
    </BrowserRouter>
  );
}

function Shell() {
  const { pathname } = useLocation();
  // Auth screens bring their own header and own the full viewport.
  const hideNav = AUTH_ROUTES.includes(pathname);

  return (
    <>
      {/* TODO: only show nav when authenticated */}
      {!hideNav && (
        <nav>
          <ul>
            <li>
              <Link to="/dashboard">Dashboard</Link>
            </li>
            <li>
              <Link to="/alerts">Alerts</Link>
            </li>
            <li>
              <Link to="/reports">Reports</Link>
            </li>
            <li>
              <Link to="/admin">Admin</Link>
            </li>
            <li>
              <Link to="/login">Login</Link>
            </li>
            <li>
              <Link to="/register">Register</Link>
            </li>
          </ul>
        </nav>
      )}
      <main>
        <AppRouter />
      </main>
    </>
  );
}
