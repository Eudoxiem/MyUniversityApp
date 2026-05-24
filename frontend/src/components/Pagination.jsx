export default function Pagination({ currentPage, totalPages, onPageChange }) {
  if (totalPages <= 1) return null;

  const pages = [];
  for (let i = 1; i <= totalPages; i++) {
    pages.push(i);
  }

  return (
    <div className="pagination">
      <button className="btn btn-secondary" disabled={currentPage === 1} onClick={() => onPageChange(currentPage - 1)}>‹ Précédent</button>
      {pages.map((p) => (
        <button key={p} className={`btn ${p === currentPage ? 'btn-primary' : 'btn-secondary'}`} onClick={() => onPageChange(p)}>{p}</button>
      ))}
      <button className="btn btn-secondary" disabled={currentPage === totalPages} onClick={() => onPageChange(currentPage + 1)}>Suivant ›</button>
    </div>
  );
}
