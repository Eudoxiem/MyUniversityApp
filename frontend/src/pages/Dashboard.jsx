import { useState, useEffect } from 'react';
import {
  BarChart, Bar, XAxis, YAxis, CartesianGrid, Tooltip, ResponsiveContainer,
  PieChart, Pie, Cell, Legend,
} from 'recharts';
import { getStatsGenerales, getStatsCours } from '../api/stats';

const COLORS = ['#1a237e', '#283593', '#3949ab', '#5c6bc0', '#7986cb', '#9fa8da'];
const PIE_COLORS = ['#2e7d32', '#f57f17', '#c62828'];

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

  const overviewData = stats ? [
    { name: 'Étudiants', value: stats.totalEtudiants },
    { name: 'Professeurs', value: stats.totalProfesseurs },
    { name: 'Cours', value: stats.totalCours },
    { name: 'Salles', value: stats.totalSalles },
    { name: 'Inscriptions', value: stats.totalInscriptions },
    { name: 'Paiements', value: stats.totalPaiements },
  ] : [];

  const paymentData = stats ? [
    { name: 'Payés', value: stats.totalPaiementsPayes },
    { name: 'En attente', value: stats.totalPaiementsEnAttente },
    { name: 'En retard', value: stats.totalPaiementsEnRetard },
  ].filter((d) => d.value > 0) : [];

  const coursChartData = statsCours.map((c) => ({
    name: c.code,
    étudiants: c.nombreEtudiants,
    moyenne: c.moyenneGenerale != null ? +c.moyenneGenerale.toFixed(1) : 0,
  }));

  return (
    <div>
      <div className="page-header">
        <h1>Tableau de bord</h1>
      </div>

      {stats && (
        <>
          <h2 className="dashboard-section-title">Vue d'ensemble</h2>
          <div className="dashboard-grid">
            {overviewData.map((card) => (
              <div key={card.name} className="dashboard-card">
                <h3>{card.value}</h3>
                <p>{card.name}</p>
              </div>
            ))}
          </div>

          <div className="chart-row">
            <div className="card chart-card">
              <h3 className="chart-title">Répartition générale</h3>
              <ResponsiveContainer width="100%" height={300}>
                <BarChart data={overviewData} margin={{ top: 10, right: 20, left: 0, bottom: 5 }}>
                  <CartesianGrid strokeDasharray="3 3" stroke="#e0e0e0" />
                  <XAxis dataKey="name" tick={{ fontSize: 12 }} />
                  <YAxis tick={{ fontSize: 12 }} />
                  <Tooltip />
                  <Bar dataKey="value" fill="#1a237e" radius={[4, 4, 0, 0]} />
                </BarChart>
              </ResponsiveContainer>
            </div>

            {paymentData.length > 0 && (
              <div className="card chart-card">
                <h3 className="chart-title">Statut des paiements</h3>
                <ResponsiveContainer width="100%" height={300}>
                  <PieChart>
                    <Pie data={paymentData} cx="50%" cy="50%" innerRadius={60} outerRadius={100} dataKey="value" label={({ name, value }) => `${name}: ${value}`}>
                      {paymentData.map((_, i) => (
                        <Cell key={i} fill={PIE_COLORS[i]} />
                      ))}
                    </Pie>
                    <Tooltip />
                    <Legend />
                  </PieChart>
                </ResponsiveContainer>
              </div>
            )}
          </div>

          <h2 className="dashboard-section-title">Paiements</h2>
          <div className="dashboard-grid">
            <div className="dashboard-card">
              <h3 style={{ color: '#2e7d32' }}>{stats.totalPaiementsPayes}</h3>
              <p>Payés</p>
            </div>
            <div className="dashboard-card">
              <h3 style={{ color: '#f57f17' }}>{stats.totalPaiementsEnAttente}</h3>
              <p>En attente</p>
            </div>
            <div className="dashboard-card">
              <h3 style={{ color: '#c62828' }}>{stats.totalPaiementsEnRetard}</h3>
              <p>En retard</p>
            </div>
          </div>
        </>
      )}

      {statsCours.length > 0 && (
        <>
          <div className="card" style={{ marginTop: 24 }}>
            <h2 className="dashboard-section-title">Étudiants par cours</h2>
            <ResponsiveContainer width="100%" height={300}>
              <BarChart data={coursChartData} margin={{ top: 10, right: 20, left: 0, bottom: 5 }}>
                <CartesianGrid strokeDasharray="3 3" stroke="#e0e0e0" />
                <XAxis dataKey="name" tick={{ fontSize: 12 }} />
                <YAxis tick={{ fontSize: 12 }} />
                <Tooltip />
                <Bar dataKey="étudiants" fill="#3949ab" radius={[4, 4, 0, 0]} />
              </BarChart>
            </ResponsiveContainer>
          </div>

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
        </>
      )}
    </div>
  );
}
