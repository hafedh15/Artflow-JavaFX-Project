package tn.artflow.services;

import com.stripe.Stripe;
import com.stripe.exception.StripeException;
import com.stripe.model.PaymentIntent;
import com.stripe.param.PaymentIntentCreateParams;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public class StripeService {
    // Remplacez ces clés par vos clés Stripe API réelles
    private static final String PUBLISHABLE_KEY = "pk_test_51RI8wo4aTYvHrpTJrFrCQpyDREOACBbLWVbFJTm84AmxQzMslIZ0UqQtZjw6PEbpt6fbJzARm7qVILxn0lgQpuwf00jTkD1TWI";
    private static final String SECRET_KEY = "sk_test_51RI8wo4aTYvHrpTJYVlDGIs3j2TLOZVQUp1ItvLl9qvr1chGoeQDqpVuTPl24o1Hjkpk4EFmoF8MpfliyYpBl8Wx00bsfhJDDK";

    public StripeService() {
        Stripe.apiKey = SECRET_KEY;
    }

    public String getPublishableKey() {
        return PUBLISHABLE_KEY;
    }

    public String createPaymentIntent(double amount, String currency, String description) {
        try {
            // Convertir le montant en centimes (Stripe utilise la plus petite unité monétaire)
            long amountInCents = (long) (amount * 100);

            System.out.println("Création du PaymentIntent pour un montant de: " + amountInCents + " " + currency);

            // Utiliser Map directement au lieu des paramètres du builder pour plus de flexibilité
            Map<String, Object> params = new HashMap<>();
            params.put("amount", amountInCents);
            params.put("currency", currency.toLowerCase());
            params.put("description", description);
            params.put("payment_method_types", Arrays.asList("card"));

            // Important - indique à Stripe d'attendre une confirmation explicite
            params.put("capture_method", "automatic");

            // Utilisez la méthode create avec Map au lieu du builder
            PaymentIntent paymentIntent = PaymentIntent.create(params);

            System.out.println("PaymentIntent créé avec succès: " + paymentIntent.getId());
            return paymentIntent.getClientSecret();
        } catch (StripeException e) {
            e.printStackTrace();
            System.err.println("Erreur lors de la création du PaymentIntent : " + e.getMessage());
            return null;
        }
    }

    public boolean isPaymentIntentConfirmed(String paymentIntentId) {
        try {
            System.out.println("Vérification du statut de paiement pour: " + paymentIntentId);
            PaymentIntent paymentIntent = PaymentIntent.retrieve(paymentIntentId);
            String status = paymentIntent.getStatus();
            System.out.println("Statut du paiement: " + status);
            return "succeeded".equals(status);
        } catch (StripeException e) {
            e.printStackTrace();
            System.err.println("Erreur lors de la vérification du PaymentIntent : " + e.getMessage());
            return false;
        }
    }
}