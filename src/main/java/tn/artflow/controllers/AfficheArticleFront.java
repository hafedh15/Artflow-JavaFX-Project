package tn.artflow.controllers;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
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
    @FXML private Label pageLabel;
    @FXML private Button prevButton;
    @FXML private Button nextButton;
    @FXML private StackPane carouselContainer;

    private final ArticleService articleService = new ArticleService();
    private List<Article> allArticles;
    private List<Article> filteredArticles;
    private List<Article> topViewedArticles = new ArrayList<>();
    private static final int ARTICLES_PAR_PAGE = 6;
    private int pageActuelle = 0;
    private int currentTopIndex = 0;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        try {
            allArticles = articleService.recuperer();
            filteredArticles = new ArrayList<>(allArticles);
            initialiserCategories();
            afficherTopArticlesVus();
            afficherPage(pageActuelle);
            afficherRecentPosts(allArticles);
            mettreAJourPagination();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void afficherPage(int pageIndex) {
        int debut = pageIndex * ARTICLES_PAR_PAGE;
        int fin = Math.min(debut + ARTICLES_PAR_PAGE, filteredArticles.size());
        List<Article> sousListe = filteredArticles.subList(debut, fin);
        afficherArticles(sousListe);
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

        File imgFile = new File("C:/xampp/htdocs/" + article.getImage());
        System.out.println(imgFile);

        if (imgFile.exists()) {
            imageView.setImage(new Image(imgFile.toURI().toString()));

        }

        Label titre = new Label(article.getTitre());
        titre.setStyle("-fx-font-weight: bold; -fx-font-size: 14;");

        Label views = new Label("Views: " + article.getViews());

        Button lirePlusBtn = new Button("Lire plus");
        lirePlusBtn.setOnAction(e -> openDetails(article));

        vbox.getChildren().addAll(imageView, titre, views, lirePlusBtn);
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
            filteredArticles = new ArrayList<>(allArticles);
        } else {
            filteredArticles = new ArrayList<>();
            for (Article article : allArticles) {
                if (article.getTitre().toLowerCase().contains(searchText) ||
                        article.getCategorie().toLowerCase().contains(searchText)) {
                    filteredArticles.add(article);
                }
            }
        }

        pageActuelle = 0;
        afficherPage(pageActuelle);
        mettreAJourPagination();
    }

    private void initialiserCategories() {
        Set<String> categories = new HashSet<>();
        for (Article article : allArticles) {
            categories.add(article.getCategorie().toUpperCase());
        }

        Button allButton = new Button("Tous");
        allButton.setStyle("-fx-background-color: #e0d6c5; -fx-text-fill: #5c4f3d; -fx-font-weight: bold; -fx-background-radius: 20px;");
        allButton.setOnAction(e -> {
            filteredArticles = new ArrayList<>(allArticles);
            pageActuelle = 0;
            afficherPage(pageActuelle);
            mettreAJourPagination();
        });
        categoryBar.getChildren().add(allButton);

        for (String categorie : categories) {
            Button button = new Button(categorie);
            button.setStyle("-fx-background-color: #e0d6c5; -fx-text-fill: #5c4f3d; -fx-font-weight: bold; -fx-background-radius: 20px;");
            button.setOnAction(e -> {
                filteredArticles = new ArrayList<>();
                for (Article article : allArticles) {
                    if (article.getCategorie().equalsIgnoreCase(categorie)) {
                        filteredArticles.add(article);
                    }
                }
                pageActuelle = 0;
                afficherPage(pageActuelle);
                mettreAJourPagination();
            });
            categoryBar.getChildren().add(button);
        }
    }

    private void mettreAJourPagination() {
        int totalPages = (int) Math.ceil((double) filteredArticles.size() / ARTICLES_PAR_PAGE);
        pageLabel.setText("Page " + (pageActuelle + 1));
        prevButton.setDisable(pageActuelle == 0);
        nextButton.setDisable(pageActuelle >= totalPages - 1);
    }

    @FXML
    private void pagePrecedente() {
        if (pageActuelle > 0) {
            pageActuelle--;
            afficherPage(pageActuelle);
            mettreAJourPagination();
        }
    }

    @FXML
    private void pageSuivante() {
        int totalPages = (int) Math.ceil((double) filteredArticles.size() / ARTICLES_PAR_PAGE);
        if (pageActuelle < totalPages - 1) {
            pageActuelle++;
            afficherPage(pageActuelle);
            mettreAJourPagination();
        }
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

            File imgFile = new File("C:/xampp/htdocs/" + article.getImage());
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

    private void afficherTopArticlesVus() {
        topViewedArticles = new ArrayList<>(allArticles);
        topViewedArticles.sort((a, b) -> Integer.compare(b.getViews(), a.getViews()));

        int limit = Math.min(3, topViewedArticles.size());
        topViewedArticles = topViewedArticles.subList(0, limit);

        if (topViewedArticles.isEmpty()) return;

        afficherArticleDansCarousel(topViewedArticles.get(0));

        Timeline scroll = new Timeline(new KeyFrame(Duration.seconds(5), e -> {
            currentTopIndex = (currentTopIndex + 1) % topViewedArticles.size();
            afficherArticleDansCarousel(topViewedArticles.get(currentTopIndex));
        }));
        scroll.setCycleCount(Timeline.INDEFINITE);
        scroll.play();
    }

    private void afficherArticleDansCarousel(Article article) {
        carouselContainer.getChildren().clear();

        VBox card = new VBox(8);
        card.setPadding(new Insets(15));
        card.setMaxWidth(720);
        card.setAlignment(Pos.CENTER);
        card.setStyle(
                "-fx-background-color: linear-gradient(to bottom right, #f3e8df, #e9d9c8);" +
                        "-fx-background-radius: 15px; -fx-border-radius: 15px;" +
                        "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.08), 8, 0, 0, 2);" +
                        "-fx-cursor: hand;"
        );


        Label titreCategorie = new Label(article.getCategorie().toUpperCase());
        titreCategorie.setStyle("-fx-font-size: 13px; -fx-text-fill: white; -fx-font-weight: bold;");

        Label titre = new Label(article.getTitre());
        titre.setWrapText(true);
        titre.setStyle("-fx-font-weight: bold; -fx-font-size: 18px; -fx-text-fill: white;");

        ImageView imageView = new ImageView();
        imageView.setFitHeight(100);
        imageView.setFitWidth(280);
        imageView.setPreserveRatio(true);

        File imgFile = new File("C:/xampp/htdocs/" + article.getImage());
        if (imgFile.exists()) {
            imageView.setImage(new Image(imgFile.toURI().toString()));
        }

        Button lireBtn = new Button("Lire");
        lireBtn.setStyle(
                "-fx-background-color: #A18E78; -fx-text-fill: white; -fx-font-weight: bold; -fx-background-radius: 8px;"
        );
        lireBtn.setOnAction(e -> openDetails(article));

        VBox.setMargin(lireBtn, new Insets(10, 0, 0, 0));
        card.getChildren().addAll(titreCategorie, imageView, titre, lireBtn);
        carouselContainer.getChildren().add(card);
    }
}
