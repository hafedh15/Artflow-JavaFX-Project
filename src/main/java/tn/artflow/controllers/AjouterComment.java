package tn.artflow.controllers;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
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
import javafx.scene.Node;

import java.io.File;
import java.io.IOException;
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
    @FXML private VBox commentList; // 💬 Ajout du conteneur pour afficher les commentaires

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

        File imageFile = new File("C:/xampp/htdocs/images/" + article.getImage());
        if (imageFile.exists()) {
            articleImage.setImage(new Image(imageFile.toURI().toString()));
        } else {
            articleImage.setImage(new Image(getClass().getResource("/images/default.png").toExternalForm()));
        }

        // 🔁 Charger et afficher les commentaires
        afficherCommentaires();
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
                        updatedComment -> {
                            System.out.println("💬 Modifié : " + updatedComment.getContenu_Comment());
                        },
                        deletedComment -> {
                            commentList.getChildren().remove(node);
                            System.out.println("🗑 Supprimé : " + deletedComment.getContenu_Comment());
                        }
                );

                commentList.getChildren().add(node);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }




    @FXML
    void envoyerCommentaire() {
        String contenu = commentaireTF.getText().trim();

        if (contenu.isEmpty()) {
            showAlert(Alert.AlertType.WARNING, "Champ vide", "Veuillez écrire un commentaire.");
            return;
        }

        try {
            Comment comment = new Comment();
            comment.setContenu_Comment(contenu);
            comment.setArticle(article);
            comment.setUser(new User(1, "Utilisateur Test")); // Simulé
            comment.setDatecom(LocalDate.now().toString());
            comment.setRating(5);

            new CommentService().ajouter(comment);

            showAlert(Alert.AlertType.INFORMATION, "Succès", "Commentaire ajouté avec succès !");
            commentaireTF.clear();
            afficherCommentaires(); // Met à jour la liste des commentaires

        } catch (Exception e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Erreur", "Une erreur est survenue lors de l’ajout.");
        }
    }

    private void showAlert(Alert.AlertType type, String title, String message) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
