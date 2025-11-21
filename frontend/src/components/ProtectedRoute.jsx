import React from "react";
import { Navigate, useLocation } from "react-router-dom";

const ProtectedRoute = ({ children }) => {
  const token = localStorage.getItem("token");
  const location = useLocation();

  const publicExports = [
    "/api/products/export/csv",
    "/api/products/export/excel",
    "/api/products/export/pdf",
    "/api/analytics/export/pdf",
    "/api/analytics/export/excel"
  ];

  // Allow export operations without token
  if (publicExports.some((path) => location.pathname.includes(path))) {
    return children;
  }

  // No token → redirect
  if (!token) {
    return <Navigate to="/login" replace />;
  }

  try {
    // Safely decode JWT
    const base64 = token.split(".")[1];

    if (!base64) {
      localStorage.removeItem("token");
      return <Navigate to="/login" replace />;
    }

    const payload = JSON.parse(atob(base64));
    const isExpired = payload.exp * 1000 < Date.now();

    if (isExpired) {
      localStorage.removeItem("token");
      return <Navigate to="/login" replace />;
    }
  } catch (err) {
    // Token corrupted
    localStorage.removeItem("token");
    return <Navigate to="/login" replace />;
  }

  return children;
};

export default ProtectedRoute;





