import { useState, useEffect } from 'react';
import { useAuth } from '../../context/AuthContext';
import { useToast } from '../../components/Toast';
import { getProfile, updateProfile, changePassword } from '../../api/profile';

function EyeIcon() {
  return (
    <svg viewBox="0 0 24 24" width="20" height="20" fill="currentColor">
      <path d="M12 4.5C7 4.5 2.73 7.61 1 12c1.73 4.39 6 7.5 11 7.5s9.27-3.11 11-7.5c-1.73-4.39-6-7.5-11-7.5zM12 17c-2.76 0-5-2.24-5-5s2.24-5 5-5 5 2.24 5 5-2.24 5-5 5zm0-8c-1.66 0-3 1.34-3 3s1.34 3 3 3 3-1.34 3-3-1.34-3-3-3z" />
    </svg>
  );
}

function EyeOffIcon() {
  return (
    <svg viewBox="0 0 24 24" width="20" height="20" fill="currentColor">
      <path d="M12 7c2.76 0 5 2.24 5 5 0 .65-.13 1.26-.36 1.83l2.92 2.92c1.51-1.26 2.7-2.89 3.43-4.75-1.73-4.39-6-7.5-11-7.5-1.4 0-2.74.25-3.98.7l2.16 2.16C10.74 7.13 11.35 7 12 7zM2 4.27l2.28 2.28.46.46C3.08 8.3 1.78 10.02 1 12c1.73 4.39 6 7.5 11 7.5 1.55 0 3.03-.3 4.38-.84l.42.42L19.73 22 21 20.73 3.27 3 2 4.27zM7.53 9.8l1.55 1.55c-.05.21-.08.43-.08.65 0 1.66 1.34 3 3 3 .22 0 .44-.03.65-.08l1.55 1.55c-.67.33-1.41.53-2.2.53-2.76 0-5-2.24-5-5 0-.79.2-1.53.53-2.2zm4.31-.78l3.15 3.15.02-.16c0-1.66-1.34-3-3-3l-.17.01z" />
    </svg>
  );
}

function PasswordInput({ id, label, value, onChange, show, onToggle }) {
  return (
    <div className="form-group">
      <label htmlFor={id}>{label}</label>
      <div className="password-wrapper">
        <input
          id={id}
          type={show ? 'text' : 'password'}
          value={value}
          onChange={onChange}
          required
          placeholder="••••••••"
        />
        <button type="button" className="password-toggle" onClick={onToggle} tabIndex={-1}>
          {show ? <EyeOffIcon /> : <EyeIcon />}
        </button>
      </div>
    </div>
  );
}

