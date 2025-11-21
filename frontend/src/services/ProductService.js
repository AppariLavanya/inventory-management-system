// src/services/ProductService.js
import api from "./api";

const base = "/products";

const ProductService = {
  // SEARCH
  search(params = {}) {
    return api.get(base, { params }).then((res) => res.data);
  },

  // CRUD
  create(payload) {
    return api.post(base, payload).then((res) => res.data);
  },

  update(id, payload) {
    return api.put(`${base}/${id}`, payload).then((res) => res.data);
  },

  delete(id) {
    return api.delete(`${base}/${id}`).then((res) => res.data);
  },

  deleteMany(ids) {
    return api.delete(base, { data: ids }).then((res) => res.data);
  },

  get(id) {
    return api.get(`${base}/${id}`).then((res) => res.data);
  },

  // LOW STOCK
  lowStock(threshold = 5) {
    return api
      .get(`/analytics/low-stock`, { params: { threshold } })
      .then((res) => res.data);
  },

  // EXPORTS
  exportCSV() {
    window.location.href = `${api.defaults.baseURL}/products/export/csv`;
  },

  exportExcel() {
    window.location.href = `${api.defaults.baseURL}/products/export/excel`;
  },

  exportPDF() {
    window.location.href = `${api.defaults.baseURL}/products/export/pdf`;
  },
};

export default ProductService;


















