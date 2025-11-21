import React from "react";
import ReactDOM from "react-dom/client";
import App from "./App";

import CssBaseline from "@mui/material/CssBaseline";
import CustomThemeProvider from "./context/ThemeContext";

// React 18 Root
const root = ReactDOM.createRoot(document.getElementById("root"));

root.render(
  <React.StrictMode>
    {/* Global Theme Provider (Light/Dark) */}
    <CustomThemeProvider>
      {/* MUI global reset + theme-aware styling */}
      <CssBaseline />

      {/* Entire Application */}
      <App />
    </CustomThemeProvider>
  </React.StrictMode>
);





