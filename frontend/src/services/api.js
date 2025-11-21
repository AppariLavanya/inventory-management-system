// src/services/api.js
import axios from "axios";

const api = axios.create({
  baseURL: "http://localhost:8080/api",   // ✅ Correct base URL for backend
  headers: {
    "Content-Type": "application/json"
  }
});

// =====================================================
// ✅ Attach JWT token for protected endpoints
//    (Export endpoints stay PUBLIC)
// =====================================================
api.interceptors.request.use(
  (config) => {
    const token = localStorage.getItem("token");

    // Allow these export endpoints without token
    const isPublicExport =
      config.url.includes("/products/export") ||
      config.url.includes("/orders/export") ||      
      config.url.includes("/analytics/export");

    if (!isPublicExport && token) {
      config.headers["Authorization"] = `Bearer ${token}`;
    }

    return config;
  },
  (error) => Promise.reject(error)
);

// =====================================================
// ✅ Auto logout on 401 (expired token)
// =====================================================
api.interceptors.response.use(
  (response) => response,
  (error) => {
    if (error.response?.status === 401) {
      localStorage.removeItem("token");
      localStorage.removeItem("email");
      window.location.replace("/login");
    }

    return Promise.reject(error);
  }
);

export default api;










