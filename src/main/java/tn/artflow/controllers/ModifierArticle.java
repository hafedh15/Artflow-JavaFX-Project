package tn.artflow.controllers;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.DatePicker;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.event.ActionEvent;
import javafx.stage.Stage;
import tn.artflow.entities.Article;
import tn.artflow.services.ArticleService;

import java.io.File;
import java.io.IOException;
import java.sql.Date;

public class ModifierArticle {

    @FXML
    private TextField titreTF;

    @FXML
    private TextArea contenuTF;

    @FXML
    private TextField categorieTF;

    @FXML
    private DatePicker dateTF;

    @FXML
    private TextField imageTF;

    @FXML
    private TextField nomauteurTF;

    private final ArticleService articleService = new ArticleService();
    private Article articleAModifier;

    // Appelé par la vue précédente pour injecter l'article à modifier
    public void setArticle(Article article) {
        this.articleAModifier = article;

        // Pré-remplir les champs
        titreTF.setText(article.getTitre());
        contenuTF.setText(article.getContenu());
        categorieTF.setText(article.getCategorie());
        nomauteurTF.setText(article.getNomAuteur());
        imageTF.setText(article.getImage());

        if (article.getDatepub() != null && article.getDatepub().length() >= 10) {
            dateTF.setValue(Date.valueOf(article.getDatepub()).toLocalDate());
        }
    }

    @FXML
    void modifier(ActionEvent event) {
        try {
            articleAModifier.setTitre(titreTF.getText());
            articleAModifier.setContenu(contenuTF.getText());
            articleAModifier.setCategorie(categorieTF.getText());
            articleAModifier.setNomAuteur(nomauteurTF.getText());
            articleAModifier.setImage(imageTF.getText());
            articleAModifier.setDatepub(dateTF.getValue().toString());

            articleService.modifier(articleAModifier);
            System.out.println("✅ Article modifié avec succès !");

            // Revenir à la liste
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/AfficherArticle.fxml"));
            Parent root = loader.load();

            Stage stage = (Stage) titreTF.getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.setTitle("Liste des articles");

        } catch (Exception e) {
            System.out.println("❌ Erreur lors de la modification : " + e.getMessage());
            e.printStackTrace();
        }
    }

    @FXML
    void image(ActionEvent event) {
        javafx.stage.FileChooser fileChooser = new javafx.stage.FileChooser();
        fileChooser.setTitle("Choisir une image");

        fileChooser.getExtensionFilters().addAll(
                new javafx.stage.FileChooser.ExtensionFilter("Images", "*.jpg", "*.jpeg", "*.png", "*.gif")
        );

        // Récupérer la fenêtre actuelle
        Stage stage = (Stage) imageTF.getScene().getWindow();

        // Ouvrir le sélecteur de fichier
        File selectedFile = fileChooser.showOpenDialog(stage);

        if (selectedFile != null) {
            // Afficher le chemin dans le champ texte
            imageTF.setText(selectedFile.getName()); // ou getAbsolutePath() si tu veux le chemin complet
            System.out.println("📷 Image sélectionnée : " + selectedFile.getName());
        } else {
            System.out.println("❌ Aucune image sélectionnée.");
        }
    }

    @FXML
    private void annuler() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/AfficherArticle.fxml"));
            Parent root = loader.load();

            Stage stage = (Stage) titreTF.getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.setTitle("Liste des articles");

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
