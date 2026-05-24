import { useState, useEffect } from 'react';
import { Link, useNavigate } from 'react-router-dom';
import { getSalles, deleteSalle } from '../../api/salles';
import Pagination from '../../components/Pagination';
import Modal from '../../components/Modal';

const ITEMS_PER_PAGE = 10;

export default function SalleList() {
  const [salles, setSalles] = useState([]);
  const [loading, setLoading] = useState(true);
  const [page, setPage] = useState(1);
  const [modalOpen, setModalOpen] = useState(false);
  const [deleteId, setDeleteId] = useState(null);
  const navigate = useNavigate();

  const fetchData = () => {
    setLoading(true);
    getSalles()
      .then((res) => setSalles(res.data))
      .catch(() => {})
      .finally(() => setLoading(false));
  };

  useEffect(() => { fetchData(); }, []);

  const handleDelete = (id) => {
    setDeleteId(id);
    setModalOpen(true);
  };

  const confirmDelete = () => {
    deleteSalle(deleteId).then(() => { fetchData(); setPage(1); }).catch(() => {});
    setModalOpen(false);
    setDeleteId(null);
  };

  const totalPages = Math.ceil(salles.length / ITEMS_PER_PAGE);
  const paginated = salles.slice((page - 1) * ITEMS_PER_PAGE, page * ITEMS_PER_PAGE);

  if (loading) return <div className="loading">Chargement...</div>;

  return (
    <div>
      <div className="page-header">
        <h1>Salles</h1>
        <Link to="/salles/nouveau" className="btn btn-primary">+ Nouvelle salle</Link>
      </div>
      <div className="card">
        {salles.length === 0 ? (
          <div className="empty-state">Aucune salle trouvée</div>
        ) : (
          <>
            <table>
              <thead>
                <tr>
                  <th>Code</th>
                  <th>Nom</th>
                  <th>Capacité</th>
                  <th>Bâtiment</th>
                  <th>Actions</th>
                </tr>
              </thead>
              <tbody>
                {paginated.map((s) => (
                  <tr key={s.id}>
                    <td>{s.code}</td>
                    <td>{s.nom}</td>
                    <td>{s.capacite}</td>
                    <td>{s.batiment}</td>
                    <td className="actions">
                      <button className="btn btn-warning" onClick={() => navigate(`/salles/${s.id}`)}>Modifier</button>
                      <button className="btn btn-danger" onClick={() => handleDelete(s.id)}>Supprimer</button>
                    </td>
                  </tr>
                ))}
              </tbody>
            </table>
            <Pagination currentPage={page} totalPages={totalPages} onPageChange={setPage} />
          </>
        )}
      </div>
      <Modal open={modalOpen} title="Confirmer la suppression" message="Êtes-vous sûr de vouloir supprimer cette salle ?" onConfirm={confirmDelete} onCancel={() => setModalOpen(false)} danger />
    </div>
  );
}
