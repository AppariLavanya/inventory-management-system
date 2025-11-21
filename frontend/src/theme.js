// src/theme.js
import { createTheme } from "@mui/material/styles";

export const getTheme = (mode = "light") =>
  createTheme({
    palette: {
      mode,

      primary: {
        main: mode === "light" ? "#1565c0" : "#60a5fa",
      },

      secondary: {
        main: "#ff9800",
      },

      background: {
        default: mode === "light" ? "#f5f7fa" : "#0f172a",
        paper: mode === "light" ? "#ffffff" : "#1e293b",
      },

      text: {
        primary: mode === "light" ? "#1e293b" : "#f8fafc",
        secondary: mode === "light" ? "#475569" : "#cbd5e1",
      },
    },

    components: {
      /* --------------------------
         CARD
      -------------------------- */
      MuiCard: {
        styleOverrides: {
          root: {
            borderRadius: 14,
            padding: 4,
            boxShadow:
              mode === "light"
                ? "0 4px 20px rgba(0,0,0,0.08)"
                : "0 4px 20px rgba(0,0,0,0.40)",
          },
        },
      },

      /* --------------------------
         PAPER
      -------------------------- */
      MuiPaper: {
        styleOverrides: {
          root: {
            borderRadius: 14,
          },
        },
      },

      /* --------------------------
         BUTTON
      -------------------------- */
      MuiButton: {
        styleOverrides: {
          root: {
            textTransform: "none",
            fontWeight: 600,
            borderRadius: 8,
            padding: "6px 14px",
            "&:hover": { opacity: 0.9 },
          },
        },
      },

      /* =====================================================
         FULL DATAGRID DARK MODE FIX  
         (Header, rows, virtual scroller, footer, cells)
      ===================================================== */
      MuiDataGrid: {
        styleOverrides: {
          root: {
            border: "none",
            backgroundColor: mode === "light" ? "#ffffff" : "#1e293b",
            color: mode === "light" ? "#1e293b" : "#f1f5f9",
          },

          /* HEADER ROW */
          columnHeaders: {
            backgroundColor:
              mode === "light" ? "#e2e8f0" : "#1e293b",
            color: mode === "light" ? "#1e293b" : "#f8fafc",
            borderBottom:
              mode === "light"
                ? "1px solid #cbd5e1"
                : "1px solid rgba(255,255,255,0.12)",
          },

          /* BODY BACKGROUND (this fixes your issue) */
          virtualScroller: {
            backgroundColor:
              mode === "light" ? "#ffffff" : "#0f172a",
          },

          /* ROW STYLE */
          row: {
            color: mode === "light" ? "#1e293b" : "#e2e8f0",
            borderBottom:
              mode === "light"
                ? "1px solid #e5e7eb"
                : "1px solid rgba(255,255,255,0.08)",
            "&:hover": {
              backgroundColor:
                mode === "light" ? "#f1f5f9" : "#334155",
            },
          },

          /* CELL STYLE */
          cell: {
            borderColor:
              mode === "light" ? "#e5e7eb" : "#334155",
          },

          /* FOOTER + PAGINATION */
          footerContainer: {
            backgroundColor:
              mode === "light" ? "#f8fafc" : "#1e293b",
            color: mode === "light" ? "#1e293b" : "#f1f5f9",
            borderTop:
              mode === "light"
                ? "1px solid #e5e7eb"
                : "1px solid rgba(255,255,255,0.12)",
          },

          /* PAGINATION BUTTONS */
          toolbarContainer: {
            color: mode === "light" ? "#1e293b" : "#f1f5f9",
          },
        },
      },
    },
  });






