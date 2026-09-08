import axios from "axios";

const coreClient = axios.create({
  baseURL: import.meta.env.VITE_CORE_API_URL,
});

// TODO: request interceptor to attach Authorization: Bearer token
// TODO: response interceptor to refresh on 401

export default coreClient;
