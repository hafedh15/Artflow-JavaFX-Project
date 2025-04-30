package tn.artflow.controllers;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.event.ActionEvent;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.image.ImageView;
import tn.artflow.entities.*;
import tn.artflow.services.OrderService;

import java.io.IOException;
import java.net.URL;
import java.sql.SQLException;
import java.util.ResourceBundle;

public class EditOrderController {

    @FXML
    private TextField addressField;

    @FXML
    private TextField phoneField;

    @FXML
    private Button saveButton;

    @FXML
    private Label successLabel;
    @FXML
    private Label addressErrorLabel;

    @FXML
    private Label phoneErrorLabel;


    @FXML
    private ImageView successGif;

    private Order currentOrder; // L'objet commande à modifier

    private final OrderService orderService = new OrderService(); // Ton service JDBC

    /**
     * Méthode à appeler pour précharger les infos de la commande dans les champs
     */
    public void setOrder(Order order) {
        this.currentOrder = order;
        addressField.setText(order.getDeliveryAddress());
        phoneField.setText(order.getPhoneNumber());
    }




    @FXML
    private void saveChanges(ActionEvent event) {
        // Réinitialiser les messages d'erreur
        addressErrorLabel.setVisible(false);
        phoneErrorLabel.setVisible(false);

        String newAddress = addressField.getText().trim();
        String newPhone = phoneField.getText().trim();

        boolean valid = true;

       // Validation adresse
        if (newAddress.isEmpty()) {
            addressErrorLabel.setText("Ce champ est vide. Veuillez remplir l'adresse.");
            addressErrorLabel.setVisible(true);
            addressField.setStyle("-fx-border-color: red; -fx-border-width: 2px;");
            valid = false;
        } else if (newAddress.length() < 10) {
            addressErrorLabel.setText("L'adresse doit contenir au moins 10 caractères.");
            addressErrorLabel.setVisible(true);
            addressField.setStyle("-fx-border-color: red; -fx-border-width: 2px;");
            valid = false;
        }

// Validation téléphone
        if (newPhone.isEmpty()) {
            phoneErrorLabel.setText("Ce champ est vide. Veuillez remplir le numéro.");
            phoneErrorLabel.setVisible(true);
            phoneField.setStyle("-fx-border-color: red; -fx-border-width: 2px;");
            valid = false;
        } else if (!newPhone.matches("\\d+")) {
            phoneErrorLabel.setText("Le numéro ne doit contenir que des chiffres.");
            phoneErrorLabel.setVisible(true);
            phoneField.setStyle("-fx-border-color: red; -fx-border-width: 2px;");
            valid = false;
        } else if (newPhone.length() != 8) {
            phoneErrorLabel.setText("Le numéro doit contenir exactement 8 chiffres.");
            phoneErrorLabel.setVisible(true);
            phoneField.setStyle("-fx-border-color: red; -fx-border-width: 2px;");
            valid = false;
        }

        if (!valid) return;

        // Si tout est bon : on met à jour
        currentOrder.setDeliveryAddress(newAddress);
        currentOrder.setPhoneNumber(newPhone);

        try {
            boolean updated = orderService.mettreAJourContactLivraison(currentOrder);
            if (updated) {
                Alert successAlert = new Alert(Alert.AlertType.INFORMATION);
                successAlert.setTitle("Mise à jour réussie");
                successAlert.setHeaderText(null);
                successAlert.setContentText("✅ Les informations de livraison ont été modifiées avec succès !");
                successAlert.showAndWait();

                successGif.setVisible(true);
                saveButton.setDisable(true);
            } else {
                addressErrorLabel.setText("Erreur : la mise à jour a échoué.");
                addressErrorLabel.setVisible(true);
            }
        } catch (SQLException e) {
            addressErrorLabel.setText("Erreur SQL lors de la mise à jour.");
            addressErrorLabel.setVisible(true);
            e.printStackTrace();
        }
    }



    @FXML
    private void cancelEdit(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/profilp.fxml"));
            Parent root = loader.load();
            addressField.getScene().setRoot(root);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // Méthodes de navigation (à compléter selon ta logique)
    @FXML
    private void goToHome(ActionEvent event) {
        // Redirection vers page accueil
    }

    @FXML
    private void goToProductList(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/ListProductFront.fxml"));
            Parent root = loader.load();
            addressField.getScene().setRoot(root);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void openCart(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/cart.fxml"));
            Parent root = loader.load();
            addressField.getScene().setRoot(root);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void profil(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/profilp.fxml"));
            Parent root = loader.load();
            addressField.getScene().setRoot(root);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void showAlert(String title, String message) {
        Alert alert = new Alert(AlertType.WARNING);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    public void goToAteliers(ActionEvent actionEvent) {
    }

    public void goToReclamations(ActionEvent actionEvent) {
    }

    public void goToAbout(ActionEvent actionEvent) {
    }
}
