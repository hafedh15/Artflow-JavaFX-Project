package tn.artflow.controllers;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.TilePane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import tn.artflow.entities.*;
import tn.artflow.services.*;

import java.io.IOException;
import java.net.URL;
import java.sql.SQLException;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

public class CartController implements Initializable {

    @FXML
    private TilePane cartItemsContainer;

    @FXML
    private Label totalLabel;

    @FXML
    private Label subtotalLabel;

    @FXML
    private Label discountLabel;

    @FXML
    private Label totalItemsLabel;

    @FXML
    private TextField promoCodeField;

    @FXML
    private CheckBox selectAllCheckbox;

    @FXML
    private Button checkoutButton;

    private ProductService productService;
    private CartService cartService;
    private List<CartItemController> cartItemControllers = new ArrayList<>();
    private double subtotal = 0.0;
    private double discount = 0.0;
    private double total = 0.0;

    // Adding currentUserId and cartId as class members
    private int currentUserId = 1; // Default value, should be set based on logged-in user
    private int cartId;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        productService = new ProductService();
        cartService = new CartService();

        // Apply CSS when scene is available
        Platform.runLater(() -> {
            if (cartItemsContainer.getScene() != null) {
                String cssFile = getClass().getResource("/styles.css").toExternalForm();
                cartItemsContainer.getScene().getStylesheets().add(cssFile);
            }
        });

