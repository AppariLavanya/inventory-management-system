// src/services/OrderService.js
import api from "./api";

const base = "/orders";

const OrderService = {

  search(params = {}) {
    return api.get(base, { params }).then((r) => r.data);
  },

  get(id) {
    return api.get(`${base}/${id}`).then((r) => r.data);
  },

  create(payload) {
    return api.post(base, payload).then((r) => r.data);
  },

  update(id, payload) {
    return api.put(`${base}/${id}`, payload).then((r) => r.data);
  },

  // Fix: 204 response has no .data
  delete(id) {
    return api.delete(`${base}/${id}`);
  },

  remove(id) {
    return this.delete(id);
  }
};

export default OrderService;








