import { BrowserRouter, Link } from "react-router-dom";
import AppRouter from "./routes/AppRouter";

export default function App() {
  return (
    <BrowserRouter>
      {/* TODO: only show nav when authenticated */}
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
      <main>
        <AppRouter />
      </main>
    </BrowserRouter>
  );
}