export default function ProfilePage() {
  const { user } = useAuth();
  const toast = useToast();

  const [mode, setMode] = useState('view');
  const [profile, setProfile] = useState(null);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState('');

  const [nom, setNom] = useState('');
  const [prenom, setPrenom] = useState('');

  const [currentPassword, setCurrentPassword] = useState('');
  const [newPassword, setNewPassword] = useState('');
  const [confirmPassword, setConfirmPassword] = useState('');
  const [showCurrent, setShowCurrent] = useState(false);
  const [showNew, setShowNew] = useState(false);
  const [showConfirm, setShowConfirm] = useState(false);

  const [submitting, setSubmitting] = useState(false);

  useEffect(() => {
    fetchProfile();
  }, []);

  const fetchProfile = async () => {
    try {
      const res = await getProfile();
      setProfile(res.data);
      setNom(res.data.nom);
      setPrenom(res.data.prenom);
    } catch {
      setError('Erreur lors du chargement du profil');
    } finally {
      setLoading(false);
    }
  };

  const handleEditSubmit = async (e) => {
    e.preventDefault();
    setError('');
    setSubmitting(true);
    try {
      const res = await updateProfile({ nom, prenom });
      setProfile(res.data);
      toast('Profil mis à jour avec succès');
      setMode('view');
    } catch {
      setError('Erreur lors de la mise à jour du profil');
    } finally {
      setSubmitting(false);
    }
  };

  const handlePasswordSubmit = async (e) => {
    e.preventDefault();
    setError('');
    if (newPassword !== confirmPassword) {
      setError('Les nouveaux mots de passe ne correspondent pas');
      return;
    }
    setSubmitting(true);
    try {
      await changePassword({ currentPassword, newPassword });
      toast('Mot de passe modifié avec succès');
      setMode('view');
      setCurrentPassword('');
      setNewPassword('');
      setConfirmPassword('');
    } catch {
      setError('Mot de passe actuel incorrect');
    } finally {
      setSubmitting(false);
    }
  };

  const cancelEdit = () => {
    setNom(profile.nom);
    setPrenom(profile.prenom);
    setError('');
    setMode('view');
  };

  const cancelPassword = () => {
    setCurrentPassword('');
    setNewPassword('');
    setConfirmPassword('');
    setError('');
    setMode('view');
  };

  if (loading) {
    return <div className="loading">Chargement...</div>;
  }

  return (
    <div>
      <div className="page-header">
        <h1>Mon Profil</h1>
      </div>

      {error && <div className="alert alert-error">{error}</div>}

      {mode === 'view' && profile && (
        <div className="profile-view card">
          <div className="profile-avatar">
            {profile.prenom?.charAt(0)}{profile.nom?.charAt(0)}
          </div>
          <div className="profile-info">
            <div className="profile-row">
              <span className="profile-label">Email</span>
              <span className="profile-value">{profile.email}</span>
            </div>
            <div className="profile-row">
              <span className="profile-label">Nom</span>
              <span className="profile-value">{profile.nom}</span>
            </div>
            <div className="profile-row">
              <span className="profile-label">Prénom</span>
              <span className="profile-value">{profile.prenom}</span>
            </div>
            <div className="profile-row">
              <span className="profile-label">Rôle</span>
              <span className="profile-value">
                <span className="profile-role-badge">{profile.role}</span>
              </span>
            </div>
          </div>
          <div className="profile-actions">
            <button className="btn btn-primary" onClick={() => setMode('edit')}>
              Modifier le profil
            </button>
            <button className="btn btn-outline" onClick={() => setMode('password')}>
              Changer le mot de passe
            </button>
          </div>
        </div>
      )}

      {mode === 'edit' && (
        <form onSubmit={handleEditSubmit} className="card">
          <h2 className="profile-form-title">Modifier le profil</h2>
          <div className="form-row">
            <div className="form-group">
              <label htmlFor="edit-nom">Nom</label>
              <input id="edit-nom" type="text" value={nom} onChange={(e) => setNom(e.target.value)} required />
            </div>
            <div className="form-group">
              <label htmlFor="edit-prenom">Prénom</label>
              <input id="edit-prenom" type="text" value={prenom} onChange={(e) => setPrenom(e.target.value)} required />
            </div>
          </div>
          <div className="form-actions">
            <button type="submit" className="btn btn-primary" disabled={submitting}>
              {submitting ? 'Enregistrement...' : 'Enregistrer'}
            </button>
            <button type="button" className="btn btn-secondary" onClick={cancelEdit}>
              Annuler
            </button>
          </div>
        </form>
      )}

      {mode === 'password' && (
        <form onSubmit={handlePasswordSubmit} className="card">
          <h2 className="profile-form-title">Changer le mot de passe</h2>
          <PasswordInput
            id="current-password"
            label="Mot de passe actuel"
            value={currentPassword}
            onChange={(e) => setCurrentPassword(e.target.value)}
            show={showCurrent}
            onToggle={() => setShowCurrent(!showCurrent)}
          />
          <PasswordInput
            id="new-password"
            label="Nouveau mot de passe"
            value={newPassword}
            onChange={(e) => setNewPassword(e.target.value)}
            show={showNew}
            onToggle={() => setShowNew(!showNew)}
          />
          <PasswordInput
            id="confirm-password"
            label="Confirmer le nouveau mot de passe"
            value={confirmPassword}
            onChange={(e) => setConfirmPassword(e.target.value)}
            show={showConfirm}
            onToggle={() => setShowConfirm(!showConfirm)}
          />
          <div className="form-actions">
            <button type="submit" className="btn btn-primary" disabled={submitting}>
              {submitting ? 'Modification...' : 'Modifier le mot de passe'}
            </button>
            <button type="button" className="btn btn-secondary" onClick={cancelPassword}>
              Annuler
            </button>
          </div>
        </form>
      )}
    </div>
  );
}
