package tn.artflow.controllers;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.VBox;
import tn.artflow.entities.*;
import tn.artflow.services.*;
import org.controlsfx.control.Rating;

import java.io.*;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class ProductCard {

    @FXML
    private VBox productCard;

    @FXML
    private ImageView productImage;

    @FXML
    private Label productName;

    @FXML
    private Label productCategory;

    @FXML
    private Label productPrice;

    @FXML
    private TextArea productDescription;

    @FXML
    private Label productStock;

    @FXML
    private Rating productRating;

    @FXML
    private Label averageRatingLabel;

    @FXML
    private Button editButton;

    @FXML
    private Button deleteButton;

    private Product product;
    private ProductService productService = new ProductService();
    private ListProductFront parentController;
    private CartService cartService = new CartService();

    public void setProduct(Product product) {
        this.product = product;
        updateCardInfo();
    }

    public void setParentController(ListProductFront parentController) {
        this.parentController = parentController;
    }

    private void updateCardInfo() {
        productCard.setPrefWidth(280);
        productCard.setPrefHeight(380);
        productCard.setMinWidth(280);
        productCard.setMaxWidth(280);
// Ajoutez une marge autour de la carte et une ombre plus prononcée
        productCard.setStyle("-fx-background-color: white; -fx-padding: 15; -fx-spacing: 10; -fx-background-radius: 10; -fx-effect: dropshadow(gaussian, rgba(0,0,0,0.15), 10, 0.3, 0, 3); -fx-border-radius: 10; -fx-margin: 10;");

        productName.setText(product.getName());
        productCategory.setText(product.getCategory());
        productPrice.setText(String.format("%.2f €", product.getPrice()));
        productDescription.setText(product.getDescription());
        productStock.setText("Stock: " + product.getStock());

        // Initialiser le Rating à 0
        productRating.setRating(0);

        // Afficher la moyenne des notes
        updateAverageRating();

        // Écouter les changements de note
        productRating.ratingProperty().addListener((obs, oldValue, newValue) -> {
            try {
                saveRatingAndStockToFile(newValue.intValue());
                updateAverageRating(); // Mettre à jour la moyenne après une nouvelle note
                showAlert(Alert.AlertType.INFORMATION, "Note enregistrée", "La note " + newValue.intValue() + " pour " + product.getName() + " a été sauvegardée.");
            } catch (IOException e) {
                showAlert(Alert.AlertType.ERROR, "Erreur", "Impossible d'enregistrer la note : " + e.getMessage());
                e.printStackTrace();
            }
        });

        // Gestion de l'image
        String imagePath = product.getImage();
        try {
            if (imagePath != null && !imagePath.isEmpty()) {
                String absolutePath = "C:/xampp/htdocs" + imagePath;
                File file = new File(absolutePath);
                if (file.exists()) {
                    Image image = new Image(file.toURI().toString());
                    productImage.setImage(image);
                    productImage.setFitWidth(260);
                    productImage.setFitHeight(160);
                    productImage.setPreserveRatio(true);
                    productImage.setSmooth(true);
                    productImage.setCache(true);
                } else {
                    System.out.println("Image introuvable : " + absolutePath);
                    Image defaultImage = new Image(getClass().getResourceAsStream("/images/default-product.png"));
                    productImage.setImage(defaultImage);
                }
            } else {
                Image defaultImage = new Image(getClass().getResourceAsStream("/images/default-product.png"));
                productImage.setImage(defaultImage);
            }
        } catch (Exception e) {
            e.printStackTrace();
            try {
                Image defaultImage = new Image(getClass().getResourceAsStream("/images/default-product.png"));
                productImage.setImage(defaultImage);
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
    }

    private void updateAverageRating() {
        try {
            double average = calculateAverageRating();
            if (average >= 0) {
                averageRatingLabel.setText(String.format("Moyenne : %.1f/5", average));
            } else {
                averageRatingLabel.setText("Moyenne : -/5");
            }
        } catch (IOException e) {
            averageRatingLabel.setText("Moyenne : Erreur");
            e.printStackTrace();
        }
    }

    private double calculateAverageRating() throws IOException {
        List<Integer> ratings = new ArrayList<>();
        String filePath = new File("src/main/resources/file.txt").getAbsolutePath();
        File file = new File(filePath);

        if (!file.exists()) {
            return -1; // Aucun fichier, pas de moyenne
        }

        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = reader.readLine()) != null) {
                if (line.contains("Produit: " + product.getName())) {
                    String[] parts = line.split(", ");
                    if (parts.length >= 2) {
                        String ratingPart = parts[1].replace("Note: ", "");
                        try {
                            int rating = Integer.parseInt(ratingPart);
                            ratings.add(rating);
                        } catch (NumberFormatException e) {
                            // Ignorer les lignes mal formées
                        }
                    }
                }
            }
        }

        if (ratings.isEmpty()) {
            return -1; // Aucune note pour ce produit
        }

        double sum = 0;
        for (int rating : ratings) {
            sum += rating;
        }
        return sum / ratings.size();
    }

    private void saveRatingAndStockToFile(int rating) throws IOException {
        String filePath = new File("src/main/resources/file.txt").getAbsolutePath();
        try (FileWriter writer = new FileWriter(filePath, true)) { // Mode append
            String line = String.format("Produit: %s, Note: %d, Stock: %d\n",
                    product.getName(), rating, product.getStock());
            writer.write(line);
            writer.flush();
        }
    }

    @FXML
    private void onEditButtonClick() {
        try {
            User user = tn.artflow.utils.UserSession.getInstance().getUser();
           int userId = user.getId();
            int cartId;

            Cart existingCart = cartService.getPanierParUserId(userId);

            if (existingCart == null) {
                Cart newCart = new Cart();
                user.setId(userId);
                newCart.setUser(user);
                newCart.setTotalPrice(0.0);
                cartService.ajouter(newCart);
                cartId = newCart.getId();
            } else {
                cartId = existingCart.getId();
            }

            cartService.ajouterProduitAuPanier(cartId, product.getId());

            showAlert(Alert.AlertType.INFORMATION, "Ajout au panier",
                    "Le produit " + product.getName() + " a été ajouté au panier avec succès.");

        } catch (SQLException e) {
            showAlert(Alert.AlertType.WARNING, "Ajout au panier",
                    "Le produit ne peut pas être ajouté au panier: " + e.getMessage());
            e.printStackTrace();
        } catch (Exception e) {
            showAlert(Alert.AlertType.ERROR, "Erreur",
                    "Une erreur s'est produite lors de l'ajout au panier: " + e.getMessage());
            e.printStackTrace();
        }
    }

    @FXML
    private void onDeleteButtonClick() {
        try {
            Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
            alert.setTitle("Confirmation de suppression");
            alert.setHeaderText(null);
            alert.setContentText("Êtes-vous sûr de vouloir supprimer le produit " + product.getName() + "?");

            alert.showAndWait().ifPresent(response -> {
                if (response == javafx.scene.control.ButtonType.OK) {
                    try {
                        productService.supprimerParId(product.getId());

                        if (parentController != null) {
                            parentController.refreshProducts();
                        }

                        showAlert(Alert.AlertType.INFORMATION, "Suppression réussie",
                                "Le produit a été supprimé avec succès.");
                    } catch (SQLException e) {
                        showAlert(Alert.AlertType.ERROR, "Erreur de suppression",
                                "Impossible de supprimer le produit: " + e.getMessage());
                        e.printStackTrace();
                    }
                }
            });

        } catch (Exception e) {
            showAlert(Alert.AlertType.ERROR, "Erreur",
                    "Une erreur s'est produite: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void showAlert(Alert.AlertType type, String title, String message) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
