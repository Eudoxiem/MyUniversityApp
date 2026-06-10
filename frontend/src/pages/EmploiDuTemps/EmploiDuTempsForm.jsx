import { useState, useEffect } from 'react';
import { useNavigate, useParams } from 'react-router-dom';
import { getEmploiDuTemps, createEmploiDuTemps, updateEmploiDuTemps } from '../../api/emploiDuTemps';
import { getCours } from '../../api/cours';
import { getSalles } from '../../api/salles';
import { getProfesseurs } from '../../api/professeurs';
import { useToast } from '../../components/Toast';

export default function EmploiDuTempsForm() {
  const { id } = useParams();
  const isEdit = Boolean(id);
  const navigate = useNavigate();
  const toast = useToast();
  const [cours, setCours] = useState([]);
  const [salles, setSalles] = useState([]);
  const [professeurs, setProfesseurs] = useState([]);
  const [form, setForm] = useState({
    coursId: '', salleId: '', professeurId: '', jourSemaine: 'LUNDI', heureDebut: '', heureFin: '', semestre: '', anneeAcademique: '',
  });
  const [error, setError] = useState('');

  useEffect(() => {
    getCours().then((res) => setCours(res.data)).catch(() => {});
    getSalles().then((res) => setSalles(res.data)).catch(() => {});
    getProfesseurs().then((res) => setProfesseurs(res.data)).catch(() => {});
    if (isEdit) {
      getEmploiDuTemps(id)
        .then((res) => {
          const e = res.data;
          setForm({
            coursId: e.coursId || '',
            salleId: e.salleId || '',
            professeurId: e.professeurId || '',
            jourSemaine: e.jourSemaine || 'LUNDI',
            heureDebut: e.heureDebut || '',
            heureFin: e.heureFin || '',
            semestre: e.semestre || '',
            anneeAcademique: e.anneeAcademique || '',
          });
        })
        .catch(() => setError('Erreur lors du chargement'));
    }
  }, [id, isEdit]);

  const handleChange = (e) => setForm({ ...form, [e.target.name]: e.target.value });

  const handleSubmit = (e) => {
    e.preventDefault();
    setError('');
    const request = isEdit ? updateEmploiDuTemps(id, form) : createEmploiDuTemps(form);
    request
      .then(() => {
        toast(isEdit ? 'Emploi du temps modifié avec succès' : 'Emploi du temps créé avec succès');
        navigate('/emploi-du-temps');
      })
      .catch((err) => {
        const msg = err.response?.data?.message || "Erreur lors de l'enregistrement (conflit possible)";
        setError(msg);
        toast(msg, 'error');
      });
  };

  return (
    <div>
      <div className="page-header">
        <h1>{isEdit ? 'Modifier' : 'Nouvel'} emploi du temps</h1>
      </div>
      <div className="card">
        {error && <div className="alert alert-error">{error}</div>}
        <form onSubmit={handleSubmit}>
          <div className="form-row">
            <div className="form-group">
              <label>Cours</label>
              <select name="coursId" value={form.coursId} onChange={handleChange} required>
                <option value="">-- Sélectionner --</option>
                {cours.map((c) => (
                  <option key={c.id} value={c.id}>{c.code} - {c.nom}</option>
                ))}
              </select>
            </div>
            <div className="form-group">
              <label>Professeur</label>
              <select name="professeurId" value={form.professeurId} onChange={handleChange} required>
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
              <select name="salleId" value={form.salleId} onChange={handleChange} required>
                <option value="">-- Sélectionner --</option>
                {salles.map((s) => (
                  <option key={s.id} value={s.id}>{s.code} - {s.nom}</option>
                ))}
              </select>
            </div>
            <div className="form-group">
              <label>Jour</label>
              <select name="jourSemaine" value={form.jourSemaine} onChange={handleChange}>
                <option value="LUNDI">Lundi</option>
                <option value="MARDI">Mardi</option>
                <option value="MERCREDI">Mercredi</option>
                <option value="JEUDI">Jeudi</option>
                <option value="VENDREDI">Vendredi</option>
                <option value="SAMEDI">Samedi</option>
              </select>
            </div>
          </div>
          <div className="form-row">
            <div className="form-group">
              <label>Heure de début</label>
              <input name="heureDebut" type="time" value={form.heureDebut} onChange={handleChange} required />
            </div>
            <div className="form-group">
              <label>Heure de fin</label>
              <input name="heureFin" type="time" value={form.heureFin} onChange={handleChange} required />
            </div>
          </div>
          <div className="form-row">
            <div className="form-group">
              <label>Semestre</label>
              <input name="semestre" value={form.semestre} onChange={handleChange} placeholder="ex: S1" required />
            </div>
            <div className="form-group">
              <label>Année académique</label>
              <input name="anneeAcademique" value={form.anneeAcademique} onChange={handleChange} placeholder="ex: 2025-2026" required />
            </div>
          </div>
          <div className="form-actions">
            <button type="submit" className="btn btn-success">
              {isEdit ? 'Enregistrer les modifications' : 'Créer'}
            </button>
            <button type="button" className="btn btn-secondary" onClick={() => navigate('/emploi-du-temps')}>
              Annuler
            </button>
          </div>
        </form>
      </div>
    </div>
  );
}
