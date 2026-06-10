import { useState, useEffect } from 'react';
import { Link, useNavigate } from 'react-router-dom';
import { getEmploisDuTemps, deleteEmploiDuTemps } from '../../api/emploiDuTemps';
import Pagination from '../../components/Pagination';
import Modal from '../../components/Modal';

const ITEMS_PER_PAGE = 10;

const jourLabels = { LUNDI: 'Lundi', MARDI: 'Mardi', MERCREDI: 'Mercredi', JEUDI: 'Jeudi', VENDREDI: 'Vendredi', SAMEDI: 'Samedi' };

function formatHeure(heure) {
  if (!heure) return '';
  return heure.substring(0, 5);
}

export default function EmploiDuTempsList() {
  const [edts, setEdts] = useState([]);
  const [loading, setLoading] = useState(true);
  const [page, setPage] = useState(1);
  const [modalOpen, setModalOpen] = useState(false);
  const [deleteId, setDeleteId] = useState(null);
  const navigate = useNavigate();

  const fetchData = () => {
    setLoading(true);
    getEmploisDuTemps()
      .then((res) => setEdts(res.data))
      .catch(() => {})
      .finally(() => setLoading(false));
  };

  useEffect(() => { fetchData(); }, []);

  const handleDelete = (id) => {
    setDeleteId(id);
    setModalOpen(true);
  };

  const confirmDelete = () => {
    deleteEmploiDuTemps(deleteId).then(() => { fetchData(); setPage(1); }).catch(() => {});
    setModalOpen(false);
    setDeleteId(null);
  };

  const totalPages = Math.ceil(edts.length / ITEMS_PER_PAGE);
  const paginated = edts.slice((page - 1) * ITEMS_PER_PAGE, page * ITEMS_PER_PAGE);

  if (loading) return <div className="loading">Chargement...</div>;

  return (
    <div>
      <div className="page-header">
        <h1>Emplois du temps</h1>
        <Link to="/emploi-du-temps/nouveau" className="btn btn-primary">+ Nouvel emploi du temps</Link>
      </div>
      <div className="card">
        {edts.length === 0 ? (
          <div className="empty-state">Aucun emploi du temps trouvé</div>
        ) : (
          <>
            <table>
              <thead>
                <tr>
                  <th>Cours</th>
                  <th>Professeur</th>
                  <th>Salle</th>
                  <th>Jour</th>
                  <th>Heure début</th>
                  <th>Heure fin</th>
                  <th>Semestre</th>
                  <th>Année</th>
                  <th>Actions</th>
                </tr>
              </thead>
              <tbody>
                {paginated.map((e) => (
                  <tr key={e.id}>
                    <td>{e.coursNom}</td>
                    <td>{e.professeurNom}</td>
                    <td>{e.salleNom}</td>
                    <td>{jourLabels[e.jourSemaine] || e.jourSemaine}</td>
                    <td>{formatHeure(e.heureDebut)}</td>
                    <td>{formatHeure(e.heureFin)}</td>
                    <td>{e.semestre}</td>
                    <td>{e.anneeAcademique}</td>
                    <td className="actions">
                      <button className="btn btn-warning" onClick={() => navigate(`/emploi-du-temps/${e.id}`)}>Modifier</button>
                      <button className="btn btn-danger" onClick={() => handleDelete(e.id)}>Supprimer</button>
                    </td>
                  </tr>
                ))}
              </tbody>
            </table>
            <Pagination currentPage={page} totalPages={totalPages} onPageChange={setPage} />
          </>
        )}
      </div>
      <Modal open={modalOpen} title="Confirmer la suppression" message="Êtes-vous sûr de vouloir supprimer cet emploi du temps ?" onConfirm={confirmDelete} onCancel={() => setModalOpen(false)} danger />
    </div>
  );
}
