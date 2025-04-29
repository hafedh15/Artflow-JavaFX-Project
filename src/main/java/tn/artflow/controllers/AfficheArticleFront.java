package tn.artflow.controllers;

import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.scene.Parent;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.util.Duration;
import tn.artflow.entities.Article;
import tn.artflow.services.ArticleService;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.*;

public class AfficheArticleFront implements Initializable {

    @FXML private BorderPane rootPane;
    @FXML private FlowPane flowPane;
    @FXML private HBox categoryBar;
    @FXML private VBox recentPostsBox;
    @FXML private TextField searchField;

    @FXML private StackPane robotOverlay;
    @FXML private HBox carouselContent;
    @FXML private ScrollPane carouselScrollPane;

    @FXML private ImageView robotOverlayImage;

    @FXML private HBox robotBox;
    @FXML private ImageView robotImage;
    @FXML private VBox robotMessages;

    private final ArticleService articleService = new ArticleService();
    private List<Article> allArticles;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        try {
            allArticles = articleService.recuperer();
            afficherArticles(allArticles);
            initialiserCategories();
            afficherRecentPosts(allArticles);
            showRobotTopArticles();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void afficherArticles(List<Article> articles) {
        flowPane.getChildren().clear();
        for (Article article : articles) {
            VBox card = createCard(article);
            flowPane.getChildren().add(card);
        }
    }

    private VBox createCard(Article article) {
        VBox vbox = new VBox(10);
        vbox.setPadding(new Insets(15));
        vbox.setStyle("-fx-background-color: linear-gradient(to bottom, #f7f7f7, #ffffff);" +
                "-fx-background-radius: 15px; -fx-border-radius: 15px;" +
                "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.08), 8, 0, 0, 2);" +
                "-fx-cursor: hand;");
        vbox.setPrefWidth(240);
        vbox.setPrefHeight(280);

        Label categorieLabel = new Label(article.getCategorie().toUpperCase());
        categorieLabel.setStyle("-fx-text-fill: #8D7B6A; -fx-font-size: 12px; -fx-font-weight: bold;");

        ImageView imageView = new ImageView();
        imageView.setFitWidth(200);
        imageView.setFitHeight(120);
        imageView.setPreserveRatio(true);

        File imgFile = new File("C:/xampp/htdocs/images/" + article.getImage());
        if (imgFile.exists()) {
            imageView.setImage(new Image(imgFile.toURI().toString()));
        }

        Label titre = new Label(article.getTitre());
        titre.setWrapText(true);
        titre.setStyle("-fx-font-weight: bold; -fx-font-size: 16px; -fx-text-fill: #2C2C2C;");

        Button lirePlusBtn = new Button("Lire pour voir");
        lirePlusBtn.setStyle("-fx-background-color: #A18E78; -fx-text-fill: white; -fx-background-radius: 8px; -fx-font-size: 12px; -fx-font-weight: bold;");
        lirePlusBtn.setOnAction(e -> openDetails(article));

        VBox.setMargin(lirePlusBtn, new Insets(10, 0, 0, 0));
        vbox.getChildren().addAll(categorieLabel, imageView, titre, lirePlusBtn);
        return vbox;
    }

    private void openDetails(Article article) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/AjouterComment.fxml"));
            Parent commentView = loader.load();

            AjouterComment controller = loader.getController();
            controller.setArticle(article);

            rootPane.setCenter(commentView);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void rechercherArticles() {
        String searchText = searchField.getText().trim().toLowerCase();
        if (searchText.isEmpty()) {
            afficherArticles(allArticles);
            return;
        }

        List<Article> filtered = new ArrayList<>();
        for (Article article : allArticles) {
            if (article.getTitre().toLowerCase().contains(searchText) ||
                    article.getCategorie().toLowerCase().contains(searchText)) {
                filtered.add(article);
            }
        }
        afficherArticles(filtered);
    }

    private void initialiserCategories() {
        Set<String> categories = new HashSet<>();
        for (Article article : allArticles) {
            categories.add(article.getCategorie().toUpperCase());
        }

        Button allButton = new Button("Tous");
        allButton.setStyle("-fx-background-color: #e0d6c5; -fx-text-fill: #5c4f3d; -fx-font-weight: bold; -fx-background-radius: 20px;");
        allButton.setOnAction(e -> afficherArticles(allArticles));
        categoryBar.getChildren().add(allButton);

        for (String categorie : categories) {
            Button button = new Button(categorie);
            button.setStyle("-fx-background-color: #e0d6c5; -fx-text-fill: #5c4f3d; -fx-font-weight: bold; -fx-background-radius: 20px;");
            button.setOnAction(e -> filtrerParCategorie(categorie));
            categoryBar.getChildren().add(button);
        }
    }

    private void filtrerParCategorie(String categorie) {
        List<Article> filtered = new ArrayList<>();
        for (Article article : allArticles) {
            if (article.getCategorie().equalsIgnoreCase(categorie)) {
                filtered.add(article);
            }
        }
        afficherArticles(filtered);
    }

    private void afficherRecentPosts(List<Article> articles) {
        recentPostsBox.getChildren().clear();
        int count = 0;
        for (Article article : articles) {
            if (count >= 3) break;

            HBox hbox = new HBox(10);
            hbox.setStyle("-fx-cursor: hand;");

            ImageView imageView = new ImageView();
            imageView.setFitWidth(60);
            imageView.setFitHeight(60);
            imageView.setPreserveRatio(true);

            File imgFile = new File("C:/xampp/htdocs/images/" + article.getImage());
            if (imgFile.exists()) {
                imageView.setImage(new Image(imgFile.toURI().toString()));
            }

            VBox infoBox = new VBox(5);
            Label categorie = new Label(article.getCategorie());
            categorie.setStyle("-fx-text-fill: #8D7B6A; -fx-font-size: 10px;");
            Label titre = new Label(article.getTitre());
            titre.setStyle("-fx-font-weight: bold; -fx-font-size: 12px;");

            infoBox.getChildren().addAll(categorie, titre);
            hbox.getChildren().addAll(imageView, infoBox);
            hbox.setOnMouseClicked(e -> openDetails(article));

            recentPostsBox.getChildren().add(hbox);
            count++;
        }
    }

    private void showRobotTopArticles() {
        robotOverlay.setVisible(true);
        robotOverlay.setManaged(true);

        File robotFile = new File("C:/xampp/htdocs/images/robot.jpg");
        if (robotFile.exists()) {
            robotOverlayImage.setImage(new Image(robotFile.toURI().toString()));
        }

        List<Article> topArticles = articleService.getTop3MostViewed();
        carouselContent.getChildren().clear();

        for (Article article : topArticles) {
            VBox card = new VBox(8);
            card.setStyle("""
            -fx-background-color: white;
            -fx-background-radius: 10;
            -fx-border-radius: 10;
            -fx-padding: 10;
            -fx-effect: dropshadow(gaussian, rgba(0,0,0,0.12), 8, 0, 0, 2);
            -fx-cursor: hand;
        """);
            card.setPrefWidth(180);

            // Image
            ImageView imageView = new ImageView();
            File imgFile = new File("C:/xampp/htdocs/images/" + article.getImage());
            if (imgFile.exists()) {
                imageView.setImage(new Image(imgFile.toURI().toString()));
            }
            imageView.setFitWidth(160);
            imageView.setFitHeight(100);
            imageView.setPreserveRatio(true);
            imageView.setStyle("-fx-border-radius: 8;");

            // Titre
            Label title = new Label(article.getTitre());
            title.setStyle("-fx-font-size: 13px; -fx-font-weight: bold; -fx-text-fill: #333;");
            title.setWrapText(true);
            title.setMaxHeight(40);

            // Vues
            Label views = new Label(article.getViews() + " vues");
            views.setStyle("-fx-text-fill: #777; -fx-font-size: 11px;");

            // Bouton
            Button lire = new Button("Lire");
            lire.setStyle("-fx-background-color: #A18E78; -fx-text-fill: white; -fx-font-size: 11px; -fx-font-weight: bold; -fx-background-radius: 6px;");
            lire.setOnAction(e -> openDetails(article));

            card.getChildren().addAll(imageView, title, views, lire);
            carouselContent.getChildren().add(card);
        }

        // Défilement automatique
        Timeline timeline = new Timeline(
                new KeyFrame(Duration.millis(100), e -> {
                    double current = carouselScrollPane.getHvalue();
                    double step = 0.02;
                    if (current + step >= 1.0) {
                        carouselScrollPane.setHvalue(0);
                    } else {
                        carouselScrollPane.setHvalue(current + step);
                    }
                })
        );
        timeline.setCycleCount(Animation.INDEFINITE);
        timeline.play();
    }


    @FXML
    private void fermerOverlay() {
        robotOverlay.setVisible(false);
        robotOverlay.setManaged(false);
    }
}
