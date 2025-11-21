// src/pages/LowStock.jsx
import React, { useEffect, useState } from "react";
import {
  Box,
  Button,
  Typography,
  TextField,
  Paper,
  Chip,
} from "@mui/material";
import { DataGrid } from "@mui/x-data-grid";
import ProductService from "../services/ProductService";
import { useNavigate } from "react-router-dom";

export default function LowStock() {
  const navigate = useNavigate();
  const [threshold, setThreshold] = useState(5);
  const [rows, setRows] = useState([]);
  const [loading, setLoading] = useState(false);

  // LOAD LOW STOCK ITEMS
  const load = async () => {
    try {
      setLoading(true);

      const data = await ProductService.lowStock(threshold);

      // BACKEND returns { threshold, count, items: [...] }
      setRows(data.items || []);
    } catch (err) {
      console.error("Low stock fetch error:", err);
      setRows([]);
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    load();
  }, []);

  const getSeverityChip = (level) => {
    switch (level) {
      case "CRITICAL":
        return <Chip color="error" size="small" label="CRITICAL" />;
      case "LOW":
        return <Chip color="warning" size="small" label="LOW" />;
      case "MEDIUM":
        return <Chip color="info" size="small" label="MEDIUM" />;
      default:
        return <Chip size="small" label="UNKNOWN" />;
    }
  };

  const getReorderChip = (flag) => {
    return flag ? (
      <Chip color="error" size="small" label="YES" />
    ) : (
      <Chip color="success" size="small" label="NO" />
    );
  };

  const columns = [
    { field: "id", headerName: "ID", width: 70 },
    { field: "sku", headerName: "SKU", width: 140 },
    { field: "name", headerName: "Name", width: 200 },
    { field: "category", headerName: "Category", width: 140 },
    { field: "brand", headerName: "Brand", width: 120 },
    { field: "stock", headerName: "Stock", width: 100 },

    {
      field: "severity",
      headerName: "Severity",
      width: 130,
      renderCell: (params) => getSeverityChip(params.row.severity),
    },

    {
      field: "reorderLevel",
      headerName: "Reorder Level",
      width: 130,
    },

    {
      field: "reorderFlag",
      headerName: "Reorder?",
      width: 110,
      renderCell: (params) => getReorderChip(params.row.reorderFlag),
    },

    {
      field: "reorderSuggestion",
      headerName: "Reorder Qty",
      width: 130,
      valueFormatter: (p) =>
        p.value == null ? "" : `${p.value}`,
    },

    {
      field: "price",
      headerName: "Price (₹)",
      width: 140,
      renderCell: (params) =>
        `₹${Number(params.row.price || 0).toLocaleString()}`,
    },
  ];

  return (
    <Box sx={{ p: 3 }}>
      <Typography variant="h4" sx={{ fontWeight: 600, mb: 2 }}>
        ⚠️ Low Stock Items
      </Typography>

      {/* FILTER BOX */}
      <Paper sx={{ p: 2, mb: 3 }}>
        <Box sx={{ display: "flex", gap: 2, alignItems: "center" }}>
          <TextField
            type="number"
            label="Stock Threshold"
            size="small"
            value={threshold}
            onChange={(e) => setThreshold(Number(e.target.value))}
          />

          <Button variant="contained" color="primary" onClick={load}>
            Apply Filter
          </Button>

          <Button
            variant="outlined"
            color="secondary"
            onClick={() => navigate("/products")}
          >
            Back to Products
          </Button>
        </Box>
      </Paper>

      {/* TABLE */}
      <Paper sx={{ height: 520, p: 1 }}>
        <DataGrid
          rows={rows}
          columns={columns}
          loading={loading}
          disableRowSelectionOnClick
          sx={{
            borderRadius: 2,
            bgcolor: "background.paper",
          }}
        />
      </Paper>
    </Box>
  );
}






