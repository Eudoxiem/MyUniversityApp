import { useState, useEffect } from 'react';
import { useNavigate, useParams } from 'react-router-dom';
import { getProfesseur, createProfesseur, updateProfesseur } from '../../api/professeurs';
import { useToast } from '../../components/Toast';

export default function ProfesseurForm() {
  const { id } = useParams();
  const isEdit = Boolean(id);
  const navigate = useNavigate();
  const toast = useToast();
  const [form, setForm] = useState({
    numeroEmploye: '', nom: '', prenom: '', email: '', telephone: '', specialite: '',
  });
  const [error, setError] = useState('');

  useEffect(() => {
    if (isEdit) {
      getProfesseur(id)
        .then((res) => {
          const p = res.data;
          setForm({
            numeroEmploye: p.numeroEmploye || '',
            nom: p.nom || '',
            prenom: p.prenom || '',
            email: p.email || '',
            telephone: p.telephone || '',
            specialite: p.specialite || '',
          });
        })
        .catch(() => setError('Erreur lors du chargement'));
    }
  }, [id, isEdit]);

  const handleChange = (e) => setForm({ ...form, [e.target.name]: e.target.value });

  const handleSubmit = (e) => {
    e.preventDefault();
    setError('');
    const request = isEdit ? updateProfesseur(id, form) : createProfesseur(form);
    request
      .then(() => {
        toast(isEdit ? 'Professeur modifié avec succès' : 'Professeur créé avec succès');
        navigate('/professeurs');
      })
      .catch((err) => {
        const msg = err.response?.data?.message || 'Erreur lors de l\'enregistrement';
        setError(msg);
        toast(msg, 'error');
      });
  };

  return (
    <div>
      <div className="page-header">
        <h1>{isEdit ? 'Modifier' : 'Nouveau'} professeur</h1>
      </div>
      <div className="card">
        {error && <div className="alert alert-error">{error}</div>}
        <form onSubmit={handleSubmit}>
          <div className="form-row">
            <div className="form-group">
              <label>N° Employé</label>
              <input name="numeroEmploye" value={form.numeroEmploye} onChange={handleChange} required />
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
              <label>Spécialité</label>
              <input name="specialite" value={form.specialite} onChange={handleChange} />
            </div>
          </div>
          <div className="form-actions">
            <button type="submit" className="btn btn-success">
              {isEdit ? 'Enregistrer les modifications' : 'Créer'}
            </button>
            <button type="button" className="btn btn-secondary" onClick={() => navigate('/professeurs')}>
              Annuler
            </button>
          </div>
        </form>
      </div>
    </div>
  );
}
