import api from './axios';

export const getSalles = () => api.get('/salles');
export const getSalle = (id) => api.get(`/salles/${id}`);
export const createSalle = (data) => api.post('/salles', data);
export const updateSalle = (id, data) => api.put(`/salles/${id}`, data);
export const deleteSalle = (id) => api.delete(`/salles/${id}`);
