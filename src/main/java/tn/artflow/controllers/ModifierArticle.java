package tn.artflow.controllers;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.event.ActionEvent;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import tn.artflow.entities.Article;
import tn.artflow.services.ArticleService;

import java.io.File;
import java.io.IOException;
import java.sql.Date;
import java.time.LocalDate;

public class ModifierArticle {

    @FXML private TextField titreTF;
    @FXML private TextArea contenuTF;
    @FXML private TextField categorieTF;
    @FXML private DatePicker dateTF;
    @FXML private TextField imageTF;
    @FXML private TextField nomauteurTF;

    @FXML private Label titreStatus;
    @FXML private Label categorieStatus;
    @FXML private Label contenuStatus;
    @FXML private Label nomauteurStatus;
    @FXML private Label dateStatus;

    private final ArticleService articleService = new ArticleService();
    private Article articleAModifier;

    public void setArticle(Article article) {
        this.articleAModifier = article;

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
    public void initialize() {
        titreTF.setOnKeyReleased(e -> validateTitre());
        categorieTF.setOnKeyReleased(e -> validateCategorie());
        nomauteurTF.setOnKeyReleased(e -> validateAuteur());
        contenuTF.setOnKeyReleased(e -> validateContenu());
        dateTF.setOnAction(e -> validateDate());
    }

    private void validateTitre() {
        String titre = titreTF.getText().trim();
        if (titre.isEmpty() || titre.length() < 5 || titre.length() > 100) {
            titreStatus.setText("Le titre doit contenir entre 5 et 100 caractères.");
        } else {
            titreStatus.setText("");
        }
    }

    private void validateCategorie() {
        if (categorieTF.getText().trim().isEmpty()) {
            categorieStatus.setText("La catégorie est obligatoire.");
        } else {
            categorieStatus.setText("");
        }
    }

    private void validateContenu() {
        if (contenuTF.getText().trim().length() < 10) {
            contenuStatus.setText("Le contenu doit contenir au moins 10 caractères.");
        } else {
            contenuStatus.setText("");
        }
    }

    private void validateAuteur() {
        String nomAuteur = nomauteurTF.getText().trim();
        if (!nomAuteur.matches("^[A-ZÀ-Ÿ][\\p{L}\\s]{7,14}$")) {
            nomauteurStatus.setText("Le nom doit commencer par une majuscule et faire 8-15 caractères.");
        } else {
            nomauteurStatus.setText("");
        }
    }

    private void validateDate() {
        LocalDate selectedDate = dateTF.getValue();
        if (selectedDate == null) {
            dateStatus.setText("La date est obligatoire.");
        } else if (!selectedDate.equals(LocalDate.now())) {
            dateStatus.setText("La date doit être aujourd'hui.");
        } else {
            dateStatus.setText("");
        }
    }

    @FXML
    void modifier(ActionEvent event) {
        try {
            if (!validateFields()) {
                return;
            }

            articleAModifier.setTitre(titreTF.getText().trim());
            articleAModifier.setContenu(contenuTF.getText().trim());
            articleAModifier.setCategorie(categorieTF.getText().trim());
            articleAModifier.setNomAuteur(nomauteurTF.getText().trim());
            articleAModifier.setImage(imageTF.getText().trim());
            articleAModifier.setDatepub(dateTF.getValue().toString());

            articleService.modifier(articleAModifier);
            System.out.println("✅ Article modifié avec succès !");

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

    private boolean validateFields() {
        validateTitre();
        validateCategorie();
        validateAuteur();
        validateContenu();
        validateDate();

        return titreStatus.getText().isEmpty()
                && categorieStatus.getText().isEmpty()
                && contenuStatus.getText().isEmpty()
                && nomauteurStatus.getText().isEmpty()
                && dateStatus.getText().isEmpty();
    }

    @FXML
    void image(ActionEvent event) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Choisir une image");

        fileChooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("Images", "*.jpg", "*.jpeg", "*.png", "*.gif")
        );

        Stage stage = (Stage) imageTF.getScene().getWindow();
        File selectedFile = fileChooser.showOpenDialog(stage);

        if (selectedFile != null) {
            imageTF.setText(selectedFile.getName());
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
