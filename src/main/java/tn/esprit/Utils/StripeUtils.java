package tn.esprit.Utils;

import com.stripe.Stripe;
public class StripeUtils {
    public static final String PUBLISHABLE_KEY = "pk_test_51RI8wo4aTYvHrpTJrFrCQpyDREOACBbLWVbFJTm84AmxQzMslIZ0UqQtZjw6PEbpt6fbJzARm7qVILxn0lgQpuwf00jTkD1TWI";
    public static final String SECRET_KEY = "sk_test_51RI8wo4aTYvHrpTJYVlDGIs3j2TLOZVQUp1ItvLl9qvr1chGoeQDqpVuTPl24o1Hjkpk4EFmoF8MpfliyYpBl8Wx00bsfhJDDK";


    static {
        // Configuration de Stripe avec la clé secrète
        Stripe.apiKey = SECRET_KEY;
    }

    // Remplacez cette méthode par la méthode fonctionnelle
    public static boolean isNetworkAvailable() {
        return testStripeConnection();
    }

    public static boolean testStripeConnection() {
        try {
            Stripe.apiKey = SECRET_KEY;
            // Essayer de récupérer un objet simple, comme la balance
            com.stripe.model.Balance balance = com.stripe.model.Balance.retrieve();
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            System.err.println("Erreur Stripe: " + e.getMessage());
            return false;
        }
    }
}