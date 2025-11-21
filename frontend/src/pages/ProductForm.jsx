// src/pages/ProductForm.jsx
import React, { useEffect, useState } from "react";
import {
  Box,
  Card,
  CardContent,
  TextField,
  Button,
  Typography,
  Grid,
  Snackbar,
  Alert,
} from "@mui/material";

import ProductService from "../services/ProductService";
import { useNavigate, useParams } from "react-router-dom";

export default function ProductForm() {
  const { id } = useParams();
  const isEdit = Boolean(id);

  const navigate = useNavigate();

  const [form, setForm] = useState({
    sku: "",
    name: "",
    category: "",
    brand: "",
    stock: 0,
    price: 0,
    reorderLevel: 5, // ⭐ NEW FIELD (Backend Linked)
  });

  const [loading, setLoading] = useState(false);
  const [snack, setSnack] = useState({
    open: false,
    severity: "success",
    message: "",
  });

  // Load product for edit
  useEffect(() => {
    if (isEdit) {
      loadData();
    }
    // eslint-disable-next-line
  }, [id]);

  const loadData = async () => {
    try {
      setLoading(true);
      const data = await ProductService.get(id);

      if (!data) throw new Error("Product not found");

      setForm({
        sku: data.sku ?? "",
        name: data.name ?? "",
        category: data.category ?? "",
        brand: data.brand ?? "",
        stock: Number(data.stock) || 0,
        price: Number(data.price) || 0,
        reorderLevel: data.reorderLevel ?? 5, // ⭐ BACKEND VALUE LOADED
      });
    } catch (error) {
      console.error(error);
      showSnack("error", "Failed to load product");
    } finally {
      setLoading(false);
    }
  };

  const showSnack = (severity, message) => {
    setSnack({ open: true, severity, message });
  };

  const change = (key, val) => {
    setForm((prev) => ({ ...prev, [key]: val }));
  };

  // Validation
  const validate = () => {
    if (!form.name.trim()) {
      showSnack("warning", "Product name is required");
      return false;
    }
    if (form.stock < 0) {
      showSnack("warning", "Stock cannot be negative");
      return false;
    }
    if (form.price < 0) {
      showSnack("warning", "Price cannot be negative");
      return false;
    }
    if (form.reorderLevel < 0) {
      showSnack("warning", "Reorder level cannot be negative");
      return false;
    }
    return true;
  };

  const save = async () => {
    if (!validate()) return;

    try {
      setLoading(true);

      const dto = {
        sku: form.sku || undefined, // Auto-generate if blank
        name: form.name,
        category: form.category,
        brand: form.brand,
        stock: Number(form.stock),
        price: Number(form.price),
        reorderLevel: Number(form.reorderLevel), // ⭐ LINKED TO BACKEND
      };

      if (isEdit) {
        await ProductService.update(id, dto);
        showSnack("success", "Product updated successfully!");
      } else {
        await ProductService.create(dto);
        showSnack("success", "Product created successfully!");
      }

      setTimeout(() => navigate("/products"), 700);
    } catch (err) {
      console.error(err);
      showSnack(
        "error",
        err?.response?.data?.message || "Failed to save product"
      );
    } finally {
      setLoading(false);
    }
  };

  return (
    <Box sx={{ p: 3 }}>
      <Typography variant="h4" sx={{ mb: 2, fontWeight: 700 }}>
        {isEdit ? "✏️ Edit Product" : "➕ Add New Product"}
      </Typography>

      <Card sx={{ borderRadius: 3 }}>
        <CardContent>
          <Grid container spacing={2}>
            {/* Name */}
            <Grid item xs={12} md={6}>
              <TextField
                label="Product Name"
                fullWidth
                value={form.name}
                onChange={(e) => change("name", e.target.value)}
              />
            </Grid>

            {/* SKU */}
            <Grid item xs={12} md={6}>
              <TextField
                label="SKU (Auto-generate if blank)"
                fullWidth
                value={form.sku}
                onChange={(e) => change("sku", e.target.value)}
              />
            </Grid>

            {/* Category */}
            <Grid item xs={12} md={4}>
              <TextField
                label="Category"
                fullWidth
                value={form.category}
                onChange={(e) => change("category", e.target.value)}
              />
            </Grid>

            {/* Brand */}
            <Grid item xs={12} md={4}>
              <TextField
                label="Brand"
                fullWidth
                value={form.brand}
                onChange={(e) => change("brand", e.target.value)}
              />
            </Grid>

            {/* Stock */}
            <Grid item xs={6} md={2}>
              <TextField
                type="number"
                label="Stock"
                fullWidth
                value={form.stock}
                onChange={(e) => change("stock", Number(e.target.value))}
              />
            </Grid>

            {/* Price */}
            <Grid item xs={6} md={2}>
              <TextField
                type="number"
                label="Price"
                fullWidth
                value={form.price}
                onChange={(e) => change("price", Number(e.target.value))}
              />
            </Grid>

            {/* ⭐ Reorder Level */}
            <Grid item xs={6} md={3}>
              <TextField
                type="number"
                label="Reorder Level"
                fullWidth
                value={form.reorderLevel}
                onChange={(e) =>
                  change("reorderLevel", Number(e.target.value))
                }
              />
            </Grid>

            {/* Buttons */}
            <Grid item xs={12} sx={{ textAlign: "right", mt: 2 }}>
              <Button sx={{ mr: 2 }} onClick={() => navigate("/products")}>
                Cancel
              </Button>

              <Button
                variant="contained"
                color="primary"
                onClick={save}
                disabled={loading}
              >
                {isEdit ? "Update Product" : "Create Product"}
              </Button>
            </Grid>
          </Grid>
        </CardContent>
      </Card>

      {/* Notifications */}
      <Snackbar
        open={snack.open}
        autoHideDuration={3000}
        onClose={() => setSnack({ ...snack, open: false })}
      >
        <Alert
          severity={snack.severity}
          variant="filled"
          onClose={() => setSnack({ ...snack, open: false })}
        >
          {snack.message}
        </Alert>
      </Snackbar>
    </Box>
  );
}



