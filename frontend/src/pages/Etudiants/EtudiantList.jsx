import { useState, useEffect } from 'react';
import { Link, useNavigate } from 'react-router-dom';
import { getEtudiants, deleteEtudiant } from '../../api/etudiants';

export default function EtudiantList() {
  const [etudiants, setEtudiants] = useState([]);
  const [loading, setLoading] = useState(true);
  const navigate = useNavigate();

  const fetchEtudiants = () => {
    setLoading(true);
    getEtudiants()
      .then((res) => setEtudiants(res.data))
      .catch(() => {})
      .finally(() => setLoading(false));
  };

  useEffect(() => { fetchEtudiants(); }, []);

  const handleDelete = (id) => {
    if (window.confirm('Supprimer cet étudiant ?')) {
      deleteEtudiant(id).then(fetchEtudiants).catch(() => {});
    }
  };

  if (loading) return <div className="loading">Chargement...</div>;

  return (
    <div>
      <div className="page-header">
        <h1>Étudiants</h1>
        <Link to="/etudiants/nouveau" className="btn btn-primary">+ Nouvel étudiant</Link>
      </div>
      <div className="card">
        {etudiants.length === 0 ? (
          <div className="empty-state">Aucun étudiant trouvé</div>
        ) : (
          <table>
            <thead>
              <tr>
                <th>Matricule</th>
                <th>Nom</th>
                <th>Prénom</th>
                <th>Email</th>
                <th>Téléphone</th>
                <th>Actions</th>
              </tr>
            </thead>
            <tbody>
              {etudiants.map((e) => (
                <tr key={e.id}>
                  <td>{e.matricule}</td>
                  <td>{e.nom}</td>
                  <td>{e.prenom}</td>
                  <td>{e.email}</td>
                  <td>{e.telephone}</td>
                  <td className="actions">
                    <button className="btn btn-warning" onClick={() => navigate(`/etudiants/${e.id}`)}>Modifier</button>
                    <button className="btn btn-danger" onClick={() => handleDelete(e.id)}>Supprimer</button>
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
