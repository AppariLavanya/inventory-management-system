import React, { useState } from "react";
import {
  Box,
  Paper,
  TextField,
  Button,
  Typography,
  Alert,
} from "@mui/material";
import { useNavigate } from "react-router-dom";
import { login } from "../services/AuthService";

export default function Login() {
  const [email, setEmail] = useState("");
  const [password, setPassword] = useState("");
  const [error, setError] = useState("");

  const navigate = useNavigate();

  const handleLogin = async () => {
    setError("");

    if (!email || !password) {
      setError("Email and password are required.");
      return;
    }

    try {
      const resp = await login(email, password);

      // Save token + email
      localStorage.setItem("token", resp.token);
      localStorage.setItem("email", resp.email);

      navigate("/products");
    } catch (e) {
      setError(
        e.response?.data?.message ||
        "Invalid email or password. Please try again."
      );
    }
  };

  return (
    <Box
      sx={{
        height: "100vh",
        display: "flex",
        justifyContent: "center",
        alignItems: "center",
        background: "linear-gradient(135deg, #6a11cb, #2575fc)",
        padding: 2,
      }}
    >
      <Paper
        elevation={6}
        sx={{
          p: 4,
          width: 380,
          borderRadius: 3,
        }}
      >
        <Typography
          variant="h5"
          sx={{ mb: 3, textAlign: "center", fontWeight: 700 }}
        >
          🔐 Admin Login
        </Typography>

        {error && (
          <Alert severity="error" sx={{ mb: 2 }}>
            {error}
          </Alert>
        )}

        <TextField
          fullWidth
          label="Email"
          value={email}
          onChange={(e) => setEmail(e.target.value)}
          sx={{ mb: 2 }}
        />

        <TextField
          fullWidth
          type="password"
          label="Password"
          value={password}
          onChange={(e) => setPassword(e.target.value)}
          sx={{ mb: 3 }}
        />

        <Button
          variant="contained"
          color="primary"
          fullWidth
          size="large"
          onClick={handleLogin}
          sx={{
            py: 1.4,
            fontWeight: 600,
            background: "linear-gradient(135deg, #2575fc, #6a11cb)",
          }}
        >
          Sign In
        </Button>
      </Paper>
    </Box>
  );
}







