import api from './axios';

export const getEtudiants = () => api.get('/etudiants');
export const getEtudiant = (id) => api.get(`/etudiants/${id}`);
export const createEtudiant = (data) => api.post('/etudiants', data);
export const updateEtudiant = (id, data) => api.put(`/etudiants/${id}`, data);
export const deleteEtudiant = (id) => api.delete(`/etudiants/${id}`);
