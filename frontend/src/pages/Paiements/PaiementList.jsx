import { useState, useEffect } from 'react';
import { Link, useNavigate } from 'react-router-dom';
import { getPaiements, deletePaiement } from '../../api/paiements';
import { exportRecuPaiement } from '../../api/export';
import Pagination from '../../components/Pagination';
import Modal from '../../components/Modal';
import { useToast } from '../../components/Toast';

const ITEMS_PER_PAGE = 10;

const statutLabels = { EN_ATTENTE: 'En attente', PAYE: 'Payé', EN_RETARD: 'En retard', ANNULE: 'Annulé' };
const statutColors = { EN_ATTENTE: '#f57f17', PAYE: '#2e7d32', EN_RETARD: '#c62828', ANNULE: '#757575' };
const modeLabels = { ESPECES: 'Espèces', VIREMENT: 'Virement', CHEQUE: 'Chèque', CARTE_BANCAIRE: 'Carte bancaire' };

export default function PaiementList() {
  const [paiements, setPaiements] = useState([]);
  const [loading, setLoading] = useState(true);
  const [page, setPage] = useState(1);
  const [modalOpen, setModalOpen] = useState(false);
  const [deleteId, setDeleteId] = useState(null);
  const navigate = useNavigate();
  const toast = useToast();

  const fetchData = () => {
    setLoading(true);
    getPaiements()
      .then((res) => setPaiements(res.data))
      .catch(() => {})
      .finally(() => setLoading(false));
  };

  useEffect(() => { fetchData(); }, []);

  const handleDelete = (id) => {
    setDeleteId(id);
    setModalOpen(true);
  };

  const confirmDelete = () => {
    deletePaiement(deleteId).then(() => { fetchData(); setPage(1); }).catch(() => {});
    setModalOpen(false);
    setDeleteId(null);
  };

  const totalPages = Math.ceil(paiements.length / ITEMS_PER_PAGE);
  const paginated = paiements.slice((page - 1) * ITEMS_PER_PAGE, page * ITEMS_PER_PAGE);

  if (loading) return <div className="loading">Chargement...</div>;

  return (
    <div>
      <div className="page-header">
        <h1>Paiements</h1>
        <Link to="/paiements/nouveau" className="btn btn-primary">+ Nouveau paiement</Link>
      </div>
      <div className="card">
        {paiements.length === 0 ? (
          <div className="empty-state">Aucun paiement trouvé</div>
        ) : (
          <>
            <table>
              <thead>
                <tr>
                  <th>Référence</th>
                  <th>Étudiant</th>
                  <th>Montant</th>
                  <th>Date</th>
                  <th>Mode</th>
                  <th>Statut</th>
                  <th>Actions</th>
                </tr>
              </thead>
              <tbody>
                {paginated.map((p) => (
                  <tr key={p.id}>
                    <td>{p.reference}</td>
                    <td>{p.etudiantNom}</td>
                    <td>{p.montant?.toLocaleString('fr-FR', { style: 'currency', currency: 'EUR' })}</td>
                    <td>{p.datePaiement}</td>
                    <td>{modeLabels[p.modePaiement] || p.modePaiement}</td>
                    <td><span className="statut-badge" style={{ background: statutColors[p.statut] }}>{statutLabels[p.statut] || p.statut}</span></td>
                    <td className="actions">
                      <button className="btn btn-primary" onClick={() => exportRecuPaiement(p.id).catch(() => toast('Erreur lors de l\'export du reçu'))}>Reçu</button>
                      <button className="btn btn-warning" onClick={() => navigate(`/paiements/${p.id}`)}>Modifier</button>
                      <button className="btn btn-danger" onClick={() => handleDelete(p.id)}>Supprimer</button>
                    </td>
                  </tr>
                ))}
              </tbody>
            </table>
            <Pagination currentPage={page} totalPages={totalPages} onPageChange={setPage} />
          </>
        )}
      </div>
      <Modal open={modalOpen} title="Confirmer la suppression" message="Êtes-vous sûr de vouloir supprimer ce paiement ?" onConfirm={confirmDelete} onCancel={() => setModalOpen(false)} danger />
    </div>
  );
}
