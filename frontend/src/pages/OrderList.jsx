// src/pages/OrderList.jsx
import React, { useEffect, useState } from "react";
import {
  Box,
  Typography,
  TextField,
  Button,
  Table,
  TableHead,
  TableRow,
  TableCell,
  TableBody,
  TableContainer,
  Paper,
  Chip,
} from "@mui/material";
import { useNavigate, useLocation } from "react-router-dom";
import OrderService from "../services/OrderService";

const STATUS_LABELS = {
  PENDING: "Pending",
  PROCESSING: "Processing",
  SHIPPED: "Shipped",
  DELIVERED: "Delivered",
  CANCELLED: "Cancelled",
};

const STATUS_COLOR = {
  PENDING: "default",
  PROCESSING: "info",
  SHIPPED: "warning",
  DELIVERED: "success",
  CANCELLED: "error",
};

export default function OrderList() {
  const navigate = useNavigate();
  const location = useLocation();

  const [orders, setOrders] = useState([]);
  const [loading, setLoading] = useState(true);

  const [customerName, setCustomerName] = useState("");
  const [minTotal, setMinTotal] = useState("");
  const [maxTotal, setMaxTotal] = useState("");

  const [page, setPage] = useState(1);
  const [size] = useState(10);
  const [totalPages, setTotalPages] = useState(1);

  useEffect(() => {
    loadOrders();
  }, [page]);

  useEffect(() => {
    if (location.state?.refresh) {
      loadOrders();
      navigate(".", { replace: true, state: {} });
    }
  }, [location.state]);

  const buildParams = () => {
    const params = { page: Math.max(0, page - 1), size };

    if (customerName) params.customerName = customerName;
    if (minTotal !== "") params.minTotal = Number(minTotal);
    if (maxTotal !== "") params.maxTotal = Number(maxTotal);

    return params;
  };

  const loadOrders = async () => {
    try {
      setLoading(true);
      const params = buildParams();
      const res = await OrderService.search(params);

      if (res?.content) {
        setOrders(res.content);
        setTotalPages(res.totalPages || 1);
      } else if (Array.isArray(res)) {
        setOrders(res);
        setTotalPages(Math.ceil(res.length / size));
      } else {
        setOrders([]);
      }
    } catch {
      setOrders([]);
    } finally {
      setLoading(false);
    }
  };

  const handleSearch = () => {
    setPage(1);
    loadOrders();
  };

  const handleRefresh = () => loadOrders();

  const deleteOrder = async (id) => {
    if (!window.confirm("Delete this order?")) return;
    try {
      await OrderService.remove(id);
      loadOrders();
    } catch (err) {
      alert("Delete failed: " + (err.response?.data?.message || err.message));
    }
  };

  return (
    <Box sx={{ p: 3 }}>
      <Typography variant="h4" sx={{ mb: 3, fontWeight: 700 }}>
        📦 Orders
      </Typography>

      <Box sx={{ display: "flex", gap: 2, mb: 3, flexWrap: "wrap" }}>
        <TextField
          label="Customer Name"
          value={customerName}
          onChange={(e) => setCustomerName(e.target.value)}
          sx={{ width: 250 }}
        />

        <TextField
          label="Min Total"
          value={minTotal}
          onChange={(e) => setMinTotal(e.target.value)}
          sx={{ width: 150 }}
        />

        <TextField
          label="Max Total"
          value={maxTotal}
          onChange={(e) => setMaxTotal(e.target.value)}
          sx={{ width: 150 }}
        />

        <Button variant="contained" onClick={handleSearch}>
          SEARCH
        </Button>

        <Button variant="outlined" onClick={() => navigate("/orders/new")}>
          + New Order
        </Button>

        <Button variant="contained" color="info" onClick={handleRefresh}>
          Refresh
        </Button>
      </Box>

      <TableContainer component={Paper}>
        <Table>
          <TableHead>
            <TableRow>
              <TableCell>ID</TableCell>
              <TableCell>Customer</TableCell>
              <TableCell>Email</TableCell>
              <TableCell>Total</TableCell>
              <TableCell>Status</TableCell>
              <TableCell width={250}>Actions</TableCell>
            </TableRow>
          </TableHead>

          <TableBody>
            {loading ? (
              <TableRow>
                <TableCell colSpan={6} align="center">
                  Loading...
                </TableCell>
              </TableRow>
            ) : orders.length === 0 ? (
              <TableRow>
                <TableCell colSpan={6} align="center">
                  No Orders Found.
                </TableCell>
              </TableRow>
            ) : (
              orders.map((o) => (
                <TableRow key={o.id}>
                  <TableCell>{o.id}</TableCell>
                  <TableCell>{o.customerName}</TableCell>
                  <TableCell>{o.customerEmail}</TableCell>
                  <TableCell>₹{o.total ?? 0}</TableCell>

                  <TableCell>
                    <Chip
                      label={STATUS_LABELS[o.status] || o.status}
                      color={STATUS_COLOR[o.status] || "default"}
                      size="small"
                    />
                  </TableCell>

                  <TableCell>
                    <Button
                      size="small"
                      variant="outlined"
                      onClick={() => navigate(`/orders/view/${o.id}`)}
                      sx={{ mr: 1 }}
                    >
                      VIEW
                    </Button>

                    <Button
                      size="small"
                      variant="contained"
                      onClick={() => navigate(`/orders/edit/${o.id}`)}
                      sx={{ mr: 1 }}
                    >
                      EDIT
                    </Button>

                    <Button
                      size="small"
                      variant="contained"
                      color="error"
                      onClick={() => deleteOrder(o.id)}
                    >
                      DELETE
                    </Button>
                  </TableCell>
                </TableRow>
              ))
            )}
          </TableBody>
        </Table>
      </TableContainer>

      <Box sx={{ textAlign: "center", mt: 2 }}>
        <Button disabled={page === 1} onClick={() => setPage(page - 1)}>
          PREV
        </Button>

        <span style={{ margin: "0 15px" }}>
          Page {page} / {totalPages}
        </span>

        <Button
          disabled={page === totalPages}
          onClick={() => setPage(page + 1)}
        >
          NEXT
        </Button>
      </Box>
    </Box>
  );
}















