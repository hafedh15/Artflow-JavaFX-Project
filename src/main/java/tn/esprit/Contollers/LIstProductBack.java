package tn.esprit.Contollers;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.GridPane;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import tn.esprit.entities.Product;
import tn.esprit.services.ProductService;

import java.io.IOException;
import java.net.URL;
import java.sql.SQLException;
import java.util.List;
import java.util.ResourceBundle;

public class LIstProductBack implements Initializable {

    @FXML
    private GridPane productGrid;

    private ProductService productService;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        productService = new ProductService();
        loadProducts();
    }

    private void loadProducts() {
        try {
            // Récupérer tous les produits
            List<Product> products = productService.recuperer();

            // Nettoyer le grid avant de remplir
            productGrid.getChildren().clear();

            // Ajouter les en-têtes
            addHeaderRow();

            // Ajouter les produits
            int rowIndex = 1; // La première ligne est pour les en-têtes
            for (Product product : products) {
                addProductRow(product, rowIndex++);
            }

        } catch (SQLException e) {
            showAlert(AlertType.ERROR, "Erreur de base de données",
                    "Impossible de charger les produits: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void addHeaderRow() {
        // Ajouter les en-têtes
        productGrid.add(new Label("ID"), 0, 0);
        productGrid.add(new Label("Nom"), 1, 0);
        productGrid.add(new Label("Description"), 2, 0);
        productGrid.add(new Label("Prix"), 3, 0);
        productGrid.add(new Label("Stock"), 4, 0);
        productGrid.add(new Label("Catégorie"), 5, 0);
        productGrid.add(new Label("Image"), 6, 0);
        productGrid.add(new Label("Status"), 7, 0);
        productGrid.add(new Label("User ID"), 8, 0);
        productGrid.add(new Label("Actions"), 9, 0);
    }

    private void addProductRow(Product product, int rowIndex) {
        // Ajouter les informations du produit
        productGrid.add(new Label(String.valueOf(product.getId())), 0, rowIndex);
        productGrid.add(new Label(product.getName()), 1, rowIndex);
        productGrid.add(new Label(product.getDescription()), 2, rowIndex);
        productGrid.add(new Label(String.valueOf(product.getPrice())), 3, rowIndex);
        productGrid.add(new Label(String.valueOf(product.getStock())), 4, rowIndex);
        productGrid.add(new Label(product.getCategory()), 5, rowIndex);
        productGrid.add(new Label(product.getImage()), 6, rowIndex);
        productGrid.add(new Label(product.getStatus()), 7, rowIndex);

        // Afficher l'ID de l'utilisateur s'il existe
        String userId = product.getUser() != null ? String.valueOf(product.getUser().getId()) : "N/A";
        productGrid.add(new Label(userId), 8, rowIndex);

        // Créer les boutons d'action
        Button editButton = new Button("Modifier");
        Button deleteButton = new Button("Supprimer");

        // Configurer les actions des boutons
        editButton.setOnAction(event -> handleEditProduct(product));
        deleteButton.setOnAction(event -> handleDeleteProduct(product));

        // Ajouter les boutons dans une seule cellule
        GridPane actionsPane = new GridPane();
        actionsPane.add(editButton, 0, 0);
        actionsPane.add(deleteButton, 1, 0);
        actionsPane.setHgap(5);

        productGrid.add(actionsPane, 9, rowIndex);
    }

    private void handleEditProduct(Product product) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/EditProduct.fxml"));
            Parent root = loader.load();

            // Use the correct controller class - EditProduct instead of AfficherProduct
            EditProduct controller = loader.getController();

            // Use the setProductData method from the EditProduct controller
            controller.setProductData(product);

            productGrid.getScene().setRoot(root);

        } catch (IOException e) {
            showAlert(AlertType.ERROR, "Erreur",
                    "Impossible de charger la vue de modification: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void handleDeleteProduct(Product product) {
        try {
            // Confirmer la suppression
            boolean confirmed = showConfirmationDialog("Confirmation de suppression",
                    "Êtes-vous sûr de vouloir supprimer le produit " + product.getName() + "?");

            if (confirmed) {
                // Supprimer le produit
                productService.supprimerParId(product.getId());

                // Rafraîchir la liste
                loadProducts();

                showAlert(AlertType.INFORMATION, "Suppression réussie",
                        "Le produit a été supprimé avec succès.");
            }

        } catch (SQLException e) {
            showAlert(AlertType.ERROR, "Erreur de suppression",
                    "Impossible de supprimer le produit: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private boolean showConfirmationDialog(String title, String message) {
        Alert alert = new Alert(AlertType.CONFIRMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);

        return alert.showAndWait().get().getButtonData().isDefaultButton();
    }

    private void showAlert(AlertType type, String title, String message) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}