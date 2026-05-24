import { useState, useEffect } from 'react';
import { Link, useNavigate } from 'react-router-dom';
import { getNotes, deleteNote } from '../../api/notes';

export default function NoteList() {
  const [notes, setNotes] = useState([]);
  const [loading, setLoading] = useState(true);
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
    if (window.confirm('Supprimer cette note ?')) {
      deleteNote(id).then(fetchData).catch(() => {});
    }
  };

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
              {notes.map((n) => (
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
        )}
      </div>
    </div>
  );
}
