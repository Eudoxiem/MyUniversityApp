import { useState, useEffect } from 'react';
import { Link, useNavigate } from 'react-router-dom';
import { getProfesseurs, deleteProfesseur } from '../../api/professeurs';

export default function ProfesseurList() {
  const [professeurs, setProfesseurs] = useState([]);
  const [loading, setLoading] = useState(true);
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
    if (window.confirm('Supprimer ce professeur ?')) {
      deleteProfesseur(id).then(fetchData).catch(() => {});
    }
  };

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
              {professeurs.map((p) => (
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
        )}
      </div>
    </div>
  );
}
