import React from "react";
import { Box, Paper, TextField, Button, Typography } from "@mui/material";
import { useNavigate } from "react-router-dom";

export default function Register() {
  const navigate = useNavigate();

  const handle = () => {
    alert("Registration is disabled.\nPlease contact the admin to create your account.");
  };

  return (
    <Box
      display="flex"
      justifyContent="center"
      alignItems="center"
      height="100vh"
      sx={{ background: "#f5f5f5" }}
    >
      <Paper sx={{ p: 4, width: 380, borderRadius: 3 }}>
        <Typography variant="h5" sx={{ mb: 2, fontWeight: 600 }}>
          Register (Disabled)
        </Typography>

        <Typography sx={{ mb: 3, color: "gray" }}>
          Creating new accounts is restricted.  
          Only administrators can create users.
        </Typography>

        <TextField
          label="Email"
          fullWidth
          disabled
          sx={{ mb: 2 }}
        />

        <TextField
          label="Password"
          type="password"
          fullWidth
          disabled
          sx={{ mb: 3 }}
        />

        <Button variant="contained" fullWidth color="error" onClick={handle}>
          Registration Disabled
        </Button>

        <Button
          variant="text"
          fullWidth
          sx={{ mt: 2 }}
          onClick={() => navigate("/login")}
        >
          Go to Login
        </Button>
      </Paper>
    </Box>
  );
}



