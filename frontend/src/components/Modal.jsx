export default function Modal({ open, title, message, onConfirm, onCancel, confirmLabel = 'Confirmer', cancelLabel = 'Annuler', danger = false }) {
  if (!open) return null;

  return (
    <div className="modal-overlay" onClick={onCancel}>
      <div className="modal-content" onClick={(e) => e.stopPropagation()}>
        <h3>{title}</h3>
        <p>{message}</p>
        <div className="modal-actions">
          <button className={`btn ${danger ? 'btn-danger' : 'btn-primary'}`} onClick={onConfirm}>{confirmLabel}</button>
          <button className="btn btn-secondary" onClick={onCancel}>{cancelLabel}</button>
        </div>
      </div>
    </div>
  );
}
