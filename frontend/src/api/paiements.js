import api from './axios';

export const getPaiements = () => api.get('/paiements');
export const getPaiement = (id) => api.get(`/paiements/${id}`);
export const getPaiementsByEtudiant = (etudiantId) => api.get(`/paiements/etudiant/${etudiantId}`);
export const getPaiementsEnRetard = () => api.get('/paiements/en-retard');
export const createPaiement = (data) => api.post('/paiements', data);
export const updatePaiement = (id, data) => api.put(`/paiements/${id}`, data);
export const deletePaiement = (id) => api.delete(`/paiements/${id}`);
