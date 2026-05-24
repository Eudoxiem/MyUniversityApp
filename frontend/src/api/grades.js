import api from './axios';

export const getGrades = () => api.get('/grades');
export const getGrade = (id) => api.get(`/grades/${id}`);
export const createGrade = (data) => api.post('/grades', data);
export const updateGrade = (id, data) => api.put(`/grades/${id}`, data);
export const deleteGrade = (id) => api.delete(`/grades/${id}`);
