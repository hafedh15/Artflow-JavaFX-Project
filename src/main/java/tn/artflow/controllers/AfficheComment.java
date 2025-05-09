package tn.artflow.controllers;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;
import tn.artflow.entities.Comment;
import tn.artflow.services.CommentService;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

public class AfficheComment implements Initializable {

    @FXML private GridPane commentGrid;
    @FXML private Button notifButton;
    @FXML private Button clearNotifButton;
    @FXML private TextField searchField;
    @FXML private Button searchButton;

    @FXML
    private Button GoToArticle;
    @FXML
    private Button GoToUser;
    @FXML
    private Button GoToAtelier;

    @FXML
    private Button GoToReservation;

    @FXML
    private Button GoToComment;

    @FXML
    private Button GoToReclamation;

    @FXML
    private Button GoToReponse;
    @FXML
    private Button orderButton;

    private final CommentService commentService = new CommentService();
    private List<Comment> allComments = new ArrayList<>();

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        try {
            allComments = commentService.recuperer();
            afficherCommentaires(allComments);
        } catch (Exception e) {
            afficherErreur("Erreur lors du chargement des commentaires.");
            e.printStackTrace();
        }
    }

    private void afficherCommentaires(List<Comment> comments) {
        commentGrid.getChildren().clear();

        commentGrid.add(new Label("ID"), 0, 0);
        commentGrid.add(new Label("Contenu"), 1, 0);
        commentGrid.add(new Label("Auteur"), 2, 0);
        commentGrid.add(new Label("Date"), 3, 0);
        commentGrid.add(new Label("Actions"), 4, 0);

        int rowIndex = 1;

        for (Comment comment : comments) {
            commentGrid.add(new Label(String.valueOf(comment.getId())), 0, rowIndex);
            commentGrid.add(new Label(comment.getContenu_Comment()), 1, rowIndex);
            commentGrid.add(new Label(comment.getUser().getName()), 2, rowIndex);
            commentGrid.add(new Label(comment.getDatecom()), 3, rowIndex);

            Button btnSupprimer = new Button("Supprimer");
            btnSupprimer.setStyle("-fx-background-color: #e53935; -fx-text-fill: white;");
            btnSupprimer.setOnAction(e -> supprimerCommentaire(comment.getId()));

            commentGrid.add(new HBox(10, btnSupprimer), 4, rowIndex);
            rowIndex++;
        }
    }

    private void supprimerCommentaire(int commentId) {
        try {
            commentService.supprimer(commentId);
            allComments = commentService.recuperer();
            afficherCommentaires(allComments);
        } catch (Exception e) {
            afficherErreur("Erreur lors de la suppression du commentaire.");
            e.printStackTrace();
        }
    }

    @FXML
    private void afficherNotifications() {
        try {
            List<String> notifications = NotificationCenter.getNotifications();

            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Notifications des Commentaires");
            alert.setHeaderText("🔔 Notifications des mots censurés");

            StringBuilder content = new StringBuilder();
            if (notifications.isEmpty()) {
                content.append("Aucune mauvaise parole détectée ✅");
            } else {
                for (String notif : notifications) {
                    content.append("- ").append(notif).append("\n");
                }
            }

            alert.setContentText(content.toString());
            alert.showAndWait();
        } catch (Exception e) {
            afficherErreur("Erreur lors de l'affichage des notifications.");
            e.printStackTrace();
        }
    }

    @FXML
    private void viderNotifications() {
        try {
            NotificationCenter.clearNotifications();
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Nettoyage des notifications");
            alert.setHeaderText(null);
            alert.setContentText("✅ Toutes les notifications ont été supprimées.");
            alert.showAndWait();
        } catch (Exception e) {
            afficherErreur("Erreur lors du nettoyage des notifications.");
            e.printStackTrace();
        }
    }

    @FXML
    private void rechercherParNom() {
        try {
            String searchTerm = searchField.getText().toLowerCase().trim();

            if (searchTerm.isEmpty()) {
                afficherCommentaires(allComments);
            } else {
                List<Comment> filtered = new ArrayList<>();
                for (Comment comment : allComments) {
                    if (comment.getUser().getName().toLowerCase().contains(searchTerm)) {
                        filtered.add(comment);
                    }
                }
                afficherCommentaires(filtered);
            }
        } catch (Exception e) {
            afficherErreur("Erreur lors de la recherche.");
            e.printStackTrace();
        }
    }

    private void afficherErreur(String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Erreur");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    @FXML
    void goToArticle(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/AfficherArticle.fxml"));
            Parent root = loader.load();
            GoToArticle.getScene().setRoot(root);  // Même principe ici
        } catch (IOException e) {

            e.printStackTrace();
        }
    }

    @FXML
    void goToReservation(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/dashReservation.fxml"));
            Parent root = loader.load();
            GoToArticle.getScene().setRoot(root);  // Même principe ici
        } catch (IOException e) {

            e.printStackTrace();
        }
    }
    @FXML
    void goToAtelier(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/dashWorkshop.fxml"));
            Parent root = loader.load();
            GoToArticle.getScene().setRoot(root);  // Même principe ici
        } catch (IOException e) {

            e.printStackTrace();
        }
    }
    @FXML
    void goToComment(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/AfficheComment.fxml"));
            Parent root = loader.load();
            GoToArticle.getScene().setRoot(root);  // Même principe ici
        } catch (IOException e) {

            e.printStackTrace();
        }
    }
    @FXML
    void goToReclamation(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/AfficherReclamation.fxml"));
            Parent root = loader.load();
            GoToArticle.getScene().setRoot(root);  // Même principe ici
        } catch (IOException e) {

            e.printStackTrace();
        }
    }
    @FXML
    void goToReponse(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/conversation.fxml"));
            Parent root = loader.load();
            GoToArticle.getScene().setRoot(root);  // Même principe ici
        } catch (IOException e) {

            e.printStackTrace();
        }
    }

    @FXML
    void goToProduit(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/Dashboard.fxml"));
            Parent root = loader.load();
            GoToArticle.getScene().setRoot(root);  // Même principe ici
        } catch (IOException e) {

            e.printStackTrace();
        }
    }

    @FXML
    void goToUser(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/AfficherUser.fxml"));
            Parent root = loader.load();
            GoToArticle.getScene().setRoot(root);  // Même principe ici
        } catch (IOException e) {

            e.printStackTrace();
        }
    }

    public void handleOrderButtonAction(ActionEvent actionEvent) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/OrdersDashboard.fxml"));
            Parent root = loader.load();

            // Obtenir la scène actuelle et la remplacer par la scène d'ajout
            Stage stage = (Stage) ((Node) actionEvent.getSource()).getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.setTitle("Ajouter un produit");
            stage.show();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
