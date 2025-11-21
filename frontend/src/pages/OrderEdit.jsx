// src/pages/OrderEdit.jsx
import React, { useEffect, useState } from "react";
import {
  Box,
  Typography,
  TextField,
  Button,
  Card,
  CardContent,
  Grid,
  MenuItem,
  Snackbar,
  Alert,
} from "@mui/material";
import { useNavigate, useParams } from "react-router-dom";
import OrderService from "../services/OrderService";
import api from "../services/api";

const STATUS_OPTIONS = ["PENDING", "PROCESSING", "SHIPPED", "DELIVERED", "CANCELLED"];

export default function OrderEdit() {
  const { id } = useParams();
  const navigate = useNavigate();

  const [order, setOrder] = useState(null);
  const [items, setItems] = useState([]);
  const [status, setStatus] = useState("PENDING");
  const [customerName, setCustomerName] = useState("");
  const [customerEmail, setCustomerEmail] = useState("");
  const [products, setProducts] = useState([]);

  const [snack, setSnack] = useState({
    open: false,
    severity: "success",
    message: "",
  });

  useEffect(() => {
    loadOrder();
    loadProducts();
    // eslint-disable-next-line react-hooks/exhaustive-deps
  }, [id]);

  const loadOrder = async () => {
    try {
      const o = await OrderService.get(id);

      setOrder(o);
      setItems(
        (o.items || []).map((it) => ({
          productId: it.productId,
          productName: it.productName,
          unitPrice: it.unitPrice,
          quantity: it.quantity,
        }))
      );
      setStatus(o.status || "PENDING");
      setCustomerName(o.customerName || "");
      setCustomerEmail(o.customerEmail || "");
    } catch (err) {
      console.error("Order load failed", err);
    }
  };

  const loadProducts = async () => {
    try {
      const res = await api.get("/products?page=0&size=999");
      const list = res.data?.content || res.data || [];
      setProducts(list);
    } catch (err) {
      console.error("Product load failed", err);
    }
  };

  const updateItem = (index, key, value) => {
    const updated = [...items];
    updated[index] = { ...updated[index], [key]: value };
    setItems(updated);
  };

  const addItem = () =>
    setItems([
      ...items,
      {
        productId: null,
        productName: "",
        unitPrice: 0,
        quantity: 1,
      },
    ]);

  const removeItem = (index) => {
    const updated = [...items];
    updated.splice(index, 1);
    setItems(updated);
  };

  const save = async () => {
    try {
      const cleanedItems = items
        .filter((i) => i.productId !== null && i.productId !== "" && i.productId !== undefined)
        .map((i) => ({
          productId: Number(i.productId),
          quantity: Number(i.quantity) || 1,
        }));

      const payload = {
        customerName,
        customerEmail,
        status,
        items: cleanedItems,
      };

      await OrderService.update(id, payload);

      setSnack({
        open: true,
        severity: "success",
        message: "Order updated successfully",
      });

      setTimeout(() => navigate("/orders"), 800);
    } catch (err) {
      console.error(err);
      setSnack({
        open: true,
        severity: "error",
        message: "Failed to save order",
      });
    }
  };

  return (
    <Box sx={{ p: 3 }}>
      <Typography variant="h4" sx={{ fontWeight: 600 }}>
        Edit Order
      </Typography>

      {!order ? (
        <Typography sx={{ mt: 2 }}>Loading...</Typography>
      ) : (
        <Card sx={{ mt: 2, borderRadius: 3 }}>
          <CardContent>
            <Grid container spacing={2}>
              <Grid item xs={12} md={4}>
                <TextField
                  fullWidth
                  label="Customer Name"
                  value={customerName}
                  onChange={(e) => setCustomerName(e.target.value)}
                />
              </Grid>

              <Grid item xs={12} md={4}>
                <TextField
                  fullWidth
                  label="Customer Email"
                  value={customerEmail}
                  onChange={(e) => setCustomerEmail(e.target.value)}
                />
              </Grid>

              <Grid item xs={12} md={4}>
                <TextField
                  select
                  fullWidth
                  label="Order Status"
                  value={status}
                  onChange={(e) => setStatus(e.target.value)}
                >
                  {STATUS_OPTIONS.map((s) => (
                    <MenuItem key={s} value={s}>
                      {s}
                    </MenuItem>
                  ))}
                </TextField>
              </Grid>

              <Grid item xs={12}>
                <Typography variant="h6" sx={{ mt: 2 }}>
                  Order Items
                </Typography>
              </Grid>

              {items.map((it, idx) => (
                <Grid item xs={12} key={idx}>
                  <Grid container spacing={1}>
                    <Grid item xs={5}>
                      <TextField
                        select
                        fullWidth
                        label="Product"
                        // keep select value as string for stable controlled input
                        value={it.productId != null ? String(it.productId) : ""}
                        onChange={(e) => {
                          const pid = Number(e.target.value);
                          const product = products.find((p) => p.id === pid);

                          updateItem(idx, "productId", pid);
                          updateItem(idx, "productName", product?.name || "");
                          updateItem(idx, "unitPrice", product?.price || 0);
                        }}
                      >
                        <MenuItem value="">Select...</MenuItem>
                        {products.map((p) => (
                          <MenuItem key={p.id} value={String(p.id)}>
                            {p.name} (₹{p.price})
                          </MenuItem>
                        ))}
                      </TextField>
                    </Grid>

                    <Grid item xs={2}>
                      <TextField
                        fullWidth
                        type="number"
                        label="Qty"
                        value={it.quantity || 1}
                        onChange={(e) =>
                          updateItem(
                            idx,
                            "quantity",
                            Math.max(1, Number(e.target.value) || 1)
                          )
                        }
                      />
                    </Grid>

                    <Grid item xs={3}>
                      <TextField
                        fullWidth
                        label="Unit Price (₹)"
                        value={it.unitPrice || 0}
                        disabled
                      />
                    </Grid>

                    <Grid item xs={2}>
                      <Button color="error" onClick={() => removeItem(idx)}>
                        Remove
                      </Button>
                    </Grid>
                  </Grid>
                </Grid>
              ))}

              <Grid item xs={12}>
                <Button onClick={addItem}>+ Add Item</Button>
              </Grid>

              <Grid item xs={12} sx={{ textAlign: "right" }}>
                <Button onClick={() => navigate("/orders")} sx={{ mr: 1 }}>
                  Cancel
                </Button>

                <Button variant="contained" onClick={save}>
                  Save Changes
                </Button>
              </Grid>
            </Grid>
          </CardContent>
        </Card>
      )}

      <Snackbar
        open={snack.open}
        autoHideDuration={3000}
        onClose={() => setSnack({ ...snack, open: false })}
      >
        <Alert severity={snack.severity}>{snack.message}</Alert>
      </Snackbar>
    </Box>
  );
}















