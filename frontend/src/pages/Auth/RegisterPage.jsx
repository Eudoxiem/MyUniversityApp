import { useState } from 'react';
import { Link, useNavigate } from 'react-router-dom';
import { useAuth } from '../../context/AuthContext';
import { useToast } from '../../components/Toast';

export default function RegisterPage() {
  const [form, setForm] = useState({ email: '', password: '', nom: '', prenom: '', role: 'ROLE_ETUDIANT' });
  const [error, setError] = useState('');
  const [submitting, setSubmitting] = useState(false);
  const { register } = useAuth();
  const navigate = useNavigate();
  const toast = useToast();

  const handleChange = (e) => setForm({ ...form, [e.target.name]: e.target.value });

  const handleSubmit = async (e) => {
    e.preventDefault();
    setError('');
    setSubmitting(true);
    try {
      await register(form);
      toast('Inscription réussie');
      navigate('/');
    } catch {
      setError('Erreur lors de l\'inscription');
    } finally {
      setSubmitting(false);
    }
  };

  return (
    <div className="auth-page">
      <div className="auth-card">
        <h1>MyUniversityApp</h1>
        <h2>Inscription</h2>
        {error && <div className="alert alert-error">{error}</div>}
        <form onSubmit={handleSubmit}>
          <div className="form-row">
            <div className="form-group">
              <label>Nom</label>
              <input name="nom" value={form.nom} onChange={handleChange} required />
            </div>
            <div className="form-group">
              <label>Prénom</label>
              <input name="prenom" value={form.prenom} onChange={handleChange} required />
            </div>
          </div>
          <div className="form-group">
            <label>Email</label>
            <input name="email" type="email" value={form.email} onChange={handleChange} required />
          </div>
          <div className="form-group">
            <label>Mot de passe</label>
            <input name="password" type="password" value={form.password} onChange={handleChange} required />
          </div>
          <div className="form-group">
            <label>Rôle</label>
            <select name="role" value={form.role} onChange={handleChange}>
              <option value="ROLE_ETUDIANT">Étudiant</option>
              <option value="ROLE_PROFESSEUR">Professeur</option>
              <option value="ROLE_ADMIN">Admin</option>
            </select>
          </div>
          <button type="submit" className="btn btn-primary btn-block" disabled={submitting}>
            {submitting ? 'Inscription...' : "S'inscrire"}
          </button>
        </form>
        <p className="auth-link">
          Déjà un compte ? <Link to="/login">Se connecter</Link>
        </p>
      </div>
    </div>
  );
}
