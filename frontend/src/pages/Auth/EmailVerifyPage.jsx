import { useEffect, useState } from 'react';
import { Link, useSearchParams } from 'react-router-dom';
import { useAuth } from '../../context/AuthContext';

export default function EmailVerifyPage() {
  const [searchParams] = useSearchParams();
  const token = searchParams.get('token');
  const [status, setStatus] = useState('loading');
  const [message, setMessage] = useState('');
  const { verifyEmail } = useAuth();

  useEffect(() => {
    if (!token) {
      setStatus('error');
      setMessage('Token de vérification manquant.');
      return;
    }

    verifyEmail(token)
      .then((data) => {
        setStatus('success');
        setMessage(data.message);
      })
      .catch((err) => {
        setStatus('error');
        setMessage(err.response?.data?.message || 'Erreur lors de la vérification');
      });
  }, [token, verifyEmail]);

  return (
    <div className="auth-page">
      <div className="auth-card">
        <h1>MyUniversityApp</h1>
        <h2>Vérification d'email</h2>
        {status === 'loading' && <p className="loading">Vérification en cours...</p>}
        {status === 'success' && (
          <>
            <div className="alert alert-success">{message}</div>
            <p className="auth-link">
              <Link to="/login">Se connecter</Link>
            </p>
          </>
        )}
        {status === 'error' && (
          <>
            <div className="alert alert-error">{message}</div>
            <p className="auth-link">
              <Link to="/login">Se connecter</Link>
            </p>
          </>
        )}
      </div>
    </div>
  );
}
