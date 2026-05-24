import { useState, useEffect } from 'react';
import { Link, useNavigate } from 'react-router-dom';
import { getGrades, deleteGrade } from '../../api/grades';

export default function GradeList() {
  const [grades, setGrades] = useState([]);
  const [loading, setLoading] = useState(true);
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
    if (window.confirm('Supprimer ce grade ?')) {
      deleteGrade(id).then(fetchData).catch(() => {});
    }
  };

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
              {grades.map((g) => (
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
        )}
      </div>
    </div>
  );
}
