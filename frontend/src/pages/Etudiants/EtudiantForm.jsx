import { useState, useEffect } from 'react';
import { useNavigate, useParams } from 'react-router-dom';
import { getEtudiant, createEtudiant, updateEtudiant } from '../../api/etudiants';

export default function EtudiantForm() {
  const { id } = useParams();
  const isEdit = Boolean(id);
  const navigate = useNavigate();
  const [form, setForm] = useState({
    matricule: '', nom: '', prenom: '', email: '', telephone: '', dateNaissance: '', adresse: '',
  });
  const [error, setError] = useState('');

  useEffect(() => {
    if (isEdit) {
      getEtudiant(id)
        .then((res) => {
          const e = res.data;
          setForm({
            matricule: e.matricule || '',
            nom: e.nom || '',
            prenom: e.prenom || '',
            email: e.email || '',
            telephone: e.telephone || '',
            dateNaissance: e.dateNaissance ? e.dateNaissance.split('T')[0] : '',
            adresse: e.adresse || '',
          });
        })
        .catch(() => setError('Erreur lors du chargement'));
    }
  }, [id, isEdit]);

  const handleChange = (e) => {
    setForm({ ...form, [e.target.name]: e.target.value });
  };

  const handleSubmit = (e) => {
    e.preventDefault();
    setError('');
    const request = isEdit ? updateEtudiant(id, form) : createEtudiant(form);
    request
      .then(() => navigate('/etudiants'))
      .catch((err) => {
        setError(err.response?.data?.message || 'Erreur lors de l\'enregistrement');
      });
  };

  return (
    <div>
      <div className="page-header">
        <h1>{isEdit ? 'Modifier' : 'Nouvel'} étudiant</h1>
      </div>
      <div className="card">
        {error && <div className="alert alert-error">{error}</div>}
        <form onSubmit={handleSubmit}>
          <div className="form-row">
            <div className="form-group">
              <label>Matricule</label>
              <input name="matricule" value={form.matricule} onChange={handleChange} required />
            </div>
            <div className="form-group">
              <label>Email</label>
              <input name="email" type="email" value={form.email} onChange={handleChange} required />
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
          <div className="form-row">
            <div className="form-group">
              <label>Téléphone</label>
              <input name="telephone" value={form.telephone} onChange={handleChange} />
            </div>
            <div className="form-group">
              <label>Date de naissance</label>
              <input name="dateNaissance" type="date" value={form.dateNaissance} onChange={handleChange} />
            </div>
          </div>
          <div className="form-group">
            <label>Adresse</label>
            <input name="adresse" value={form.adresse} onChange={handleChange} />
          </div>
          <div className="form-actions">
            <button type="submit" className="btn btn-success">
              {isEdit ? 'Enregistrer les modifications' : 'Créer'}
            </button>
            <button type="button" className="btn btn-secondary" onClick={() => navigate('/etudiants')}>
              Annuler
            </button>
          </div>
        </form>
      </div>
    </div>
  );
}
