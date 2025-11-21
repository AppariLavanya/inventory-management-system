import React, { useContext } from "react";
import {
  AppBar,
  Toolbar,
  Button,
  Typography,
  IconButton,
  Box,
} from "@mui/material";

import LightModeIcon from "@mui/icons-material/LightMode";
import DarkModeIcon from "@mui/icons-material/DarkMode";

import { ThemeContext } from "../context/ThemeContext";
import { Link, useLocation, useNavigate } from "react-router-dom";

export default function Navbar() {
  const navigate = useNavigate();
  const location = useLocation();
  const token = localStorage.getItem("token");

  const { mode, toggleTheme } = useContext(ThemeContext);

  // Highlight active tab
  const isActive = (path) => location.pathname.startsWith(path);

  return (
    <AppBar
      position="static"
      color="primary"
      elevation={1}
      sx={{
        transition: "all 0.3s ease",
      }}
    >
      <Toolbar>
        {/* App Title */}
        <Typography
          variant="h6"
          sx={{
            flexGrow: 1,
            fontWeight: "bold",
            letterSpacing: "0.5px",
          }}
        >
          Inventory System
        </Typography>

        {/* Theme Toggle */}
        <IconButton
          color="inherit"
          onClick={toggleTheme}
          sx={{ mr: 1, transition: "all 0.3s ease" }}
        >
          {mode === "light" ? <DarkModeIcon /> : <LightModeIcon />}
        </IconButton>

        {/* Navigation Links */}
        {token && (
          <Box sx={{ display: "flex", gap: 1 }}>
            <Button
              color="inherit"
              component={Link}
              to="/products"
              sx={{
                borderBottom: isActive("/products")
                  ? "2px solid white"
                  : "2px solid transparent",
                borderRadius: 0,
                fontWeight: 600,
              }}
            >
              PRODUCTS
            </Button>

            <Button
              color="inherit"
              component={Link}
              to="/orders"
              sx={{
                borderBottom: isActive("/orders")
                  ? "2px solid white"
                  : "2px solid transparent",
                borderRadius: 0,
                fontWeight: 600,
              }}
            >
              ORDERS
            </Button>

            <Button
              color="inherit"
              component={Link}
              to="/low-stock"
              sx={{
                borderBottom: isActive("/low-stock")
                  ? "2px solid white"
                  : "2px solid transparent",
                borderRadius: 0,
                fontWeight: 600,
              }}
            >
              LOW STOCK
            </Button>

            <Button
              color="inherit"
              component={Link}
              to="/analytics"
              sx={{
                borderBottom: isActive("/analytics")
                  ? "2px solid white"
                  : "2px solid transparent",
                borderRadius: 0,
                fontWeight: 600,
              }}
            >
              ANALYTICS
            </Button>

            {/* Logout */}
            <Button
              color="inherit"
              sx={{ fontWeight: 600 }}
              onClick={() => {
                localStorage.removeItem("token");
                navigate("/login");
              }}
            >
              LOGOUT
            </Button>
          </Box>
        )}
      </Toolbar>
    </AppBar>
  );
}








