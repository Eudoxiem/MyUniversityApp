import { useState, useEffect } from 'react';
import { Link, useNavigate } from 'react-router-dom';
import { getCours, deleteCour } from '../../api/cours';
import Pagination from '../../components/Pagination';
import Modal from '../../components/Modal';

const ITEMS_PER_PAGE = 10;

export default function CoursList() {
  const [cours, setCours] = useState([]);
  const [loading, setLoading] = useState(true);
  const [page, setPage] = useState(1);
  const [modalOpen, setModalOpen] = useState(false);
  const [deleteId, setDeleteId] = useState(null);
  const navigate = useNavigate();

  const fetchData = () => {
    setLoading(true);
    getCours()
      .then((res) => setCours(res.data))
      .catch(() => {})
      .finally(() => setLoading(false));
  };

  useEffect(() => { fetchData(); }, []);

  const handleDelete = (id) => {
    setDeleteId(id);
    setModalOpen(true);
  };

  const confirmDelete = () => {
    deleteCour(deleteId).then(() => { fetchData(); setPage(1); }).catch(() => {});
    setModalOpen(false);
    setDeleteId(null);
  };

  const totalPages = Math.ceil(cours.length / ITEMS_PER_PAGE);
  const paginated = cours.slice((page - 1) * ITEMS_PER_PAGE, page * ITEMS_PER_PAGE);

  if (loading) return <div className="loading">Chargement...</div>;

  return (
    <div>
      <div className="page-header">
        <h1>Cours</h1>
        <Link to="/cours/nouveau" className="btn btn-primary">+ Nouveau cours</Link>
      </div>
      <div className="card">
        {cours.length === 0 ? (
          <div className="empty-state">Aucun cours trouvé</div>
        ) : (
          <>
            <table>
              <thead>
                <tr>
                  <th>Code</th>
                  <th>Nom</th>
                  <th>Crédits</th>
                  <th>Professeur</th>
                  <th>Salle</th>
                  <th>Actions</th>
                </tr>
              </thead>
              <tbody>
                {paginated.map((c) => (
                  <tr key={c.id}>
                    <td>{c.code}</td>
                    <td>{c.nom}</td>
                    <td>{c.credits}</td>
                    <td>{c.professeurNom || c.professeurId}</td>
                    <td>{c.salleNom || c.salleId}</td>
                    <td className="actions">
                      <button className="btn btn-warning" onClick={() => navigate(`/cours/${c.id}`)}>Modifier</button>
                      <button className="btn btn-danger" onClick={() => handleDelete(c.id)}>Supprimer</button>
                    </td>
                  </tr>
                ))}
              </tbody>
            </table>
            <Pagination currentPage={page} totalPages={totalPages} onPageChange={setPage} />
          </>
        )}
      </div>
      <Modal open={modalOpen} title="Confirmer la suppression" message="Êtes-vous sûr de vouloir supprimer ce cours ?" onConfirm={confirmDelete} onCancel={() => setModalOpen(false)} danger />
    </div>
  );
}
