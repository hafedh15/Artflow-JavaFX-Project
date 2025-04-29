package tn.artflow.controllers;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.TextArea;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;
import tn.artflow.entities.Article;
import tn.artflow.entities.Comment;
import tn.artflow.entities.User;
import tn.artflow.services.CommentService;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AjouterComment {

    @FXML private Label titreLabel;
    @FXML private Label auteurLabel;
    @FXML private Label categorieTopLabel;
    @FXML private Label dateLabel;
    @FXML private Label viewsLabel;
    @FXML private ImageView articleImage;
    @FXML private ImageView authorImage;
    @FXML private TextArea commentaireTF;
    @FXML private VBox commentList;
    @FXML private Label commentaireStatus;
    @FXML private HBox ratingStars;
    @FXML private VBox ratingsSummary;
    @FXML private Label averageRatingLabel;
    @FXML private HBox averageStars;
    @FXML private ProgressBar progress5, progress4, progress3, progress2, progress1;
    @FXML private Label count5, count4, count3, count2, count1;
    @FXML private WebView contenuWebView;

    private int selectedRating = 0;
    private Article article;

    public void setArticle(Article article) {
        this.article = article;
        incrementerViews(article.getId());

        titreLabel.setText(article.getTitre());
        auteurLabel.setText(article.getNomAuteur());
        dateLabel.setText(article.getDatepub());
        viewsLabel.setText("👁 " + (article.getViews() + 1));

        if (categorieTopLabel != null) {
            categorieTopLabel.setText(article.getCategorie());
        }

        File imageFile = new File("C:/xampp/htdocs/images/" + article.getImage());
        if (imageFile.exists()) {
            articleImage.setImage(new Image(imageFile.toURI().toString()));
        } else {
            articleImage.setImage(null);
        }

        // 🔥 Afficher contenu stylé avec une couleur
        WebEngine engine = contenuWebView.getEngine();
        String styledContent = "<div style='color: #5C4F3D; font-size: 14px; font-family: Arial;'>" +
                article.getContenu() +
                "</div>";
        engine.loadContent(styledContent);

        afficherCommentaires();
        commentaireTF.setOnKeyReleased(e -> validateCommentaire());
        setupRatingStars();
        chargerStatsRatings();
    }

    private void afficherCommentaires() {
        try {
            List<Comment> comments = new CommentService().recupererParArticle(article.getId());
            commentList.getChildren().clear();

            for (Comment comment : comments) {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/Commentmof.fxml"));
                Node node = loader.load();
                Commentmof controller = loader.getController();
                controller.setComment(comment, updatedComment -> {}, deletedComment -> commentList.getChildren().remove(node));
                commentList.getChildren().add(node);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void setupRatingStars() {
        ratingStars.getChildren().clear();
        for (int i = 1; i <= 5; i++) {
            Label star = new Label("☆");
            star.setStyle("-fx-font-size: 24px; -fx-cursor: hand;");
            int ratingValue = i;
            star.setOnMouseClicked(event -> {
                selectedRating = ratingValue;
                updateStars();
            });
            ratingStars.getChildren().add(star);
        }
        updateStars();
    }

    private void updateStars() {
        for (int i = 0; i < ratingStars.getChildren().size(); i++) {
            Label star = (Label) ratingStars.getChildren().get(i);
            if (i < selectedRating) {
                star.setText("★");
                star.setStyle("-fx-font-size: 24px; -fx-text-fill: gold; -fx-cursor: hand;");
            } else {
                star.setText("☆");
                star.setStyle("-fx-font-size: 24px; -fx-cursor: hand;");
            }
        }
    }

    @FXML
    void envoyerCommentaire() {
        if (!validateCommentaire()) {
            showError("Veuillez écrire un commentaire avant d'envoyer.");
            return;
        }

        try {
            Comment comment = new Comment();
            comment.setContenu_Comment(commentaireTF.getText().trim());
            comment.setArticle(article);
            comment.setUser(new User(1, "Utilisateur Test"));
            comment.setDatecom(LocalDate.now().toString());
            comment.setRating(selectedRating);

            new CommentService().ajouter(comment);

            showAlert(Alert.AlertType.INFORMATION, "Succès", "Commentaire ajouté avec succès.");
            commentaireTF.clear();
            commentaireStatus.setText("");
            afficherCommentaires();
            selectedRating = 0;
            updateStars();

        } catch (Exception e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Erreur", "Une erreur est survenue lors de l’ajout.");
        }
    }

    private boolean validateCommentaire() {
        String contenu = commentaireTF.getText().trim();
        if (contenu.isEmpty()) {
            commentaireStatus.setText("❌");
            commentaireStatus.setStyle("-fx-text-fill: red; -fx-font-size: 20px;");
            return false;
        }
        commentaireStatus.setText("✅");
        commentaireStatus.setStyle("-fx-text-fill: green; -fx-font-size: 20px;");
        return true;
    }

    private void showError(String message) {
        showAlert(Alert.AlertType.ERROR, "Erreur de validation", message);
    }

    private void showAlert(Alert.AlertType type, String title, String message) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    private void incrementerViews(int articleId) {
        try {
            new CommentService().incrementerViews(articleId);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void chargerStatsRatings() {
        if (article == null) return;

        Map<Integer, Integer> stats = getRatingsStatsFromApi(article.getId());

        int total = stats.values().stream().mapToInt(Integer::intValue).sum();
        if (total == 0) total = 1;

        double moyenne = stats.entrySet().stream()
                .mapToDouble(entry -> entry.getKey() * entry.getValue())
                .sum() / total;
        moyenne = Math.round(moyenne * 10) / 10.0;

        averageRatingLabel.setText(String.valueOf(moyenne));
        averageStars.getChildren().clear();

        for (int i = 1; i <= 5; i++) {
            Label star = new Label(i <= Math.round(moyenne) ? "★" : "☆");
            star.setStyle("-fx-font-size: 20px; -fx-text-fill: gold;");
            averageStars.getChildren().add(star);
        }

        updateProgress(progress5, count5, stats.getOrDefault(5, 0), total);
        updateProgress(progress4, count4, stats.getOrDefault(4, 0), total);
        updateProgress(progress3, count3, stats.getOrDefault(3, 0), total);
        updateProgress(progress2, count2, stats.getOrDefault(2, 0), total);
        updateProgress(progress1, count1, stats.getOrDefault(1, 0), total);
    }

    private void updateProgress(ProgressBar progressBar, Label countLabel, int count, int total) {
        if (progressBar != null && countLabel != null) {
            progressBar.setProgress((double) count / total);
            countLabel.setText(String.valueOf(count));
        }
    }

    private Map<Integer, Integer> getRatingsStatsFromApi(int articleId) {
        Map<Integer, Integer> stats = new HashMap<>();
        try {
            URL url = new URL("http://localhost:8080/api/ratings/" + articleId);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");
            conn.setRequestProperty("Accept", "application/json");

            if (conn.getResponseCode() == 200) {
                BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                StringBuilder response = new StringBuilder();
                String line;
                while ((line = in.readLine()) != null) {
                    response.append(line);
                }
                in.close();

                JSONObject json = new JSONObject(response.toString());
                JSONObject details = json.getJSONObject("details");

                for (int i = 1; i <= 5; i++) {
                    stats.put(i, details.optInt(String.valueOf(i), 0));
                }
            } else {
                System.out.println("Erreur API : code réponse = " + conn.getResponseCode());
            }
            conn.disconnect();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return stats;
    }
}
