package tn.esprit.Contollers;

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
import tn.esprit.entities.Cart;
import tn.esprit.entities.Product;
import tn.esprit.entities.User;
import tn.esprit.services.CartService;
import tn.esprit.services.ProductService;

import java.io.File;
import java.io.IOException;
import java.sql.SQLException;

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
    private Button editButton;

    @FXML
    private Button deleteButton;

    private Product product;
    private ProductService productService = new ProductService();
    private ListProductFront parentController;

    public void setProduct(Product product) {
        this.product = product;
        updateCardInfo();
    }



    private void updateCardInfo() {
        productCard.setPrefWidth(280);
        productCard.setPrefHeight(350);
        productCard.setMinWidth(280);
        productCard.setMaxWidth(280);
        productCard.setStyle("-fx-background-color: white; -fx-padding: 10; -fx-spacing: 10; -fx-background-radius: 10; -fx-effect: dropshadow(gaussian, rgba(0,0,0,0.1), 5, 0.2, 0, 2);");

        productName.setText(product.getName());
        productCategory.setText(product.getCategory());
        productPrice.setText(String.format("%.2f €", product.getPrice()));
        productDescription.setText(product.getDescription());
        productStock.setText("Stock: " + product.getStock());

        // 🌟 Modification ici : afficher image depuis htdocs si elle existe
        String imagePath = product.getImage(); // ex: "/images/products/nom_image.jpg"
        try {
            if (imagePath != null && !imagePath.isEmpty()) {
                String absolutePath = "C:/xampp/htdocs" + imagePath; // Chemin complet
                File file = new File(absolutePath);
                if (file.exists()) {
                    Image image = new Image(file.toURI().toString());
                    productImage.setImage(image);
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
    private CartService cartService = new CartService();
    @FXML
    private void onEditButtonClick() {
        try {
            // Utiliser directement l'ID utilisateur 1
            int userId = 1;
            int cartId;

            // Vérifier si l'utilisateur a déjà un panier
            Cart existingCart = cartService.getPanierParUserId(userId);

            if (existingCart == null) {
                // Créer un nouveau panier pour l'utilisateur 1
                Cart newCart = new Cart();
                User user = new User();
                user.setId(userId);
                newCart.setUser(user);
                newCart.setTotalPrice(0.0);
                cartService.ajouter(newCart);
                cartId = newCart.getId();
            } else {
                cartId = existingCart.getId();
            }

            // Ajouter le produit au panier
            cartService.ajouterProduitAuPanier(cartId, product.getId());

            showAlert(AlertType.INFORMATION, "Ajout au panier",
                    "Le produit " + product.getName() + " a été ajouté au panier avec succès.");

        } catch (SQLException e) {
            showAlert(AlertType.WARNING, "Ajout au panier",
                    "Le produit ne peut pas être ajouté au panier: " + e.getMessage());
            e.printStackTrace();
        } catch (Exception e) {
            showAlert(AlertType.ERROR, "Erreur",
                    "Une erreur s'est produite lors de l'ajout au panier: " + e.getMessage());
            e.printStackTrace();
        }
    }
    @FXML
    private void onDeleteButtonClick() {
        try {
            Alert alert = new Alert(AlertType.CONFIRMATION);
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

                        showAlert(AlertType.INFORMATION, "Suppression réussie",
                                "Le produit a été supprimé avec succès.");
                    } catch (SQLException e) {
                        showAlert(AlertType.ERROR, "Erreur de suppression",
                                "Impossible de supprimer le produit: " + e.getMessage());
                        e.printStackTrace();
                    }
                }
            });

        } catch (Exception e) {
            showAlert(AlertType.ERROR, "Erreur",
                    "Une erreur s'est produite: " + e.getMessage());
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

    public void setParentController(ListProductFront listProductFront) {
    }
}
