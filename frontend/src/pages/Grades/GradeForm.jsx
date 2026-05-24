import { useState, useEffect } from 'react';
import { useNavigate, useParams } from 'react-router-dom';
import { getGrade, createGrade, updateGrade } from '../../api/grades';
import { getInscriptions } from '../../api/inscriptions';
import { useToast } from '../../components/Toast';

export default function GradeForm() {
  const { id } = useParams();
  const isEdit = Boolean(id);
  const navigate = useNavigate();
  const toast = useToast();
  const [inscriptions, setInscriptions] = useState([]);
  const [form, setForm] = useState({
    valeurFinale: '', mention: '', dateValidation: '', inscriptionId: '',
  });
  const [error, setError] = useState('');

  useEffect(() => {
    getInscriptions().then((res) => setInscriptions(res.data)).catch(() => {});
    if (isEdit) {
      getGrade(id)
        .then((res) => {
          const g = res.data;
          setForm({
            valeurFinale: g.valeurFinale || '',
            mention: g.mention || '',
            dateValidation: g.dateValidation ? g.dateValidation.split('T')[0] : '',
            inscriptionId: g.inscriptionId || '',
          });
        })
        .catch(() => setError('Erreur lors du chargement'));
    }
  }, [id, isEdit]);

  const handleChange = (e) => setForm({ ...form, [e.target.name]: e.target.value });

  const handleSubmit = (e) => {
    e.preventDefault();
    setError('');
    const payload = { ...form, valeurFinale: Number(form.valeurFinale) };
    const request = isEdit ? updateGrade(id, payload) : createGrade(payload);
    request
      .then(() => {
        toast(isEdit ? 'Grade modifié avec succès' : 'Grade créé avec succès');
        navigate('/grades');
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
        <h1>{isEdit ? 'Modifier' : 'Nouveau'} grade</h1>
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
              <label>Valeur finale</label>
              <input name="valeurFinale" type="number" step="0.01" value={form.valeurFinale} onChange={handleChange} required />
            </div>
            <div className="form-group">
              <label>Mention</label>
              <select name="mention" value={form.mention} onChange={handleChange}>
                <option value="">-- Sélectionner --</option>
                <option value="A">A (Excellent)</option>
                <option value="B">B (Très bien)</option>
                <option value="C">C (Bien)</option>
                <option value="D">D (Passable)</option>
                <option value="E">E (Insuffisant)</option>
              </select>
            </div>
          </div>
          <div className="form-group">
            <label>Date de validation</label>
            <input name="dateValidation" type="date" value={form.dateValidation} onChange={handleChange} />
          </div>
          <div className="form-actions">
            <button type="submit" className="btn btn-success">
              {isEdit ? 'Enregistrer les modifications' : 'Créer'}
            </button>
            <button type="button" className="btn btn-secondary" onClick={() => navigate('/grades')}>
              Annuler
            </button>
          </div>
        </form>
      </div>
    </div>
  );
}
