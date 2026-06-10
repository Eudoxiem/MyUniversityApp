import api from './axios';

export const getStatsGenerales = () => api.get('/stats/general');
export const getStatsCours = () => api.get('/stats/cours');
