package tn.artflow.controllers;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import tn.artflow.entities.Comment;
import tn.artflow.services.CommentService;

import java.io.File;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Scanner;
import java.util.function.Consumer;

public class Commentmof {

    @FXML
    private Label contenuLabel;

    @FXML
    private TextArea editTextArea;

    @FXML
    private HBox buttonsBox;

    @FXML
    private ImageView userImage;

    @FXML
    private Label usernameLabel;

    @FXML
    private Label dateLabel;

    @FXML
    private HBox starsContainer;

    @FXML
    private Label errorLabel;

    private Comment comment;
    private Consumer<Comment> onCommentModified;
    private Consumer<Comment> onCommentDeleted;

    private int selectedRating = 0;

    public void setComment(Comment comment, Consumer<Comment> onCommentModified, Consumer<Comment> onCommentDeleted) {
        this.comment = comment;
        this.onCommentModified = onCommentModified;
        this.onCommentDeleted = onCommentDeleted;

        usernameLabel.setText(comment.getUser().getName());
        dateLabel.setText(comment.getDatecom());
        selectedRating = comment.getRating();

        String contenuCensure = censurerMauvaisMots(comment.getContenu_Comment(), comment.getUser().getName());
        contenuLabel.setText(contenuCensure);

        updateStars(selectedRating);
        loadUserImage(comment.getUser().getPhoto());
    }

    private void updateStars(int rating) {
        starsContainer.getChildren().clear();
        for (int i = 1; i <= 5; i++) {
            Label star = new Label(i <= rating ? "★" : "☆");
            star.setStyle("-fx-font-size: 18px; -fx-text-fill: " + (i <= rating ? "#FFB400" : "#cccccc") + ";");
            int finalI = i;
            star.setOnMouseClicked(e -> {
                selectedRating = finalI;
                updateStars(selectedRating);
            });
            starsContainer.getChildren().add(star);
        }
    }

    private void loadUserImage(String imageName) {
        try {
            if (imageName != null && !imageName.isEmpty()) {
                File imageFile = new File("C:/xampp/htdocs/images/" + imageName);
                if (imageFile.exists()) {
                    userImage.setImage(new Image(imageFile.toURI().toString()));
                } else {
                    userImage.setImage(null);
                }
            } else {
                userImage.setImage(null);
            }
        } catch (Exception e) {
            e.printStackTrace();
            userImage.setImage(null);
        }
    }

    @FXML
    void activerModification() {
        editTextArea.setText(comment.getContenu_Comment());
        editTextArea.setVisible(true);
        editTextArea.setManaged(true);
        buttonsBox.setVisible(true);
        buttonsBox.setManaged(true);
        contenuLabel.setVisible(false);
        errorLabel.setVisible(false);
        errorLabel.setManaged(false);
    }

    @FXML
    void confirmerModification() {
        String nouveauContenu = editTextArea.getText().trim();
        if (nouveauContenu.isEmpty()) {
            errorLabel.setText("Le commentaire ne peut pas être vide.");
            errorLabel.setVisible(true);
            errorLabel.setManaged(true);
            return;
        }

        comment.setContenu_Comment(nouveauContenu);
        comment.setRating(selectedRating);

        try {
            new CommentService().modifier(comment);
            contenuLabel.setText(censurerMauvaisMots(nouveauContenu, comment.getUser().getName()));
            updateStars(selectedRating);
            if (onCommentModified != null) {
                onCommentModified.accept(comment);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        annulerModification();
    }

    @FXML
    void annulerModification() {
        editTextArea.setVisible(false);
        editTextArea.setManaged(false);
        buttonsBox.setVisible(false);
        buttonsBox.setManaged(false);
        contenuLabel.setVisible(true);
        errorLabel.setVisible(false);
        errorLabel.setManaged(false);
        updateStars(comment.getRating());
    }

    @FXML
    void supprimerCommentaire() {
        try {
            new CommentService().supprimer(comment.getId());
            if (onCommentDeleted != null) {
                onCommentDeleted.accept(comment);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private String censurerMauvaisMots(String texte, String username) {
        try {
            String urlStr = "https://www.purgomalum.com/service/json?text=" + texte.replaceAll(" ", "%20");
            URL url = new URL(urlStr);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");

            Scanner sc = new Scanner(conn.getInputStream());
            StringBuilder jsonResponse = new StringBuilder();
            while (sc.hasNext()) {
                jsonResponse.append(sc.nextLine());
            }
            sc.close();

            String result = jsonResponse.toString();
            int start = result.indexOf(":\"") + 2;
            int end = result.lastIndexOf("\"");
            if (start > 1 && end > start) {
                String censuredText = result.substring(start, end);
                if (censuredText.contains("***")) {
                    NotificationCenter.addNotification("Utilisateur: " + username + ", Contenu censuré: " + censuredText);
                }
                return censuredText;
            } else {
                return texte;
            }
        } catch (Exception e) {
            e.printStackTrace();
            return texte;
        }
    }
}
