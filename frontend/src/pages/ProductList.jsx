// src/pages/ProductList.jsx
import React, { useEffect, useMemo, useState, useRef } from "react";
import {
  Box,
  Button,
  TextField,
  Typography,
  Grid,
  MenuItem,
  Snackbar,
  Alert,
  Paper,
  IconButton,
  Tooltip,
  Divider,
  Chip,
} from "@mui/material";

import AddIcon from "@mui/icons-material/Add";
import DeleteIcon from "@mui/icons-material/Delete";
import PictureAsPdfIcon from "@mui/icons-material/PictureAsPdf";
import TableViewIcon from "@mui/icons-material/TableView";
import RefreshIcon from "@mui/icons-material/Refresh";

import { DataGrid } from "@mui/x-data-grid";
import ProductService from "../services/ProductService";
import { useNavigate } from "react-router-dom";

export default function ProductList() {
  const navigate = useNavigate();

  const [rows, setRows] = useState([]);
  const [loading, setLoading] = useState(false);
  const [rowCount, setRowCount] = useState(0);
  const [selectionModel, setSelectionModel] = useState([]);

  const [filters, setFilters] = useState({
    q: "",
    minPrice: "",
    maxPrice: "",
    minStock: "",
    maxStock: "",
    category: "",
    sort: "",
  });

  const [page, setPage] = useState(0);
  const [pageSize, setPageSize] = useState(10);
  const [sortModel, setSortModel] = useState([]);

  const [snack, setSnack] = useState({
    open: false,
    severity: "success",
    message: "",
  });

  const lastFetchId = useRef(0);

  // debounce search text
  const [searchDebounce, setSearchDebounce] = useState(0);
  useEffect(() => {
    const t = setTimeout(() => setSearchDebounce((s) => s + 1), 350);
    return () => clearTimeout(t);
  }, [filters.q]);

  // Build query params for backend search endpoint
  const buildParams = () => {
    const params = {
      page,
      size: pageSize,
      q: filters.q || undefined,
      minPrice: filters.minPrice || undefined,
      maxPrice: filters.maxPrice || undefined,
      minStock: filters.minStock || undefined,
      maxStock: filters.maxStock || undefined,
      category: filters.category || undefined,
    };

    if (filters.sort && filters.sort !== "") {
      params.sort = filters.sort;
    } else if (sortModel && sortModel.length > 0) {
      const s = sortModel[0];
      params.sort = s.sort === "desc" ? `-${s.field}` : s.field;
    }

    return params;
  };

  const load = async () => {
    const fetchId = ++lastFetchId.current;
    setLoading(true);

    try {
      const params = buildParams();
      const res = await ProductService.search(params);

      if (fetchId !== lastFetchId.current) return;

      if (res && res.content) {
        setRows(res.content);
        setRowCount(res.totalElements ?? res.content.length);
      } else if (Array.isArray(res)) {
        setRows(res);
        setRowCount(res.length);
      } else {
        setRows([]);
        setRowCount(0);
      }
    } catch (err) {
      console.error("Failed to fetch products:", err);
      setRows([]);
      setRowCount(0);
      setSnack({
        open: true,
        severity: "error",
        message: "Failed to load products",
      });
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    load();
  }, [
    page,
    pageSize,
    sortModel,
    filters.minPrice,
    filters.maxPrice,
    filters.minStock,
    filters.maxStock,
    filters.category,
    searchDebounce,
    filters.sort,
  ]);

  const applyFilters = () => {
    if (filters.sort && filters.sort.includes(",")) {
      const parts = filters.sort.split(",", 2);
      const dir = parts[1].trim().toLowerCase() === "desc" ? "desc" : "asc";
      setSortModel([{ field: parts[0].trim(), sort: dir }]);
    } else if (filters.sort && filters.sort.startsWith("-")) {
      setSortModel([{ field: filters.sort.substring(1), sort: "desc" }]);
    } else if (filters.sort) {
      setSortModel([{ field: filters.sort, sort: "asc" }]);
    } else {
      setSortModel([]);
    }

    setPage(0);
    load();
  };

  // Exports: CSV Removed
  const exportExcel = () => ProductService.exportExcel();
  const exportPDF = () => ProductService.exportPDF();

  const deleteSelected = async () => {
    if (!selectionModel || selectionModel.length === 0)
      return setSnack({
        open: true,
        severity: "warning",
        message: "No products selected",
      });

    if (!window.confirm(`Delete ${selectionModel.length} selected product(s)?`))
      return;

    try {
      await ProductService.deleteMany(selectionModel);
      setSnack({
        open: true,
        severity: "success",
        message: "Deleted selected products",
      });
      setSelectionModel([]);
      load();
    } catch (err) {
      console.error(err);
      setSnack({
        open: true,
        severity: "error",
        message: "Bulk delete failed",
      });
    }
  };

  const deleteOne = async (id) => {
    if (!window.confirm("Delete this product?")) return;

    try {
      await ProductService.delete(id);
      setSnack({
        open: true,
        severity: "success",
        message: "Product deleted",
      });
      load();
    } catch (err) {
      console.error(err);
      setSnack({
        open: true,
        severity: "error",
        message: "Delete failed",
      });
    }
  };

  const columns = useMemo(
    () => [
      { field: "id", headerName: "ID", width: 80 },
      { field: "sku", headerName: "SKU", width: 150 },
      { field: "name", headerName: "Name", width: 260, flex: 1 },
      { field: "category", headerName: "Category", width: 140 },
      { field: "brand", headerName: "Brand", width: 140 },
      { field: "stock", headerName: "Stock", width: 100 },
      {
        field: "price",
        headerName: "Price",
        width: 140,
        valueFormatter: (p) => `₹${Number(p.value || 0).toLocaleString()}`,
      },

      // Severity
      {
        field: "severity",
        headerName: "Severity",
        width: 130,
        renderCell: (params) => {
          const v = params.value || params.row.severity || "UNKNOWN";
          let color = "default";
          if (v === "CRITICAL") color = "error";
          else if (v === "LOW") color = "warning";
          else if (v === "MEDIUM") color = "info";
          else color = "default";
          return <Chip label={v} size="small" color={color} />;
        },
      },

      {
        field: "reorderFlag",
        headerName: "Reorder?",
        width: 110,
        renderCell: (params) =>
          params.value ? (
            <Chip label="YES" size="small" color="error" />
          ) : (
            <Chip label="NO" size="small" color="success" />
          ),
      },
      {
        field: "reorderSuggestion",
        headerName: "Reorder Qty",
        width: 120,
        valueFormatter: (p) => (p.value == null ? "" : `${p.value}`),
      },

      {
        field: "actions",
        headerName: "Actions",
        width: 220,
        renderCell: (params) => (
          <Box sx={{ display: "flex", gap: 1 }}>
            <Button
              size="small"
              variant="outlined"
              onClick={() => navigate(`/products/${params.row.id}/edit`)}
            >
              Edit
            </Button>
            <Button
              size="small"
              color="error"
              variant="contained"
              onClick={() => deleteOne(params.row.id)}
            >
              Delete
            </Button>
          </Box>
        ),
      },
    ],
    [navigate]
  );

  return (
    <Box
      sx={{
        p: 3,
        minHeight: "100vh",
        backgroundColor: "background.default",
        color: "text.primary",
      }}
    >
      <Typography variant="h4" sx={{ mb: 2, fontWeight: 700 }}>
        🛍 Products
      </Typography>

      <Paper
        sx={{
          p: 2,
          mb: 2,
          backgroundColor: "background.paper",
          color: "text.primary",
        }}
      >
        <Grid container spacing={2} alignItems="center">
          <Grid item xs={12} md={6} lg={7} sx={{ display: "flex", gap: 1 }}>
            <Button
              variant="contained"
              startIcon={<AddIcon />}
              onClick={() => navigate("/products/new")}
            >
              Add Product
            </Button>

            <Button
              variant="outlined"
              color="error"
              startIcon={<DeleteIcon />}
              onClick={deleteSelected}
            >
              Bulk Delete
            </Button>

            <Divider orientation="vertical" flexItem sx={{ mx: 1 }} />

            <Tooltip title="Refresh">
              <IconButton onClick={load}>
                <RefreshIcon />
              </IconButton>
            </Tooltip>

            {/* CSV REMOVED */}

            <Tooltip title="Export Excel">
              <IconButton onClick={exportExcel}>
                <TableViewIcon />
              </IconButton>
            </Tooltip>

            <Tooltip title="Export PDF">
              <IconButton onClick={exportPDF}>
                <PictureAsPdfIcon />
              </IconButton>
            </Tooltip>
          </Grid>

          {/* Filters */}
          <Grid item xs={12} md={6} lg={5}>
            <Grid container spacing={1}>
              <Grid item xs={12} sm={6} md={4}>
                <TextField
                  label="Search"
                  size="small"
                  fullWidth
                  value={filters.q}
                  onChange={(e) =>
                    setFilters((p) => ({ ...p, q: e.target.value }))
                  }
                />
              </Grid>

              <Grid item xs={6} sm={3} md={2}>
                <TextField
                  label="Min Price"
                  type="number"
                  size="small"
                  fullWidth
                  value={filters.minPrice}
                  onChange={(e) =>
                    setFilters((p) => ({ ...p, minPrice: e.target.value }))
                  }
                />
              </Grid>

              <Grid item xs={6} sm={3} md={2}>
                <TextField
                  label="Max Price"
                  type="number"
                  size="small"
                  fullWidth
                  value={filters.maxPrice}
                  onChange={(e) =>
                    setFilters((p) => ({ ...p, maxPrice: e.target.value }))
                  }
                />
              </Grid>

              <Grid item xs={6} sm={3} md={2}>
                <TextField
                  label="Min Stock"
                  type="number"
                  size="small"
                  fullWidth
                  value={filters.minStock}
                  onChange={(e) =>
                    setFilters((p) => ({ ...p, minStock: e.target.value }))
                  }
                />
              </Grid>

              <Grid item xs={6} sm={3} md={2}>
                <TextField
                  label="Max Stock"
                  type="number"
                  size="small"
                  fullWidth
                  value={filters.maxStock}
                  onChange={(e) =>
                    setFilters((p) => ({ ...p, maxStock: e.target.value }))
                  }
                />
              </Grid>

              <Grid item xs={12} sm={6} md={3}>
                <TextField
                  label="Category"
                  size="small"
                  fullWidth
                  value={filters.category}
                  onChange={(e) =>
                    setFilters((p) => ({ ...p, category: e.target.value }))
                  }
                />
              </Grid>

              <Grid item xs={12} sm={6} md={3}>
                <TextField
                  select
                  label="Sort"
                  size="small"
                  fullWidth
                  value={filters.sort}
                  onChange={(e) =>
                    setFilters((p) => ({ ...p, sort: e.target.value }))
                  }
                >
                  <MenuItem value="">None</MenuItem>
                  <MenuItem value="price,asc">Price ↑</MenuItem>
                  <MenuItem value="price,desc">Price ↓</MenuItem>
                  <MenuItem value="stock,asc">Stock ↑</MenuItem>
                  <MenuItem value="stock,desc">Stock ↓</MenuItem>
                  <MenuItem value="name,asc">Name A→Z</MenuItem>
                  <MenuItem value="name,desc">Name Z→A</MenuItem>
                </TextField>
              </Grid>

              <Grid item xs={12} sm={12} md={3}>
                <Button variant="contained" fullWidth onClick={applyFilters}>
                  Apply Filters
                </Button>
              </Grid>
            </Grid>
          </Grid>
        </Grid>
      </Paper>

      <Box
        sx={{
          height: 640,
          width: "100%",
          backgroundColor: "background.paper",
          color: "text.primary",
          borderRadius: 2,
        }}
      >
        <DataGrid
          rows={rows}
          columns={columns}
          loading={loading}
          rowCount={rowCount}
          page={page}
          pageSize={pageSize}
          pagination
          paginationMode="server"
          onPageChange={(p) => setPage(p)}
          onPageSizeChange={(n) => {
            setPageSize(n);
            setPage(0);
          }}
          rowsPerPageOptions={[5, 10, 25, 50]}
          checkboxSelection
          selectionModel={selectionModel}
          onSelectionModelChange={(m) =>
            setSelectionModel(Array.isArray(m) ? m : [m])
          }
          sortingMode="server"
          sortModel={sortModel}
          onSortModelChange={(m) => {
            setSortModel(m);
            setFilters((p) => ({ ...p, sort: "" }));
          }}
          disableSelectionOnClick
        />
      </Box>

      <Snackbar
        open={snack.open}
        autoHideDuration={4000}
        onClose={() => setSnack((s) => ({ ...s, open: false }))}
      >
        <Alert severity={snack.severity}>{snack.message}</Alert>
      </Snackbar>
    </Box>
  );
}















