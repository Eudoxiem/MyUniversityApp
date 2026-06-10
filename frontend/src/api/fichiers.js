import api from './axios';

export const getFichiersByEntite = (entiteType, entiteId) => api.get(`/fichiers/entite/${entiteType}/${entiteId}`);
export const getFichier = (id) => api.get(`/fichiers/${id}`);
export const uploadFichier = (file, entiteType, entiteId) => {
  const formData = new FormData();
  formData.append('file', file);
  return api.post(`/fichiers/upload/${entiteType}/${entiteId}`, formData, {
    headers: { 'Content-Type': 'multipart/form-data' },
  });
};
export const deleteFichier = (id) => api.delete(`/fichiers/${id}`);
export const getDownloadUrl = (id) => `/api/fichiers/download/${id}`;
