package tn.artflow.controllers;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import tn.artflow.entities.Article;
import tn.artflow.services.ArticleService;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;

public class AfficherArticle implements Initializable {

    @FXML
    private GridPane articleGrid;

    private final ArticleService articleService = new ArticleService();

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        try {
            // 🔹 En-tête du tableau
            Label headerId = new Label("ID");
            headerId.setStyle("-fx-font-weight: bold; -fx-text-fill: #333;");
            articleGrid.add(headerId, 0, 0);
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

                // Bouton modifier
                Button btnModifier = new Button("Update");
                btnModifier.setStyle("-fx-background-color: #4CAF50; -fx-text-fill: white; -fx-background-radius: 5;");


                btnModifier.setOnAction(e -> {
                    try {
                        FXMLLoader loader = new FXMLLoader(getClass().getResource("/ModifierArticle.fxml"));
                        Parent formModifier = loader.load();

                        ModifierArticle controller = loader.getController();
                        controller.setArticle(article);

                        // Remplacer la scène courante (pas ouvrir nouvelle fenêtre)
                        Stage currentStage = (Stage) articleGrid.getScene().getWindow();
                        currentStage.setScene(new Scene(formModifier));
                        currentStage.setTitle("Modifier un article");

                    } catch (IOException ex) {
                        ex.printStackTrace();
                    }
                });

                // Bouton supprimer
                Button btnSupprimer = new Button("Delete");
                btnSupprimer.setStyle("-fx-background-color: #F44336; -fx-text-fill: white; -fx-background-radius: 5;");

                btnSupprimer.setOnAction(e -> {
                    try {
                        FXMLLoader loader = new FXMLLoader(getClass().getResource("/DeleteArticle.fxml"));
                        Parent root = loader.load();

                        DeleteArticle controller = loader.getController();
                        controller.setArticle(article);

                        Stage stage = new Stage();
                        stage.setTitle("Confirmation de suppression");
                        stage.setScene(new Scene(root));
                        stage.show();
                    } catch (IOException ex) {
                        ex.printStackTrace();
                    }
                });

                // Ajoute les deux boutons
                HBox actions = new HBox(10, btnModifier, btnSupprimer);
                articleGrid.add(actions, 5, rowIndex);

                rowIndex++;
            }
        } catch (Exception e) {
            e.printStackTrace(); // ✅ indispensable pour capturer SQLException ou autre
        }
    }
    @FXML
    private VBox formContainer;

    @FXML
    void afficherFormulaireAjout() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/AjouterArticle.fxml"));
            Parent root = loader.load();

            // Accès à la scène actuelle
            Stage currentStage = (Stage) articleGrid.getScene().getWindow();

            // Remplacer la scène par celle du formulaire
            currentStage.setScene(new Scene(root));
            currentStage.setTitle("Ajouter un article");

            // Tu peux passer le stage au contrôleur suivant si besoin
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


}