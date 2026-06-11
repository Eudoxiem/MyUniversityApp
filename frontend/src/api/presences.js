import api from './axios';

export const getPresence = (id) => api.get(`/presences/${id}`);
export const getPresencesByCoursAndDate = (coursId, date) => api.get(`/presences/cours/${coursId}`, { params: { date } });
export const getPresencesByEtudiant = (etudiantId) => api.get(`/presences/etudiant/${etudiantId}`);
export const getPresencesByEtudiantAndCours = (etudiantId, coursId) => api.get(`/presences/etudiant/${etudiantId}/cours/${coursId}`);
export const createPresence = (data) => api.post('/presences', data);
export const faireAppel = (data) => api.post('/presences/appel', data);
export const updatePresence = (id, data) => api.put(`/presences/${id}`, data);
export const deletePresence = (id) => api.delete(`/presences/${id}`);
