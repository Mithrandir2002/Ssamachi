import axios from "axios";

const authClient = axios.create({
  baseURL: import.meta.env.VITE_AUTH_API_URL,
});

// TODO: request interceptor to attach Authorization: Bearer token
// TODO: response interceptor to refresh on 401

export default authClient;
