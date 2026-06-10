import { useState, useEffect } from 'react';
import { getStatsGenerales, getStatsCours } from '../api/stats';

export default function Dashboard() {
  const [stats, setStats] = useState(null);
  const [statsCours, setStatsCours] = useState([]);
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    Promise.all([getStatsGenerales(), getStatsCours()])
      .then(([gen, cours]) => {
        setStats(gen.data);
        setStatsCours(cours.data);
      })
      .catch(() => {})
      .finally(() => setLoading(false));
  }, []);

  if (loading) return <div className="loading">Chargement...</div>;

  const mainCards = stats ? [
    { label: 'Étudiants', value: stats.totalEtudiants },
    { label: 'Professeurs', value: stats.totalProfesseurs },
    { label: 'Cours', value: stats.totalCours },
    { label: 'Salles', value: stats.totalSalles },
    { label: 'Inscriptions', value: stats.totalInscriptions },
    { label: 'Paiements', value: stats.totalPaiements },
    { label: 'Payés', value: stats.totalPaiementsPayes },
    { label: 'En attente', value: stats.totalPaiementsEnAttente },
    { label: 'En retard', value: stats.totalPaiementsEnRetard },
  ] : [];

  return (
    <div>
      <div className="page-header">
        <h1>Tableau de bord</h1>
      </div>

      {stats && (
        <>
          <h2 style={{ marginBottom: 16, color: '#1a237e', fontSize: '1.1rem' }}>Vue d'ensemble</h2>
          <div className="dashboard-grid">
            {mainCards.slice(0, 6).map((card) => (
              <div key={card.label} className="dashboard-card">
                <h3>{card.value}</h3>
                <p>{card.label}</p>
              </div>
            ))}
          </div>

          <h2 style={{ margin: '24px 0 16px', color: '#1a237e', fontSize: '1.1rem' }}>Paiements</h2>
          <div className="dashboard-grid">
            {mainCards.slice(6).map((card) => (
              <div key={card.label} className="dashboard-card">
                <h3 style={{ color: card.label === 'En retard' ? '#c62828' : card.label === 'Payés' ? '#2e7d32' : '#f57f17' }}>{card.value}</h3>
                <p>{card.label}</p>
              </div>
            ))}
          </div>
        </>
      )}

      {statsCours.length > 0 && (
        <div className="card" style={{ marginTop: 24 }}>
          <h2 style={{ marginBottom: 16, color: '#1a237e', fontSize: '1.1rem' }}>Statistiques par cours</h2>
          <table>
            <thead>
              <tr>
                <th>Code</th>
                <th>Cours</th>
                <th>Inscriptions</th>
                <th>Étudiants</th>
                <th>Moyenne</th>
              </tr>
            </thead>
            <tbody>
              {statsCours.map((c) => (
                <tr key={c.coursId}>
                  <td>{c.code}</td>
                  <td>{c.coursNom}</td>
                  <td>{c.totalInscriptions}</td>
                  <td>{c.nombreEtudiants}</td>
                  <td>{c.moyenneGenerale != null ? c.moyenneGenerale.toFixed(2) : '-'}</td>
                </tr>
              ))}
            </tbody>
          </table>
        </div>
      )}
    </div>
  );
}
