import React from "react";
import { BrowserRouter as Router, Routes, Route, Navigate } from "react-router-dom";

import Navbar from "./layout/Navbar";
import Login from "./pages/Login";

// PRODUCTS
import ProductList from "./pages/ProductList";
import ProductForm from "./pages/ProductForm";

// ORDERS
import OrderList from "./pages/OrderList";
import OrderForm from "./pages/OrderForm";
import OrderView from "./pages/OrderView";
import OrderEdit from "./pages/OrderEdit";

// OTHER PAGES
import LowStock from "./pages/LowStock";
import Analytics from "./pages/Analytics";

// SECURITY
import ProtectedRoute from "./components/ProtectedRoute";

function App() {
  return (
    <Router>
      <Navbar />

      <div style={{ padding: "20px" }}>
        <Routes>

          {/* ---------------------- PUBLIC ---------------------- */}
          <Route path="/login" element={<Login />} />

          {/* ---------------------- PRODUCTS ---------------------- */}
          <Route
            path="/products"
            element={
              <ProtectedRoute>
                <ProductList />
              </ProtectedRoute>
            }
          />

          <Route
            path="/products/new"
            element={
              <ProtectedRoute>
                <ProductForm />
              </ProtectedRoute>
            }
          />

          <Route
            path="/products/:id/edit"
            element={
              <ProtectedRoute>
                <ProductForm />
              </ProtectedRoute>
            }
          />

          {/* ---------------------- ORDERS ---------------------- */}
          <Route
            path="/orders"
            element={
              <ProtectedRoute>
                <OrderList />
              </ProtectedRoute>
            }
          />

          <Route
            path="/orders/new"
            element={
              <ProtectedRoute>
                <OrderForm />
              </ProtectedRoute>
            }
          />

          {/* VIEW ORDER */}
          <Route
            path="/orders/view/:id"
            element={
              <ProtectedRoute>
                <OrderView />
              </ProtectedRoute>
            }
          />

          {/* EDIT ORDER */}
          <Route
            path="/orders/edit/:id"
            element={
              <ProtectedRoute>
                <OrderEdit />
              </ProtectedRoute>
            }
          />

          {/* ---------------------- LOW STOCK ---------------------- */}
          <Route
            path="/low-stock"
            element={
              <ProtectedRoute>
                <LowStock />
              </ProtectedRoute>
            }
          />

          {/* ---------------------- ANALYTICS ---------------------- */}
          <Route
            path="/analytics"
            element={
              <ProtectedRoute>
                <Analytics />
              </ProtectedRoute>
            }
          />

          {/* ---------------------- DEFAULT ---------------------- */}
          <Route path="*" element={<Navigate to="/products" replace />} />

        </Routes>
      </div>
    </Router>
  );
}

export default App;














