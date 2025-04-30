package tn.artflow.controllers;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import tn.artflow.entities.Article;
import tn.artflow.services.ArticleService;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.time.LocalDate;

public class AjouterArticle {

    @FXML private TextField categorieTF;
    @FXML private TextArea contenuTF;
    @FXML private DatePicker dateTF;
    @FXML private TextField imageTF;
    @FXML private TextField nomauteurTF;
    @FXML private TextField titreTF;

    @FXML private Label titreStatus;
    @FXML private Label categorieStatus;
    @FXML private Label contenuStatus;
    @FXML private Label nomauteurStatus;
    @FXML private Label dateStatus;

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
        contenuTF.setOnKeyReleased(e -> validateContenu());
        dateTF.setOnAction(e -> validateDate());
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

    private void validateContenu() {
        String contenu = contenuTF.getText().trim();
        if (contenu.length() < 10) {
            contenuStatus.setText("❌ Le contenu doit faire au moins 10 caractères");
            contenuStatus.setStyle("-fx-text-fill: red;");
        } else {
            contenuStatus.setText("✅");
            contenuStatus.setStyle("-fx-text-fill: green;");
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
            if (!validateFields()) {
                return;
            }

            Article article = new Article(
                    titreTF.getText().trim(),
                    contenuTF.getText().trim(),
                    dateTF.getValue().toString(),
                    imageTF.getText().trim(),
                    categorieTF.getText().trim(),
                    nomauteurTF.getText().trim(),
                    0
            );
            articleService.ajouter(article);

            if (retourCallback != null) {
                retourCallback.run();
            }

        } catch (Exception ex) {
            showError("Erreur : " + ex.getMessage());
            ex.printStackTrace();
        }
    }

    private boolean validateFields() {
        validateTitre();
        validateCategorie();
        validateContenu();
        validateAuteur();
        validateDate();

        boolean allValid = titreStatus.getText().equals("✅")
                && categorieStatus.getText().equals("✅")
                && contenuStatus.getText().equals("✅")
                && nomauteurStatus.getText().equals("✅")
                && dateStatus.getText().equals("✅");

        if (!allValid) {
            showError("Merci de corriger les erreurs avant d'ajouter l'article.");
        }

        return allValid;
    }

    private void showError(String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Erreur de validation");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    @FXML
//    void image(ActionEvent event) {
//        FileChooser fileChooser = new FileChooser();
//        fileChooser.setTitle("Choisir une image");
//        fileChooser.getExtensionFilters().addAll(
//                new FileChooser.ExtensionFilter("Images", "*.jpg", "*.jpeg", "*.png", "*.gif")
//        );
//
//        Stage stage = (Stage) imageTF.getScene().getWindow();
//        File selectedFile = fileChooser.showOpenDialog(stage);
//
//        if (selectedFile != null) {
//            imageTF.setText(selectedFile.getName());
//        }
//    }

    void image(ActionEvent event) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Select an Image File");

        // Corriger les filtres d'extension - noter l'astérisque avant le point
        fileChooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("Image Files", "*.png", "*.jpg", "*.jpeg", "*.gif")
        );


        // Définir un répertoire initial pour faciliter la navigation
        fileChooser.setInitialDirectory(new File(System.getProperty("user.home")));

        File selectedFile = fileChooser.showOpenDialog(null);

        if (selectedFile != null) {
            System.out.println("Fichier sélectionné : " + selectedFile.getAbsolutePath());
            try {
                // Destination path in XAMPP htdocs folder
                String destinationPath = "C:/xampp/htdocs/images/Article/";
                File destDir = new File(destinationPath);
                if (!destDir.exists()) {
                    destDir.mkdirs();
                    System.out.println("Répertoire créé : " + destinationPath);
                }

                String newFileName = System.currentTimeMillis() + "_" + selectedFile.getName();
                File destinationFile = new File(destDir, newFileName);

                Files.copy(selectedFile.toPath(), destinationFile.toPath(), StandardCopyOption.REPLACE_EXISTING);
                System.out.println("Fichier copié vers : " + destinationFile.getAbsolutePath());

                // URL relative que vous allez stocker dans la base de données
                String webURL = "/images/Article/" + newFileName;
                imageTF.setText(webURL);
                System.out.println("URL définie : " + webURL);

            } catch (IOException e) {
                System.out.println("Erreur de copie: " + e.getMessage());
                e.printStackTrace();
            }
        } else {
            System.out.println("Aucun fichier sélectionné");
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
