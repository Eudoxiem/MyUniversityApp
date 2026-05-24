import { useState, useEffect } from 'react';
import { Link, useNavigate } from 'react-router-dom';
import { getSalles, deleteSalle } from '../../api/salles';

export default function SalleList() {
  const [salles, setSalles] = useState([]);
  const [loading, setLoading] = useState(true);
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
    if (window.confirm('Supprimer cette salle ?')) {
      deleteSalle(id).then(fetchData).catch(() => {});
    }
  };

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
              {salles.map((s) => (
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
        )}
      </div>
    </div>
  );
}
