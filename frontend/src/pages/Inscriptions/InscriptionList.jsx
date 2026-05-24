import { useState, useEffect } from 'react';
import { Link, useNavigate } from 'react-router-dom';
import { getInscriptions, deleteInscription } from '../../api/inscriptions';

export default function InscriptionList() {
  const [inscriptions, setInscriptions] = useState([]);
  const [loading, setLoading] = useState(true);
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
    if (window.confirm('Supprimer cette inscription ?')) {
      deleteInscription(id).then(fetchData).catch(() => {});
    }
  };

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
              {inscriptions.map((ins) => (
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
        )}
      </div>
    </div>
  );
}
