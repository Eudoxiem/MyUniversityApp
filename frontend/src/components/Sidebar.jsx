import { NavLink } from 'react-router-dom';

const links = [
  { to: '/', label: 'Tableau de bord' },
  { to: '/etudiants', label: 'Étudiants' },
  { to: '/professeurs', label: 'Professeurs' },
  { to: '/cours', label: 'Cours' },
  { to: '/salles', label: 'Salles' },
  { to: '/inscriptions', label: 'Inscriptions' },
  { to: '/notes', label: 'Notes' },
  { to: '/grades', label: 'Grades' },
];

export default function Sidebar() {
  return (
    <aside className="sidebar">
      <h2>MyUniversityApp</h2>
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
      </nav>
    </aside>
  );
}
