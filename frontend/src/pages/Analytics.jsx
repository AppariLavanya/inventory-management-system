// src/pages/Analytics.jsx
import React, { useEffect, useState, useRef } from "react";
import {
  Box,
  Typography,
  Grid,
  Card,
  CardContent,
  CircularProgress,
} from "@mui/material";

import {
  PieChart,
  Pie,
  Cell,
  Legend,
  ResponsiveContainer,
  BarChart,
  Bar,
  XAxis,
  YAxis,
  CartesianGrid,
  Tooltip,
} from "recharts";

import TrendingUpIcon from "@mui/icons-material/TrendingUp";
import InventoryIcon from "@mui/icons-material/Inventory";
import ShoppingCartIcon from "@mui/icons-material/ShoppingCart";
import WarningAmberIcon from "@mui/icons-material/WarningAmber";

import api from "../services/api";

export default function Analytics() {
  const [stats, setStats] = useState(null);
  const [dailySales, setDailySales] = useState([]);
  const [loading, setLoading] = useState(true);
  const rootRef = useRef(null);

  useEffect(() => {
    loadStats();
  }, []);

  const loadStats = async () => {
    try {
      const res = await api.get("/analytics/summary");
      setStats(res.data);

      try {
        const daily = await api.get("/analytics/sales-daily");
        setDailySales(daily.data || []);
      } catch {
        setDailySales([]);
      }
    } catch {
      setStats(null);
    } finally {
      setLoading(false);
    }
  };

  const monthlyData =
    stats?.monthlyRevenue?.map((m) => ({
      month: m.month,
      value: m.revenue,
    })) || [];

  const dailyData =
    dailySales.length > 0
      ? dailySales.map((d) => ({ day: d.day, value: d.revenue }))
      : monthlyData.length > 0
      ? monthlyData.map((m) => ({ day: m.month, value: m.value }))
      : [];

  const categoryData =
    stats && stats.categoryCounts
      ? Object.entries(stats.categoryCounts).map(([name, value]) => ({
          name,
          value,
        }))
      : [];

  const PIE_COLORS = [
    "#6a11cb",
    "#2575fc",
    "#ff5e62",
    "#ff9966",
    "#38ef7d",
    "#11998e",
  ];

  const cards = [
    {
      label: "Total Products",
      value: stats?.totalProducts ?? 0,
      icon: <InventoryIcon sx={{ fontSize: 40 }} />,
      bg: "linear-gradient(135deg,#6a11cb,#2575fc)",
    },
    {
      label: "Total Orders",
      value: stats?.totalOrders ?? 0,
      icon: <ShoppingCartIcon sx={{ fontSize: 40 }} />,
      bg: "linear-gradient(135deg,#ff9966,#ff5e62)",
    },
    {
      label: "Revenue",
      value: "₹" + (stats?.totalRevenue ?? 0),
      icon: <TrendingUpIcon sx={{ fontSize: 40 }} />,
      bg: "linear-gradient(135deg,#11998e,#38ef7d)",
    },
    {
      label: "Low Stock Items",
      value: stats?.lowStockCount ?? 0,
      icon: <WarningAmberIcon sx={{ fontSize: 40 }} />,
      bg: "linear-gradient(135deg,#f7971e,#ffd200)",
    },
  ];

  if (loading)
    return (
      <Box sx={{ p: 4, textAlign: "center" }}>
        <CircularProgress />
        <Typography sx={{ mt: 2 }}>Loading analytics...</Typography>
      </Box>
    );

  if (!stats)
    return (
      <Typography sx={{ p: 3, color: "red" }}>
        Failed to load analytics.
      </Typography>
    );

  return (
    <Box sx={{ p: 3 }} ref={rootRef}>
      <Typography variant="h4" sx={{ fontWeight: 700, mb: 3 }}>
        📊 Analytics Dashboard
      </Typography>

      {/* KPI Cards */}
      <Grid container spacing={3}>
        {cards.map((card, i) => (
          <Grid item xs={12} md={3} key={i}>
            <Card
              sx={{
                background: card.bg,
                color: "#fff",
                borderRadius: 3,
                p: 1,
              }}
            >
              <CardContent>
                <Box
                  sx={{
                    display: "flex",
                    justifyContent: "space-between",
                  }}
                >
                  <Box>
                    <Typography variant="h6">{card.label}</Typography>
                    <Typography variant="h4" sx={{ fontWeight: "bold" }}>
                      {card.value}
                    </Typography>
                  </Box>
                  {card.icon}
                </Box>
              </CardContent>
            </Card>
          </Grid>
        ))}
      </Grid>

      {/* Daily Sales + Category Pie */}
      <Grid container spacing={3} sx={{ mt: 2 }}>
        <Grid item xs={12} md={6}>
          <Card sx={{ p: 3 }}>
            <Typography variant="h6" sx={{ mb: 2 }}>
              📅 Daily Sales Trend
            </Typography>

            <div style={{ width: "100%", height: 350 }}>
              <ResponsiveContainer>
                <BarChart data={dailyData}>
                  <CartesianGrid strokeDasharray="3 3" />
                  <XAxis dataKey="day" />
                  <YAxis />
                  <Tooltip />
                  <Bar dataKey="value" fill="#00C49F" />
                </BarChart>
              </ResponsiveContainer>
            </div>
          </Card>
        </Grid>

        <Grid item xs={12} md={6}>
          <Card sx={{ p: 3 }}>
            <Typography variant="h6" sx={{ mb: 2 }}>
              🧁 Product Category Distribution
            </Typography>

            <div style={{ width: "100%", height: 350 }}>
              <ResponsiveContainer>
                <PieChart>
                  <Pie
                    data={categoryData}
                    cx="50%"
                    cy="50%"
                    outerRadius={120}
                    dataKey="value"
                    label
                  >
                    {categoryData.map((entry, i) => (
                      <Cell
                        key={i}
                        fill={PIE_COLORS[i % PIE_COLORS.length]}
                      />
                    ))}
                  </Pie>
                  <Legend />
                </PieChart>
              </ResponsiveContainer>
            </div>
          </Card>
        </Grid>
      </Grid>

      {/* Monthly Trends */}
      <Grid container spacing={3} sx={{ mt: 2 }}>
        <Grid item xs={12} md={6}>
          <Card sx={{ p: 3 }}>
            <Typography variant="h6" sx={{ mb: 2 }}>
              📈 Monthly Revenue Trend
            </Typography>

            <div style={{ width: "100%", height: 350 }}>
              <ResponsiveContainer>
                <BarChart data={monthlyData}>
                  <CartesianGrid strokeDasharray="3 3" />
                  <XAxis dataKey="month" />
                  <YAxis />
                  <Tooltip />
                  <Bar dataKey="value" fill="#2575fc" />
                </BarChart>
              </ResponsiveContainer>
            </div>
          </Card>
        </Grid>

        <Grid item xs={12} md={6}>
          <Card sx={{ p: 3 }}>
            <Typography variant="h6" sx={{ mb: 2 }}>
              💹 Monthly Revenue Comparison
            </Typography>

            <div style={{ width: "100%", height: 350 }}>
              <ResponsiveContainer>
                <BarChart data={monthlyData}>
                  <CartesianGrid strokeDasharray="3 3" />
                  <XAxis dataKey="month" />
                  <YAxis />
                  <Tooltip />
                  <Bar dataKey="value" fill="#ff5e62" />
                </BarChart>
              </ResponsiveContainer>
            </div>
          </Card>
        </Grid>
      </Grid>
    </Box>
  );
}



















