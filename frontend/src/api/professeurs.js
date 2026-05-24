import api from './axios';

export const getProfesseurs = () => api.get('/professeurs');
export const getProfesseur = (id) => api.get(`/professeurs/${id}`);
export const createProfesseur = (data) => api.post('/professeurs', data);
export const updateProfesseur = (id, data) => api.put(`/professeurs/${id}`, data);
export const deleteProfesseur = (id) => api.delete(`/professeurs/${id}`);
