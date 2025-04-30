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
    private Label labelObjetError;
    @FXML
    private Label labelMessageError;

    ReclamationService rs = new ReclamationService();
    @FXML
    private AnchorPane productContainer;

   /* @FXML
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

        if (!isValid) {
            return;
        }

        try {

            User user = tn.artflow.utils.UserSession.getInstance().getUser();
            rs.ajouter(new Reclamation(
                    0, // id
                    user.getId(), // user_id — à changer quand user est prêt
                    objet,
                    message,
                    "Open", // default status
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
            labelObjetError.setVisible(false);
            labelMessageError.setVisible(false);

        } catch (SQLException e) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Erreur");
            alert.setHeaderText("Erreur lors de l'ajout");
            alert.setContentText(e.getMessage());
            alert.showAndWait();
        }
    }*/

    @FXML
    void ajouter(ActionEvent event) {
        labelObjetError.setVisible(false);
        labelMessageError.setVisible(false);

        boolean isValid = true;
        String objet = tfObjet.getText().trim();
        String message = taMessage.getText().trim();

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

        if (!isValid) {
            return;
        }

        try {
            User user = tn.artflow.utils.UserSession.getInstance().getUser();
            rs.ajouter(new Reclamation(
                    0,
                    user.getId(),
                    objet,
                    message,
                    "Open",
                    false,
                    LocalDateTime.now()
            ));

            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Succès");
            alert.setHeaderText(null);
            alert.setContentText("Réclamation ajoutée !");
            alert.showAndWait();

            tfObjet.clear();
            taMessage.clear();
            labelObjetError.setVisible(false);
            labelMessageError.setVisible(false);

            // Redirection après ajout
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/ClientConversation.fxml"));
            Parent conversationPage = loader.load();
            productContainer.getScene().setRoot(conversationPage);

        } catch (SQLException | IOException e) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Erreur");
            alert.setHeaderText("Erreur lors de l'ajout");
            alert.setContentText(e.getMessage());
            alert.showAndWait();
            e.printStackTrace();
        }
    }


    public void annuler(ActionEvent actionEvent) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/ListProductFront.fxml"));
            Parent profilPage = loader.load();

            // Remplacer le contenu de la scène actuelle
            productContainer.getScene().setRoot(profilPage);

        } catch (IOException e) {

            e.printStackTrace();
        }
    }
}
    

