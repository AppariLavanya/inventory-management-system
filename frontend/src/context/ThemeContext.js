import React, { createContext, useEffect, useState } from "react";
import { createTheme, ThemeProvider } from "@mui/material/styles";

export const ThemeContext = createContext();

export default function CustomThemeProvider({ children }) {
  const stored = localStorage.getItem("theme") || "light";
  const [mode, setMode] = useState(stored);

  const toggleTheme = () => {
    const newMode = mode === "light" ? "dark" : "light";
    setMode(newMode);
    localStorage.setItem("theme", newMode);
  };

  useEffect(() => {
    localStorage.setItem("theme", mode);
  }, [mode]);

  const theme = createTheme({
    palette: {
      mode,
      ...(mode === "light"
        ? {
            background: {
              default: "#f5f7fa",
              paper: "#ffffff",
            },
            primary: { main: "#1976d2" },
            secondary: { main: "#424242" },
            text: {
              primary: "#1e293b",
              secondary: "#475569",
            },
          }
        : {
            background: {
              default: "#0f172a",
              paper: "#1e293b",
            },
            primary: { main: "#3b82f6" },
            secondary: { main: "#9ca3af" },
            text: {
              primary: "#e2e8f0",
              secondary: "#cbd5e1",
            },
          }),
    },

    components: {
      MuiPaper: {
        styleOverrides: {
          root: {
            borderRadius: 10,
            transition: "all 0.25s ease",
          },
        },
      },

      MuiButton: {
        styleOverrides: {
          root: {
            textTransform: "none",
            fontWeight: 600,
            borderRadius: 8,
          },
        },
      },

      MuiTableRow: {
        styleOverrides: {
          root: {
            transition: "background 0.2s ease",
            "&:hover": {
              backgroundColor:
                mode === "light" ? "#e8f1fc" : "rgba(255,255,255,0.05)",
            },
          },
        },
      },
    },

    transitions: {
      duration: {
        shortest: 150,
        shorter: 200,
        short: 250,
        standard: 300,
        complex: 375,
        enteringScreen: 225,
        leavingScreen: 195,
      },
    },
  });

  return (
    <ThemeContext.Provider value={{ mode, toggleTheme }}>
      <ThemeProvider theme={theme}>{children}</ThemeProvider>
    </ThemeContext.Provider>
  );
}

