package tn.esprit.controllers;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import tn.esprit.entities.Reclamation;
import tn.esprit.services.ReclamationService;

import java.sql.SQLException;
import java.time.LocalDateTime;

public class    AjouterReclamation {

    @FXML
    private TextField tfObjet;
    @FXML
    private TextArea taMessage;

    ReclamationService rs = new ReclamationService();

    @FXML
    void ajouter(ActionEvent event) {
        try {
            String objet = tfObjet.getText();
            String message = taMessage.getText();

            rs.ajouter(new Reclamation(
                    0, // id
                    1, // user_id — à changer quand user est prêt
                    objet,
                    message,
                    "en attente", // default status
                    false, // is_marked
                    LocalDateTime.now()
            ));

            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Succès");
            alert.setHeaderText(null);
            alert.setContentText("Réclamation ajoutée !");
            alert.showAndWait();

            tfObjet.clear();
            taMessage.clear();

        } catch (SQLException e) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Erreur");
            alert.setHeaderText("Erreur lors de l'ajout");
            alert.setContentText(e.getMessage());
            alert.showAndWait();
        }
    }
}
