// src/pages/OrderForm.jsx
import React, { useState, useEffect } from "react";
import {
  Box,
  Button,
  Typography,
  Card,
  Grid,
  MenuItem,
  TextField,
  IconButton,
  Snackbar,
  Alert
} from "@mui/material";

import DeleteIcon from "@mui/icons-material/Delete";
import api from "../services/api";
import { useNavigate } from "react-router-dom";

export default function OrderForm() {
  const navigate = useNavigate();

  const [products, setProducts] = useState([]);
  const [items, setItems] = useState([
    { productId: "", quantity: 1, unitPrice: 0, stock: 0 }
  ]);

  const [customerName, setCustomerName] = useState("");
  const [customerEmail, setCustomerEmail] = useState("");

  const [snack, setSnack] = useState({ open: false, message: "" });

  useEffect(() => {
    loadProducts();
  }, []);

  const loadProducts = async () => {
    try {
      const res = await api.get("/products?page=0&size=999");
      setProducts(res.data?.content || []);
    } catch (err) {
      console.error("Failed to load products", err);
    }
  };

  const handleProductChange = (index, productId) => {
    const product = products.find((p) => p.id === Number(productId));
    const updated = [...items];

    updated[index] = {
      ...updated[index],
      productId: Number(productId),
      unitPrice: product?.price || 0,
      stock: product?.stock || 0,
      quantity: 1,
    };

    setItems(updated);
  };

  const handleQuantityChange = (index, qty) => {
    const updated = [...items];

    if (qty > updated[index].stock) {
      alert("Not enough stock available!");
      return;
    }

    updated[index].quantity = qty;
    setItems(updated);
  };

  const addItem = () => {
    setItems([
      ...items,
      { productId: "", quantity: 1, unitPrice: 0, stock: 0 }
    ]);
  };

  const removeItem = (index) => {
    const updated = [...items];
    updated.splice(index, 1);
    setItems(updated);
  };

  const total = items.reduce(
    (sum, i) => sum + i.quantity * i.unitPrice,
    0
  );

  const submitOrder = async () => {
    const payload = {
      customerName,
      customerEmail,
      items: items
        .filter((i) => i.productId)
        .map((i) => ({
          productId: Number(i.productId),
          quantity: Number(i.quantity),
        })),
    };

    try {
      await api.post("/orders", payload);

      setSnack({ open: true, message: "Order Created Successfully!" });

      // Redirect back with refresh flag
      setTimeout(() => {
        navigate("/orders", { state: { refresh: true } });
      }, 1200);
      
    } catch (err) {
      alert("Order failed: " + (err.response?.data?.message || err.message));
    }
  };

  return (
    <Box sx={{ p: 3 }}>
      
      {/* BACK BUTTON */}
      <Button
        variant="outlined"
        sx={{ mb: 2 }}
        onClick={() => navigate("/orders")}
      >
        ← Back to Orders
      </Button>

      <Typography variant="h4" sx={{ mb: 2, fontWeight: 600 }}>
        🛒 Create New Order
      </Typography>

      {/* CUSTOMER DETAILS */}
      <Card sx={{ p: 3, mb: 3, borderRadius: 3 }}>
        <Typography variant="h6" sx={{ mb: 2 }}>
          Customer Details
        </Typography>

        <Grid container spacing={2}>
          <Grid item xs={12} md={6}>
            <TextField
              fullWidth
              label="Customer Name"
              value={customerName}
              onChange={(e) => setCustomerName(e.target.value)}
            />
          </Grid>

          <Grid item xs={12} md={6}>
            <TextField
              fullWidth
              label="Customer Email"
              value={customerEmail}
              onChange={(e) => setCustomerEmail(e.target.value)}
            />
          </Grid>
        </Grid>
      </Card>

      {/* ORDER ITEMS */}
      <Card sx={{ p: 3, borderRadius: 3 }}>
        <Typography variant="h6" sx={{ mb: 2 }}>
          Order Items
        </Typography>

        {items.map((item, index) => (
          <Card sx={{ p: 2, mb: 2, borderRadius: 2 }} key={index}>
            <Grid container spacing={2}>

              <Grid item xs={12} md={4}>
                <TextField
                  select
                  fullWidth
                  label="Product"
                  value={item.productId}
                  onChange={(e) => handleProductChange(index, e.target.value)}
                >
                  <MenuItem value="">Select Product...</MenuItem>
                  {products.map((p) => (
                    <MenuItem key={p.id} value={p.id}>
                      {p.name} — Stock: {p.stock}
                    </MenuItem>
                  ))}
                </TextField>
              </Grid>

              <Grid item xs={12} md={3}>
                <TextField
                  type="number"
                  fullWidth
                  label="Quantity"
                  value={item.quantity}
                  onChange={(e) =>
                    handleQuantityChange(index, Number(e.target.value))
                  }
                />
              </Grid>

              <Grid item xs={12} md={3}>
                <TextField
                  fullWidth
                  label="Unit Price (₹)"
                  value={item.unitPrice}
                  disabled
                />
              </Grid>

              <Grid
                item
                xs={12}
                md={2}
                sx={{ display: "flex", alignItems: "center" }}
              >
                <IconButton color="error" onClick={() => removeItem(index)}>
                  <DeleteIcon />
                </IconButton>
              </Grid>

            </Grid>
          </Card>
        ))}

        <Button variant="contained" onClick={addItem}>
          + Add Item
        </Button>

        <Typography variant="h5" sx={{ mt: 3 }}>
          Total: ₹{total}
        </Typography>

        <Button
          variant="contained"
          color="success"
          sx={{ mt: 3 }}
          onClick={submitOrder}
        >
          Submit Order
        </Button>
      </Card>

      <Snackbar
        open={snack.open}
        autoHideDuration={2000}
        onClose={() => setSnack({ ...snack, open: false })}
      >
        <Alert severity="success">{snack.message}</Alert>
      </Snackbar>
    </Box>
  );
}










