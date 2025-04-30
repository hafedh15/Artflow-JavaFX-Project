package tn.artflow.controllers;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import tn.artflow.entities.Article;
import tn.artflow.services.ArticleService;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;

public class AfficheArticleFront implements Initializable {

    @FXML
    private BorderPane rootPane;

    @FXML
    private GridPane gridPane;

    private final ArticleService articleService = new ArticleService();

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        try {
            List<Article> articles = articleService.recuperer();
            int column = 0;
            int row = 0;

            for (Article article : articles) {
                VBox card = createCard(article);
                gridPane.add(card, column++, row);
                GridPane.setMargin(card, new Insets(10));

                if (column == 3) {
                    column = 0;
                    row++;
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private VBox createCard(Article article) {
        VBox vbox = new VBox(10);
        vbox.setPadding(new Insets(10));
        vbox.setStyle("-fx-background-color: #ffffff; -fx-border-color: #ddd; -fx-border-radius: 8; -fx-background-radius: 8;");
        vbox.setPrefWidth(180);

        ImageView imageView = new ImageView();
        imageView.setFitWidth(160);
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
}
