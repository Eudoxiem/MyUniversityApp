import { useState, useEffect } from 'react';
import { Link, useNavigate } from 'react-router-dom';
import { getPresencesByCoursAndDate, deletePresence } from '../../api/presences';
import { getCours } from '../../api/cours';
import Pagination from '../../components/Pagination';
import Modal from '../../components/Modal';

const ITEMS_PER_PAGE = 10;

export default function PresenceList() {
  const [presences, setPresences] = useState([]);
  const [cours, setCours] = useState([]);
  const [selectedCoursId, setSelectedCoursId] = useState('');
  const [date, setDate] = useState(new Date().toISOString().split('T')[0]);
  const [loading, setLoading] = useState(false);
  const [page, setPage] = useState(1);
  const [modalOpen, setModalOpen] = useState(false);
  const [deleteId, setDeleteId] = useState(null);
  const navigate = useNavigate();

  useEffect(() => {
    getCours().then((res) => setCours(res.data)).catch(() => {});
  }, []);

  const fetchData = () => {
    if (!selectedCoursId || !date) return;
    setLoading(true);
    getPresencesByCoursAndDate(selectedCoursId, date)
      .then((res) => setPresences(res.data))
      .catch(() => setPresences([]))
      .finally(() => setLoading(false));
  };

  const handleSearch = (e) => { e.preventDefault(); fetchData(); setPage(1); };

  const handleDelete = (id) => {
    setDeleteId(id);
    setModalOpen(true);
  };

  const confirmDelete = () => {
    deletePresence(deleteId).then(() => { fetchData(); setPage(1); }).catch(() => {});
    setModalOpen(false);
    setDeleteId(null);
  };

  const totalPages = Math.ceil(presences.length / ITEMS_PER_PAGE);
  const paginated = presences.slice((page - 1) * ITEMS_PER_PAGE, page * ITEMS_PER_PAGE);

  return (
    <div>
      <div className="page-header">
        <h1>Présences</h1>
        <div className="page-actions">
          <Link to="/presences/appel" className="btn btn-success">+ Faire l'appel</Link>
          <Link to="/presences/nouveau" className="btn btn-primary">+ Nouvelle présence</Link>
        </div>
      </div>
      <div className="card">
        <form className="search-form" onSubmit={handleSearch}>
          <div className="form-row">
            <div className="form-group">
              <label>Cours</label>
              <select value={selectedCoursId} onChange={(e) => setSelectedCoursId(e.target.value)} required>
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
            <div className="form-group" style={{ alignSelf: 'flex-end' }}>
              <button type="submit" className="btn btn-primary">Rechercher</button>
            </div>
          </div>
        </form>
      </div>
      <div className="card">
        {loading ? (
          <div className="loading">Chargement...</div>
        ) : presences.length === 0 ? (
          <div className="empty-state">Aucune présence trouvée pour ce cours à cette date</div>
        ) : (
          <>
            <table>
              <thead>
                <tr>
                  <th>Matricule</th>
                  <th>Étudiant</th>
                  <th>Présent</th>
                  <th>Justifié</th>
                  <th>Justification</th>
                  <th>Actions</th>
                </tr>
              </thead>
              <tbody>
                {paginated.map((p) => (
                  <tr key={p.id}>
                    <td>{p.etudiantMatricule}</td>
                    <td>{p.etudiantNom} {p.etudiantPrenom}</td>
                    <td>{p.present ? 'Oui' : 'Non'}</td>
                    <td>{p.justifie ? 'Oui' : 'Non'}</td>
                    <td>{p.justification || '-'}</td>
                    <td className="actions">
                      <button className="btn btn-warning" onClick={() => navigate(`/presences/${p.id}`)}>Modifier</button>
                      <button className="btn btn-danger" onClick={() => handleDelete(p.id)}>Supprimer</button>
                    </td>
                  </tr>
                ))}
              </tbody>
            </table>
            <Pagination currentPage={page} totalPages={totalPages} onPageChange={setPage} />
          </>
        )}
      </div>
      <Modal open={modalOpen} title="Confirmer la suppression" message="Êtes-vous sûr de vouloir supprimer cette présence ?" onConfirm={confirmDelete} onCancel={() => setModalOpen(false)} danger />
    </div>
  );
}
