package tn.artflow.controllers;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.DatePicker;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import tn.artflow.entities.Article;
import tn.artflow.services.ArticleService;

import java.io.File;
import java.io.IOException;
import java.sql.Date;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class AjouterArticle {

    @FXML
    private TextField categorieTF;

    @FXML
    private TextArea contenuTF;

    @FXML
    private DatePicker dateTF;

    @FXML
    private TextField imageTF;

    @FXML
    private TextField nomauteurTF;

    @FXML
    private TextField titreTF;

    private final ArticleService articleService = new ArticleService();
    private Runnable retourCallback;

    public void setRetourCallback(Runnable callback) {
        this.retourCallback = callback;
    }

    @FXML
    void ajouter(ActionEvent event) {
        try {
            // 🔸 Récupération des données
            String titre = titreTF.getText();
            String contenu = contenuTF.getText();
            String categorie = categorieTF.getText();
            String image = imageTF.getText();
            String nomAuteur = nomauteurTF.getText();
            LocalDate selectedDate = dateTF.getValue();

            // 🔒 VALIDATIONS
            if (titre.isEmpty() || titre.length() < 5 || titre.length() > 100) {
                showError("Le titre est obligatoire et doit faire entre 5 et 100 caractères.");
                return;
            }

            if (contenu.isEmpty() || contenu.length() < 10) {
                showError("Le contenu est obligatoire et doit contenir au moins 10 caractères.");
                return;
            }

            if (categorie.isEmpty()) {
                showError("La catégorie est obligatoire.");
                return;
            }

            if (nomAuteur.isEmpty()
                    || nomAuteur.length() < 8
                    || nomAuteur.length() > 15
                    || !nomAuteur.matches("^[A-ZÀ-Ÿ][\\p{L}\\s]*$")) {
                showError("Le nom de l’auteur doit commencer par une majuscule, contenir uniquement des lettres et des espaces, et avoir entre 8 et 15 caractères.");
                return;
            }

            if (selectedDate == null) {
                showError("La date de publication est obligatoire.");
                return;
            }

            if (!selectedDate.equals(LocalDate.now())) {
                showError("Vous devez entrer la date d’aujourd’hui.");
                return;
            }

            // 🔹 Création de l'article
            String datePubString = selectedDate.toString();
            Article article = new Article(titre, contenu, datePubString, image, categorie, nomAuteur, 0);
            articleService.ajouter(article);

            System.out.println("✅ Article ajouté avec succès !");
            if (retourCallback != null) {
                retourCallback.run(); // retourne à l'affichage de la liste
            }

        } catch (SQLException e) {
            showError("Erreur SQL : " + e.getMessage());
            e.printStackTrace();
        } catch (Exception ex) {
            showError("Erreur : " + ex.getMessage());
            ex.printStackTrace();
        }
    }
    private void showError(String message) {
        javafx.scene.control.Alert alert = new javafx.scene.control.Alert(javafx.scene.control.Alert.AlertType.ERROR);
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
                new FileChooser.ExtensionFilter("images", "*.png", "*.jpg", "*.jpeg", "*.gif")
        );

        // Récupère la fenêtre actuelle
        Stage stage = (Stage) imageTF.getScene().getWindow();

        File selectedFile = fileChooser.showOpenDialog(stage);
        if (selectedFile != null) {
            try {
                // Dossier de destination
                String destinationFolder = "C:/xampp/htdocs/images/";
                File destinationDir = new File(destinationFolder);
                if (!destinationDir.exists()) {
                    destinationDir.mkdirs();
                }

                // Nom du fichier
                String fileName = selectedFile.getName();

                // Destination complète
                File destFile = new File(destinationFolder + fileName);

                // Copier l'image
                java.nio.file.Files.copy(
                        selectedFile.toPath(),
                        destFile.toPath(),
                        java.nio.file.StandardCopyOption.REPLACE_EXISTING
                );

                // Ne stocke que le nom du fichier dans le champ texte
                imageTF.setText(fileName);

                System.out.println("✅ Image copiée dans le dossier images : " + destFile.getAbsolutePath());

            } catch (Exception e) {
                System.out.println("❌ Erreur lors de la copie de l'image : " + e.getMessage());
                e.printStackTrace();
            }
        } else {
            System.out.println("📂 Aucun fichier sélectionné.");
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