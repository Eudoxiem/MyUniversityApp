import api from './axios';

export const login = (data) => api.post('/auth/login', data);
export const register = (data) => api.post('/auth/register', data);
export const logoutApi = () => api.post('/auth/logout');
export const refreshTokenApi = (refreshToken) => api.post('/auth/refresh', { refreshToken });
export const verifyEmail = (token) => api.get(`/auth/verify?token=${token}`);
export const forgotPassword = (email) => api.post('/auth/forgot-password', { email });
export const resetPassword = (token, nouveauMotDePasse) => api.post('/auth/reset-password', { token, nouveauMotDePasse });
export const resendVerification = (email) => api.post(`/auth/verify/resend?email=${email}`);
