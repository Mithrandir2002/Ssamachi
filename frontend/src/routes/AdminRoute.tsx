import { Outlet } from "react-router-dom";

export default function AdminRoute() {
  // TODO: check authStore and redirect if not authenticated or not ADMIN role
  return <Outlet />;
}
