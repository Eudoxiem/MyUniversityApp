import { createContext, useContext, useState, useCallback, useEffect } from 'react';
import {
  login as apiLogin,
  register as apiRegister,
  logoutApi,
  verifyEmail as apiVerifyEmail,
  forgotPassword as apiForgotPassword,
  resetPassword as apiResetPassword,
  resendVerification as apiResendVerification,
} from '../api/auth';

const AuthContext = createContext(null);

export function useAuth() {
  return useContext(AuthContext);
}

export function AuthProvider({ children }) {
  const [user, setUser] = useState(null);
  const [token, setToken] = useState(null);
  const [refreshToken, setRefreshToken] = useState(null);
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    const savedToken = localStorage.getItem('token');
    const savedRefreshToken = localStorage.getItem('refreshToken');
    const savedUser = localStorage.getItem('user');
    if (savedToken && savedUser) {
      setToken(savedToken);
      setRefreshToken(savedRefreshToken);
      setUser(JSON.parse(savedUser));
    }
    setLoading(false);
  }, []);

  const login = useCallback(async (email, password) => {
    const res = await apiLogin({ email, password });
    const data = res.data;
    localStorage.setItem('token', data.token);
    localStorage.setItem('refreshToken', data.refreshToken);
    localStorage.setItem('user', JSON.stringify({
      userId: data.userId, email: data.email, nom: data.nom, prenom: data.prenom, role: data.role,
    }));
    setToken(data.token);
    setRefreshToken(data.refreshToken);
    setUser({ userId: data.userId, email: data.email, nom: data.nom, prenom: data.prenom, role: data.role });
    return data;
  }, []);

  const register = useCallback(async (data) => {
    const res = await apiRegister(data);
    return res.data;
  }, []);

  const logout = useCallback(async () => {
    try {
      await logoutApi();
    } catch {
      // déconnexion côté serveur ignorée si échoue
    }
    localStorage.removeItem('token');
    localStorage.removeItem('refreshToken');
    localStorage.removeItem('user');
    setToken(null);
    setRefreshToken(null);
    setUser(null);
  }, []);

  const verifyEmail = useCallback(async (token) => {
    const res = await apiVerifyEmail(token);
    return res.data;
  }, []);

  const forgotPassword = useCallback(async (email) => {
    const res = await apiForgotPassword(email);
    return res.data;
  }, []);

  const resetPassword = useCallback(async (token, nouveauMotDePasse) => {
    const res = await apiResetPassword(token, nouveauMotDePasse);
    return res.data;
  }, []);

  const resendVerification = useCallback(async (email) => {
    const res = await apiResendVerification(email);
    return res.data;
  }, []);

  return (
    <AuthContext.Provider value={{
      user, token, refreshToken, loading,
      login, register, logout, verifyEmail, forgotPassword, resetPassword, resendVerification,
    }}>
      {children}
    </AuthContext.Provider>
  );
}
