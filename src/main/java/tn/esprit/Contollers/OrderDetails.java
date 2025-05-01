package tn.esprit.Contollers;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.ImageView;
import javafx.stage.Stage;
import tn.esprit.entities.Cart;
import tn.esprit.entities.User;
import tn.esprit.services.OrderService;
import tn.esprit.entities.Order;

import java.io.IOException;
import java.util.Date;
import java.util.Optional;

public class OrderDetails {

    private int cartId;
    private String orderSummary;
    @FXML
    private ImageView successGif;
    // === Méthodes pour recevoir les données depuis CartController ===
    public void setCartId(int id) {
        this.cartId = id;
    }

    public void setOrderSummary(String orderSummary) {
        this.orderSummary = orderSummary;
    }

    @FXML
    private TextField addressField;

    @FXML
    private TextField phoneField;

    @FXML
    private Button confirmOrderButton;
    @FXML
    private Label addressErrorLabel;

    @FXML
    private Label phoneErrorLabel;

    @FXML
    void confirmOrder(ActionEvent event) {
        String address = addressField.getText().trim();
        String phone = phoneField.getText().trim();

        // Réinitialiser les messages d'erreur et les styles
        addressErrorLabel.setVisible(false);
        phoneErrorLabel.setVisible(false);
        addressField.setStyle(""); // réinitialise le style
        phoneField.setStyle("");

        boolean valid = true;

        // Validation adresse
        if (address.isEmpty()) {
            addressErrorLabel.setText("Ce champ est vide. Veuillez remplir l'adresse.");
            addressErrorLabel.setVisible(true);
            addressField.setStyle("-fx-border-color: red; -fx-border-width: 2px;");
            valid = false;
        } else if (address.length() < 10) {
            addressErrorLabel.setText("L'adresse doit contenir au moins 10 caractères.");
            addressErrorLabel.setVisible(true);
            addressField.setStyle("-fx-border-color: red; -fx-border-width: 2px;");
            valid = false;
        }

        // Validation téléphone
        // Validation téléphone
        if (phone.isEmpty()) {
            phoneErrorLabel.setText("Ce champ est vide. Veuillez remplir le numéro.");
            phoneErrorLabel.setVisible(true);
            phoneField.setStyle("-fx-border-color: red; -fx-border-width: 2px;");
            valid = false;
        } else if (!phone.matches("\\d+")) {
            phoneErrorLabel.setText("Le numéro ne doit contenir que des chiffres.");
            phoneErrorLabel.setVisible(true);
            phoneField.setStyle("-fx-border-color: red; -fx-border-width: 2px;");
            valid = false;
        } else if (phone.length() != 8) {
            phoneErrorLabel.setText("Le numéro doit contenir exactement 8 chiffres.");
            phoneErrorLabel.setVisible(true);
            phoneField.setStyle("-fx-border-color: red; -fx-border-width: 2px;");
            valid = false;
        }


        if (!valid) {
            return;
        }

        // Création de l'objet Order
        Order order = new Order();
        Cart cart = new Cart(); // appel du constructeur vide
        cart.setId(cartId);     // affectation de l'ID
        order.setCart(cart);    // puis passer l'objet cart

        order.setDeliveryAddress(address);
        order.setPhoneNumber(phone);
        order.setDateOrder(new Date());
        order.setOrderHistory(orderSummary); // Utilise le résumé déjà préparé
        order.setPaid(false);
        order.setPaymentIntentId(0);

        try {
            OrderService orderService = new OrderService();
            orderService.ajouter(order);

            successGif.setVisible(true);
            confirmOrderButton.setDisable(true);

            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Commande confirmée");
            alert.setHeaderText(null);
            alert.setContentText("🎉 Votre commande a été passée avec succès !");
            alert.showAndWait();


// ...

// ✅ Créer les boutons personnalisés
            ButtonType payerMaintenant = new ButtonType("Payer maintenant");
            ButtonType pasMaintenant = new ButtonType("Pas maintenant");

            Alert alert1 = new Alert(Alert.AlertType.CONFIRMATION,
                    "Souhaitez-vous effectuer le paiement en ligne maintenant ?",
                    payerMaintenant, pasMaintenant);

            alert1.setTitle("Paiement");
            alert1.setHeaderText("Confirmation du paiement");

            Optional<ButtonType> result = alert1.showAndWait();

            if (result.isPresent()) {
                if (result.get() == payerMaintenant) {
                    // Rediriger vers payment.fxml
                    FXMLLoader loader = new FXMLLoader(getClass().getResource("/payment.fxml"));
                    Parent root = loader.load();
                    Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
                    stage.setScene(new Scene(root));
                    stage.show();
                } else if (result.get() == pasMaintenant) {
                    // Rediriger vers ListProductFront.fxml
                    FXMLLoader loader = new FXMLLoader(getClass().getResource("/ListProductFront.fxml"));
                    Parent root = loader.load();
                    Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
                    stage.setScene(new Scene(root));
                    stage.show();
                }
            }


        } catch (Exception e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Erreur", "Échec lors de la sauvegarde de la commande : " + e.getMessage());
        }
    }

    // Méthode utilitaire pour afficher des messages
    private void showAlert(Alert.AlertType type, String title, String message) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }



    public void openCart(ActionEvent actionEvent) {
    }
    @FXML
    void goToProductList(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/ListProductFront.fxml"));
            Parent root = loader.load();
            confirmOrderButton.getScene().setRoot(root);  // Utilisation du bouton déjà injecté
        } catch (IOException e) {
            showAlert(Alert.AlertType.ERROR, "Erreur de navigation",
                    "Impossible de charger la liste des produits: " + e.getMessage());
            e.printStackTrace();
        }
    }




    @FXML
    void goToHome(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/Home.fxml"));
            Parent root = loader.load();
            confirmOrderButton.getScene().setRoot(root);  // Même principe ici
        } catch (IOException e) {
            showAlert(Alert.AlertType.ERROR, "Erreur de navigation",
                    "Impossible de charger la page d'accueil: " + e.getMessage());
            e.printStackTrace();
        }
    }


    @FXML
    public void profil(ActionEvent actionEvent) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/Profil.fxml"));
            Parent root = loader.load();

            // Récupérer la scène actuelle depuis un élément (ex: le bouton confirmOrderButton)
            confirmOrderButton.getScene().setRoot(root);
        } catch (IOException e) {
            showAlert(Alert.AlertType.ERROR, "Erreur de navigation",
                    "Impossible de charger la page de profil: " + e.getMessage());
            e.printStackTrace();
        }
    }

}
