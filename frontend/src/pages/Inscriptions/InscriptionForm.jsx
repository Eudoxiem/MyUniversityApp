import { useState, useEffect } from 'react';
import { useNavigate, useParams } from 'react-router-dom';
import { getInscription, createInscription, updateInscription } from '../../api/inscriptions';
import { getEtudiants } from '../../api/etudiants';
import { getCours } from '../../api/cours';

export default function InscriptionForm() {
  const { id } = useParams();
  const isEdit = Boolean(id);
  const navigate = useNavigate();
  const [etudiants, setEtudiants] = useState([]);
  const [cours, setCours] = useState([]);
  const [form, setForm] = useState({
    dateInscription: new Date().toISOString().split('T')[0], etudiantId: '', coursId: '',
  });
  const [error, setError] = useState('');

  useEffect(() => {
    getEtudiants().then((res) => setEtudiants(res.data)).catch(() => {});
    getCours().then((res) => setCours(res.data)).catch(() => {});
    if (isEdit) {
      getInscription(id)
        .then((res) => {
          const ins = res.data;
          setForm({
            dateInscription: ins.dateInscription ? ins.dateInscription.split('T')[0] : '',
            etudiantId: ins.etudiantId || '',
            coursId: ins.coursId || '',
          });
        })
        .catch(() => setError('Erreur lors du chargement'));
    }
  }, [id, isEdit]);

  const handleChange = (e) => setForm({ ...form, [e.target.name]: e.target.value });

  const handleSubmit = (e) => {
    e.preventDefault();
    setError('');
    const request = isEdit ? updateInscription(id, form) : createInscription(form);
    request
      .then(() => navigate('/inscriptions'))
      .catch((err) => setError(err.response?.data?.message || 'Erreur lors de l\'enregistrement'));
  };

  return (
    <div>
      <div className="page-header">
        <h1>{isEdit ? 'Modifier' : 'Nouvelle'} inscription</h1>
      </div>
      <div className="card">
        {error && <div className="alert alert-error">{error}</div>}
        <form onSubmit={handleSubmit}>
          <div className="form-row">
            <div className="form-group">
              <label>Étudiant</label>
              <select name="etudiantId" value={form.etudiantId} onChange={handleChange} required>
                <option value="">-- Sélectionner --</option>
                {etudiants.map((e) => (
                  <option key={e.id} value={e.id}>{e.matricule} - {e.nom} {e.prenom}</option>
                ))}
              </select>
            </div>
            <div className="form-group">
              <label>Cours</label>
              <select name="coursId" value={form.coursId} onChange={handleChange} required>
                <option value="">-- Sélectionner --</option>
                {cours.map((c) => (
                  <option key={c.id} value={c.id}>{c.code} - {c.nom}</option>
                ))}
              </select>
            </div>
          </div>
          <div className="form-group">
            <label>Date d'inscription</label>
            <input name="dateInscription" type="date" value={form.dateInscription} onChange={handleChange} required />
          </div>
          <div className="form-actions">
            <button type="submit" className="btn btn-success">
              {isEdit ? 'Enregistrer les modifications' : 'Créer'}
            </button>
            <button type="button" className="btn btn-secondary" onClick={() => navigate('/inscriptions')}>
              Annuler
            </button>
          </div>
        </form>
      </div>
    </div>
  );
}
