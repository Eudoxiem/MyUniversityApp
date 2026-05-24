import { useState, useEffect } from 'react';
import { getEtudiants } from '../api/etudiants';
import { getProfesseurs } from '../api/professeurs';
import { getCours } from '../api/cours';
import { getSalles } from '../api/salles';
import { getInscriptions } from '../api/inscriptions';

export default function Dashboard() {
  const [stats, setStats] = useState({});
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    Promise.all([
      getEtudiants(),
      getProfesseurs(),
      getCours(),
      getSalles(),
      getInscriptions(),
    ])
      .then(([etudiants, professeurs, cours, salles, inscriptions]) => {
        setStats({
          etudiants: etudiants.data.length,
          professeurs: professeurs.data.length,
          cours: cours.data.length,
          salles: salles.data.length,
          inscriptions: inscriptions.data.length,
        });
      })
      .catch(() => {})
      .finally(() => setLoading(false));
  }, []);

  if (loading) return <div className="loading">Chargement...</div>;

  const cards = [
    { label: 'Étudiants', value: stats.etudiants },
    { label: 'Professeurs', value: stats.professeurs },
    { label: 'Cours', value: stats.cours },
    { label: 'Salles', value: stats.salles },
    { label: 'Inscriptions', value: stats.inscriptions },
  ];

  return (
    <div>
      <div className="page-header">
        <h1>Tableau de bord</h1>
      </div>
      <div className="dashboard-grid">
        {cards.map((card) => (
          <div key={card.label} className="dashboard-card">
            <h3>{card.value}</h3>
            <p>{card.label}</p>
          </div>
        ))}
      </div>
    </div>
  );
}
