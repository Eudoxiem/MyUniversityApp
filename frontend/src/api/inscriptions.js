import api from './axios';

export const getInscriptions = () => api.get('/inscriptions');
export const getInscription = (id) => api.get(`/inscriptions/${id}`);
export const createInscription = (data) => api.post('/inscriptions', data);
export const updateInscription = (id, data) => api.put(`/inscriptions/${id}`, data);
export const deleteInscription = (id) => api.delete(`/inscriptions/${id}`);
