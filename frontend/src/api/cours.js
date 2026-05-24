import api from './axios';

export const getCours = () => api.get('/cours');
export const getCour = (id) => api.get(`/cours/${id}`);
export const createCour = (data) => api.post('/cours', data);
export const updateCour = (id, data) => api.put(`/cours/${id}`, data);
export const deleteCour = (id) => api.delete(`/cours/${id}`);
