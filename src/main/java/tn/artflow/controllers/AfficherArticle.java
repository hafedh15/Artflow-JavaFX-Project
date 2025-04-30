package tn.artflow.controllers;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import tn.artflow.entities.Article;
import tn.artflow.services.ArticleService;

import java.io.IOException;
import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;

public class AfficherArticle implements Initializable {

    @FXML
    private GridPane articleGrid;

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

    @FXML
    private VBox formContainer;

    private final ArticleService articleService = new ArticleService();

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        chargerArticles();
    }

    private void chargerArticles() {
        try {
            articleGrid.getChildren().clear();

            articleGrid.add(new Label("ID"), 0, 0);
            articleGrid.add(new Label("Titre"), 1, 0);
            articleGrid.add(new Label("Catégorie"), 2, 0);
            articleGrid.add(new Label("Auteur"), 3, 0);
            articleGrid.add(new Label("Date"), 4, 0);
            articleGrid.add(new Label("Actions"), 5, 0);

            List<Article> articles = articleService.recuperer();
            int rowIndex = 1;

            for (Article article : articles) {
                articleGrid.add(new Label(String.valueOf(article.getId())), 0, rowIndex);
                articleGrid.add(new Label(article.getTitre()), 1, rowIndex);
                articleGrid.add(new Label(article.getCategorie()), 2, rowIndex);
                articleGrid.add(new Label(article.getNomAuteur()), 3, rowIndex);
                articleGrid.add(new Label(article.getDatepub()), 4, rowIndex);

                Button btnModifier = new Button("Update");
                btnModifier.setStyle("-fx-background-color: #4CAF50; -fx-text-fill: white; -fx-background-radius: 5;");
                btnModifier.setOnAction(e -> ouvrirFormulaireModification(article));

                Button btnSupprimer = new Button("Delete");
                btnSupprimer.setStyle("-fx-background-color: #F44336; -fx-text-fill: white; -fx-background-radius: 5;");
                btnSupprimer.setOnAction(e -> supprimerArticle(article));

                HBox actions = new HBox(10, btnModifier, btnSupprimer);
                articleGrid.add(actions, 5, rowIndex);

                rowIndex++;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @FXML
    void afficherFormulaireAjout() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/AjouterArticle.fxml"));
            Parent root = loader.load();
            Stage currentStage = (Stage) articleGrid.getScene().getWindow();
            currentStage.setScene(new Scene(root));
            currentStage.setTitle("Ajouter un article");

            AjouterArticle controller = loader.getController();
            controller.setRetourCallback(() -> {
                try {
                    FXMLLoader retourLoader = new FXMLLoader(getClass().getResource("/AfficherArticle.fxml"));
                    Parent retourRoot = retourLoader.load();
                    currentStage.setScene(new Scene(retourRoot));
                    currentStage.setTitle("Liste des articles");
                } catch (IOException e) {
                    e.printStackTrace();
                }
            });

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void ouvrirFormulaireModification(Article article) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/ModifierArticle.fxml"));
            Parent formModifier = loader.load();
            ModifierArticle controller = loader.getController();
            controller.setArticle(article);

            Stage currentStage = (Stage) articleGrid.getScene().getWindow();
            currentStage.setScene(new Scene(formModifier));
            currentStage.setTitle("Modifier un article");

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void supprimerArticle(Article article) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Confirmation de suppression");
        alert.setHeaderText(null);
        alert.setContentText("Voulez-vous vraiment supprimer l'article : \"" + article.getTitre() + "\" ?");

        alert.showAndWait().ifPresent(response -> {
            if (response == ButtonType.OK) {
                try {
                    articleService.supprimer(article.getId());
                    chargerArticles(); // Recharge les articles directement
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
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
