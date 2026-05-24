import { useState, useEffect } from 'react';
import { Link, useNavigate } from 'react-router-dom';
import { getGrades, deleteGrade } from '../../api/grades';
import Pagination from '../../components/Pagination';
import Modal from '../../components/Modal';

const ITEMS_PER_PAGE = 10;

export default function GradeList() {
  const [grades, setGrades] = useState([]);
  const [loading, setLoading] = useState(true);
  const [page, setPage] = useState(1);
  const [modalOpen, setModalOpen] = useState(false);
  const [deleteId, setDeleteId] = useState(null);
  const navigate = useNavigate();

  const fetchData = () => {
    setLoading(true);
    getGrades()
      .then((res) => setGrades(res.data))
      .catch(() => {})
      .finally(() => setLoading(false));
  };

  useEffect(() => { fetchData(); }, []);

  const handleDelete = (id) => {
    setDeleteId(id);
    setModalOpen(true);
  };

  const confirmDelete = () => {
    deleteGrade(deleteId).then(() => { fetchData(); setPage(1); }).catch(() => {});
    setModalOpen(false);
    setDeleteId(null);
  };

  const totalPages = Math.ceil(grades.length / ITEMS_PER_PAGE);
  const paginated = grades.slice((page - 1) * ITEMS_PER_PAGE, page * ITEMS_PER_PAGE);

  if (loading) return <div className="loading">Chargement...</div>;

  return (
    <div>
      <div className="page-header">
        <h1>Grades</h1>
        <Link to="/grades/nouveau" className="btn btn-primary">+ Nouveau grade</Link>
      </div>
      <div className="card">
        {grades.length === 0 ? (
          <div className="empty-state">Aucun grade trouvé</div>
        ) : (
          <>
            <table>
              <thead>
                <tr>
                  <th>Inscription</th>
                  <th>Valeur finale</th>
                  <th>Mention</th>
                  <th>Date validation</th>
                  <th>Actions</th>
                </tr>
              </thead>
              <tbody>
                {paginated.map((g) => (
                  <tr key={g.id}>
                    <td>{g.inscriptionId}</td>
                    <td>{g.valeurFinale}</td>
                    <td>{g.mention}</td>
                    <td>{g.dateValidation ? new Date(g.dateValidation).toLocaleDateString('fr-FR') : ''}</td>
                    <td className="actions">
                      <button className="btn btn-warning" onClick={() => navigate(`/grades/${g.id}`)}>Modifier</button>
                      <button className="btn btn-danger" onClick={() => handleDelete(g.id)}>Supprimer</button>
                    </td>
                  </tr>
                ))}
              </tbody>
            </table>
            <Pagination currentPage={page} totalPages={totalPages} onPageChange={setPage} />
          </>
        )}
      </div>
      <Modal open={modalOpen} title="Confirmer la suppression" message="Êtes-vous sûr de vouloir supprimer ce grade ?" onConfirm={confirmDelete} onCancel={() => setModalOpen(false)} danger />
    </div>
  );
}
