package tn.esprit.Contollers;

import javafx.application.Platform;
import javafx.concurrent.Worker;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.web.WebView;
import javafx.stage.Stage;
import netscape.javascript.JSObject;
import tn.esprit.services.OrderService;
import tn.esprit.services.StripeService;
import javafx.event.ActionEvent;
import java.io.IOException;
import java.net.URL;
import java.sql.SQLException;
import java.util.ResourceBundle;

public class Paiement implements Initializable {

    @FXML
    private WebView paymentWebView;

    private String clientSecret;
    private StripeService stripeService = new StripeService();
    private OrderService orderService = new OrderService();
    private double totalAmount;
    private int userId = 1; // Consider passing this dynamically if needed
    private int orderId; // ID de la commande à payer

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        System.out.println("Initializing Paiement controller");
        paymentWebView.getEngine().setJavaScriptEnabled(true); // Ensure JavaScript is enabled
    }

    public void setOrderId(int orderId) {
        this.orderId = orderId;
        System.out.println("Order ID set for payment: " + orderId);
        initializePayment();
    }

    private void initializePayment() {
        System.out.println("Starting initializePayment for order ID: " + orderId);
        try {
            // Récupérer le montant total pour la commande spécifique
            totalAmount = orderService.extractTotalFororder(orderId); // Fixed method name
            System.out.println("Total amount retrieved for order ID " + orderId + ": " + totalAmount);

            if (totalAmount <= 0 || Double.isNaN(totalAmount) || Double.isInfinite(totalAmount)) {
                showError("Montant invalide pour la commande ID: " + orderId + " (total: " + totalAmount + ")");
                return;
            }

            this.clientSecret = stripeService.createPaymentIntent(totalAmount, "usd", "Payment for order ID: " + orderId);
            System.out.println("Client secret created: " + (clientSecret != null ? clientSecret : "Failed"));

            if (clientSecret == null || clientSecret.isEmpty()) {
                showError("Impossible de créer le PaymentIntent. Vérifiez la clé secrète Stripe et la connectivité réseau.");
                return;
            }

            loadPaymentForm();

        } catch (SQLException e) {
            e.printStackTrace();
            showError("Erreur SQL lors de l'initialisation du paiement: " + e.getMessage());
        } catch (Exception e) {
            e.printStackTrace();
            showError("Erreur lors de l'initialisation du paiement: " + e.getMessage());
        }
    }

    private void loadPaymentForm() {
        System.out.println("Loading payment form for order ID: " + orderId);
        if (clientSecret == null || clientSecret.isEmpty()) {
            System.err.println("ClientSecret est nul ou vide");
            showError("ClientSecret est nul ou vide");
            return;
        }

        String paymentFormHtml = getStripePaymentForm(clientSecret, totalAmount);
        if (paymentFormHtml == null) {
            showError("Échec de la génération du formulaire de paiement");
            return;
        }
        System.out.println("Loading HTML content into WebView");
        paymentWebView.getEngine().loadContent(paymentFormHtml);

        paymentWebView.getEngine().getLoadWorker().stateProperty().addListener((observable, oldValue, newValue) -> {
            System.out.println("WebView load state changed: " + newValue);
            if (newValue == Worker.State.SUCCEEDED) {
                System.out.println("WebView content loaded successfully");
                try {
                    JSObject window = (JSObject) paymentWebView.getEngine().executeScript("window");
                    window.setMember("javaFXBridge", new JavaFXBridge());
                    paymentWebView.getEngine().executeScript(
                            "console.log = function(message) { window.javaFXBridge.log(message); };" +
                                    "console.error = function(message) { window.javaFXBridge.logError(message); };"
                    );
                    System.out.println("JavaFXBridge initialized");
                } catch (Exception e) {
                    System.err.println("Error setting up JavaFXBridge: " + e.getMessage());
                    showError("Erreur lors de l'initialisation du pont JavaScript: " + e.getMessage());
                }
            } else if (newValue == Worker.State.FAILED) {
                System.err.println("WebView content failed to load");
                showError("Échec du chargement du formulaire de paiement dans WebView");
            }
        });

        paymentWebView.getEngine().setOnError(event -> {
            System.err.println("WebView error: " + event.getMessage());
            showError("Erreur WebView: " + event.getMessage());
        });
    }

    private String getStripePaymentForm(String clientSecret, double total) {
        String publishableKey = stripeService.getPublishableKey();

        // Formatter le prix pour l'affichage
        String formattedTotal = String.format("%.2f", total);
        return String.format("""
<!DOCTYPE html>
<html>
<head>
    <title>Paiement Stripe</title>
    <script src="https://js.stripe.com/v3/"></script>
    <meta charset="UTF-8">
    <style>
        body { 
            font-family: Arial, sans-serif; 
            padding: 20px; 
            background-color: #f0e6d8;  /* Couleur marron clair/beige */ 
        }
        
        .title {
            color: #6b4226;
            text-align: center;
            font-size: 24px;
            margin-bottom: 20px;
            font-weight: bold;
        }
        
       #card-element {\s
                            width: 100%%;
                            max-width: 400px;
                            margin: 0 auto 20px auto;
                            border: 1px solid #ccc;\s
                            padding: 10px;\s
                            border-radius: 4px;\s
                            background-color: white;
                        }
                
        #submit { 
            background-color: #4CAF50; 
            color: white; 
            padding: 10px 15px; 
            border: none; 
            border-radius: 4px; 
            cursor: pointer; 
        }
        
        #submit:hover { 
            background-color: #45a049; 
        }
        
        #error-message { 
            color: red; 
            margin-top: 10px; 
        }
        
        .loading { 
            font-size: 18px; 
            color: #666; 
        }
        
        .card-info { 
            margin-top: 10px; 
            font-size: 14px; 
            color: #666; 
        }
    </style>
</head>
<body>
    <div class="title">Paiement Sécurisé </div>
    
    <div class="product-info">
        <h3>Commande</h3>
        <p><strong>Total: $%s</strong></p>
    </div>
    
    <form id="payment-form">
        <h3>Payment Information</h3>
        <div id="card-element"></div>
     <div style="text-align: center;">
                                                                  <button id="submit" style="background-color: #f5f5f5; color: black; padding: 10px 15px; border: none; border-radius: 4px; cursor: pointer;">
                                                                      Payer $%s
                                                                  </button>
                                                         
                
                                                              </div>
                
        <div id="error-message"></div>
        <p id="status-message" class="loading"></p>
    </form>
<!-- Bouton Annuler placé en dehors du formulaire -->
<div style="text-align: center; margin-top: 20px;">
    <button type="button" id="cancel" style="...">Annuler</button>
</div>
    <script>
    document.addEventListener('DOMContentLoaded', function() {
    
        // Initialize Stripe with publishable key
        var stripe = Stripe('%s');
        var elements = stripe.elements();
        var style = {
            base: { 
                fontSize: '16px', 
                color: '#32325d', 
                fontFamily: 'Arial, sans-serif',
                '::placeholder': { color: '#aab7c4' }
            },
            invalid: { 
                color: '#fa755a', 
                iconColor: '#fa755a' 
            }
        };
        document.getElementById('cancel').addEventListener('click', function() {
                            if (window.javaFXBridge) {
                                window.javaFXBridge.cancelPayment();
                            }
                        });
          
                
        // Create and mount the card element
        var cardElement = elements.create('card', {style: style});
        cardElement.mount('#card-element');
        
        // Handle form submission
        var form = document.getElementById('payment-form');
        var errorElement = document.getElementById('error-message');
        var statusMessage = document.getElementById('status-message');
        var submitButton = document.getElementById('submit');
        
        // Handle real-time validation errors
        cardElement.addEventListener('change', function(event) {
            if (event.error) {
                errorElement.textContent = event.error.message;
            } else {
                errorElement.textContent = '';
            }
        });
        
        form.addEventListener('submit', function(event) {
            event.preventDefault();
            
            // Disable the submit button to prevent repeated clicks
            submitButton.disabled = true;
            submitButton.textContent = 'Traitement en cours...';
            statusMessage.textContent = 'Traitement du paiement...';
            errorElement.textContent = '';
            
            // Get postal code if available
            var postalCode = '12345'; // Default postal code
            
            // Create payment method first
            stripe.createPaymentMethod({
                type: 'card',
                card: cardElement,
                billing_details: {
                    name: 'Test User',
                    address: {
                        postal_code: postalCode
                    }
                }
            }).then(function(paymentMethodResult) {
                if (paymentMethodResult.error) {
                    errorElement.textContent = paymentMethodResult.error.message;
                    submitButton.disabled = false;
                    submitButton.textContent = 'Payer $%s';
                    statusMessage.textContent = '';
                    return;
                }
                
                // Now confirm the payment with the created payment method
                stripe.confirmCardPayment('%s', {
                    payment_method: paymentMethodResult.paymentMethod.id
                }).then(function(result) {
                    if (result.error) {
                        // Show error to customer
                        errorElement.textContent = result.error.message;
                        submitButton.disabled = false;
                        submitButton.textContent = 'Payer $%s';
                        statusMessage.textContent = '';
                    } else {
                        // The payment succeeded!
                        if (result.paymentIntent.status === 'succeeded') {
                            document.getElementById('payment-form').innerHTML = '<h2>Paiement réussi! ✅</h2><p>Merci pour votre paiement.</p>';
                            window.javaFXBridge.paymentSuccess(result.paymentIntent.id);
                        } else {
                            errorElement.textContent = 'Statut du paiement: ' + result.paymentIntent.status;
                            submitButton.disabled = false;
                            submitButton.textContent = 'Payer $%s';
                        }
                    }
                }).catch(function(error) {
                    errorElement.textContent = 'Une erreur inattendue est survenue. Veuillez réessayer.';
                    submitButton.disabled = false;
                    submitButton.textContent = 'Payer $%s';
                    statusMessage.textContent = '';
                });
            });
        });
    });
    
    // Global error handler
    window.addEventListener('error', function(event) {
        console.error('JavaScript error:', event.message);
        if (window.javaFXBridge) {
            window.javaFXBridge.logError('JS Error: ' + event.message);
        }
    });
    </script>
</body>
</html>
""", formattedTotal, formattedTotal, publishableKey, formattedTotal, clientSecret, formattedTotal, formattedTotal, formattedTotal);
    }


    @FXML
    private void cancelPayment(ActionEvent event) {
        System.out.println("Cancel payment button clicked in JavaFX");
        navigateToProfile(event);
    }

    private void navigateToProfile(ActionEvent event) {
        System.out.println("Navigating to profile");
        try {
            Parent root = FXMLLoader.load(getClass().getResource("/profil.fxml"));
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            Scene scene = new Scene(root);
            stage.setScene(scene);
            stage.show();
            System.out.println("Navigation to profile successful");
        } catch (IOException e) {
            e.printStackTrace();
            showError("Erreur lors de la navigation vers le profil: " + e.getMessage());
        }
    }

    private void showError(String message) {
        System.out.println("Showing error: " + message);
        Platform.runLater(() -> {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Erreur");
            alert.setHeaderText(null);
            alert.setContentText(message);
            alert.showAndWait();
        });
    }

    private void showSuccessDialog(String paymentIntentId) {
        System.out.println("Showing success dialog for payment ID: " + paymentIntentId);
        Platform.runLater(() -> {
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Paiement réussi");
            alert.setHeaderText(null);
            alert.setContentText("Votre paiement de $" + String.format("%.2f", totalAmount) + " a été traité avec succès !\nID de paiement: " + paymentIntentId);
            alert.showAndWait();

            try {
                System.out.println("Updating order status to paid for order ID: " + orderId);
                orderService.updatePaidStatus(orderId, "true");
                System.out.println("Order status updated successfully for order ID: " + orderId);
            } catch (SQLException e) {
                e.printStackTrace();
                showError("Erreur lors de la mise à jour du statut de la commande: " + e.getMessage());
            }

            // Navigate back to profile
            Stage stage = (Stage) paymentWebView.getScene().getWindow();
            try {
                Parent root = FXMLLoader.load(getClass().getResource("/profil.fxml"));
                Scene scene = new Scene(root);
                stage.setScene(scene);
                stage.show();
                System.out.println("Navigated back to profile after successful payment");
            } catch (IOException e) {
                e.printStackTrace();
                showError("Erreur lors de la navigation vers le profil: " + e.getMessage());
            }
        });
    }

    public class JavaFXBridge {
        public void cancelPayment() {
            System.out.println("cancelPayment() called from JavaScript");
            Platform.runLater(() -> {
                try {
                    Parent root = FXMLLoader.load(getClass().getResource("/profil.fxml"));
                    Stage stage = (Stage) paymentWebView.getScene().getWindow();
                    Scene scene = new Scene(root);
                    stage.setScene(scene);
                    stage.show();
                    System.out.println("Navigation to profile from JavaScript cancel successful");
                } catch (IOException e) {
                    e.printStackTrace();
                    showError("Erreur lors de la navigation vers le profil: " + e.getMessage());
                }
            });
        }

        public void paymentSuccess(String paymentIntentId) {
            System.out.println("paymentSuccess called with Payment ID: " + paymentIntentId);
            try {
                // Verify the payment was successful
                if (stripeService.isPaymentIntentConfirmed(paymentIntentId)) {
                    System.out.println("Payment intent confirmed for ID: " + paymentIntentId);
                    showSuccessDialog(paymentIntentId);
                } else {
                    System.out.println("Payment intent not confirmed for ID: " + paymentIntentId);
                    showError("Le paiement n'a pas pu être confirmé. Veuillez réessayer.");
                }
            } catch (Exception e) {
                e.printStackTrace();
                showError("Erreur lors de la mise à jour de la commande: " + e.getMessage());
            }
        }

        public void log(String message) {
            System.out.println("JavaScript log: " + message);
        }

        public void logError(String error) {
            System.err.println("JavaScript error: " + error);
        }
    }
}