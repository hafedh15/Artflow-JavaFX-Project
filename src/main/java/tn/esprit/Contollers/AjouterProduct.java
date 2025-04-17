package tn.esprit.Contollers;

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
import tn.esprit.entities.Product;
import tn.esprit.services.ProductService;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.sql.SQLException;
import java.util.ResourceBundle;

public class AjouterProduct implements Initializable {

    @FXML
    private TextField txtcategory;

    @FXML
    private TextField txtdescription;

    @FXML
    private TextField txtimage;

    @FXML
    private TextField txtname;

    @FXML
    private TextField txtprice;

    @FXML
    private VBox mainContainer;



    @FXML
    private Label errorName;

    @FXML
    private Label errorDescription;

    @FXML
    private Label errorPrice;

    @FXML
    private Label errorCategory;

    @FXML
    private Label errorImage;


    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        // Appliquer le CSS au chargement de la page
        Platform.runLater(() -> {
            String cssFile = getClass().getResource("/styles.css").toExternalForm();
            mainContainer.getScene().getStylesheets().add(cssFile);
        });
    }

    @FXML
    void addProduct(ActionEvent event) {
        boolean valid = true;
        double price = 0;  // Déclarez la variable 'price' avant de l'utiliser

        // Réinitialiser les messages d'erreur
        errorName.setVisible(false);
        errorDescription.setVisible(false);
        errorPrice.setVisible(false);
        errorCategory.setVisible(false);
        errorImage.setVisible(false);

        // Réinitialiser les styles
        txtname.setStyle("");
        txtdescription.setStyle("");
        txtprice.setStyle("");
        txtcategory.setStyle("");
        txtimage.setStyle("");

        // Validation du nom
        if (txtname.getText().trim().isEmpty()) {
            errorName.setText("Le nom est obligatoire");
            errorName.setVisible(true);
            txtname.setStyle("-fx-border-color: red; -fx-border-width: 2px;");
            valid = false;
        } else if (!txtname.getText().trim().matches("^[a-zA-Z0-9\\s]+$")) {
            errorName.setText("Le nom ne doit pas contenir de caractères spéciaux");
            errorName.setVisible(true);
            txtname.setStyle("-fx-border-color: red; -fx-border-width: 2px;");
            valid = false;
        } else if (txtname.getText().trim().length() < 3) {
            errorName.setText("Le nom doit contenir au moins 3 caractères");
            errorName.setVisible(true);
            txtname.setStyle("-fx-border-color: red; -fx-border-width: 2px;");
            valid = false;
        }

        // Validation de la description
        if (txtdescription.getText().trim().isEmpty()) {
            errorDescription.setText("La description est obligatoire");
            errorDescription.setVisible(true);
            txtdescription.setStyle("-fx-border-color: red; -fx-border-width: 2px;");
            valid = false;
        } else if (txtdescription.getText().trim().length() > 500) {
            errorDescription.setText("Description trop longue (max 500 caractères)");
            errorDescription.setVisible(true);
            txtdescription.setStyle("-fx-border-color: red; -fx-border-width: 2px;");
            valid = false;
        } else if (txtdescription.getText().trim().matches("\\d+")) {
            errorDescription.setText("La description ne doit pas contenir uniquement des chiffres");
            errorDescription.setVisible(true);
            txtdescription.setStyle("-fx-border-color: red; -fx-border-width: 2px;");
            valid = false;
        }

        // Validation de la catégorie
        if (txtcategory.getText().trim().isEmpty()) {
            errorCategory.setText("La catégorie est obligatoire");
            errorCategory.setVisible(true);
            txtcategory.setStyle("-fx-border-color: red; -fx-border-width: 2px;");
            valid = false;
        }

        // Validation de l'image
        if (txtimage.getText().trim().isEmpty()) {
            errorImage.setText("Veuillez sélectionner une image");
            errorImage.setVisible(true);
            txtimage.setStyle("-fx-border-color: red; -fx-border-width: 2px;");
            valid = false;
        } else if (!txtimage.getText().trim().matches(".*\\.(png|jpg|jpeg)$")) {
            errorImage.setText("Format non valide (png, jpg, jpeg)");
            errorImage.setVisible(true);
            txtimage.setStyle("-fx-border-color: red; -fx-border-width: 2px;");
            valid = false;
        }

        // Validation du prix
        if (txtprice.getText().trim().isEmpty()) {
            errorPrice.setText("Le prix est obligatoire");
            errorPrice.setVisible(true);
            txtprice.setStyle("-fx-border-color: red; -fx-border-width: 2px;");
            valid = false;
        } else {
            try {
                price = Double.parseDouble(txtprice.getText().trim());  // Utilisation de la variable 'price' ici
                if (price <= 0) {
                    errorPrice.setText("Le prix doit être > 0");
                    errorPrice.setVisible(true);
                    txtprice.setStyle("-fx-border-color: red; -fx-border-width: 2px;");
                    valid = false;
                } else {
                    txtprice.setStyle("");  // Réinitialiser le style si le prix est valide
                }
            } catch (NumberFormatException e) {
                errorPrice.setText("Prix invalide");
                errorPrice.setVisible(true);
                txtprice.setStyle("-fx-border-color: red; -fx-border-width: 2px;");
                valid = false;
            }
        }

        if (!valid) return;

        String name = txtname.getText();
        String description = txtdescription.getText();
        String category = txtcategory.getText();
        String image = txtimage.getText();

        Product p = new Product();
        // Définir les propriétés du produit
        p.setName(name);
        p.setDescription(description);
        p.setPrice(price);  // Utilisation de la variable 'price' correctement ici
        p.setCategory(category);
        p.setImage(image);

        ProductService ps = new ProductService();
        try {
            ps.ajouter(p);
            showAlert(AlertType.INFORMATION, "Succès", "Produit ajouté avec succès");

            // Navigation vers la liste des produits
            goToProductList();
        } catch (SQLException e) {
            showAlert(AlertType.ERROR, "Erreur d'ajout", "Impossible d'ajouter le produit: " + e.getMessage());
            e.printStackTrace();
        }
    }


    @FXML
    void choosefile(ActionEvent event) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Select an Image File");

        fileChooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("Image Files", "*.png", "*.jpg", "*.jpeg", "*.gif")
        );

        fileChooser.setInitialDirectory(new File(System.getProperty("user.home")));

        File selectedFile = fileChooser.showOpenDialog(null);

        if (selectedFile != null) {
            System.out.println("Fichier sélectionné : " + selectedFile.getAbsolutePath());
            try {
                // Destination path in XAMPP htdocs folder
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

                // URL relative que vous allez stocker dans la base de données
                String webURL = "/images/products/" + newFileName;
                txtimage.setText(webURL);
                System.out.println("URL définie : " + webURL);

            } catch (IOException e) {
                showAlert(AlertType.ERROR, "Erreur", "Impossible de copier le fichier: " + e.getMessage());
                e.printStackTrace();
            }
        } else {
            System.out.println("Aucun fichier sélectionné");
        }
    }

    // Méthodes de navigation pour la navbar
    @FXML
    void goToProductList(ActionEvent event) {
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

    private void showAlert(AlertType type, String title, String message) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}