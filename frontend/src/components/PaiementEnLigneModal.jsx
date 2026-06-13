import { useState, useEffect } from 'react';
import { loadStripe } from '@stripe/stripe-js';
import { Elements, CardElement, useStripe, useElements } from '@stripe/react-stripe-js';
import { initierPaiementEnLigne, confirmerPaiement, getStripeConfig } from '../api/paiements';
import { useToast } from './Toast';

function PaymentForm({ paiement, clientSecret, onSuccess, onError }) {
  const stripe = useStripe();
  const elements = useElements();
  const [submitting, setSubmitting] = useState(false);
  const toast = useToast();

  const handleSubmit = async (e) => {
    e.preventDefault();
    if (!stripe || !elements) return;

    setSubmitting(true);
    try {
      const { error, paymentIntent } = await stripe.confirmCardPayment(clientSecret, {
        payment_method: { card: elements.getElement(CardElement) },
      });

      if (error) {
        toast(error.message || 'Erreur de paiement');
        onError(error);
        return;
      }

      if (paymentIntent.status === 'succeeded') {
        await confirmerPaiement(paymentIntent.id);
        toast('Paiement effectué avec succès !');
        onSuccess();
      }
    } catch {
      toast('Erreur lors du paiement');
      onError();
    } finally {
      setSubmitting(false);
    }
  };

  return (
    <form onSubmit={handleSubmit}>
      <div className="form-group">
        <label>Carte bancaire</label>
        <div style={{ padding: '10px 12px', border: '1px solid #ddd', borderRadius: 6, background: '#fff' }}>
          <CardElement options={{ style: { base: { fontSize: '16px', color: '#333' } } }} />
        </div>
      </div>
      <p style={{ fontSize: '0.85rem', color: '#999', marginBottom: 16 }}>
        Montant : {paiement.montant?.toLocaleString('fr-FR', { style: 'currency', currency: 'EUR' })}
      </p>
      <button type="submit" className="btn btn-success btn-block" disabled={!stripe || submitting}>
        {submitting ? 'Paiement en cours...' : `Payer ${paiement.montant?.toLocaleString('fr-FR', { style: 'currency', currency: 'EUR' })}`}
      </button>
    </form>
  );
}

export default function PaiementEnLigneModal({ paiement, onClose, onPaid }) {
  const [stripePromise, setStripePromise] = useState(null);
  const [clientSecret, setClientSecret] = useState(null);
  const [error, setError] = useState('');
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    let cancelled = false;

    async function init() {
      try {
        const configRes = await getStripeConfig();
        if (cancelled) return;
        const stripe = await loadStripe(configRes.data.publishableKey);
        if (cancelled) return;
        setStripePromise(stripe);

        const piRes = await initierPaiementEnLigne(paiement.id);
        if (cancelled) return;
        setClientSecret(piRes.data.clientSecret);
      } catch {
        if (!cancelled) setError('Impossible d\'initialiser le paiement en ligne');
      } finally {
        if (!cancelled) setLoading(false);
      }
    }

    init();
    return () => { cancelled = true; };
  }, [paiement.id]);

  return (
    <div className="modal-overlay" onClick={onClose}>
      <div className="modal-content" onClick={(e) => e.stopPropagation()}>
        <h3>Paiement en ligne</h3>
        <p style={{ marginBottom: 20, color: '#666' }}>
          Réf. {paiement.reference} — {paiement.etudiantNom}
        </p>

        {loading && <p className="loading">Initialisation du paiement...</p>}
        {error && <div className="alert alert-error">{error}</div>}

        {stripePromise && clientSecret && (
          <Elements stripe={stripePromise} options={{ clientSecret }}>
            <PaymentForm
              paiement={paiement}
              clientSecret={clientSecret}
              onSuccess={() => { onPaid(); onClose(); }}
              onError={() => setError('Le paiement a échoué. Veuillez réessayer.')}
            />
          </Elements>
        )}

        <div className="modal-actions" style={{ marginTop: 16 }}>
          <button className="btn btn-secondary" onClick={onClose}>Annuler</button>
        </div>
      </div>
    </div>
  );
}
