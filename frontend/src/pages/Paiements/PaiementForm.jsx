import { useState, useEffect } from 'react';
import { useNavigate, useParams } from 'react-router-dom';
import { getPaiement, createPaiement, updatePaiement } from '../../api/paiements';
import { getEtudiants } from '../../api/etudiants';
import { useToast } from '../../components/Toast';

export default function PaiementForm() {
  const { id } = useParams();
  const isEdit = Boolean(id);
  const navigate = useNavigate();
  const toast = useToast();
  const [etudiants, setEtudiants] = useState([]);
  const [form, setForm] = useState({
    reference: '', etudiantId: '', montant: '', datePaiement: '', dateEcheance: '', modePaiement: 'ESPECES', statut: 'EN_ATTENTE', description: '',
  });
  const [error, setError] = useState('');

  useEffect(() => {
    getEtudiants().then((res) => setEtudiants(res.data)).catch(() => {});
    if (isEdit) {
      getPaiement(id)
        .then((res) => {
          const p = res.data;
          setForm({
            reference: p.reference || '',
            etudiantId: p.etudiantId || '',
            montant: p.montant || '',
            datePaiement: p.datePaiement || '',
            dateEcheance: p.dateEcheance || '',
            modePaiement: p.modePaiement || 'ESPECES',
            statut: p.statut || 'EN_ATTENTE',
            description: p.description || '',
          });
        })
        .catch(() => setError('Erreur lors du chargement'));
    }
  }, [id, isEdit]);

  const handleChange = (e) => setForm({ ...form, [e.target.name]: e.target.value });

  const handleSubmit = (e) => {
    e.preventDefault();
    setError('');
    const payload = { ...form, montant: Number(form.montant) };
    const request = isEdit ? updatePaiement(id, payload) : createPaiement(payload);
    request
      .then(() => {
        toast(isEdit ? 'Paiement modifié avec succès' : 'Paiement créé avec succès');
        navigate('/paiements');
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
        <h1>{isEdit ? 'Modifier' : 'Nouveau'} paiement</h1>
      </div>
      <div className="card">
        {error && <div className="alert alert-error">{error}</div>}
        <form onSubmit={handleSubmit}>
          <div className="form-row">
            <div className="form-group">
              <label>Référence</label>
              <input name="reference" value={form.reference} onChange={handleChange} required />
            </div>
            <div className="form-group">
              <label>Étudiant</label>
              <select name="etudiantId" value={form.etudiantId} onChange={handleChange} required disabled={isEdit}>
                <option value="">-- Sélectionner --</option>
                {etudiants.map((e) => (
                  <option key={e.id} value={e.id}>{e.nom} {e.prenom} ({e.matricule})</option>
                ))}
              </select>
            </div>
          </div>
          <div className="form-row">
            <div className="form-group">
              <label>Montant</label>
              <input name="montant" type="number" step="0.01" value={form.montant} onChange={handleChange} required />
            </div>
            <div className="form-group">
              <label>Mode de paiement</label>
              <select name="modePaiement" value={form.modePaiement} onChange={handleChange}>
                <option value="ESPECES">Espèces</option>
                <option value="VIREMENT">Virement</option>
                <option value="CHEQUE">Chèque</option>
                <option value="CARTE_BANCAIRE">Carte bancaire</option>
              </select>
            </div>
          </div>
          <div className="form-row">
            <div className="form-group">
              <label>Date de paiement</label>
              <input name="datePaiement" type="date" value={form.datePaiement} onChange={handleChange} required />
            </div>
            <div className="form-group">
              <label>Date d'échéance</label>
              <input name="dateEcheance" type="date" value={form.dateEcheance} onChange={handleChange} />
            </div>
          </div>
          <div className="form-row">
            <div className="form-group">
              <label>Statut</label>
              <select name="statut" value={form.statut} onChange={handleChange}>
                <option value="EN_ATTENTE">En attente</option>
                <option value="PAYE">Payé</option>
                <option value="EN_RETARD">En retard</option>
                <option value="ANNULE">Annulé</option>
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
            <button type="button" className="btn btn-secondary" onClick={() => navigate('/paiements')}>
              Annuler
            </button>
          </div>
        </form>
      </div>
    </div>
  );
}
