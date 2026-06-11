import { NavLink, useNavigate } from 'react-router-dom';
import { useAuth } from '../context/AuthContext';
import { useToast } from './Toast';

const links = [
  { to: '/', label: 'Tableau de bord' },
  { to: '/etudiants', label: 'Étudiants' },
  { to: '/professeurs', label: 'Professeurs' },
  { to: '/cours', label: 'Cours' },
  { to: '/salles', label: 'Salles' },
  { to: '/inscriptions', label: 'Inscriptions' },
  { to: '/notes', label: 'Notes' },
  { to: '/grades', label: 'Grades' },
  { to: '/paiements', label: 'Paiements' },
  { to: '/emploi-du-temps', label: 'Emploi du temps' },
  { to: '/presences', label: 'Présences' },
  { to: '/fichiers', label: 'Fichiers' },
];

export default function Sidebar() {
  const { user, logout } = useAuth();
  const navigate = useNavigate();
  const toast = useToast();

  const handleLogout = () => {
    logout();
    toast('Déconnexion réussie');
    navigate('/login');
  };

  return (
    <aside className="sidebar">
      <h2>MyUniversityApp</h2>
      {user && (
        <div className="sidebar-user">
          <span className="sidebar-user-name">{user.prenom} {user.nom}</span>
          <span className="sidebar-user-role">{user.role}</span>
        </div>
      )}
      <nav>
        {links.map((link) => (
          <NavLink
            key={link.to}
            to={link.to}
            end={link.to === '/'}
            className={({ isActive }) => (isActive ? 'active' : '')}
          >
            {link.label}
          </NavLink>
        ))}
        {user?.role === 'ROLE_ADMIN' && (
          <>
            <div className="sidebar-section">Administration</div>
            <NavLink to="/admin/utilisateurs" className={({ isActive }) => (isActive ? 'active' : '')}>
              Utilisateurs
            </NavLink>
            <NavLink to="/admin/audit-logs" className={({ isActive }) => (isActive ? 'active' : '')}>
              Logs d'audit
            </NavLink>
          </>
        )}
      </nav>
      <div className="sidebar-footer">
        <button className="btn btn-logout" onClick={handleLogout}>Déconnexion</button>
      </div>
    </aside>
  );
}
