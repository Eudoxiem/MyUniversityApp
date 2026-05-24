import { useState, useEffect } from 'react';
import { useNavigate, useParams } from 'react-router-dom';
import { getCour, createCour, updateCour } from '../../api/cours';
import { getProfesseurs } from '../../api/professeurs';
import { getSalles } from '../../api/salles';

export default function CoursForm() {
  const { id } = useParams();
  const isEdit = Boolean(id);
  const navigate = useNavigate();
  const [professeurs, setProfesseurs] = useState([]);
  const [salles, setSalles] = useState([]);
  const [form, setForm] = useState({
    code: '', nom: '', credits: '', description: '', professeurId: '', salleId: '',
  });
  const [error, setError] = useState('');

  useEffect(() => {
    getProfesseurs().then((res) => setProfesseurs(res.data)).catch(() => {});
    getSalles().then((res) => setSalles(res.data)).catch(() => {});
    if (isEdit) {
      getCour(id)
        .then((res) => {
          const c = res.data;
          setForm({
            code: c.code || '',
            nom: c.nom || '',
            credits: c.credits || '',
            description: c.description || '',
            professeurId: c.professeurId || '',
            salleId: c.salleId || '',
          });
        })
        .catch(() => setError('Erreur lors du chargement'));
    }
  }, [id, isEdit]);

  const handleChange = (e) => setForm({ ...form, [e.target.name]: e.target.value });

  const handleSubmit = (e) => {
    e.preventDefault();
    setError('');
    const payload = { ...form, credits: Number(form.credits) };
    const request = isEdit ? updateCour(id, payload) : createCour(payload);
    request
      .then(() => navigate('/cours'))
      .catch((err) => setError(err.response?.data?.message || 'Erreur lors de l\'enregistrement'));
  };

  return (
    <div>
      <div className="page-header">
        <h1>{isEdit ? 'Modifier' : 'Nouveau'} cours</h1>
      </div>
      <div className="card">
        {error && <div className="alert alert-error">{error}</div>}
        <form onSubmit={handleSubmit}>
          <div className="form-row">
            <div className="form-group">
              <label>Code</label>
              <input name="code" value={form.code} onChange={handleChange} required />
            </div>
            <div className="form-group">
              <label>Nom</label>
              <input name="nom" value={form.nom} onChange={handleChange} required />
            </div>
          </div>
          <div className="form-row">
            <div className="form-group">
              <label>Crédits</label>
              <input name="credits" type="number" value={form.credits} onChange={handleChange} required />
            </div>
            <div className="form-group">
              <label>Professeur</label>
              <select name="professeurId" value={form.professeurId} onChange={handleChange}>
                <option value="">-- Sélectionner --</option>
                {professeurs.map((p) => (
                  <option key={p.id} value={p.id}>{p.nom} {p.prenom}</option>
                ))}
              </select>
            </div>
          </div>
          <div className="form-row">
            <div className="form-group">
              <label>Salle</label>
              <select name="salleId" value={form.salleId} onChange={handleChange}>
                <option value="">-- Sélectionner --</option>
                {salles.map((s) => (
                  <option key={s.id} value={s.id}>{s.code} - {s.nom}</option>
                ))}
              </select>
            </div>
          </div>
          <div className="form-group">
            <label>Description</label>
            <textarea name="description" value={form.description} onChange={handleChange} rows="3" />
          </div>
          <div className="form-actions">
            <button type="submit" className="btn btn-success">
              {isEdit ? 'Enregistrer les modifications' : 'Créer'}
            </button>
            <button type="button" className="btn btn-secondary" onClick={() => navigate('/cours')}>
              Annuler
            </button>
          </div>
        </form>
      </div>
    </div>
  );
}
