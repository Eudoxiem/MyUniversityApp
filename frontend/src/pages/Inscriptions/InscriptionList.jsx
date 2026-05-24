import { useState, useEffect } from 'react';
import { Link, useNavigate } from 'react-router-dom';
import { getInscriptions, deleteInscription } from '../../api/inscriptions';
import Pagination from '../../components/Pagination';
import Modal from '../../components/Modal';

const ITEMS_PER_PAGE = 10;

export default function InscriptionList() {
  const [inscriptions, setInscriptions] = useState([]);
  const [loading, setLoading] = useState(true);
  const [page, setPage] = useState(1);
  const [modalOpen, setModalOpen] = useState(false);
  const [deleteId, setDeleteId] = useState(null);
  const navigate = useNavigate();

  const fetchData = () => {
    setLoading(true);
    getInscriptions()
      .then((res) => setInscriptions(res.data))
      .catch(() => {})
      .finally(() => setLoading(false));
  };

  useEffect(() => { fetchData(); }, []);

  const handleDelete = (id) => {
    setDeleteId(id);
    setModalOpen(true);
  };

  const confirmDelete = () => {
    deleteInscription(deleteId).then(() => { fetchData(); setPage(1); }).catch(() => {});
    setModalOpen(false);
    setDeleteId(null);
  };

  const totalPages = Math.ceil(inscriptions.length / ITEMS_PER_PAGE);
  const paginated = inscriptions.slice((page - 1) * ITEMS_PER_PAGE, page * ITEMS_PER_PAGE);

  if (loading) return <div className="loading">Chargement...</div>;

  return (
    <div>
      <div className="page-header">
        <h1>Inscriptions</h1>
        <Link to="/inscriptions/nouveau" className="btn btn-primary">+ Nouvelle inscription</Link>
      </div>
      <div className="card">
        {inscriptions.length === 0 ? (
          <div className="empty-state">Aucune inscription trouvée</div>
        ) : (
          <>
            <table>
              <thead>
                <tr>
                  <th>Étudiant</th>
                  <th>Cours</th>
                  <th>Date d'inscription</th>
                  <th>Actions</th>
                </tr>
              </thead>
              <tbody>
                {paginated.map((ins) => (
                  <tr key={ins.id}>
                    <td>{ins.etudiantNom || ins.etudiantId}</td>
                    <td>{ins.coursNom || ins.coursId}</td>
                    <td>{ins.dateInscription ? new Date(ins.dateInscription).toLocaleDateString('fr-FR') : ''}</td>
                    <td className="actions">
                      <button className="btn btn-warning" onClick={() => navigate(`/inscriptions/${ins.id}`)}>Modifier</button>
                      <button className="btn btn-danger" onClick={() => handleDelete(ins.id)}>Supprimer</button>
                    </td>
                  </tr>
                ))}
              </tbody>
            </table>
            <Pagination currentPage={page} totalPages={totalPages} onPageChange={setPage} />
          </>
        )}
      </div>
      <Modal open={modalOpen} title="Confirmer la suppression" message="Êtes-vous sûr de vouloir supprimer cette inscription ?" onConfirm={confirmDelete} onCancel={() => setModalOpen(false)} danger />
    </div>
  );
}
