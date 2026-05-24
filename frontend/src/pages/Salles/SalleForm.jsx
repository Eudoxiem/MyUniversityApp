import { useState, useEffect } from 'react';
import { useNavigate, useParams } from 'react-router-dom';
import { getSalle, createSalle, updateSalle } from '../../api/salles';

export default function SalleForm() {
  const { id } = useParams();
  const isEdit = Boolean(id);
  const navigate = useNavigate();
  const [form, setForm] = useState({
    code: '', nom: '', capacite: '', batiment: '',
  });
  const [error, setError] = useState('');

  useEffect(() => {
    if (isEdit) {
      getSalle(id)
        .then((res) => {
          const s = res.data;
          setForm({
            code: s.code || '',
            nom: s.nom || '',
            capacite: s.capacite || '',
            batiment: s.batiment || '',
          });
        })
        .catch(() => setError('Erreur lors du chargement'));
    }
  }, [id, isEdit]);

  const handleChange = (e) => setForm({ ...form, [e.target.name]: e.target.value });

  const handleSubmit = (e) => {
    e.preventDefault();
    setError('');
    const payload = { ...form, capacite: Number(form.capacite) };
    const request = isEdit ? updateSalle(id, payload) : createSalle(payload);
    request
      .then(() => navigate('/salles'))
      .catch((err) => setError(err.response?.data?.message || 'Erreur lors de l\'enregistrement'));
  };

  return (
    <div>
      <div className="page-header">
        <h1>{isEdit ? 'Modifier' : 'Nouvelle'} salle</h1>
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
              <label>Capacité</label>
              <input name="capacite" type="number" value={form.capacite} onChange={handleChange} required />
            </div>
            <div className="form-group">
              <label>Bâtiment</label>
              <input name="batiment" value={form.batiment} onChange={handleChange} />
            </div>
          </div>
          <div className="form-actions">
            <button type="submit" className="btn btn-success">
              {isEdit ? 'Enregistrer les modifications' : 'Créer'}
            </button>
            <button type="button" className="btn btn-secondary" onClick={() => navigate('/salles')}>
              Annuler
            </button>
          </div>
        </form>
      </div>
    </div>
  );
}
