import api from './axios';

export const getEmploisDuTemps = () => api.get('/emploi-du-temps');
export const getEmploiDuTemps = (id) => api.get(`/emploi-du-temps/${id}`);
export const getEDTBySemestre = (semestre, annee) => api.get(`/emploi-du-temps/semestre?semestre=${semestre}&annee=${annee}`);
export const createEmploiDuTemps = (data) => api.post('/emploi-du-temps', data);
export const updateEmploiDuTemps = (id, data) => api.put(`/emploi-du-temps/${id}`, data);
export const deleteEmploiDuTemps = (id) => api.delete(`/emploi-du-temps/${id}`);
