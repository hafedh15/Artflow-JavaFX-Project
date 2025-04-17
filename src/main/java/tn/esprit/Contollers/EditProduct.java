package tn.esprit.Contollers;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import tn.esprit.entities.Product;
import tn.esprit.services.ProductService;

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
        // Validation des champs
        if (nameE.getText().isEmpty() || descriptionE.getText().isEmpty() ||
                priceE.getText().isEmpty() || categoryE.getText().isEmpty()) {
            showAlert(AlertType.ERROR, "Erreur de validation", "Tous les champs sont obligatoires.");
            return;
        }

        // Vérification que le prix est un nombre valide
        double price;
        try {
            price = Double.parseDouble(priceE.getText());
            if (price <= 0) {
                showAlert(AlertType.ERROR, "Erreur de validation", "Le prix doit être supérieur à 0.");
                return;
            }
        } catch (NumberFormatException e) {
            showAlert(AlertType.ERROR, "Erreur de validation", "Le prix doit être un nombre valide.");
            return;
        }

        // Récupérer les valeurs des champs
        String name = nameE.getText();
        String description = descriptionE.getText();
        String category = categoryE.getText();
        String image = imageE.getText();

        System.out.println("Mise à jour du produit avec ID: " + productId);
        System.out.println("Nom: " + name);
        System.out.println("Description: " + description);
        System.out.println("Prix: " + price);
        System.out.println("Catégorie: " + category);
        System.out.println("Image: " + image);

        // Créer un objet produit avec les valeurs mises à jour
        Product p = new Product();
        p.setId(productId);
        p.setName(name);
        p.setDescription(description);
        p.setPrice(price);
        p.setCategory(category);
        p.setImage(image);

        // Appel du service pour mettre à jour le produit
        ProductService ps = new ProductService();
        try {
            ps.modifierP(p);
            showAlert(AlertType.INFORMATION, "Succès", "Produit modifié avec succès");

            // Navigation vers la liste des produits
            goToProductList();
        } catch (SQLException e) {
            showAlert(AlertType.ERROR, "Erreur de modification", "Impossible de modifier le produit: " + e.getMessage());
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