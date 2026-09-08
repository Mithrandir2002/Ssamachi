import { Outlet } from "react-router-dom";

export default function ProtectedRoute() {
  // TODO: check authStore and redirect to /login if not authenticated
  return <Outlet />;
}
