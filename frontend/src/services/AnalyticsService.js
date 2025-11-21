// src/services/AnalyticsService.js
import api from "./api";

const base = "/analytics";

const AnalyticsService = {
  getSummary() {
    return api.get(`${base}/summary`).then((res) => res.data);
  },

  getDaily() {
    return api.get(`${base}/sales-daily`).then((res) => res.data);
  },

  getLowStock(threshold = 5) {
    return api
      .get(`${base}/low-stock`, { params: { threshold } })
      .then((res) => res.data);
  },

  exportExcel() {
    window.open(`${api.defaults.baseURL}${base}/export/excel`, "_blank");
  },

  exportPdf() {
    window.open(`${api.defaults.baseURL}${base}/export/pdf`, "_blank");
  },
};

export default AnalyticsService;
