package tn.esprit.Contollers;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.VBox;
import tn.esprit.entities.Product;
import tn.esprit.services.CartService;

import java.io.File;
import java.io.InputStream;
import java.net.URL;
import java.sql.SQLException;
import java.util.ResourceBundle;

public class CartItemController implements Initializable {
    @FXML
    private VBox cartItemCard;

    @FXML
    private CheckBox selectCheckbox;

    @FXML
    private Button removeButton;

    @FXML
    private ImageView productImage;

    @FXML
    private Label productName;

    @FXML
    private Label productCategory;

    @FXML
    private TextArea productDescription;

    // Removed ComboBox from FXML but keeping reference for backward compatibility
    private ComboBox<Integer> quantityComboBox;

    @FXML
    private Label productPrice;

    private Product product;
    private int cartId;
    private CartController parentController;
    private CartService cartService = new CartService();

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        // No need to initialize quantity combo box as it's removed

        // Set up listeners
        selectCheckbox.setOnAction(e -> {
            if (parentController != null) {
                parentController.updateOrderSummary();
            }
        });
    }

    public void setProduct(Product product, int cartId) {
        this.product = product;
        this.cartId = cartId;

        // Set the product data
        productName.setText(product.getName());
        productCategory.setText(product.getCategory());
        productDescription.setText(product.getDescription());
        productPrice.setText(String.format("%.2f TND", product.getPrice()));

        // Load the product image using the updated method
        loadProductImage();
    }

    public void setParentController(CartController controller) {
        this.parentController = controller;
    }

    private void loadProductImage() {
        try {
            // Get image path from product
            String imagePath = product.getImage();

            if (imagePath != null && !imagePath.isEmpty()) {
                // Use the same path approach as ProductCard
                String absolutePath = "C:/xampp/htdocs" + imagePath;
                File file = new File(absolutePath);

                if (file.exists()) {
                    Image image = new Image(file.toURI().toString());
                    productImage.setImage(image);
                } else {
                    System.out.println("Image introuvable : " + absolutePath);
                    loadDefaultImage();
                }
            } else {
                loadDefaultImage();
            }
        } catch (Exception e) {
            loadDefaultImage();
            System.err.println("Failed to load image: " + e.getMessage());
        }
    }

    private void loadDefaultImage() {
        try {
            InputStream is = getClass().getResourceAsStream("/images/product-placeholder.jpg");
            if (is != null) {
                Image defaultImage = new Image(is);
                productImage.setImage(defaultImage);
            }
        } catch (Exception e) {
            System.err.println("Failed to load default image: " + e.getMessage());
        }
    }

    @FXML
    void removeItem() {
        try {
            cartService.supprimerProduitDuPanier(cartId, product.getId());
            if (parentController != null) {
                parentController.loadCartItems();
            }
            showAlert(Alert.AlertType.INFORMATION, "Succès", "Produit supprimé du panier");
        } catch (SQLException e) {
            showAlert(Alert.AlertType.ERROR, "Erreur",
                    "Impossible de supprimer le produit: " + e.getMessage());
            e.printStackTrace();
        }
    }

    // This method is no longer connected to any UI element but kept for compatibility
    void updateQuantity() {
        // Since we've removed the quantity selection, assume quantity is always 1
        double totalPrice = product.getPrice();
        productPrice.setText(String.format("%.2f TND", totalPrice));

        if (parentController != null) {
            parentController.updateOrderSummary();
        }
    }

    public boolean isSelected() {
        return selectCheckbox.isSelected();
    }

    public Product getProduct() {
        return product;
    }

    private void showAlert(Alert.AlertType type, String title, String message) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}