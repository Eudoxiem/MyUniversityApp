import { useState, useEffect } from 'react';
import { Link, useNavigate } from 'react-router-dom';
import { getCours, deleteCour } from '../../api/cours';

export default function CoursList() {
  const [cours, setCours] = useState([]);
  const [loading, setLoading] = useState(true);
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
    if (window.confirm('Supprimer ce cours ?')) {
      deleteCour(id).then(fetchData).catch(() => {});
    }
  };

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
              {cours.map((c) => (
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
        )}
      </div>
    </div>
  );
}
