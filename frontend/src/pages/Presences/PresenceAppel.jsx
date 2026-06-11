import { useState, useEffect } from 'react';
import { useNavigate } from 'react-router-dom';
import { faireAppel } from '../../api/presences';
import { getCours } from '../../api/cours';
import { getEtudiants } from '../../api/etudiants';
import { useToast } from '../../components/Toast';

export default function PresenceAppel() {
  const navigate = useNavigate();
  const toast = useToast();
  const [cours, setCours] = useState([]);
  const [etudiants, setEtudiants] = useState([]);
  const [coursId, setCoursId] = useState('');
  const [date, setDate] = useState(new Date().toISOString().split('T')[0]);
  const [presences, setPresences] = useState({});
  const [error, setError] = useState('');

  useEffect(() => {
    getCours().then((res) => setCours(res.data)).catch(() => {});
    getEtudiants().then((res) => setEtudiants(res.data)).catch(() => {});
  }, []);

  const togglePresence = (etudiantId) => {
    setPresences((prev) => ({ ...prev, [etudiantId]: !prev[etudiantId] }));
  };

  const allPresent = () => {
    const all = {};
    etudiants.forEach((e) => { all[e.id] = true; });
    setPresences(all);
  };

  const allAbsent = () => {
    setPresences({});
  };

  const handleSubmit = (e) => {
    e.preventDefault();
    if (!coursId || !date) {
      setError('Veuillez sélectionner un cours et une date');
      return;
    }
    setError('');

    const payload = {
      coursId: Number(coursId),
      date,
      presences: etudiants.map((etudiant) => ({
        etudiantId: etudiant.id,
        present: !!presences[etudiant.id],
      })),
    };

    faireAppel(payload)
      .then(() => {
        toast('Appel enregistré avec succès');
        navigate('/presences');
      })
      .catch((err) => {
        const msg = err.response?.data?.message || "Erreur lors de l'appel";
        setError(msg);
        toast(msg, 'error');
      });
  };

  return (
    <div>
      <div className="page-header">
        <h1>Faire l'appel</h1>
      </div>
      <div className="card">
        {error && <div className="alert alert-error">{error}</div>}
        <form onSubmit={handleSubmit}>
          <div className="form-row">
            <div className="form-group">
              <label>Cours</label>
              <select value={coursId} onChange={(e) => setCoursId(e.target.value)} required>
                <option value="">-- Sélectionner --</option>
                {cours.map((c) => (
                  <option key={c.id} value={c.id}>{c.code} - {c.nom}</option>
                ))}
              </select>
            </div>
            <div className="form-group">
              <label>Date</label>
              <input type="date" value={date} onChange={(e) => setDate(e.target.value)} required />
            </div>
          </div>
          <div className="appel-actions">
            <button type="button" className="btn btn-outline" onClick={allPresent}>Tous présents</button>
            <button type="button" className="btn btn-outline" onClick={allAbsent}>Tous absents</button>
          </div>
          <table>
            <thead>
              <tr>
                <th>Matricule</th>
                <th>Étudiant</th>
                <th>Présent</th>
              </tr>
            </thead>
            <tbody>
              {etudiants.map((etudiant) => (
                <tr key={etudiant.id}>
                  <td>{etudiant.matricule}</td>
                  <td>{etudiant.nom} {etudiant.prenom}</td>
                  <td>
                    <label className="toggle-switch">
                      <input
                        type="checkbox"
                        checked={!!presences[etudiant.id]}
                        onChange={() => togglePresence(etudiant.id)}
                      />
                      <span className="toggle-slider"></span>
                    </label>
                  </td>
                </tr>
              ))}
            </tbody>
          </table>
          <div className="form-actions">
            <button type="submit" className="btn btn-success">Enregistrer l'appel</button>
            <button type="button" className="btn btn-secondary" onClick={() => navigate('/presences')}>Annuler</button>
          </div>
        </form>
      </div>
    </div>
  );
}
