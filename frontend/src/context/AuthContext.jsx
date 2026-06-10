import { createContext, useContext, useState, useCallback, useEffect } from 'react';
import { login as apiLogin, register as apiRegister } from '../api/auth';

const AuthContext = createContext(null);

export function useAuth() {
  return useContext(AuthContext);
}

export function AuthProvider({ children }) {
  const [user, setUser] = useState(null);
  const [token, setToken] = useState(null);
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    const savedToken = localStorage.getItem('token');
    const savedUser = localStorage.getItem('user');
    if (savedToken && savedUser) {
      setToken(savedToken);
      setUser(JSON.parse(savedUser));
    }
    setLoading(false);
  }, []);

  const login = useCallback(async (email, password) => {
    const res = await apiLogin({ email, password });
    const data = res.data;
    localStorage.setItem('token', data.token);
    localStorage.setItem('user', JSON.stringify({ userId: data.userId, email: data.email, nom: data.nom, prenom: data.prenom, role: data.role }));
    setToken(data.token);
    setUser({ userId: data.userId, email: data.email, nom: data.nom, prenom: data.prenom, role: data.role });
    return data;
  }, []);

  const register = useCallback(async (data) => {
    const res = await apiRegister(data);
    const authData = res.data;
    localStorage.setItem('token', authData.token);
    localStorage.setItem('user', JSON.stringify({ userId: authData.userId, email: authData.email, nom: authData.nom, prenom: authData.prenom, role: authData.role }));
    setToken(authData.token);
    setUser({ userId: authData.userId, email: authData.email, nom: authData.nom, prenom: authData.prenom, role: authData.role });
    return authData;
  }, []);

  const logout = useCallback(() => {
    localStorage.removeItem('token');
    localStorage.removeItem('user');
    setToken(null);
    setUser(null);
  }, []);

  return (
    <AuthContext.Provider value={{ user, token, loading, login, register, logout }}>
      {children}
    </AuthContext.Provider>
  );
}
