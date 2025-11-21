// src/pages/OrderView.jsx
import React, { useEffect, useState } from "react";
import {
  Box,
  Typography,
  Card,
  CardContent,
  Grid,
  CircularProgress,
  Table,
  TableHead,
  TableRow,
  TableCell,
  TableBody,
  Paper,
  TableContainer,
  Button,
  Chip,
} from "@mui/material";
import { useParams, useNavigate } from "react-router-dom";
import OrderService from "../services/OrderService";

// Status formatting
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

export default function OrderView() {
  const { id } = useParams();
  const navigate = useNavigate();

  const [order, setOrder] = useState(null);
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    load();
  }, [id]);

  const load = async () => {
    try {
      setLoading(true);
      const o = await OrderService.get(id);
      setOrder(o);
    } catch (err) {
      console.error("Fetch failed:", err);
      setOrder(null);
    } finally {
      setLoading(false);
    }
  };

  if (loading)
    return (
      <Box sx={{ textAlign: "center", p: 4 }}>
        <CircularProgress />
        <Typography sx={{ mt: 2 }}>Loading order details...</Typography>
      </Box>
    );

  if (!order)
    return (
      <Typography sx={{ p: 3, color: "red", fontWeight: 600 }}>
        Order not found.
      </Typography>
    );

  return (
    <Box sx={{ p: 3 }}>
      {/* Back Button */}
      <Button variant="outlined" sx={{ mb: 2 }} onClick={() => navigate("/orders")}>
        ← Back to Orders
      </Button>

      {/* Title + Status */}
      <Box sx={{ display: "flex", alignItems: "center", gap: 2 }}>
        <Typography variant="h4" sx={{ fontWeight: 700 }}>
          🧾 Order #{order.id}
        </Typography>

        <Chip
          label={STATUS_LABELS[order.status] || order.status}
          color={STATUS_COLOR[order.status] || "default"}
          sx={{ fontSize: 14, fontWeight: 600 }}
        />
      </Box>

      {/* Customer + Summary */}
      <Card sx={{ mt: 3, mb: 3, p: 2 }}>
        <CardContent>
          <Grid container spacing={3}>
            {/* Customer Info */}
            <Grid item xs={12} md={6}>
              <Typography variant="h6" sx={{ mb: 1 }}>
                👤 Customer Details
              </Typography>

              <Typography>Name: {order.customerName}</Typography>
              <Typography>Email: {order.customerEmail}</Typography>
            </Grid>

            {/* Order Summary */}
            <Grid item xs={12} md={6}>
              <Typography variant="h6" sx={{ mb: 1 }}>
                📌 Order Summary
              </Typography>

              <Typography>
                Date:{" "}
                {order.createdAt
                  ? new Date(order.createdAt).toLocaleString()
                  : "—"}
              </Typography>

              <Typography sx={{ fontWeight: "bold", mt: 1 }}>
                Total: ₹{order.total ?? 0}
              </Typography>
            </Grid>
          </Grid>
        </CardContent>
      </Card>

      {/* Items Table */}
      <TableContainer component={Paper}>
        <Table>
          <TableHead sx={{ backgroundColor: "#f5f5f5" }}>
            <TableRow>
              <TableCell>Product</TableCell>
              <TableCell>Unit Price</TableCell>
              <TableCell>Quantity</TableCell>
              <TableCell>Total</TableCell>
            </TableRow>
          </TableHead>

          <TableBody>
            {(order.items || []).map((item, i) => (
              <TableRow key={i}>
                <TableCell>{item.productName}</TableCell>
                <TableCell>₹{item.unitPrice ?? 0}</TableCell>
                <TableCell>{item.quantity ?? 0}</TableCell>
                <TableCell>
                  ₹{((item.unitPrice ?? 0) * (item.quantity ?? 0)).toFixed(2)}
                </TableCell>
              </TableRow>
            ))}
          </TableBody>
        </Table>
      </TableContainer>
    </Box>
  );
}








