package tn.artflow.controllers;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.VBox;
import tn.artflow.entities.Article;
import tn.artflow.entities.Comment;
import tn.artflow.entities.User;
import tn.artflow.services.CommentService;

import java.io.File;
import java.time.LocalDate;
import java.util.List;

public class AjouterComment {

    @FXML private Label titreLabel;
    @FXML private Label auteurLabel;
    @FXML private Label categorieTopLabel;
    @FXML private Label contenuLabel;
    @FXML private Label dateLabel;
    @FXML private Label viewsLabel;
    @FXML private ImageView articleImage;
    @FXML private TextArea commentaireTF;
    @FXML private VBox commentList;
    @FXML private Label commentaireStatus;

    private Article article;

    public void setArticle(Article article) {
        this.article = article;

        titreLabel.setText(article.getTitre());
        auteurLabel.setText(article.getNomAuteur());
        contenuLabel.setText(article.getContenu());
        dateLabel.setText(article.getDatepub());
        viewsLabel.setText("👁 " + article.getViews());

        if (categorieTopLabel != null) {
            categorieTopLabel.setText(article.getCategorie());
        }

//        String imageName = article.getImage();
//        String imageFullPath = "C:/xampp/htdocs/" + imageName;
//
//        File imageFile = new File(imageFullPath);
//        Image image;
//
//        if (imageFile.exists()) {
//            image = new Image(imageFile.toURI().toString());
//        } else {
//            // Try to load from resources
//            var defaultStream = getClass().getResourceAsStream("/images/default.png");
//            if (defaultStream != null) {
//                image = new Image(defaultStream);
//            } else {
//                System.err.println("❌ Default image not found in resources!");
//                return; // or show a placeholder
//            }
//        }
//
//        articleImage.setImage(image);


        String imageName = article.getImage(); // Ex: "example.jpg"
        String imageFullPath = "C:/xampp/htdocs/" + imageName;

        File imageFile = new File(imageFullPath);
        Image image = imageFile.exists()
                ? new Image(imageFile.toURI().toString())
                : new Image(getClass().getResourceAsStream("/images/default.png"));

        articleImage.setImage(image);


        afficherCommentaires();
        commentaireTF.setOnKeyReleased(e -> validateCommentaire());
    }

    private void afficherCommentaires() {
        try {
            List<Comment> comments = new CommentService().recupererParArticle(article.getId());
            commentList.getChildren().clear();

            for (Comment comment : comments) {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/Commentmof.fxml"));
                Node node = loader.load();

                Commentmof controller = loader.getController();
                controller.setComment(
                        comment,
                        updatedComment -> {},
                        deletedComment -> commentList.getChildren().remove(node)
                );

                commentList.getChildren().add(node);
            }
        } catch (Exception e) {
            e.printStackTrace();
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
            User user = tn.artflow.utils.UserSession.getInstance().getUser();
            comment.setUser(user);
           // comment.setUser(new User(1, "Utilisateur Test"));
            comment.setDatecom(LocalDate.now().toString());
            comment.setRating(5);

            new CommentService().ajouter(comment);

            showAlert(Alert.AlertType.INFORMATION, "Succès", "Commentaire ajouté avec succès.");
            commentaireTF.clear();
            commentaireStatus.setText("");
            afficherCommentaires();

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
}
