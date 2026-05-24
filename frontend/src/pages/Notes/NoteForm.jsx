import { useState, useEffect } from 'react';
import { useNavigate, useParams } from 'react-router-dom';
import { getNote, createNote, updateNote } from '../../api/notes';
import { getInscriptions } from '../../api/inscriptions';

export default function NoteForm() {
  const { id } = useParams();
  const isEdit = Boolean(id);
  const navigate = useNavigate();
  const [inscriptions, setInscriptions] = useState([]);
  const [form, setForm] = useState({
    valeur: '', coefficient: '1', type: 'CC', dateSaisie: new Date().toISOString().split('T')[0], inscriptionId: '',
  });
  const [error, setError] = useState('');

  useEffect(() => {
    getInscriptions().then((res) => setInscriptions(res.data)).catch(() => {});
    if (isEdit) {
      getNote(id)
        .then((res) => {
          const n = res.data;
          setForm({
            valeur: n.valeur || '',
            coefficient: n.coefficient || '1',
            type: n.type || 'CC',
            dateSaisie: n.dateSaisie ? n.dateSaisie.split('T')[0] : '',
            inscriptionId: n.inscriptionId || '',
          });
        })
        .catch(() => setError('Erreur lors du chargement'));
    }
  }, [id, isEdit]);

  const handleChange = (e) => setForm({ ...form, [e.target.name]: e.target.value });

  const handleSubmit = (e) => {
    e.preventDefault();
    setError('');
    const payload = { ...form, valeur: Number(form.valeur), coefficient: Number(form.coefficient) };
    const request = isEdit ? updateNote(id, payload) : createNote(payload);
    request
      .then(() => navigate('/notes'))
      .catch((err) => setError(err.response?.data?.message || 'Erreur lors de l\'enregistrement'));
  };

  return (
    <div>
      <div className="page-header">
        <h1>{isEdit ? 'Modifier' : 'Nouvelle'} note</h1>
      </div>
      <div className="card">
        {error && <div className="alert alert-error">{error}</div>}
        <form onSubmit={handleSubmit}>
          <div className="form-group">
            <label>Inscription</label>
            <select name="inscriptionId" value={form.inscriptionId} onChange={handleChange} required>
              <option value="">-- Sélectionner --</option>
              {inscriptions.map((ins) => (
                <option key={ins.id} value={ins.id}>Inscription #{ins.id}</option>
              ))}
            </select>
          </div>
          <div className="form-row">
            <div className="form-group">
              <label>Valeur</label>
              <input name="valeur" type="number" step="0.01" value={form.valeur} onChange={handleChange} required />
            </div>
            <div className="form-group">
              <label>Coefficient</label>
              <input name="coefficient" type="number" step="0.1" value={form.coefficient} onChange={handleChange} />
            </div>
          </div>
          <div className="form-row">
            <div className="form-group">
              <label>Type</label>
              <select name="type" value={form.type} onChange={handleChange}>
                <option value="CC">Contrôle Continu</option>
                <option value="TP">Travaux Pratiques</option>
                <option value="Examen">Examen</option>
              </select>
            </div>
            <div className="form-group">
              <label>Date de saisie</label>
              <input name="dateSaisie" type="date" value={form.dateSaisie} onChange={handleChange} />
            </div>
          </div>
          <div className="form-actions">
            <button type="submit" className="btn btn-success">
              {isEdit ? 'Enregistrer les modifications' : 'Créer'}
            </button>
            <button type="button" className="btn btn-secondary" onClick={() => navigate('/notes')}>
              Annuler
            </button>
          </div>
        </form>
      </div>
    </div>
  );
}
