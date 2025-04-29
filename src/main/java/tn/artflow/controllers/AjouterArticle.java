package tn.artflow.controllers;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.scene.web.WebView;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.scene.paint.Color;

import tn.artflow.entities.Article;
import tn.artflow.services.ArticleService;

import java.io.File;
import java.io.IOException;
import java.time.LocalDate;

public class AjouterArticle {

    @FXML private TextField categorieTF;
    @FXML private DatePicker dateTF;
    @FXML private TextField imageTF;
    @FXML private TextField nomauteurTF;
    @FXML private TextField titreTF;
    @FXML private Label titreStatus;
    @FXML private Label categorieStatus;
    @FXML private Label contenuStatus;
    @FXML private Label nomauteurStatus;
    @FXML private Label dateStatus;

    @FXML private WebView contenuWebView;
    @FXML private ToolBar toolbar;
    @FXML private Button boldButton;
    @FXML private Button italicButton;
    @FXML private Button underlineButton;
    @FXML private ColorPicker colorPicker;
    @FXML private ComboBox<String> fontComboBox;

    private final ArticleService articleService = new ArticleService();
    private Runnable retourCallback;

    public void setRetourCallback(Runnable callback) {
        this.retourCallback = callback;
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
        contenuWebView.getEngine().loadContent(
                "<html><body contenteditable='true' style='font-family: Arial; font-size: 14px;'></body></html>"
        );

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
            titreStatus.setText("❌ Le titre doit contenir entre 5 et 100 caractères");
            titreStatus.setStyle("-fx-text-fill: red;");
        } else {
            titreStatus.setText("✅");
            titreStatus.setStyle("-fx-text-fill: green;");
        }
    }

    private void validateCategorie() {
        String categorie = categorieTF.getText().trim();
        if (categorie.isEmpty()) {
            categorieStatus.setText("❌ La catégorie est obligatoire");
            categorieStatus.setStyle("-fx-text-fill: red;");
        } else {
            categorieStatus.setText("✅");
            categorieStatus.setStyle("-fx-text-fill: green;");
        }
    }

    private void validateAuteur() {
        String nomAuteur = nomauteurTF.getText().trim();
        if (nomAuteur.isEmpty() || nomAuteur.length() < 8 || nomAuteur.length() > 15 || !nomAuteur.matches("^[A-ZÀ-Ÿ][\\p{L}\\s]*$")) {
            nomauteurStatus.setText("❌ Le nom doit commencer par une majuscule et faire 8-15 lettres");
            nomauteurStatus.setStyle("-fx-text-fill: red;");
        } else {
            nomauteurStatus.setText("✅");
            nomauteurStatus.setStyle("-fx-text-fill: green;");
        }
    }

    private void validateDate() {
        LocalDate selectedDate = dateTF.getValue();
        if (selectedDate == null) {
            dateStatus.setText("❌ Veuillez choisir une date");
            dateStatus.setStyle("-fx-text-fill: red;");
        } else if (!selectedDate.equals(LocalDate.now())) {
            dateStatus.setText("❌ La date doit être aujourd'hui");
            dateStatus.setStyle("-fx-text-fill: red;");
        } else {
            dateStatus.setText("✅");
            dateStatus.setStyle("-fx-text-fill: green;");
        }
    }

    @FXML
    void ajouter(ActionEvent event) {
        try {
            if (!validateFields()) return;

            String contenu = (String) contenuWebView.getEngine().executeScript("document.body.innerHTML");

            Article article = new Article(
                    titreTF.getText().trim(),
                    contenu,
                    dateTF.getValue().toString(),
                    imageTF.getText().trim(),
                    categorieTF.getText().trim(),
                    nomauteurTF.getText().trim(),
                    0
            );
            articleService.ajouter(article);

            if (retourCallback != null) retourCallback.run();

        } catch (Exception ex) {
            showError("Erreur : " + ex.getMessage());
            ex.printStackTrace();
        }
    }

    private boolean validateFields() {
        validateTitre();
        validateCategorie();
        validateAuteur();
        validateDate();

        String contenu = (String) contenuWebView.getEngine().executeScript("document.body.innerText");
        if (contenu.trim().length() < 10) {
            contenuStatus.setText("❌ Le contenu doit faire au moins 10 caractères");
            contenuStatus.setStyle("-fx-text-fill: red;");
            return false;
        } else {
            contenuStatus.setText("✅");
            contenuStatus.setStyle("-fx-text-fill: green;");
        }

        return titreStatus.getText().equals("✅")
                && categorieStatus.getText().equals("✅")
                && contenuStatus.getText().equals("✅")
                && nomauteurStatus.getText().equals("✅")
                && dateStatus.getText().equals("✅");
    }

    private void showError(String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Erreur de validation");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
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
            try {
                File targetDir = new File("C:/xampp/htdocs/images/");
                if (!targetDir.exists()) targetDir.mkdirs();

                File destFile = new File(targetDir, selectedFile.getName());
                java.nio.file.Files.copy(selectedFile.toPath(), destFile.toPath(), java.nio.file.StandardCopyOption.REPLACE_EXISTING);

                imageTF.setText(selectedFile.getName());
                System.out.println("✅ Image copiée avec succès : " + destFile.getAbsolutePath());

            } catch (IOException e) {
                System.out.println("❌ Erreur lors de la copie de l'image : " + e.getMessage());
                e.printStackTrace();
            }
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
