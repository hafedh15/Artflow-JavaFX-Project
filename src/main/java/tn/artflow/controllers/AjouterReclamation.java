package tn.artflow.controllers;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.*;
import javafx.scene.layout.AnchorPane;
import tn.artflow.entities.Reclamation;
import tn.artflow.entities.User;
import tn.artflow.services.ReclamationService;

import java.io.IOException;
import java.sql.SQLException;
import java.time.LocalDateTime;

public class AjouterReclamation {

    @FXML
    private TextField tfObjet;
    @FXML
    private TextArea taMessage;
    @FXML
    private Button btnAjouter;
    @FXML
    private Label labelObjetError;
    @FXML
    private Label labelMessageError;
    @FXML
    private Label lblMessageCount; // New label to display message count

    ReclamationService rs = new ReclamationService();

    User user = tn.artflow.utils.UserSession.getInstance().getUser();
    private final int userId = user.getId(); // TODO: Replace with actual connected user ID

    @FXML
    public void initialize() {
        try {
            checkReclamationLimit();
            updateMessageCount(); // Update message count on initialization
        } catch (SQLException e) {
            showError("Erreur lors de la vérification du quota", e.getMessage());
        }
    }

    @FXML
    void ajouter(ActionEvent event) {
        // Reset error messages
        labelObjetError.setVisible(false);
        labelMessageError.setVisible(false);

        boolean isValid = true;
        String objet = tfObjet.getText().trim();
        String message = taMessage.getText().trim();

        // Validation
        if (objet.isEmpty()) {
            labelObjetError.setText("Objet requis");
            labelObjetError.setVisible(true);
            isValid = false;
        }

        if (message.isEmpty()) {
            labelMessageError.setText("Message requis");
            labelMessageError.setVisible(true);
            isValid = false;
        }

        if (!isValid) return;

        try {
            // Count check
            int countToday = rs.countReclamationsToday(userId);
            if (countToday >= 3) {
                showWarning("Limite atteinte", "Vous avez déjà envoyé 3 réclamations aujourd'hui.");
                btnAjouter.setDisable(true);
                return;
            }

            // Add reclamation
            rs.ajouter(new Reclamation(
                    0,
                    userId,
                    objet,
                    message,
                    "en attente",
                    false,
                    LocalDateTime.now()
            ));

            showInfo("Réclamation ajoutée !");
            tfObjet.clear();
            taMessage.clear();

            // Re-check limit
            checkReclamationLimit();
            updateMessageCount(); // Update message count after adding a new one

        } catch (SQLException e) {
            showError("Erreur lors de l'ajout", e.getMessage());
        }
    }

    private void checkReclamationLimit() throws SQLException {
        int count = rs.countReclamationsToday(userId);
        btnAjouter.setDisable(count >= 3);
    }

    // Method to update the message count label
    private void updateMessageCount() {
        try {
            int count = rs.countReclamationsToday(userId);
            lblMessageCount.setText(count + "/3 Messages envoyés, you cant send more than 3 messages");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void showInfo(String msg) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Succès");
        alert.setHeaderText(null);
        alert.setContentText(msg);
        alert.showAndWait();
    }

    private void showWarning(String title, String msg) {
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(msg);
        alert.showAndWait();
    }

    private void showError(String title, String msg) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(title);
        alert.setContentText(msg);
        alert.showAndWait();
    }
}

