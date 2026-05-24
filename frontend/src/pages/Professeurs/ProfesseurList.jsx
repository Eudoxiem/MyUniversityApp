import { useState, useEffect } from 'react';
import { Link, useNavigate } from 'react-router-dom';
import { getProfesseurs, deleteProfesseur } from '../../api/professeurs';
import Pagination from '../../components/Pagination';
import Modal from '../../components/Modal';

const ITEMS_PER_PAGE = 10;

export default function ProfesseurList() {
  const [professeurs, setProfesseurs] = useState([]);
  const [loading, setLoading] = useState(true);
  const [page, setPage] = useState(1);
  const [modalOpen, setModalOpen] = useState(false);
  const [deleteId, setDeleteId] = useState(null);
  const navigate = useNavigate();

  const fetchData = () => {
    setLoading(true);
    getProfesseurs()
      .then((res) => setProfesseurs(res.data))
      .catch(() => {})
      .finally(() => setLoading(false));
  };

  useEffect(() => { fetchData(); }, []);

  const handleDelete = (id) => {
    setDeleteId(id);
    setModalOpen(true);
  };

  const confirmDelete = () => {
    deleteProfesseur(deleteId).then(() => { fetchData(); setPage(1); }).catch(() => {});
    setModalOpen(false);
    setDeleteId(null);
  };

  const totalPages = Math.ceil(professeurs.length / ITEMS_PER_PAGE);
  const paginated = professeurs.slice((page - 1) * ITEMS_PER_PAGE, page * ITEMS_PER_PAGE);

  if (loading) return <div className="loading">Chargement...</div>;

  return (
    <div>
      <div className="page-header">
        <h1>Professeurs</h1>
        <Link to="/professeurs/nouveau" className="btn btn-primary">+ Nouveau professeur</Link>
      </div>
      <div className="card">
        {professeurs.length === 0 ? (
          <div className="empty-state">Aucun professeur trouvé</div>
        ) : (
          <>
            <table>
              <thead>
                <tr>
                  <th>N° Employé</th>
                  <th>Nom</th>
                  <th>Prénom</th>
                  <th>Email</th>
                  <th>Spécialité</th>
                  <th>Actions</th>
                </tr>
              </thead>
              <tbody>
                {paginated.map((p) => (
                  <tr key={p.id}>
                    <td>{p.numeroEmploye}</td>
                    <td>{p.nom}</td>
                    <td>{p.prenom}</td>
                    <td>{p.email}</td>
                    <td>{p.specialite}</td>
                    <td className="actions">
                      <button className="btn btn-warning" onClick={() => navigate(`/professeurs/${p.id}`)}>Modifier</button>
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
      <Modal open={modalOpen} title="Confirmer la suppression" message="Êtes-vous sûr de vouloir supprimer ce professeur ?" onConfirm={confirmDelete} onCancel={() => setModalOpen(false)} danger />
    </div>
  );
}
