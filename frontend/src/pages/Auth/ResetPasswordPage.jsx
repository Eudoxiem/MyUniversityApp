import { useState } from 'react';
import { Link, useSearchParams } from 'react-router-dom';
import { useAuth } from '../../context/AuthContext';
import { useToast } from '../../components/Toast';

export default function ResetPasswordPage() {
  const [searchParams] = useSearchParams();
  const token = searchParams.get('token');
  const [nouveauMotDePasse, setNouveauMotDePasse] = useState('');
  const [confirmPassword, setConfirmPassword] = useState('');
  const [showPassword, setShowPassword] = useState(false);
  const [error, setError] = useState('');
  const [success, setSuccess] = useState(false);
  const [submitting, setSubmitting] = useState(false);
  const { resetPassword } = useAuth();
  const toast = useToast();

  const handleSubmit = async (e) => {
    e.preventDefault();
    setError('');

    if (nouveauMotDePasse !== confirmPassword) {
      setError('Les mots de passe ne correspondent pas');
      return;
    }

    if (nouveauMotDePasse.length < 6) {
      setError('Le mot de passe doit contenir au moins 6 caractères');
      return;
    }

    setSubmitting(true);
    try {
      await resetPassword(token, nouveauMotDePasse);
      setSuccess(true);
      toast('Mot de passe réinitialisé avec succès');
    } catch (err) {
      setError(err.response?.data?.message || 'Erreur lors de la réinitialisation');
    } finally {
      setSubmitting(false);
    }
  };

  if (!token) {
    return (
      <div className="auth-page">
        <div className="auth-card">
          <h1>MyUniversityApp</h1>
          <h2>Lien invalide</h2>
          <div className="alert alert-error">Ce lien de réinitialisation est invalide.</div>
          <p className="auth-link">
            <Link to="/forgot-password">Demander un nouveau lien</Link>
          </p>
        </div>
      </div>
    );
  }

  return (
    <div className="auth-page">
      <div className="auth-card">
        <h1>MyUniversityApp</h1>
        <h2>Réinitialiser le mot de passe</h2>
        {success ? (
          <>
            <div className="alert alert-success">
              Mot de passe réinitialisé avec succès ! Vous pouvez maintenant vous connecter.
            </div>
            <p className="auth-link">
              <Link to="/login">Se connecter</Link>
            </p>
          </>
        ) : (
          <>
            {error && <div className="alert alert-error">{error}</div>}
            <form onSubmit={handleSubmit}>
              <div className="form-group">
                <label>Nouveau mot de passe</label>
                <div className="password-wrapper">
                  <input type={showPassword ? 'text' : 'password'} value={nouveauMotDePasse} onChange={(e) => setNouveauMotDePasse(e.target.value)} required placeholder="Au moins 6 caractères" />
                  <button type="button" className="password-toggle" onClick={() => setShowPassword(!showPassword)} aria-label={showPassword ? 'Masquer' : 'Afficher'}>
                    {showPassword ? (
                      <svg xmlns="http://www.w3.org/2000/svg" width="20" height="20" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2" strokeLinecap="round" strokeLinejoin="round">
                        <path d="M17.94 17.94A10.07 10.07 0 0 1 12 20c-7 0-11-8-11-8a18.45 18.45 0 0 1 5.06-5.94" />
                        <path d="M9.9 4.24A9.12 9.12 0 0 1 12 4c7 0 11 8 11 8a18.5 18.5 0 0 1-2.16 3.19" />
                        <line x1="1" y1="1" x2="23" y2="23" />
                      </svg>
                    ) : (
                      <svg xmlns="http://www.w3.org/2000/svg" width="20" height="20" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2" strokeLinecap="round" strokeLinejoin="round">
                        <path d="M1 12s4-8 11-8 11 8 11 8-4 8-11 8-11-8-11-8z" />
                        <circle cx="12" cy="12" r="3" />
                      </svg>
                    )}
                  </button>
                </div>
              </div>
              <div className="form-group">
                <label>Confirmer le mot de passe</label>
                <input type="password" value={confirmPassword} onChange={(e) => setConfirmPassword(e.target.value)} required placeholder="Répétez le mot de passe" />
              </div>
              <button type="submit" className="btn btn-primary btn-block" disabled={submitting}>
                {submitting ? 'Réinitialisation...' : 'Réinitialiser'}
              </button>
            </form>
            <p className="auth-link">
              <Link to="/login">Retour à la connexion</Link>
            </p>
          </>
        )}
      </div>
    </div>
  );
}
