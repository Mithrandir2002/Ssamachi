import { defineConfig } from "vite";
import react from "@vitejs/plugin-react";
import tailwindcss from "@tailwindcss/vite";

// https://vite.dev/config/
export default defineConfig({
  plugins: [react(), tailwindcss()],
  server: {
    port: 3000,
    // Calls go out same-origin and Vite forwards them, so the browser never
    // makes a cross-origin request and no CORS config is needed on the backends.
    // Order matters: the more specific /api/auth prefix must come first.
    proxy: {
      "/api/auth": {
        target: "http://localhost:8081",
        changeOrigin: true,
      },
      "/api": {
        target: "http://localhost:8082",
        changeOrigin: true,
      },
    },
  },
});
