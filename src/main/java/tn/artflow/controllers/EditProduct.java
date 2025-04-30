package tn.artflow.controllers;


import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import tn.artflow.entities.*;
import tn.artflow.services.ProductService;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.sql.SQLException;
import java.util.ResourceBundle;

public class EditProduct implements Initializable {

    @FXML
    private TextField categoryE;

    @FXML
    private TextField descriptionE;

    @FXML
    private TextField imageE;

    @FXML
    private TextField nameE;

    @FXML
    private TextField priceE;

    @FXML
    private VBox mainContainer;

    private int productId; // Pour stocker l'ID du produit en cours d'édition

    @FXML private Label errorNameE, errorDescriptionE, errorPriceE, errorCategoryE, errorImageE;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        // Appliquer le CSS au chargement de la page
        Platform.runLater(() -> {
            if (mainContainer.getScene() != null) {
                String cssFile = getClass().getResource("/styles.css").toExternalForm();
                mainContainer.getScene().getStylesheets().add(cssFile);
            }
        });
    }

    // Méthode pour définir les données du produit avant d'afficher le formulaire
    public void setProductData(Product product) {
        this.productId = product.getId();
        nameE.setText(product.getName());
        descriptionE.setText(product.getDescription());
        priceE.setText(String.valueOf(product.getPrice()));
        categoryE.setText(product.getCategory());
        imageE.setText(product.getImage());
    }

    @FXML
    void Edit(ActionEvent event) {
        boolean valid = true;

        // Réinitialiser erreurs
        errorNameE.setVisible(false);
        errorDescriptionE.setVisible(false);
        errorPriceE.setVisible(false);
        errorCategoryE.setVisible(false);
        errorImageE.setVisible(false);

        nameE.setStyle("");
        descriptionE.setStyle("");
        priceE.setStyle("");
        categoryE.setStyle("");
        imageE.setStyle("");

        // Nom
        if (nameE.getText().trim().isEmpty()) {
            errorNameE.setText("Le nom est obligatoire");
            errorNameE.setVisible(true);
            nameE.setStyle("-fx-border-color: red; -fx-border-width: 2px;");
            valid = false;
        } else if (!nameE.getText().trim().matches("^[a-zA-Z\\s]+$")) {
            errorNameE.setText("Le nom ne doit contenir que des lettres et des espaces");
            errorNameE.setVisible(true);
            nameE.setStyle("-fx-border-color: red; -fx-border-width: 2px;");
            valid = false;
        } else if (nameE.getText().trim().length() < 3) {
            errorNameE.setText("Le nom doit contenir au moins 3 caractères");
            errorNameE.setVisible(true);
            nameE.setStyle("-fx-border-color: red; -fx-border-width: 2px;");
            valid = false;
        }


        // Description
        if (descriptionE.getText().trim().isEmpty()) {
            errorDescriptionE.setText("La description est obligatoire");
            errorDescriptionE.setVisible(true);
            descriptionE.setStyle("-fx-border-color: red; -fx-border-width: 2px;");
            valid = false;
        } else if (descriptionE.getText().trim().length() > 500) {
            errorDescriptionE.setText("Description trop longue (max 500 caractères)");
            errorDescriptionE.setVisible(true);
            descriptionE.setStyle("-fx-border-color: red; -fx-border-width: 2px;");
            valid = false;
        }

        // Prix
        double price = 0;
        if (priceE.getText().trim().isEmpty()) {
            errorPriceE.setText("Le prix est obligatoire");
            errorPriceE.setVisible(true);
            priceE.setStyle("-fx-border-color: red; -fx-border-width: 2px;");
            valid = false;
        } else {
            try {
                price = Double.parseDouble(priceE.getText().trim());
                if (price <= 0) {
                    errorPriceE.setText("Le prix doit être > 0");
                    errorPriceE.setVisible(true);
                    priceE.setStyle("-fx-border-color: red; -fx-border-width: 2px;");
                    valid = false;
                }
            } catch (NumberFormatException e) {
                errorPriceE.setText("Prix invalide");
                errorPriceE.setVisible(true);
                priceE.setStyle("-fx-border-color: red; -fx-border-width: 2px;");
                valid = false;
            }
        }

        // Catégorie
        if (categoryE.getText().trim().isEmpty()) {
            errorCategoryE.setText("La catégorie est obligatoire");
            errorCategoryE.setVisible(true);
            categoryE.setStyle("-fx-border-color: red; -fx-border-width: 2px;");
            valid = false;
        }

        // Image
        if (imageE.getText().trim().isEmpty()) {
            errorImageE.setText("Veuillez sélectionner une image");
            errorImageE.setVisible(true);
            imageE.setStyle("-fx-border-color: red; -fx-border-width: 2px;");
            valid = false;
        } else if (!imageE.getText().trim().matches(".*\\.(png|jpg|jpeg)$")) {
            errorImageE.setText("Format non valide (png, jpg, jpeg)");
            errorImageE.setVisible(true);
            imageE.setStyle("-fx-border-color: red; -fx-border-width: 2px;");
            valid = false;
        }

        if (!valid) return;

        // Création de l’objet produit
        Product p = new Product();
        p.setId(productId);
        p.setName(nameE.getText());
        p.setDescription(descriptionE.getText());
        p.setPrice(price);
        p.setCategory(categoryE.getText());
        p.setImage(imageE.getText());

        ProductService ps = new ProductService();
        try {
            ps.modifierP(p);
            showAlert(AlertType.INFORMATION, "Succès", "Produit modifié avec succès");

        } catch (SQLException e) {
            showAlert(AlertType.ERROR, "Erreur", "Erreur lors de la modification : " + e.getMessage());
            e.printStackTrace();
        }
    }


    @FXML
    void choosefile(ActionEvent event) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Select an Image File");

        // Définir les filtres d'extension pour les fichiers image
        fileChooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("Image Files", "*.png", "*.jpg", "*.jpeg", "*.gif")
        );

        // Définir le répertoire initial pour une navigation plus facile
        fileChooser.setInitialDirectory(new File(System.getProperty("user.home")));

        File selectedFile = fileChooser.showOpenDialog(null);

        if (selectedFile != null) {
            System.out.println("Fichier sélectionné : " + selectedFile.getAbsolutePath());
            try {
                // Chemin de destination dans le dossier XAMPP htdocs
                String destinationPath = "C:/xampp/htdocs/images/products/";
                File destDir = new File(destinationPath);
                if (!destDir.exists()) {
                    destDir.mkdirs();
                    System.out.println("Répertoire créé : " + destinationPath);
                }

                String newFileName = System.currentTimeMillis() + "_" + selectedFile.getName();
                File destinationFile = new File(destDir, newFileName);

                Files.copy(selectedFile.toPath(), destinationFile.toPath(), StandardCopyOption.REPLACE_EXISTING);
                System.out.println("Fichier copié vers : " + destinationFile.getAbsolutePath());

                // URL relative à stocker dans la base de données
                String webURL = "/images/products/" + newFileName;
                imageE.setText(webURL);
                System.out.println("URL définie : " + webURL);

            } catch (IOException e) {
                showAlert(AlertType.ERROR, "Erreur", "Impossible de copier le fichier: " + e.getMessage());
                e.printStackTrace();
            }
        } else {
            System.out.println("Aucun fichier sélectionné");
        }
    }

    @FXML
    void cancel(ActionEvent event) {
        goToProductList();
    }

    private void goToProductList() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/ListProductFront.fxml"));
            Parent root = loader.load();
            mainContainer.getScene().setRoot(root);
        } catch (IOException e) {
            showAlert(AlertType.ERROR, "Erreur de navigation",
                    "Impossible de charger la liste des produits: " + e.getMessage());
            e.printStackTrace();
        }
    }

    // Méthodes de navigation pour la navbar
    @FXML
    void goToHome(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/Home.fxml"));
            Parent root = loader.load();
            mainContainer.getScene().setRoot(root);
        } catch (IOException e) {
            showAlert(AlertType.ERROR, "Erreur de navigation",
                    "Impossible de charger la page d'accueil: " + e.getMessage());
            e.printStackTrace();
        }
    }

    @FXML
    void goToProfil(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/Profil.fxml"));
            Parent root = loader.load();
            mainContainer.getScene().setRoot(root);
        } catch (IOException e) {
            showAlert(AlertType.ERROR, "Erreur de navigation",
                    "Impossible de charger la page profil: " + e.getMessage());
            e.printStackTrace();
        }
    }

    @FXML
    void goToProductList(ActionEvent event) {
        goToProductList();
    }

    private void showAlert(AlertType type, String title, String message) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }


}