        // Add a listener for when the scene becomes available
        cartItemsContainer.sceneProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue != null) {
                String cssFile = getClass().getResource("/styles.css").toExternalForm();
                newValue.getStylesheets().add(cssFile);
            }
        });

        // Get cart for current user
        try {
            Cart userCart = cartService.getPanierParUserId(currentUserId);
            if (userCart != null) {
                cartId = userCart.getId();
                loadCartItems();
            } else {
                // Create a new cart for the user if none exists
                User currentUser = new User();
                currentUser.setId(currentUserId);
                Cart newCart = new Cart();
                newCart.setUser(currentUser);
                newCart.setTotalPrice(0.0); // Initialize with zero
                cartService.ajouter(newCart);
                cartId = newCart.getId();
                showEmptyCart();
            }
        } catch (SQLException e) {
            showAlert(Alert.AlertType.ERROR, "Erreur",
                    "Impossible de récupérer le panier: " + e.getMessage());
            e.printStackTrace();
        }

        // Set up event listeners
        selectAllCheckbox.setOnAction(e -> toggleSelectAll());
    }

    private void showEmptyCart() {
        cartItemsContainer.getChildren().clear();
        Label emptyLabel = new Label("Votre panier est vide");
        emptyLabel.setStyle("-fx-font-size: 16px; -fx-text-fill: #666;");
        cartItemsContainer.getChildren().add(emptyLabel);
        updateOrderSummary();
    }

    public void loadCartItems() {
        try {
            // Clear the container and controller list
            cartItemsContainer.getChildren().clear();
            cartItemControllers.clear();

            // Get cart items from service
            List<Product> cartProducts = cartService.getProductsByCartId(cartId);

            // If cart is empty
            if (cartProducts.isEmpty()) {
                showEmptyCart();
                return;
            }

            // Add products to the cart UI
            for (Product product : cartProducts) {
                try {
                    FXMLLoader loader = new FXMLLoader(getClass().getResource("/CartItem.fxml"));
                    VBox cartItemCard = loader.load();

                    CartItemController controller = loader.getController();
                    controller.setProduct(product, cartId);
                    controller.setParentController(this);
                    cartItemControllers.add(controller);

                    cartItemsContainer.getChildren().add(cartItemCard);
                } catch (IOException e) {
                    System.err.println("Error loading cart item: " + e.getMessage());
                    e.printStackTrace();
                }
            }

            // Update total items label
            if (totalItemsLabel != null) {
                totalItemsLabel.setText(cartProducts.size() + " produit" + (cartProducts.size() > 1 ? "s" : ""));
            }

            updateOrderSummary();

        } catch (SQLException e) {
            showAlert(Alert.AlertType.ERROR, "Erreur",
                    "Impossible de charger les produits du panier: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public void updateOrderSummary() {
        subtotal = 0.0;

        // Calculate subtotal from selected items
        for (CartItemController controller : cartItemControllers) {
            if (controller.isSelected()) {
                Product product = controller.getProduct();
                if (product != null) {
                    subtotal += product.getPrice();
                }
            }
        }

        // Calculate discount (10% for example)
        discount = subtotal * 0.1;
        total = subtotal - discount;

        // Update labels with null checks
        DecimalFormat df = new DecimalFormat("#,##0.00");

        if (subtotalLabel != null) {
            subtotalLabel.setText(df.format(subtotal) + " TND");
        }

        if (discountLabel != null) {
            discountLabel.setText("-" + df.format(discount) + " TND");
        }

        if (totalLabel != null) {
            totalLabel.setText(df.format(total) + " TND");
        }

        // Update cart total price in database
        try {
            Cart cart = cartService.getPanierParUserId(currentUserId);
            if (cart != null) {
                cart.setTotalPrice(total);
                cartService.updateCartTotal(cart);
            }
        } catch (SQLException e) {
            System.err.println("Error updating cart total: " + e.getMessage());
        }
    }

    @FXML
    private void toggleSelectAll() {
        boolean selectAll = selectAllCheckbox.isSelected();

        for (CartItemController controller : cartItemControllers) {
            // Update the CheckBox - this would need a method in CartItemController
            // to set the selected state of the checkbox
            // controller.setSelected(selectAll);
        }

        updateOrderSummary();
    }

    @FXML
    void applyPromoCode(ActionEvent event) {
        String promoCode = promoCodeField.getText().trim();

        if (promoCode.isEmpty()) {
            showAlert(Alert.AlertType.WARNING, "Attention", "Veuillez entrer un code promo");
            return;
        }

        // Implement promo code logic here
        // For demonstration, let's apply an additional 5% discount for any code
        double additionalDiscount = subtotal * 0.05;
        discount += additionalDiscount;
        total = subtotal - discount;

        // Update labels
        DecimalFormat df = new DecimalFormat("#,##0.00");
        discountLabel.setText("-" + df.format(discount) + " TND");
        totalLabel.setText(df.format(total) + " TND");

        showAlert(Alert.AlertType.INFORMATION, "Succès", "Code promo appliqué avec succès!");
    }

    @FXML
    void checkout(ActionEvent event) throws SQLException {
        // Vérifier les produits sélectionnés
        boolean hasSelectedProducts = false;
        for (CartItemController controller : cartItemControllers) {
            if (controller.isSelected()) {
                hasSelectedProducts = true;
                break;
            }
        }
        OrderService orderService = new OrderService();
        if (orderService.isCartAlreadyOrdered(cartId)) {
            showAlert(Alert.AlertType.WARNING, "Commande déjà existante", "Une commande a déjà été passée pour ce panier.");
            return;
        }

        if (!hasSelectedProducts) {
            showAlert(Alert.AlertType.WARNING, "Attention", "Veuillez sélectionner au moins un produit");
            return;
        }

        try {
            // Récupération des produits sélectionnés
            List<Product> selectedProducts = new ArrayList<>();
            double totalAmount = 0.0;

            for (CartItemController controller : cartItemControllers) {
                if (controller.isSelected()) {
                    Product product = controller.getProduct();
                    selectedProducts.add(product);
                    totalAmount += product.getPrice();
                }
            }

            // === Étape importante : Récupérer le panier de l'utilisateur ===
            int currentUserId = 1; // À remplacer par l'utilisateur connecté
            CartService cartService = new CartService();
            Cart cart = cartService.getPanierParUserId(currentUserId);

            if (cart == null) {
                showAlert(Alert.AlertType.ERROR, "Erreur", "Aucun panier trouvé pour cet utilisateur.");
                return;
            }

            int cartId = cart.getId(); // 🎯 Ici tu récupères l'ID du panier
            System.out.println("ID du panier : " + cartId);

          //  int cartId = cart.getId(); // 🎯 Ici tu récupères l'ID du panier

// 💥 Vérifie si une commande existe déjà pour ce panier

            // Créer le résumé de commande sous forme de String
            String orderSummary = buildOrderSummary(selectedProducts, totalAmount);
            System.out.println("Résumé de commande :\n" + orderSummary);

            // === Navigation vers OrderDetails.fxml ===
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/OrderDetails.fxml"));
            Parent root = loader.load();

            // Passer les produits et total à OrderDetails
            OrderDetails controller = loader.getController();
            controller.setOrderSummary(orderSummary); // méthode à créer dans OrderDetails
            controller.setCartId(cartId); // Très bien
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.show();

        } catch (Exception e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Erreur",
                    "Une erreur s'est produite lors du traitement de la commande: " + e.getMessage());
        }
    }

    private String buildOrderSummary(List<Product> products, double total) {
        StringBuilder builder = new StringBuilder();
        for (Product product : products) {
            builder.append("- ").append(product.getName())
                    .append(" : ").append(product.getPrice()).append(" dt\n");
        }
        builder.append("\nTotal : ").append(total).append(" dt");
        return builder.toString();
    }


    @FXML
    void deleteSelectedProducts(ActionEvent event) {
        try {
            List<Integer> productIdsToRemove = new ArrayList<>();

            for (CartItemController controller : cartItemControllers) {
                if (controller.isSelected()) {
                    Product product = controller.getProduct();
                    if (product != null) {
                        productIdsToRemove.add(product.getId());
                    }
                }
            }

            if (productIdsToRemove.isEmpty()) {
                showAlert(Alert.AlertType.WARNING, "Attention", "Veuillez sélectionner au moins un produit");
                return;
            }

            for (Integer productId : productIdsToRemove) {
                cartService.supprimerProduitDuPanier(cartId, productId);
            }

            loadCartItems();
            showAlert(Alert.AlertType.INFORMATION, "Succès", "Produits supprimés du panier");

        } catch (SQLException e) {
            showAlert(Alert.AlertType.ERROR, "Erreur",
                    "Impossible de supprimer les produits: " + e.getMessage());
            e.printStackTrace();
        }
    }

    @FXML
    void goToProductList(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/ListProductFront.fxml"));
            Parent root = loader.load();
            cartItemsContainer.getScene().setRoot(root);
        } catch (IOException e) {
            showAlert(Alert.AlertType.ERROR, "Erreur de navigation",
                    "Impossible de charger la liste des produits: " + e.getMessage());
            e.printStackTrace();
        }
    }

    @FXML
    void goToProfil(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/Profilp.fxml"));
            Parent root = loader.load();
            cartItemsContainer.getScene().setRoot(root);
        } catch (IOException e) {
            showAlert(Alert.AlertType.ERROR, "Erreur de navigation",
                    "Impossible de charger la page profil: " + e.getMessage());
            e.printStackTrace();
        }
    }

    @FXML
    void goToHome(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/Home.fxml"));
            Parent root = loader.load();
            cartItemsContainer.getScene().setRoot(root);
        } catch (IOException e) {
            showAlert(Alert.AlertType.ERROR, "Erreur de navigation",
                    "Impossible de charger la page d'accueil: " + e.getMessage());
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

    @FXML
    public void navigateToWorkshops(ActionEvent actionEvent) {
        // Navigate to workshops page
    }

    @FXML
    public void navigateToComplaints(ActionEvent actionEvent) {
        // Navigate to complaints page
    }

    @FXML
    public void navigateToAbout(ActionEvent actionEvent) {
        // Navigate to about page
    }

    @FXML
    public void addGiftWrapping(ActionEvent actionEvent) {
        // Add gift wrapping logic
    }
}