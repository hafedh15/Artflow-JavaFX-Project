package tn.esprit.Contollers;

import javafx.animation.PauseTransition;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.scene.Parent;
import javafx.scene.control.*;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.layout.HBox;
import javafx.scene.layout.TilePane;
import javafx.scene.layout.VBox;
import javafx.util.Duration;
import tn.esprit.entities.Product;
import tn.esprit.services.ProductService;

import java.io.IOException;
import java.net.URL;
import java.sql.SQLException;
import java.util.List;
import java.util.ResourceBundle;

public class ListProductFront implements Initializable {

    @FXML
    private TilePane productContainer;

    @FXML
    private TextField searchField;
    @FXML
    private VBox categoryBox;

    private ToggleGroup categoryGroup = new ToggleGroup();
    @FXML
    private HBox categoryBoxx;

    // Ajoutez ce sélecteur dans votre fichier FXML
    @FXML
    private ComboBox<String> languageSelector;
    @FXML
    private TextField minPriceField;

    @FXML
    private TextField maxPriceField;


    private ProductService productService;
    private PauseTransition searchDebouncer = new PauseTransition(Duration.millis(300));

    // Variable pour stocker la catégorie sélectionnée
    private String selectedCategory = "all";


    private void loadCategoriesFromDatabase() {
        try {
            List<String> categories = productService.getAllCategories(); // Tu dois créer cette méthode dans ProductService

            categoryBox.getChildren().clear(); // Nettoyer les anciennes catégories

            // Ajouter le bouton "All Categories"
            RadioButton allButton = new RadioButton("All Categories");
            allButton.setToggleGroup(categoryGroup);
            allButton.setSelected(true);
            categoryBox.getChildren().add(allButton);

            // Ajouter les autres catégories
            for (String category : categories) {
                int count = productService.getProductCountByCategory(category); // Optionnel, si tu veux afficher "(n)"
                RadioButton button = new RadioButton(category + " (" + count + ")");
                button.setToggleGroup(categoryGroup);
                categoryBox.getChildren().add(button);
            }

        } catch (SQLException e) {
            e.printStackTrace();
            showAlert(AlertType.ERROR, "Erreur", "Impossible de charger les catégories : " + e.getMessage());
        }
    }
    private void loadCategoryButtons() {
        try {
            List<String> categories = productService.getAllCategories(); // Méthode existante

            categoryBoxx.getChildren().clear();

            for (String category : categories) {
                Button button = new Button(category);
                button.getStyleClass().add("category-button");

                // Action sur clic : filtrer les produits par cette catégorie
                button.setOnAction(event -> {
                    selectedCategory = category;
                    try {
                        filterProductsByCategory(category);
                    } catch (SQLException e) {
                        showAlert(AlertType.ERROR, "Erreur de filtre",
                                "Impossible de filtrer les produits par catégorie : " + e.getMessage());
                        e.printStackTrace();
                    }
                });

                categoryBoxx.getChildren().add(button);
            }

        } catch (SQLException e) {
            e.printStackTrace();
            showAlert(AlertType.ERROR, "Erreur", "Impossible de charger les boutons de catégories : " + e.getMessage());
        }
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        productService = new ProductService();

        loadCategoriesFromDatabase();
        loadCategoryButtons();


        Platform.runLater(() -> {
            String cssFile = getClass().getResource("/styles.css").toExternalForm();
            productContainer.getScene().getStylesheets().add(cssFile);
        });

        // Initialize the debouncer for search
        searchDebouncer = new PauseTransition(Duration.millis(300));

        // Add text change listener to search field with debounce
        searchField.textProperty().addListener((observable, oldValue, newValue) -> {
            searchDebouncer.setOnFinished(event -> {
                try {
                    if (newValue.isEmpty()) {
                        if ("all".equals(selectedCategory)) {
                            loadProducts();
                        } else {
                            filterProductsByCategory(selectedCategory);
                        }
                    } else if (newValue.length() >= 2) {
                        searchProducts(newValue);
                    }
                } catch (SQLException e) {
                    e.printStackTrace();
                    showAlert(AlertType.ERROR, "Erreur de recherche",
                            "Une erreur s'est produite lors de la recherche: " + e.getMessage());
                }
            });
            searchDebouncer.playFromStart();
        });

        // Add listener for category selection
        categoryGroup.selectedToggleProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue != null) {
                RadioButton selectedRadioButton = (RadioButton) newValue;
                String category = extractCategoryName(selectedRadioButton.getText());
                selectedCategory = category;

                try {
                    if ("all".equals(category)) {
                        loadProducts();
                    } else {
                        filterProductsByCategory(category);
                    }
                } catch (SQLException e) {
                    e.printStackTrace();
                    showAlert(AlertType.ERROR, "Erreur de filtre",
                            "Une erreur s'est produite lors du filtrage par catégorie: " + e.getMessage());
                }
            }
        });

        loadProducts();
    }

    // Extraire le nom de la catégorie du texte du RadioButton (ex: "accessories (1)" -> "accessories")
    private String extractCategoryName(String buttonText) {
        if (buttonText.startsWith("All")) {
            return "all";
        }

        int parenIndex = buttonText.indexOf('(');
        if (parenIndex > 0) {
            return buttonText.substring(0, parenIndex).trim();
        }
        return buttonText.trim();
    }

    private void filterProductsByCategory(String category) throws SQLException {
        List<Product> products;

        if ("all".equals(category)) {
            products = productService.recuperer();
        } else {
            products = productService.getProductsByCategory(category);
        }

        displayProducts(products);
    }

    private void loadProducts() {
        try {
            // Récupérer tous les produits
            List<Product> products = productService.recupererDispo();

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

    @FXML
    public void applyPriceFilter(ActionEvent actionEvent) {
        try {
            double minPrice = Double.parseDouble(minPriceField.getText());
            double maxPrice = Double.parseDouble(maxPriceField.getText());

            List<Product> products = productService.getProductsByPriceRange(minPrice, maxPrice);
            displayProducts(products);
        } catch (NumberFormatException e) {
            showAlert(AlertType.ERROR, "Erreur de saisie",
                    "Veuillez entrer des valeurs numériques valides pour les prix.");
        } catch (SQLException e) {
            e.printStackTrace();
            showAlert(AlertType.ERROR, "Erreur de filtrage",
                    "Une erreur s'est produite lors du filtrage par prix: " + e.getMessage());
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
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/profil.fxml"));
            Parent profilPage = loader.load();

            // Get the current scene and set the profil page
            productContainer.getScene().setRoot(profilPage);

        } catch (IOException e) {
            showAlert(AlertType.ERROR, "Erreur",
                    "Impossible de charger la page profil: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void displayProducts(List<Product> products) {
        // Clear existing products
        productContainer.getChildren().clear();

        for (Product product : products) {
            try {
                // Make sure this path is correct
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/ProductCard.fxml"));
                Parent productCard = loader.load();

                // Get controller and set product
                ProductCard productCardController = loader.getController();
                productCardController.setProduct(product);
                productCardController.setParentController(this);

                // Add the product card to the container
                productContainer.getChildren().add(productCard);
            } catch (IOException e) {
                e.printStackTrace();
                showAlert(AlertType.ERROR, "Erreur",
                        "Impossible de charger la carte produit: " + e.getMessage());
            }
        }
    }

    private void searchProducts(String searchTerm) throws SQLException {
        // Rechercher les produits correspondant au terme
        List<Product> products = productService.searchProducts(searchTerm);

        // Afficher les produits trouvés
        displayProducts(products);
    }

    public void onSearchButtonClick(ActionEvent actionEvent) {
        try {
            String searchTerm = searchField.getText();
            if (searchTerm == null || searchTerm.isEmpty()) {
                loadProducts(); // Load all products if search field is empty
            } else {
                searchProducts(searchTerm);
            }
        } catch (SQLException e) {
            e.printStackTrace();
            showAlert(AlertType.ERROR, "Erreur de recherche",
                    "Une erreur s'est produite lors de la recherche: " + e.getMessage());
        }
    }


}