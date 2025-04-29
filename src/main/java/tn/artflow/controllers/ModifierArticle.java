package tn.artflow.controllers;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.web.WebView;
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
    @FXML private TextField categorieTF;
    @FXML private DatePicker dateTF;
    @FXML private TextField imageTF;
    @FXML private TextField nomauteurTF;

    @FXML private WebView contenuWebView;
    @FXML private ToolBar toolbar;
    @FXML private Button boldButton;
    @FXML private Button italicButton;
    @FXML private Button underlineButton;
    @FXML private ColorPicker colorPicker;
    @FXML private ComboBox<String> fontComboBox;

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
        categorieTF.setText(article.getCategorie());
        nomauteurTF.setText(article.getNomAuteur());
        imageTF.setText(article.getImage());

        if (article.getDatepub() != null && article.getDatepub().length() >= 10) {
            dateTF.setValue(Date.valueOf(article.getDatepub()).toLocalDate());
        }

        // Charger le contenu HTML existant dans le WebView
        contenuWebView.getEngine().loadContent(
                "<html><body contenteditable='true' style='font-family: Arial; font-size: 14px;'>"
                        + article.getContenu() +
                        "</body></html>"
        );
    }

    @FXML
    public void initialize() {
        titreTF.setOnKeyReleased(e -> validateTitre());
        categorieTF.setOnKeyReleased(e -> validateCategorie());
        nomauteurTF.setOnKeyReleased(e -> validateAuteur());
        dateTF.setOnAction(e -> validateDate());

        setupEditor();
    }

    private void setupEditor() {
        boldButton.setOnAction(e -> contenuWebView.getEngine().executeScript("document.execCommand('bold', false, null)"));
        italicButton.setOnAction(e -> contenuWebView.getEngine().executeScript("document.execCommand('italic', false, null)"));
        underlineButton.setOnAction(e -> contenuWebView.getEngine().executeScript("document.execCommand('underline', false, null)"));

        colorPicker.setOnAction(e -> {
            String color = toRgbString(colorPicker.getValue());
            contenuWebView.getEngine().executeScript("document.execCommand('foreColor', false, '" + color + "')");
        });

        fontComboBox.getItems().addAll("Arial", "Courier New", "Times New Roman", "Verdana", "Georgia", "Comic Sans MS");
        fontComboBox.setValue("Arial");
        fontComboBox.setOnAction(e -> {
            String font = fontComboBox.getValue();
            contenuWebView.getEngine().executeScript("document.execCommand('fontName', false, '" + font + "')");
        });
    }

    private String toRgbString(Color color) {
        int r = (int) (color.getRed() * 255);
        int g = (int) (color.getGreen() * 255);
        int b = (int) (color.getBlue() * 255);
        return String.format("#%02X%02X%02X", r, g, b);
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

            String contenu = (String) contenuWebView.getEngine().executeScript("document.body.innerHTML");

            articleAModifier.setTitre(titreTF.getText().trim());
            articleAModifier.setContenu(contenu);
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
        validateDate();

        String contenu = (String) contenuWebView.getEngine().executeScript("document.body.innerText");
        if (contenu.trim().length() < 10) {
            contenuStatus.setText("Le contenu doit contenir au moins 10 caractères.");
            return false;
        } else {
            contenuStatus.setText("");
        }

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
