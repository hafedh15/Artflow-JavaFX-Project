package tn.artflow.controllers;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.layout.TilePane;
import tn.artflow.entities.*;
import tn.artflow.services.*;

import java.io.IOException;
import java.net.URL;
import java.sql.SQLException;
import java.util.List;
import java.util.ResourceBundle;

public class ListProductFront  implements Initializable {

    @FXML
    private TilePane productContainer;

    private ProductService productService;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        productService = new ProductService();

        Platform.runLater(() -> {
            String cssFile = getClass().getResource("/styles.css").toExternalForm();
            productContainer.getScene().getStylesheets().add(cssFile);
        });



        loadProducts();
    }

    private void loadProducts() {
        try {
            // Récupérer tous les produits
            List<Product> products = productService.recuperer();

            // Nettoyer le container avant de remplir
            productContainer.getChildren().clear();

            // Ajouter les produits
            for (Product product : products) {
                try {
                    // Charger le modèle de carte pour chaque produit
                    FXMLLoader loader = new FXMLLoader(getClass().getResource("/ProductCard.fxml"));
                    Parent productCard = loader.load();

                    // Configurer la carte avec les données du produit
                    ProductCard cardController = loader.getController();
                    cardController.setProduct(product);
                    cardController.setParentController(this);

                    // Ajouter la carte au conteneur
                    productContainer.getChildren().add(productCard);
                } catch (IOException e) {
                    e.printStackTrace();
                    showAlert(AlertType.ERROR, "Erreur",
                            "Impossible de charger la carte produit: " + e.getMessage());
                }
            }

        } catch (SQLException e) {
            showAlert(AlertType.ERROR, "Erreur de base de données",
                    "Impossible de charger les produits: " + e.getMessage());
            e.printStackTrace();
        }
    }


    // Méthode pour rafraîchir la liste des produits (appelée après suppression)
    public void refreshProducts() {
        loadProducts();
    }
    @FXML
    public void openCart(ActionEvent actionEvent) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/Cart.fxml"));
            Parent cartPage = loader.load();

            // Obtenir la scène actuelle et définir la page du panier
            productContainer.getScene().setRoot(cartPage);
        } catch (IOException e) {
            showAlert(AlertType.ERROR, "Erreur",
                    "Impossible de charger la page panier: " + e.getMessage());
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

    public void profil(ActionEvent actionEvent) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/profilp.fxml"));
            Parent profilPage = loader.load();

            // Get the current scene and set the profil page
            productContainer.getScene().setRoot(profilPage);

        } catch (IOException e) {
            showAlert(AlertType.ERROR, "Erreur",
                    "Impossible de charger la page profil: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public void openRec(ActionEvent actionEvent) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/AjouterReclamation.fxml"));
            Parent profilPage = loader.load();

            // Get the current scene and set the profil page
            productContainer.getScene().setRoot(profilPage);

        } catch (IOException e) {
            showAlert(AlertType.ERROR, "Erreur",
                    "Impossible de charger la page profil: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public void openArticle(ActionEvent actionEvent) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/AfficheArticleFront.fxml"));
            Parent profilPage = loader.load();

            // Get the current scene and set the profil page
            productContainer.getScene().setRoot(profilPage);

        } catch (IOException e) {
            showAlert(AlertType.ERROR, "Erreur",
                    "Impossible de charger la page profil: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public void openAtelier(ActionEvent actionEvent) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/frontWorkshop.fxml"));
            Parent profilPage = loader.load();

            // Get the current scene and set the profil page
            productContainer.getScene().setRoot(profilPage);

        } catch (IOException e) {
            showAlert(AlertType.ERROR, "Erreur",
                    "Impossible de charger la page profil: " + e.getMessage());
            e.printStackTrace();
        }
    }
}