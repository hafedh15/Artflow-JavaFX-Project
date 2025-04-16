package tn.artflow.controllers;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.layout.HBox;
import tn.artflow.entities.Comment;
import tn.artflow.services.CommentService;

import java.util.function.Consumer;

public class Commentmof {

    @FXML
    private Label contenuLabel;

    @FXML
    private TextArea editTextArea;

    @FXML
    private HBox buttonsBox;

    private Comment comment;

    private Consumer<Comment> onCommentModified; // callback pour MAJ UI
    private Consumer<Comment> onCommentDeleted;  // callback pour suppression

    public void setComment(Comment comment, Consumer<Comment> onCommentModified, Consumer<Comment> onCommentDeleted) {
        this.comment = comment;
        this.onCommentModified = onCommentModified;
        this.onCommentDeleted = onCommentDeleted;
        contenuLabel.setText(comment.getContenu_Comment());
    }


    @FXML
    void activerModification() {
        editTextArea.setText(comment.getContenu_Comment());
        editTextArea.setVisible(true);
        editTextArea.setManaged(true);
        buttonsBox.setVisible(true);
        buttonsBox.setManaged(true);
        contenuLabel.setVisible(false);
    }

    @FXML
    void confirmerModification() {
        String nouveauContenu = editTextArea.getText().trim();
        if (!nouveauContenu.isEmpty()) {
            comment.setContenu_Comment(nouveauContenu);
            try {
                new CommentService().modifier(comment);
                contenuLabel.setText(nouveauContenu);
                if (onCommentModified != null) {
                    onCommentModified.accept(comment);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
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
    }

    @FXML
    void supprimerCommentaire() {
        try {
            new CommentService().supprimer(comment.getId());
            if (onCommentDeleted != null) {
                onCommentDeleted.accept(comment); // Retire de la liste dans l'UI
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
