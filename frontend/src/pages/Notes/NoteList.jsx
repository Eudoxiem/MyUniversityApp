import { useState, useEffect } from 'react';
import { Link, useNavigate } from 'react-router-dom';
import { getNotes, deleteNote } from '../../api/notes';
import Pagination from '../../components/Pagination';
import Modal from '../../components/Modal';

const ITEMS_PER_PAGE = 10;

export default function NoteList() {
  const [notes, setNotes] = useState([]);
  const [loading, setLoading] = useState(true);
  const [page, setPage] = useState(1);
  const [modalOpen, setModalOpen] = useState(false);
  const [deleteId, setDeleteId] = useState(null);
  const navigate = useNavigate();

  const fetchData = () => {
    setLoading(true);
    getNotes()
      .then((res) => setNotes(res.data))
      .catch(() => {})
      .finally(() => setLoading(false));
  };

  useEffect(() => { fetchData(); }, []);

  const handleDelete = (id) => {
    setDeleteId(id);
    setModalOpen(true);
  };

  const confirmDelete = () => {
    deleteNote(deleteId).then(() => { fetchData(); setPage(1); }).catch(() => {});
    setModalOpen(false);
    setDeleteId(null);
  };

  const totalPages = Math.ceil(notes.length / ITEMS_PER_PAGE);
  const paginated = notes.slice((page - 1) * ITEMS_PER_PAGE, page * ITEMS_PER_PAGE);

  if (loading) return <div className="loading">Chargement...</div>;

  return (
    <div>
      <div className="page-header">
        <h1>Notes</h1>
        <Link to="/notes/nouveau" className="btn btn-primary">+ Nouvelle note</Link>
      </div>
      <div className="card">
        {notes.length === 0 ? (
          <div className="empty-state">Aucune note trouvée</div>
        ) : (
          <>
            <table>
              <thead>
                <tr>
                  <th>Inscription</th>
                  <th>Valeur</th>
                  <th>Coefficient</th>
                  <th>Type</th>
                  <th>Date</th>
                  <th>Actions</th>
                </tr>
              </thead>
              <tbody>
                {paginated.map((n) => (
                  <tr key={n.id}>
                    <td>{n.inscriptionId}</td>
                    <td>{n.valeur}</td>
                    <td>{n.coefficient}</td>
                    <td>{n.type}</td>
                    <td>{n.dateSaisie ? new Date(n.dateSaisie).toLocaleDateString('fr-FR') : ''}</td>
                    <td className="actions">
                      <button className="btn btn-warning" onClick={() => navigate(`/notes/${n.id}`)}>Modifier</button>
                      <button className="btn btn-danger" onClick={() => handleDelete(n.id)}>Supprimer</button>
                    </td>
                  </tr>
                ))}
              </tbody>
            </table>
            <Pagination currentPage={page} totalPages={totalPages} onPageChange={setPage} />
          </>
        )}
      </div>
      <Modal open={modalOpen} title="Confirmer la suppression" message="Êtes-vous sûr de vouloir supprimer cette note ?" onConfirm={confirmDelete} onCancel={() => setModalOpen(false)} danger />
    </div>
  );
}
