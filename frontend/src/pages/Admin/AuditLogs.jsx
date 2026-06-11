import { useState, useEffect } from 'react';
import { getAuditLogs } from '../../api/admin';
import Pagination from '../../components/Pagination';

const PAGE_SIZE = 20;

export default function AuditLogs() {
  const [logs, setLogs] = useState({ content: [], totalPages: 0, totalElements: 0 });
  const [loading, setLoading] = useState(true);
  const [page, setPage] = useState(0);

  const fetchData = () => {
    setLoading(true);
    getAuditLogs({ page, size: PAGE_SIZE })
      .then((res) => setLogs(res.data))
      .catch(() => {})
      .finally(() => setLoading(false));
  };

  useEffect(() => { fetchData(); }, [page]);

  const formatTimestamp = (ts) => {
    if (!ts) return '';
    const d = new Date(ts);
    return d.toLocaleString('fr-FR');
  };

  if (loading) return <div className="loading">Chargement...</div>;

  return (
    <div>
      <div className="page-header">
        <h1>Logs d'audit</h1>
      </div>
      <div className="card">
        {logs.content.length === 0 ? (
          <div className="empty-state">Aucun log trouvé</div>
        ) : (
          <>
            <p className="text-muted">{logs.totalElements} entrées au total</p>
            <table>
              <thead>
                <tr>
                  <th>Date</th>
                  <th>Action</th>
                  <th>Email</th>
                  <th>Détails</th>
                </tr>
              </thead>
              <tbody>
                {logs.content.map((log) => (
                  <tr key={log.id}>
                    <td>{formatTimestamp(log.timestamp)}</td>
                    <td><span className="statut-badge badge-info">{log.action}</span></td>
                    <td>{log.email}</td>
                    <td>{log.details || '-'}</td>
                  </tr>
                ))}
              </tbody>
            </table>
            <Pagination currentPage={logs.number + 1} totalPages={logs.totalPages} onPageChange={(p) => setPage(p - 1)} />
          </>
        )}
      </div>
    </div>
  );
}
