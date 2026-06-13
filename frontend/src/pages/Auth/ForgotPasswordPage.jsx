import { useState } from 'react';
import { Link } from 'react-router-dom';
import { useAuth } from '../../context/AuthContext';
import { useToast } from '../../components/Toast';

export default function ForgotPasswordPage() {
  const [email, setEmail] = useState('');
  const [sent, setSent] = useState(false);
  const [error, setError] = useState('');
  const [submitting, setSubmitting] = useState(false);
  const { forgotPassword } = useAuth();
  const toast = useToast();

  const handleSubmit = async (e) => {
    e.preventDefault();
    setError('');
    setSubmitting(true);
    try {
      await forgotPassword(email);
      setSent(true);
      toast('Email de réinitialisation envoyé');
    } catch {
      setError('Erreur lors de l\'envoi de l\'email');
    } finally {
      setSubmitting(false);
    }
  };

  return (
    <div className="auth-page">
      <div className="auth-card">
        <h1>MyUniversityApp</h1>
        <h2>Mot de passe oublié</h2>
        {sent ? (
          <>
            <div className="alert alert-success">
              Si un compte existe avec cet email, vous recevrez un lien de réinitialisation.
            </div>
            <p className="auth-link">
              <Link to="/login">Retour à la connexion</Link>
            </p>
          </>
        ) : (
          <>
            {error && <div className="alert alert-error">{error}</div>}
            <form onSubmit={handleSubmit}>
              <div className="form-group">
                <label>Email</label>
                <input type="email" value={email} onChange={(e) => setEmail(e.target.value)} required placeholder="votre@email.com" />
              </div>
              <button type="submit" className="btn btn-primary btn-block" disabled={submitting}>
                {submitting ? 'Envoi...' : 'Envoyer le lien'}
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
