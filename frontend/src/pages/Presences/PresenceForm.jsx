import { useState, useEffect } from 'react';
import { useNavigate, useParams } from 'react-router-dom';
import { getPresence, createPresence, updatePresence } from '../../api/presences';
import { getCours } from '../../api/cours';
import { getEtudiants } from '../../api/etudiants';
import { useToast } from '../../components/Toast';

export default function PresenceForm() {
  const { id } = useParams();
  const isEdit = Boolean(id);
  const navigate = useNavigate();
  const toast = useToast();
  const [cours, setCours] = useState([]);
  const [etudiants, setEtudiants] = useState([]);
  const [form, setForm] = useState({
    etudiantId: '', coursId: '', date: new Date().toISOString().split('T')[0],
    present: 'false', justifie: 'false', justification: '',
  });
  const [error, setError] = useState('');

  useEffect(() => {
    getCours().then((res) => setCours(res.data)).catch(() => {});
    getEtudiants().then((res) => setEtudiants(res.data)).catch(() => {});
    if (isEdit) {
      getPresence(id)
        .then((res) => {
          const p = res.data;
          setForm({
            etudiantId: p.etudiantId || '',
            coursId: p.coursId || '',
            date: p.date || '',
            present: p.present ? 'true' : 'false',
            justifie: p.justifie ? 'true' : 'false',
            justification: p.justification || '',
          });
        })
        .catch(() => setError('Erreur lors du chargement'));
    }
  }, [id, isEdit]);

  const handleChange = (e) => setForm({ ...form, [e.target.name]: e.target.value });

  const handleSubmit = (e) => {
    e.preventDefault();
    setError('');
    const payload = {
      etudiantId: Number(form.etudiantId),
      coursId: Number(form.coursId),
      date: form.date,
      present: form.present === 'true',
      justifie: form.justifie === 'true',
      justification: form.justification || null,
    };
    const request = isEdit ? updatePresence(id, payload) : createPresence(payload);
    request
      .then(() => {
        toast(isEdit ? 'Présence modifiée avec succès' : 'Présence créée avec succès');
        navigate('/presences');
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
        <h1>{isEdit ? 'Modifier' : 'Nouvelle'} présence</h1>
      </div>
      <div className="card">
        {error && <div className="alert alert-error">{error}</div>}
        <form onSubmit={handleSubmit}>
          <div className="form-row">
            <div className="form-group">
              <label>Étudiant</label>
              <select name="etudiantId" value={form.etudiantId} onChange={handleChange} required disabled={isEdit}>
                <option value="">-- Sélectionner --</option>
                {etudiants.map((e) => (
                  <option key={e.id} value={e.id}>{e.matricule} - {e.nom} {e.prenom}</option>
                ))}
              </select>
            </div>
            <div className="form-group">
              <label>Cours</label>
              <select name="coursId" value={form.coursId} onChange={handleChange} required disabled={isEdit}>
                <option value="">-- Sélectionner --</option>
                {cours.map((c) => (
                  <option key={c.id} value={c.id}>{c.code} - {c.nom}</option>
                ))}
              </select>
            </div>
          </div>
          <div className="form-row">
            <div className="form-group">
              <label>Date</label>
              <input name="date" type="date" value={form.date} onChange={handleChange} required disabled={isEdit} />
            </div>
            <div className="form-group">
              <label>Présent</label>
              <select name="present" value={form.present} onChange={handleChange}>
                <option value="false">Non</option>
                <option value="true">Oui</option>
              </select>
            </div>
          </div>
          <div className="form-row">
            <div className="form-group">
              <label>Justifié</label>
              <select name="justifie" value={form.justifie} onChange={handleChange}>
                <option value="false">Non</option>
                <option value="true">Oui</option>
              </select>
            </div>
            <div className="form-group">
              <label>Justification</label>
              <input name="justification" value={form.justification} onChange={handleChange} placeholder="Motif (si justifié)" />
            </div>
          </div>
          <div className="form-actions">
            <button type="submit" className="btn btn-success">
              {isEdit ? 'Enregistrer les modifications' : 'Créer'}
            </button>
            <button type="button" className="btn btn-secondary" onClick={() => navigate('/presences')}>Annuler</button>
          </div>
        </form>
      </div>
    </div>
  );
}
