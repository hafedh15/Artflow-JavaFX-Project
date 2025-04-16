package tn.artflow.controllers;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import tn.artflow.entities.Comment;
import tn.artflow.services.CommentService;

import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;

public class AfficheComment implements Initializable {

    @FXML
    private GridPane commentGrid;

    private final CommentService commentService = new CommentService();

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        try {
            commentGrid.getChildren().clear(); // Important pour éviter doublons

            // En-tête
            commentGrid.add(new Label("ID"), 0, 0);
            commentGrid.add(new Label("Contenu"), 1, 0);
            commentGrid.add(new Label("Auteur"), 2, 0);
            commentGrid.add(new Label("Date"), 3, 0);
            commentGrid.add(new Label("Actions"), 4, 0);

            List<Comment> comments = commentService.recuperer();
            int rowIndex = 1;

            for (Comment comment : comments) {
                commentGrid.add(new Label(String.valueOf(comment.getId())), 0, rowIndex);
                commentGrid.add(new Label(comment.getContenu_Comment()), 1, rowIndex);
                commentGrid.add(new Label(comment.getUser().getName()), 2, rowIndex);
                commentGrid.add(new Label(comment.getDatecom()), 3, rowIndex);

                // ❌ Supprimer uniquement
                Button btnSupprimer = new Button("Delete");
                btnSupprimer.setStyle("-fx-background-color: #e53935; -fx-text-fill: white;");
                btnSupprimer.setOnAction(e -> {
                    try {
                        commentService.supprimer(comment.getId());
                        initialize(null, null); // Recharge la grille
                    } catch (Exception ex) {
                        ex.printStackTrace();
                    }
                });

                HBox actionBox = new HBox(10, btnSupprimer);
                commentGrid.add(actionBox, 4, rowIndex);

                rowIndex++;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
