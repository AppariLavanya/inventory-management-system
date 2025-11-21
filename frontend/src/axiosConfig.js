// src/services/api.js
// Central axios instance – attaches JWT token & handles 401 globally

import axios from "axios";

// ---------------------------------------------
// 🔧 BASE URL (supports .env overrides)
// ---------------------------------------------
const API_BASE =
  process.env.REACT_APP_API_BASE || "http://localhost:8080/api";

const api = axios.create({
  baseURL: API_BASE,
  timeout: 15000,
  headers: {
    "Content-Type": "application/json",
  },
});

// ---------------------------------------------
// 🔐 REQUEST INTERCEPTOR – Attach JWT Token
// ---------------------------------------------
api.interceptors.request.use(
  (config) => {
    const token = localStorage.getItem("token");

    if (token) {
      config.headers = config.headers || {};
      config.headers["Authorization"] = `Bearer ${token}`;
    }

    return config;
  },
  (error) => Promise.reject(error)
);

// ---------------------------------------------
// 🚫 RESPONSE INTERCEPTOR – Global Error Handler
// ---------------------------------------------
api.interceptors.response.use(
  (response) => response,
  (error) => {
    const status = error?.response?.status;

    // Token expired or invalid → remove token
    if (status === 401) {
      localStorage.removeItem("token");
      // ProtectedRoute will auto-redirect to login
    }

    return Promise.reject(error);
  }
);

export default api;





