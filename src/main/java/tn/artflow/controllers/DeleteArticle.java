package tn.artflow.controllers;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.stage.Stage;
import tn.artflow.entities.Article;
import tn.artflow.services.ArticleService;

public class DeleteArticle {

    private final ArticleService articleService = new ArticleService();
    private Article articleASupprimer;

    @FXML
    private Label messageLabel;

    public void setArticle(Article article) {
        this.articleASupprimer = article;
        messageLabel.setText("Voulez-vous vraiment supprimer l'article : \"" + article.getTitre() + "\" ?");
    }

    @FXML
    void confirmerSuppression(ActionEvent event) {
        try {
            if (articleASupprimer != null) {
                articleService.supprimer(articleASupprimer.getId());
                System.out.println("✅ Article supprimé : " + articleASupprimer.getTitre());
            }
            fermerFenetre(event);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @FXML
    void annulerSuppression(ActionEvent event) {
        fermerFenetre(event);
    }

    private void fermerFenetre(ActionEvent event) {
        Stage stage = (Stage) ((javafx.scene.Node) event.getSource()).getScene().getWindow();
        stage.close();
    }
}
