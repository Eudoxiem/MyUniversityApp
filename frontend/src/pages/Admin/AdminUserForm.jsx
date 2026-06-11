import { useState, useEffect } from 'react';
import { useNavigate, useParams } from 'react-router-dom';
import { getUser, createUser, updateUser } from '../../api/admin';
import { useToast } from '../../components/Toast';

const roleLabels = {
  ROLE_ADMIN: 'Admin',
  ROLE_PROFESSEUR: 'Professeur',
  ROLE_ETUDIANT: 'Étudiant',
};

export default function AdminUserForm() {
  const { id } = useParams();
  const isEdit = Boolean(id);
  const navigate = useNavigate();
  const toast = useToast();
  const [form, setForm] = useState({
    email: '', password: '', nom: '', prenom: '', role: 'ROLE_ETUDIANT',
  });
  const [error, setError] = useState('');

  useEffect(() => {
    if (isEdit) {
      getUser(id)
        .then((res) => {
          const u = res.data;
          setForm({
            email: u.email || '',
            password: '',
            nom: u.nom || '',
            prenom: u.prenom || '',
            role: u.role || 'ROLE_ETUDIANT',
          });
        })
        .catch(() => setError('Erreur lors du chargement'));
    }
  }, [id, isEdit]);

  const handleChange = (e) => setForm({ ...form, [e.target.name]: e.target.value });

  const handleSubmit = (e) => {
    e.preventDefault();
    setError('');
    const payload = isEdit
      ? { nom: form.nom, prenom: form.prenom, role: form.role }
      : form;
    const request = isEdit ? updateUser(id, payload) : createUser(payload);
    request
      .then(() => {
        toast(isEdit ? 'Utilisateur modifié avec succès' : 'Utilisateur créé avec succès');
        navigate('/admin/utilisateurs');
      })
      .catch((err) => {
        const msg = err.response?.data?.message || "Erreur lors de l'enregistrement";
        setError(msg);
        toast(msg, 'error');
      });
  };

  return (
    <div>
      <div className="page-header">
        <h1>{isEdit ? 'Modifier' : 'Nouvel'} utilisateur</h1>
      </div>
      <div className="card">
        {error && <div className="alert alert-error">{error}</div>}
        <form onSubmit={handleSubmit}>
          <div className="form-row">
            <div className="form-group">
              <label>Email</label>
              <input name="email" type="email" value={form.email} onChange={handleChange} required={!isEdit} readOnly={isEdit} />
            </div>
            <div className="form-group">
              <label>Mot de passe</label>
              <input name="password" type="password" value={form.password} onChange={handleChange} required={!isEdit} placeholder={isEdit ? 'Laisser vide pour conserver' : ''} />
            </div>
          </div>
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
            <label>Rôle</label>
            <select name="role" value={form.role} onChange={handleChange} required>
              {Object.entries(roleLabels).map(([value, label]) => (
                <option key={value} value={value}>{label}</option>
              ))}
            </select>
          </div>
          <div className="form-actions">
            <button type="submit" className="btn btn-success">
              {isEdit ? 'Enregistrer les modifications' : 'Créer'}
            </button>
            <button type="button" className="btn btn-secondary" onClick={() => navigate('/admin/utilisateurs')}>Annuler</button>
          </div>
        </form>
      </div>
    </div>
  );
}
