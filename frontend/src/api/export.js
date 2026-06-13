import api from './axios';

function downloadPdf(url, filename) {
  return api.get(url, { responseType: 'blob' }).then((res) => {
    const blob = new Blob([res.data], { type: 'application/pdf' });
    const link = document.createElement('a');
    link.href = URL.createObjectURL(blob);
    link.download = filename;
    document.body.appendChild(link);
    link.click();
    document.body.removeChild(link);
    URL.revokeObjectURL(link.href);
  });
}

export const exportBulletin = (etudiantId) =>
  downloadPdf(`/export/bulletin/${etudiantId}`, `bulletin_${etudiantId}.pdf`);

export const exportListeEtudiants = (coursId) =>
  downloadPdf(`/export/liste-etudiants/${coursId}`, `liste_etudiants_cours_${coursId}.pdf`);

export const exportRecuPaiement = (paiementId) =>
  downloadPdf(`/export/recu-paiement/${paiementId}`, `recu_paiement_${paiementId}.pdf`);
