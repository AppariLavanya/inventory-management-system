// src/services/authService.js
import api from "./api"; // corrected clean import

// ----------------------------------------------------
// 🔐 LOGIN USER
// ----------------------------------------------------
export const login = async (email, password) => {
  const res = await api.post("/auth/login", { email, password });

  const token = res.data?.token;
  const returnedEmail = res.data?.email;

  if (token) {
    localStorage.setItem("token", token);
  }
  if (returnedEmail) {
    localStorage.setItem("email", returnedEmail);
  }

  return res.data;
};

// ----------------------------------------------------
// 🔐 LOGOUT USER
// ----------------------------------------------------
export const logout = () => {
  localStorage.removeItem("token");
  localStorage.removeItem("email");
};

// ----------------------------------------------------
// 🔍 CHECK IF USER IS LOGGED IN (token + validity)
// ----------------------------------------------------
export const isAuthenticated = () => {
  const token = localStorage.getItem("token");
  if (!token) return false;

  try {
    const payload = JSON.parse(atob(token.split(".")[1]));
    const expired = payload.exp * 1000 < Date.now();
    if (expired) {
      logout();
      return false;
    }
    return true;
  } catch {
    logout();
    return false;
  }
};





