import { useState, useEffect } from 'react';
import { getFichiersByEntite, uploadFichier, deleteFichier, getDownloadUrl } from '../../api/fichiers';
import { getCours } from '../../api/cours';
import { getEtudiants } from '../../api/etudiants';
import { useToast } from '../../components/Toast';
import Modal from '../../components/Modal';

const entiteTypes = [
  { value: 'COURS', label: 'Cours' },
  { value: 'ETUDIANT', label: 'Étudiant' },
];

function formatTaille(bytes) {
  if (!bytes) return '';
  const kb = bytes / 1024;
  if (kb < 1024) return `${kb.toFixed(1)} Ko`;
  return `${(kb / 1024).toFixed(1)} Mo`;
}

function formatDate(dateStr) {
  if (!dateStr) return '';
  return new Date(dateStr).toLocaleDateString('fr-FR', { day: '2-digit', month: '2-digit', year: 'numeric', hour: '2-digit', minute: '2-digit' });
}

export default function FichierList() {
  const [entiteType, setEntiteType] = useState('COURS');
  const [entiteId, setEntiteId] = useState('');
  const [entites, setEntites] = useState([]);
  const [fichiers, setFichiers] = useState([]);
  const [loading, setLoading] = useState(false);
  const [uploading, setUploading] = useState(false);
  const [modalOpen, setModalOpen] = useState(false);
  const [deleteId, setDeleteId] = useState(null);
  const [file, setFile] = useState(null);
  const toast = useToast();

  useEffect(() => {
    const fetcher = entiteType === 'COURS' ? getCours() : getEtudiants();
    fetcher.then((res) => setEntites(res.data)).catch(() => {});
  }, [entiteType]);

  useEffect(() => {
    if (!entiteId) { setFichiers([]); return; }
    setLoading(true);
    getFichiersByEntite(entiteType, Number(entiteId))
      .then((res) => setFichiers(res.data))
      .catch(() => {})
      .finally(() => setLoading(false));
  }, [entiteType, entiteId]);

  const handleUpload = () => {
    if (!file || !entiteId) return;
    setUploading(true);
    uploadFichier(file, entiteType, Number(entiteId))
      .then(() => {
        toast('Fichier uploadé avec succès');
        setFile(null);
        return getFichiersByEntite(entiteType, Number(entiteId));
      })
      .then((res) => setFichiers(res.data))
      .catch(() => toast("Erreur lors de l'upload", 'error'))
      .finally(() => setUploading(false));
  };

  const confirmDelete = () => {
    deleteFichier(deleteId)
      .then(() => {
        toast('Fichier supprimé');
        return getFichiersByEntite(entiteType, Number(entiteId));
      })
      .then((res) => setFichiers(res.data))
      .catch(() => toast('Erreur lors de la suppression', 'error'));
    setModalOpen(false);
    setDeleteId(null);
  };

  return (
    <div>
      <div className="page-header">
        <h1>Fichiers</h1>
      </div>
      <div className="card">
        <div className="form-row">
          <div className="form-group">
            <label>Type d'entité</label>
            <select value={entiteType} onChange={(e) => { setEntiteType(e.target.value); setEntiteId(''); }}>
              {entiteTypes.map((t) => (
                <option key={t.value} value={t.value}>{t.label}</option>
              ))}
            </select>
          </div>
          <div className="form-group">
            <label>{entiteType === 'COURS' ? 'Cours' : 'Étudiant'}</label>
            <select value={entiteId} onChange={(e) => setEntiteId(e.target.value)}>
              <option value="">-- Sélectionner --</option>
              {entites.map((e) => (
                <option key={e.id} value={e.id}>{e.nom || e.code} {e.prenom || ''}</option>
              ))}
            </select>
          </div>
        </div>
      </div>

      {entiteId && (
        <div className="card">
          <h3 style={{ marginBottom: 16 }}>Uploader un fichier</h3>
          <div className="form-row" style={{ alignItems: 'end' }}>
            <div className="form-group">
              <input type="file" onChange={(e) => setFile(e.target.files[0])} />
            </div>
            <div className="form-group">
              <button className="btn btn-success" onClick={handleUpload} disabled={!file || uploading}>
                {uploading ? 'Upload...' : 'Uploader'}
              </button>
            </div>
          </div>
        </div>
      )}

      <div className="card">
        {loading ? (
          <div className="loading">Chargement...</div>
        ) : fichiers.length === 0 ? (
          <div className="empty-state">Aucun fichier trouvé pour cette entité</div>
        ) : (
          <table>
            <thead>
              <tr>
                <th>Nom</th>
                <th>Type</th>
                <th>Taille</th>
                <th>Date d'upload</th>
                <th>Actions</th>
              </tr>
            </thead>
            <tbody>
              {fichiers.map((f) => (
                <tr key={f.id}>
                  <td>{f.nomOriginal}</td>
                  <td>{f.typeMime}</td>
                  <td>{formatTaille(f.taille)}</td>
                  <td>{formatDate(f.dateUpload)}</td>
                  <td className="actions">
                    <a href={getDownloadUrl(f.id)} className="btn btn-primary" download>Télécharger</a>
                    <button className="btn btn-danger" onClick={() => { setDeleteId(f.id); setModalOpen(true); }}>Supprimer</button>
                  </td>
                </tr>
              ))}
            </tbody>
          </table>
        )}
      </div>

      <Modal open={modalOpen} title="Confirmer la suppression" message="Êtes-vous sûr de vouloir supprimer ce fichier ?" onConfirm={confirmDelete} onCancel={() => setModalOpen(false)} danger />
    </div>
  );
}
