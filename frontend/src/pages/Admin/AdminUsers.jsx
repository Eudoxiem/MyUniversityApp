import { useState, useEffect } from 'react';
import { useNavigate } from 'react-router-dom';
import { getUsers, toggleActif, deblloquer } from '../../api/admin';
import Pagination from '../../components/Pagination';
import { useToast } from '../../components/Toast';

const ITEMS_PER_PAGE = 10;

const roleLabels = {
  ROLE_ADMIN: 'Admin',
  ROLE_PROFESSEUR: 'Professeur',
  ROLE_ETUDIANT: 'Étudiant',
};

export default function AdminUsers() {
  const [users, setUsers] = useState([]);
  const [loading, setLoading] = useState(true);
  const [page, setPage] = useState(1);
  const navigate = useNavigate();
  const toast = useToast();

  const fetchData = () => {
    setLoading(true);
    getUsers()
      .then((res) => setUsers(res.data))
      .catch(() => toast('Erreur lors du chargement des utilisateurs', 'error'))
      .finally(() => setLoading(false));
  };

  useEffect(() => { fetchData(); }, []);

  const handleToggleActif = (id, email) => {
    toggleActif(id)
      .then(() => {
        toast(`Statut de ${email} modifié`);
        fetchData();
      })
      .catch(() => toast('Erreur lors du changement de statut', 'error'));
  };

  const handleDebloquer = (id, email) => {
    deblloquer(id)
      .then(() => {
        toast(`${email} débloqué`);
        fetchData();
      })
      .catch(() => toast('Erreur lors du déblocage', 'error'));
  };

  const totalPages = Math.ceil(users.length / ITEMS_PER_PAGE);
  const paginated = users.slice((page - 1) * ITEMS_PER_PAGE, page * ITEMS_PER_PAGE);

  if (loading) return <div className="loading">Chargement...</div>;

  return (
    <div>
      <div className="page-header">
        <h1>Gestion des utilisateurs</h1>
        <div className="page-actions">
          <button className="btn btn-primary" onClick={() => navigate('/admin/utilisateurs/nouveau')}>+ Nouvel utilisateur</button>
        </div>
      </div>
      <div className="card">
        {users.length === 0 ? (
          <div className="empty-state">Aucun utilisateur trouvé</div>
        ) : (
          <>
            <table>
              <thead>
                <tr>
                  <th>Email</th>
                  <th>Nom</th>
                  <th>Prénom</th>
                  <th>Rôle</th>
                  <th>Actif</th>
                  <th>Verrouillé</th>
                  <th>Actions</th>
                </tr>
              </thead>
              <tbody>
                {paginated.map((u) => (
                  <tr key={u.id}>
                    <td>{u.email}</td>
                    <td>{u.nom}</td>
                    <td>{u.prenom}</td>
                    <td>{roleLabels[u.role] || u.role}</td>
                    <td>
                      <span className={`statut-badge ${u.actif ? 'badge-success' : 'badge-error'}`}>
                        {u.actif ? 'Oui' : 'Non'}
                      </span>
                    </td>
                    <td>
                      {u.verrouille ? (
                        <span className="statut-badge badge-error">Oui</span>
                      ) : (
                        <span className="statut-badge badge-success">Non</span>
                      )}
                    </td>
                    <td className="actions">
                      <button className="btn btn-warning" onClick={() => navigate(`/admin/utilisateurs/${u.id}`)}>Modifier</button>
                      <button className={`btn ${u.actif ? 'btn-danger' : 'btn-success'}`} onClick={() => handleToggleActif(u.id, u.email)}>
                        {u.actif ? 'Désactiver' : 'Activer'}
                      </button>
                      {u.verrouille && (
                        <button className="btn btn-outline" onClick={() => handleDebloquer(u.id, u.email)}>Débloquer</button>
                      )}
                    </td>
                  </tr>
                ))}
              </tbody>
            </table>
            <Pagination currentPage={page} totalPages={totalPages} onPageChange={setPage} />
          </>
        )}
      </div>
    </div>
  );
}
