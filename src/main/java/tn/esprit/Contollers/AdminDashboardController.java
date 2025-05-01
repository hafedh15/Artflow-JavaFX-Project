package tn.esprit.Contollers;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;
import tn.esprit.entities.Product;
import tn.esprit.services.EmailService;
import tn.esprit.services.ProductService;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

import javafx.scene.layout.Priority;
import tn.esprit.tools.MyDataBase;

public class AdminDashboardController implements Initializable {
    @FXML
    private Button addProductButton;

    // Ajoutez ces attributs FXML
    @FXML
    private Button productsButton;

    @FXML
    private StackPane productNotificationBadge;
    @FXML
    private EmailService emailService;
    @FXML
    private Label productNotificationCount;
    @FXML
    private FlowPane productsContainer; // Correspond à l'id du FlowPane dans le FXML

    @FXML
    private Label totalProductsLabel; // Label pour afficher le nombre total de produits

    @FXML
    private Label inStockLabel; // Label pour afficher le nombre de produits en stock

    @FXML
    private FlowPane productContainer;

    @FXML
    private Label outOfStockLabel; // Label pour afficher le nombre de produits hors stock

    private ProductService productService;

    @FXML
    private Label pendingProductsLabel;

    // Ajoutez cette classe interne pour stocker les informations des produits en attente
    private static class PendingProduct {
        private int id;
        private String name;
        private String userName;

        public PendingProduct(int id, String name, String userName) {
            this.id = id;
            this.name = name;
            this.userName = userName;
        }

        public int getId() { return id; }
        public String getName() { return name; }
        public String getUserName() { return userName; }
    }

    // Ajoutez cette variable pour stocker la liste des produits en attente
    private List<PendingProduct> pendingProducts = new ArrayList<>();


    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        productService = new ProductService();

        // Reportez le chargement du CSS après que la scène soit disponible
        Platform.runLater(() -> {
            try {
                // Assurez-vous que l'élément est attaché à une scène avant d'essayer d'accéder à celle-ci
                if (productsContainer != null && productsContainer.getScene() != null) {
                    String cssFile = getClass().getResource("/styles.css").toExternalForm();
                    productsContainer.getScene().getStylesheets().add(cssFile);
                } else {
                    System.err.println("productsContainer est null ou n'est pas encore attaché à une scène");
                }
            } catch (Exception e) {
                System.err.println("Erreur lors du chargement du CSS: " + e.getMessage());
            }

            // Maintenant que l'interface est initialisée, chargez les produits
            loadProducts();
            updateNotificationBadge();
            // Mettre à jour les statistiques
            updateStatistics();
        });
    }
    // Méthode pour mettre à jour le badge de notification
    private void updateNotificationBadge() {
        try {
            // Récupérer le nombre de produits en attente
            int pendingCount = productService.getTotalProductsPending();

            // Mettre à jour le texte du compteur
            productNotificationCount.setText(String.valueOf(pendingCount));

            // Afficher ou masquer le badge selon qu'il y a des notifications ou non
            productNotificationBadge.setVisible(pendingCount > 0);
            productNotificationBadge.setManaged(pendingCount > 0);
        } catch (SQLException e) {
            System.err.println("Erreur lors de la mise à jour du badge de notification: " + e.getMessage());
            e.printStackTrace();
        }
    }

    // Créer un élément de liste pour un produit
    private HBox createProductListItem(Product product) {
        // Créer un conteneur pour l'élément
        HBox productItem = new HBox(15);
        productItem.setPadding(new Insets(10));
        productItem.setStyle("-fx-background-color: white; -fx-border-color: #E8DECD; -fx-border-radius: 5; " +
                "-fx-background-radius: 5; -fx-effect: dropshadow(gaussian, rgba(0,0,0,0.05), 3, 0, 0, 1);");

        // Image du produit
        ImageView productImage = new ImageView();
        productImage.setFitHeight(60);
        productImage.setFitWidth(60);
        productImage.setPreserveRatio(true);

        // Charger l'image du produit
        try {
            if (product.getImage() != null && !product.getImage().isEmpty()) {
                String absolutePath = "C:/xampp/htdocs" + product.getImage();
                File file = new File(absolutePath);
                if (file.exists()) {
                    Image image = new Image(file.toURI().toString());
                    productImage.setImage(image);
                } else {
                    Image defaultImage = new Image(getClass().getResourceAsStream("/images/product.jpg"));
                    productImage.setImage(defaultImage);
                }
            } else {
                Image defaultImage = new Image(getClass().getResourceAsStream("/images/product.jpg"));
                productImage.setImage(defaultImage);
            }
        } catch (Exception e) {
            try {
                Image defaultImage = new Image(getClass().getResourceAsStream("/images/product.jpg"));
                productImage.setImage(defaultImage);
            } catch (Exception ex) {
                System.err.println("Impossible de charger l'image par défaut: " + ex.getMessage());
            }
        }

        // Informations du produit
        VBox infoContainer = new VBox(5);
        infoContainer.setAlignment(Pos.CENTER_LEFT);
        //infoContainer.setCursor(Priority.ALWAYS);

        Label nameLabel = new Label(product.getName());
        nameLabel.setStyle("-fx-font-size: 14px; -fx-font-weight: bold; -fx-text-fill: #5C4F3D;");

        Label priceLabel = new Label(product.getPrice() + " TND");
        priceLabel.setStyle("-fx-font-size: 12px; -fx-text-fill: #8D7B6A;");

        Label categoryLabel = new Label(product.getCategory());
        categoryLabel.setStyle("-fx-font-size: 12px; -fx-text-fill: #8D7B6A;");

        infoContainer.getChildren().addAll(nameLabel, priceLabel, categoryLabel);

        // Boutons d'action
        HBox buttonsContainer = new HBox(5);
        buttonsContainer.setAlignment(Pos.CENTER_RIGHT);

        Button viewButton = new Button("Voir");
        viewButton.setStyle("-fx-background-color: #F5EEE6; -fx-text-fill: #7D6E5B; -fx-background-radius: 5; -fx-padding: 5 10;");
        viewButton.setOnAction(e -> showProductDetails(product));

        Button approveButton = new Button("Accepter");
        approveButton.setStyle("-fx-background-color: #A5D6A7; -fx-text-fill: white; -fx-background-radius: 5; -fx-padding: 5 10;");
        approveButton.setOnAction(e -> {
            handleProductApproval(product, true);
            productItem.setVisible(false);
            productItem.setManaged(false);
        });

        Button rejectButton = new Button("Refuser");
        rejectButton.setStyle("-fx-background-color: #EF9A9A; -fx-text-fill: white; -fx-background-radius: 5; -fx-padding: 5 10;");
        rejectButton.setOnAction(e -> {
            handleProductApproval(product, false);
            productItem.setVisible(false);
            productItem.setManaged(false);
        });

        buttonsContainer.getChildren().addAll(viewButton, approveButton, rejectButton);

        // Ajouter tous les éléments à l'item
        productItem.getChildren().addAll(productImage, infoContainer, buttonsContainer);
        HBox.setHgrow(infoContainer, Priority.ALWAYS);

        return productItem;
    }

    // Méthode pour afficher les détails d'un produit
    private void showProductDetails(Product product) {
        // Réutilisez votre code existant pour afficher les détails d'un produit
        // Ou adaptez-le pour montrer une fenêtre plus simple
        Stage dialog = new Stage();
        dialog.initModality(javafx.stage.Modality.APPLICATION_MODAL);
        dialog.setTitle("Détails du produit");

        VBox detailsContainer = new VBox(15);
        detailsContainer.setPadding(new javafx.geometry.Insets(20));
        detailsContainer.setAlignment(javafx.geometry.Pos.CENTER);

        // Image du produit
        ImageView productImage = new ImageView();
        productImage.setFitHeight(150);
        productImage.setFitWidth(150);
        productImage.setPreserveRatio(true);

        // Charger l'image comme avant
        try {
            if (product.getImage() != null && !product.getImage().isEmpty()) {
                String absolutePath = "C:/xampp/htdocs" + product.getImage();
                File file = new File(absolutePath);
                if (file.exists()) {
                    Image image = new Image(file.toURI().toString());
                    productImage.setImage(image);
                } else {
                    Image defaultImage = new Image(getClass().getResourceAsStream("/images/product.jpg"));
                    productImage.setImage(defaultImage);
                }
            } else {
                Image defaultImage = new Image(getClass().getResourceAsStream("/images/product.jpg"));
                productImage.setImage(defaultImage);
            }
        } catch (Exception e) {
            try {
                Image defaultImage = new Image(getClass().getResourceAsStream("/images/product.jpg"));
                productImage.setImage(defaultImage);
            } catch (Exception ex) {
                System.err.println("Impossible de charger l'image par défaut: " + ex.getMessage());
            }
        }

        // Infos du produit
        Label nameLabel = new Label(product.getName());
        nameLabel.setStyle("-fx-font-size: 18px; -fx-font-weight: bold; -fx-text-fill: #5C4F3D;");

        Label descriptionLabel = new Label("Description: " + product.getDescription());
        descriptionLabel.setStyle("-fx-font-size: 14px; -fx-text-fill: #8D7B6A;");
        descriptionLabel.setWrapText(true);

        Label categoryLabel = new Label("Catégorie: " + product.getCategory());
        categoryLabel.setStyle("-fx-font-size: 14px; -fx-text-fill: #8D7B6A;");

        Label priceLabel = new Label("Prix: " + product.getPrice() + " TND");
        priceLabel.setStyle("-fx-font-size: 14px; -fx-text-fill: #8D7B6A;");

        Label stockLabel = new Label("Stock: " + product.getStock());
        stockLabel.setStyle("-fx-font-size: 14px; -fx-text-fill: #8D7B6A;");

        // Boutons d'action
        HBox buttonsContainer = new HBox(15);
        buttonsContainer.setAlignment(javafx.geometry.Pos.CENTER);

        Button approveButton = new Button("Accepter");
        approveButton.setStyle("-fx-background-color: #A5D6A7; -fx-text-fill: white; -fx-background-radius: 5; -fx-padding: 10 20;");
        approveButton.setOnAction(e -> {
            handleProductApproval(product, true);
            dialog.close();
        });

        Button rejectButton = new Button("Refuser");
        rejectButton.setStyle("-fx-background-color: #EF9A9A; -fx-text-fill: white; -fx-background-radius: 5; -fx-padding: 10 20;");
        rejectButton.setOnAction(e -> {
            handleProductApproval(product, false);
            dialog.close();
        });

        Button closeButton = new Button("Fermer");
        closeButton.setStyle("-fx-background-color: #E5DDD1; -fx-text-fill: #7D6E5B; -fx-background-radius: 5; -fx-padding: 10 20;");
        closeButton.setOnAction(e -> dialog.close());

        buttonsContainer.getChildren().addAll(approveButton, rejectButton, closeButton);

        // Ajouter tous les éléments au conteneur
        detailsContainer.getChildren().addAll(productImage, nameLabel, descriptionLabel,
                categoryLabel, priceLabel, stockLabel, buttonsContainer);

        Scene dialogScene = new Scene(detailsContainer, 400, 500);
        dialog.setScene(dialogScene);
        dialog.show();
    }
    // Add this method to your AdminDashboardController class
    @FXML
    private void handleProductsButtonClick(ActionEvent event) {
        try {
            // Charger les produits en attente
            List<Product> pendingProducts = productService.getattenteeProducts();

            if (pendingProducts.isEmpty()) {
                showAlert(AlertType.INFORMATION, "Information", "Aucun produit en attente d'approbation.");
                return;
            }

            // Afficher les produits en attente dans une liste déroulante
            showPendingProductsInScrollableList(pendingProducts);
            // Refresh products list, notification badge, AND statistics
            updateNotificationBadge();
            updateStatistics(); // Add this line to refresh statistics
        } catch (SQLException e) {
            showAlert(AlertType.ERROR, "Erreur",
                    "Impossible de charger les produits en attente: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void showPendingProductsPopup(List<Product> pendingProducts) {
        try {
            // Get the first pending product to display
            if (!pendingProducts.isEmpty()) {
                showProductApprovalDialog(pendingProducts.get(0), pendingProducts, 0);
            }
        } catch (Exception e) {
            showAlert(AlertType.ERROR, "Erreur",
                    "Erreur lors de l'affichage du popup: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void showProductApprovalDialog(Product product, List<Product> allPendingProducts, int currentIndex) {
        // Create the custom dialog
        Stage dialog = new Stage();
        dialog.initModality(javafx.stage.Modality.APPLICATION_MODAL);
        dialog.setTitle("Approbation de produit");

        // Create the layout
        VBox dialogVbox = new VBox(15);
        // Palette beige/gris/orange/crème
        dialogVbox.setStyle("-fx-background-color: #F9F5F0; -fx-background-radius: 10; -fx-border-color: #EAE0D5; " +
                "-fx-border-radius: 10; -fx-border-width: 2; -fx-effect: dropshadow(gaussian, rgba(0,0,0,0.1), 5, 0, 0, 0);");

        dialogVbox.setAlignment(javafx.geometry.Pos.CENTER);
        dialogVbox.setPadding(new javafx.geometry.Insets(20));

        // Product image container with styled background
        StackPane imageContainer = new StackPane();
        imageContainer.setStyle("-fx-background-color: #F5EEE6; -fx-background-radius: 8; -fx-padding: 10;");
        imageContainer.setPrefWidth(220);
        imageContainer.setPrefHeight(170);

        // Product image
        ImageView productImage = new ImageView();
        productImage.setFitHeight(150);
        productImage.setFitWidth(150);
        productImage.setPreserveRatio(true);

        // Load product image
        try {
            if (product.getImage() != null && !product.getImage().isEmpty()) {
                String absolutePath = "C:/xampp/htdocs" + product.getImage();
                File file = new File(absolutePath);
                if (file.exists()) {
                    Image image = new Image(file.toURI().toString());
                    productImage.setImage(image);
                } else {
                    Image defaultImage = new Image(getClass().getResourceAsStream("/images/product.jpg"));
                    productImage.setImage(defaultImage);
                }
            } else {
                Image defaultImage = new Image(getClass().getResourceAsStream("/images/product.jpg"));
                productImage.setImage(defaultImage);
            }
        } catch (Exception e) {
            System.err.println("Impossible de charger l'image: " + e.getMessage());
            try {
                Image defaultImage = new Image(getClass().getResourceAsStream("/images/product.jpg"));
                productImage.setImage(defaultImage);
            } catch (Exception ex) {
                System.err.println("Impossible de charger l'image par défaut: " + ex.getMessage());
            }
        }

        imageContainer.getChildren().add(productImage);

        // Product details - Title with elegant styling
        Label titleLabel = new Label("Produit en attente d'approbation");
        titleLabel.setStyle("-fx-font-size: 18px; -fx-font-weight: bold; -fx-text-fill: #5C4F3D; -fx-padding: 0 0 10 0; " +
                "-fx-border-color: transparent transparent #EAE0D5 transparent; -fx-border-width: 0 0 1 0; -fx-padding: 0 0 8 0;");

        // Info container
        VBox infoContainer = new VBox(10);
        infoContainer.setStyle("-fx-background-color: white; -fx-background-radius: 8; -fx-padding: 15; " +
                "-fx-border-color: #EAE0D5; -fx-border-radius: 8; -fx-effect: dropshadow(gaussian, rgba(0,0,0,0.05), 3, 0, 0, 1);");

        Label nameLabel = new Label("Nom: " + product.getName());
        nameLabel.setStyle("-fx-font-size: 14px; -fx-font-weight: bold; -fx-text-fill: #5C4F3D;");

        Label descriptionLabel = new Label("Description: " + product.getDescription());
        descriptionLabel.setStyle("-fx-font-size: 14px; -fx-text-fill: #7D7168; -fx-wrap-text: true;");
        descriptionLabel.setWrapText(true);
        descriptionLabel.setMaxWidth(350);

        Label categoryLabel = new Label("Catégorie: " + product.getCategory());
        categoryLabel.setStyle("-fx-font-size: 14px; -fx-text-fill: #7D7168;");

        Label priceLabel = new Label("Prix: " + product.getPrice() + " TND");
        priceLabel.setStyle("-fx-font-size: 14px; -fx-text-fill: #E76F51; -fx-font-weight: bold;");

        Label stockLabel = new Label("Stock: " + product.getStock());
        stockLabel.setStyle("-fx-font-size: 14px; -fx-text-fill: #7D7168;");

        Label userLabel = new Label("Vendeur: " + product.getName());
        userLabel.setStyle("-fx-font-size: 14px; -fx-text-fill: #7D7168;");

        infoContainer.getChildren().addAll(nameLabel, descriptionLabel, categoryLabel, priceLabel, stockLabel, userLabel);

        // Progress indicator with nice styling
        HBox progressContainer = new HBox();
        progressContainer.setAlignment(javafx.geometry.Pos.CENTER);
        progressContainer.setStyle("-fx-padding: 10 0;");

        Label progressLabel = new Label((currentIndex + 1) + " / " + allPendingProducts.size());
        progressLabel.setStyle("-fx-font-size: 14px; -fx-text-fill: #A8A29E; -fx-background-color: #F5EEE6; " +
                "-fx-padding: 5 15; -fx-background-radius: 20;");

        progressContainer.getChildren().add(progressLabel);

        // Buttons
        HBox buttonsBox = new HBox(15);
        buttonsBox.setAlignment(javafx.geometry.Pos.CENTER);
        buttonsBox.setPadding(new javafx.geometry.Insets(10, 0, 0, 0));

        Button approveButton = new Button("Accepter");
        approveButton.setStyle("-fx-background-color: #A5D6A7; -fx-text-fill: white; -fx-background-radius: 5; " +
                "-fx-padding: 10 25; -fx-font-weight: bold; -fx-cursor: hand; -fx-effect: dropshadow(gaussian, rgba(0,0,0,0.1), 2, 0, 0, 1);");

        // Hover effect for approve button
        approveButton.setOnMouseEntered(e ->
                approveButton.setStyle("-fx-background-color: #8BC34A; -fx-text-fill: white; -fx-background-radius: 5; " +
                        "-fx-padding: 10 25; -fx-font-weight: bold; -fx-cursor: hand; -fx-effect: dropshadow(gaussian, rgba(0,0,0,0.2), 3, 0, 0, 1);")
        );

        approveButton.setOnMouseExited(e ->
                approveButton.setStyle("-fx-background-color: #A5D6A7; -fx-text-fill: white; -fx-background-radius: 5; " +
                        "-fx-padding: 10 25; -fx-font-weight: bold; -fx-cursor: hand; -fx-effect: dropshadow(gaussian, rgba(0,0,0,0.1), 2, 0, 0, 1);")
        );

        Button rejectButton = new Button("Refuser");
        rejectButton.setStyle("-fx-background-color: #EF9A9A; -fx-text-fill: white; -fx-background-radius: 5; " +
                "-fx-padding: 10 25; -fx-font-weight: bold; -fx-cursor: hand; -fx-effect: dropshadow(gaussian, rgba(0,0,0,0.1), 2, 0, 0, 1);");

        // Hover effect for reject button
        rejectButton.setOnMouseEntered(e ->
                rejectButton.setStyle("-fx-background-color: #E57373; -fx-text-fill: white; -fx-background-radius: 5; " +
                        "-fx-padding: 10 25; -fx-font-weight: bold; -fx-cursor: hand; -fx-effect: dropshadow(gaussian, rgba(0,0,0,0.2), 3, 0, 0, 1);")
        );

        rejectButton.setOnMouseExited(e ->
                rejectButton.setStyle("-fx-background-color: #EF9A9A; -fx-text-fill: white; -fx-background-radius: 5; " +
                        "-fx-padding: 10 25; -fx-font-weight: bold; -fx-cursor: hand; -fx-effect: dropshadow(gaussian, rgba(0,0,0,0.1), 2, 0, 0, 1);")
        );

        buttonsBox.getChildren().addAll(approveButton, rejectButton);

        // Add components to dialog
        dialogVbox.getChildren().addAll(titleLabel, imageContainer, infoContainer, progressContainer, buttonsBox);

        // Set handlers for approve and reject
        approveButton.setOnAction(e -> {
            handleProductApproval(product, true);
            dialog.close();

            // Show next product if available
            if (currentIndex + 1 < allPendingProducts.size()) {
                showProductApprovalDialog(allPendingProducts.get(currentIndex + 1), allPendingProducts, currentIndex + 1);
            } else {
                showAlert(AlertType.INFORMATION, "Information", "Tous les produits ont été traités.");
                refreshProducts();
            }
        });

        rejectButton.setOnAction(e -> {
            handleProductApproval(product, false);
            dialog.close();

            // Show next product if available
            if (currentIndex + 1 < allPendingProducts.size()) {
                showProductApprovalDialog(allPendingProducts.get(currentIndex + 1), allPendingProducts, currentIndex + 1);
            } else {
                showAlert(AlertType.INFORMATION, "Information", "Tous les produits ont été traités.");
                refreshProducts();
            }
        });

        Scene dialogScene = new Scene(dialogVbox, 400, 650);
        dialog.setScene(dialogScene);
        dialog.show();
    }

    private void handleProductApproval(Product product, boolean isApproved) {
        try {
            ProductService productService = new ProductService(); // Or use your existing instance
            String userEmail = productService.getUserEmailByProductId(product.getId());
            String artisanName = productService.getArtisanNameByProductId(product.getId()); // Implémentez cette méthode
            if (isApproved) {
                productService.updateProductStatus(product.getId(), "dispo");
                // Afficher une alerte de succès avec du style
                Alert alert = new Alert(AlertType.INFORMATION);
                alert.setTitle("Succès");
                alert.setHeaderText(null);
                alert.setContentText("Le produit \"" + product.getName() + "\" a été approuvé avec succès.");

                // Personnaliser le style de l'alerte
                DialogPane dialogPane = alert.getDialogPane();
                dialogPane.setStyle("-fx-background-color: #F9F5F0; -fx-border-color: #EAE0D5; -fx-border-width: 2;");

                // Personnaliser les boutons
                Button okButton = (Button) dialogPane.lookupButton(ButtonType.OK);
                okButton.setStyle("-fx-background-color: #A5D6A7; -fx-text-fill: white; -fx-background-radius: 5;");

                // Envoyer un email de confirmation d'acceptation
                if (userEmail != null && !userEmail.isEmpty()) {
                    EmailService emailService = new EmailService();
                    emailService.sendProductApprovalEmail(userEmail, product.getName(), artisanName);
                }
                alert.showAndWait();
            } else {
                productService.updateProductStatus(product.getId(), "refuse");
                // Afficher une alerte de refus avec du style
                Alert alert = new Alert(AlertType.INFORMATION);
                alert.setTitle("Information");
                alert.setHeaderText(null);
                alert.setContentText("Le produit \"" + product.getName() + "\" a été refusé.");

                // Envoyer un email de notification de refus
                if (userEmail != null && !userEmail.isEmpty()) {
                    EmailService emailService = new EmailService();
                    emailService.sendProductRejectionEmail(userEmail, product.getName(), artisanName);
                }
                // Personnaliser le style de l'alerte
                DialogPane dialogPane = alert.getDialogPane();
                dialogPane.setStyle("-fx-background-color: #F9F5F0; -fx-border-color: #EAE0D5; -fx-border-width: 2;");

                // Personnaliser les boutons
                Button okButton = (Button) dialogPane.lookupButton(ButtonType.OK);
                okButton.setStyle("-fx-background-color: #EF9A9A; -fx-text-fill: white; -fx-background-radius: 5;");

                alert.showAndWait();
            }

            // Refresh products list and notification badge
            updateNotificationBadge();

        } catch (SQLException e) {
            showAlert(AlertType.ERROR, "Erreur",
                    "Erreur lors du traitement du produit: " + e.getMessage());
            e.printStackTrace();
        }
    }


    // Méthode modifiée pour afficher les produits en attente dans une liste défilante avec style
    private void showPendingProductsInScrollableList(List<Product> pendingProducts) {
        try {
            // Créer une nouvelle fenêtre
            Stage dialog = new Stage();
            dialog.initModality(javafx.stage.Modality.APPLICATION_MODAL);
            dialog.setTitle("Produits en attente d'approbation");

            // Créer un conteneur principal
            VBox mainContainer = new VBox(15);
            mainContainer.setPadding(new javafx.geometry.Insets(20));
            mainContainer.setAlignment(javafx.geometry.Pos.CENTER);
            mainContainer.setStyle("-fx-background-color: #F9F5F0;");

            // Titre avec style
            Label titleLabel = new Label("Liste des produits en attente");
            titleLabel.setStyle("-fx-font-size: 22px; -fx-font-weight: bold; -fx-text-fill: #5C4F3D; " +
                    "-fx-padding: 5 0 15 0; -fx-border-color: transparent transparent #E76F51 transparent; " +
                    "-fx-border-width: 0 0 2 0; -fx-padding: 0 0 10 0;");

            // Conteneur pour la liste déroulante
            ScrollPane scrollPane = new ScrollPane();
            scrollPane.setFitToWidth(true);
            scrollPane.setStyle("-fx-background-color: transparent; -fx-background: transparent; " +
                    "-fx-border-color: #EAE0D5; -fx-border-radius: 5;");
            scrollPane.setPrefHeight(400);

            // Conteneur pour les produits
            VBox productsContainer = new VBox(10);
            productsContainer.setPadding(new javafx.geometry.Insets(10));
            productsContainer.setStyle("-fx-background-color: transparent;");

            // Ajouter chaque produit à la liste avec un style modernisé
            for (Product product : pendingProducts) {
                HBox productItem = createStyledProductListItem(product);
                productsContainer.getChildren().add(productItem);
            }

            scrollPane.setContent(productsContainer);

            // Ajouter bouton de fermeture avec style
            Button closeButton = new Button("Fermer");
            closeButton.setStyle("-fx-background-color: #D5C7B6; -fx-text-fill: #5C4F3D; -fx-background-radius: 5; " +
                    "-fx-padding: 10 30; -fx-font-weight: bold; -fx-cursor: hand; " +
                    "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.1), 2, 0, 0, 1);");

            // Effet de survol
            closeButton.setOnMouseEntered(e ->
                    closeButton.setStyle("-fx-background-color: #EAE0D5; -fx-text-fill: #5C4F3D; -fx-background-radius: 5; " +
                            "-fx-padding: 10 30; -fx-font-weight: bold; -fx-cursor: hand; " +
                            "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.2), 3, 0, 0, 1);")
            );

            closeButton.setOnMouseExited(e ->
                    closeButton.setStyle("-fx-background-color: #D5C7B6; -fx-text-fill: #5C4F3D; -fx-background-radius: 5; " +
                            "-fx-padding: 10 30; -fx-font-weight: bold; -fx-cursor: hand; " +
                            "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.1), 2, 0, 0, 1);")
            );

            closeButton.setOnAction(e -> dialog.close());

            // Ajouter les éléments au conteneur principal
            mainContainer.getChildren().addAll(titleLabel, scrollPane, closeButton);

            // Configurer la scène
            Scene dialogScene = new Scene(mainContainer, 550, 550);
            dialog.setScene(dialogScene);
            dialog.show();

        } catch (Exception e) {
            showAlert(AlertType.ERROR, "Erreur", "Erreur lors de l'affichage de la liste: " + e.getMessage());
            e.printStackTrace();
        }
    }

    // Nouvelle méthode pour créer un élément de liste de produit avec un style amélioré
    private HBox createStyledProductListItem(Product product) {
        // Créer un conteneur pour l'élément
        HBox productItem = new HBox(15);
        productItem.setPadding(new javafx.geometry.Insets(12));
        productItem.setStyle("-fx-background-color: white; -fx-border-color: #EAE0D5; -fx-border-radius: 8; " +
                "-fx-background-radius: 8; -fx-effect: dropshadow(gaussian, rgba(0,0,0,0.05), 3, 0, 0, 1);");

        // Image du produit avec un container stylisé
        StackPane imageWrapper = new StackPane();
        imageWrapper.setStyle("-fx-background-color: #F5EEE6; -fx-background-radius: 5; -fx-padding: 5;");
        imageWrapper.setPrefWidth(70);
        imageWrapper.setPrefHeight(70);

        ImageView productImage = new ImageView();
        productImage.setFitHeight(60);
        productImage.setFitWidth(60);
        productImage.setPreserveRatio(true);

        // Charger l'image du produit
        try {
            if (product.getImage() != null && !product.getImage().isEmpty()) {
                String absolutePath = "C:/xampp/htdocs" + product.getImage();
                File file = new File(absolutePath);
                if (file.exists()) {
                    Image image = new Image(file.toURI().toString());
                    productImage.setImage(image);
                } else {
                    Image defaultImage = new Image(getClass().getResourceAsStream("/images/product.jpg"));
                    productImage.setImage(defaultImage);
                }
            } else {
                Image defaultImage = new Image(getClass().getResourceAsStream("/images/product.jpg"));
                productImage.setImage(defaultImage);
            }
        } catch (Exception e) {
            try {
                Image defaultImage = new Image(getClass().getResourceAsStream("/images/product.jpg"));
                productImage.setImage(defaultImage);
            } catch (Exception ex) {
                System.err.println("Impossible de charger l'image par défaut: " + ex.getMessage());
            }
        }

        imageWrapper.getChildren().add(productImage);

        // Informations du produit
        VBox infoContainer = new VBox(5);
        infoContainer.setAlignment(javafx.geometry.Pos.CENTER_LEFT);
        javafx.scene.layout.HBox.setHgrow(infoContainer, javafx.scene.layout.Priority.ALWAYS);

        Label nameLabel = new Label(product.getName());
        nameLabel.setStyle("-fx-font-size: 14px; -fx-font-weight: bold; -fx-text-fill: #5C4F3D;");

        Label priceLabel = new Label(product.getPrice() + " TND");
        priceLabel.setStyle("-fx-font-size: 12px; -fx-text-fill: #E76F51; -fx-font-weight: bold;");

        Label categoryLabel = new Label(product.getCategory());
        categoryLabel.setStyle("-fx-font-size: 12px; -fx-text-fill: #7D7168;");

        infoContainer.getChildren().addAll(nameLabel, priceLabel, categoryLabel);

        // Boutons d'action
        HBox buttonsContainer = new HBox(8);
        buttonsContainer.setAlignment(javafx.geometry.Pos.CENTER_RIGHT);

        Button viewButton = new Button("Voir");
        viewButton.setStyle("-fx-background-color: #F5EEE6; -fx-text-fill: #7D6E5B; -fx-background-radius: 5; " +
                "-fx-padding: 5 12; -fx-cursor: hand;");

        // Effet de survol
        viewButton.setOnMouseEntered(e ->
                viewButton.setStyle("-fx-background-color: #EAE0D5; -fx-text-fill: #5C4F3D; -fx-background-radius: 5; " +
                        "-fx-padding: 5 12; -fx-cursor: hand;")
        );

        viewButton.setOnMouseExited(e ->
                viewButton.setStyle("-fx-background-color: #F5EEE6; -fx-text-fill: #7D6E5B; -fx-background-radius: 5; " +
                        "-fx-padding: 5 12; -fx-cursor: hand;")
        );

        viewButton.setOnAction(e -> showProductDetails(product));

        Button approveButton = new Button("Accepter");
        approveButton.setStyle("-fx-background-color: #A5D6A7; -fx-text-fill: white; -fx-background-radius: 5; " +
                "-fx-padding: 5 12; -fx-cursor: hand;");

        // Effet de survol
        approveButton.setOnMouseEntered(e ->
                approveButton.setStyle("-fx-background-color: #8BC34A; -fx-text-fill: white; -fx-background-radius: 5; " +
                        "-fx-padding: 5 12; -fx-cursor: hand;")
        );

        approveButton.setOnMouseExited(e ->
                approveButton.setStyle("-fx-background-color: #A5D6A7; -fx-text-fill: white; -fx-background-radius: 5; " +
                        "-fx-padding: 5 12; -fx-cursor: hand;")
        );

        approveButton.setOnAction(e -> {
            handleProductApproval(product, true);
            productItem.setVisible(false);
            productItem.setManaged(false);
        });

        Button rejectButton = new Button("Refuser");
        rejectButton.setStyle("-fx-background-color: #EF9A9A; -fx-text-fill: white; -fx-background-radius: 5; " +
                "-fx-padding: 5 12; -fx-cursor: hand;");

        // Effet de survol
        rejectButton.setOnMouseEntered(e ->
                rejectButton.setStyle("-fx-background-color: #E57373; -fx-text-fill: white; -fx-background-radius: 5; " +
                        "-fx-padding: 5 12; -fx-cursor: hand;")
        );

        rejectButton.setOnMouseExited(e ->
                rejectButton.setStyle("-fx-background-color: #EF9A9A; -fx-text-fill: white; -fx-background-radius: 5; " +
                        "-fx-padding: 5 12; -fx-cursor: hand;")
        );

        rejectButton.setOnAction(e -> {
            handleProductApproval(product, false);
            productItem.setVisible(false);
            productItem.setManaged(false);
        });

        buttonsContainer.getChildren().addAll(viewButton, approveButton, rejectButton);

        // Ajouter tous les éléments à l'item
        productItem.getChildren().addAll(imageWrapper, infoContainer, buttonsContainer);

        return productItem;
    }
    private void loadProducts() {
        try {
            // Vider le conteneur avant de charger les produits
            productsContainer.getChildren().clear();

            // Récupérer tous les produits
            List<Product> products = productService.recupererDispo();

            // Vérifier si la liste est vide
            if (products.isEmpty()) {
                showEmptyProductsMessage();
                return;
            }

            // Ajouter chaque produit au FlowPane
            for (Product product : products) {
                productsContainer.getChildren().add(createProductCard(product));
            }

        } catch (SQLException e) {
            showAlert(AlertType.ERROR, "Erreur de base de données",
                    "Impossible de charger les produits: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private VBox createProductCard(Product product) {
        try {
            // Créer une carte pour le produit selon le modèle de votre FXML
            VBox productCard = new VBox();
            productCard.setPrefHeight(280);
            productCard.setPrefWidth(220);
            productCard.setStyle("-fx-background-color: white; -fx-border-color: #E8DECD; -fx-border-radius: 10; " +
                    "-fx-background-radius: 10; -fx-effect: dropshadow(gaussian, rgba(0,0,0,0.05), 3, 0, 0, 1);");

            // Image du produit
            StackPane imageContainer = new StackPane();
            imageContainer.setPrefHeight(140);
            imageContainer.setStyle("-fx-background-color: #F9F6F2; -fx-background-radius: 10 10 0 0;");

            ImageView productImage = new ImageView();
            productImage.setFitHeight(120);
            productImage.setFitWidth(120);
            productImage.setPreserveRatio(true);

            // Charger l'image du produit ou utiliser une image par défaut
            try {
                if (product.getImage() != null && !product.getImage().isEmpty()) {
                    String absolutePath = "C:/xampp/htdocs" + product.getImage();
                    File file = new File(absolutePath);
                    if (file.exists()) {
                        Image image = new Image(file.toURI().toString());
                        productImage.setImage(image);
                    } else {
                        System.out.println("Image introuvable : " + absolutePath);
                        Image defaultImage = new Image(getClass().getResourceAsStream("/images/product.jpg"));
                        productImage.setImage(defaultImage);
                    }
                } else {
                    Image defaultImage = new Image(getClass().getResourceAsStream("/images/product.jpg"));
                    productImage.setImage(defaultImage);
                }
            } catch (Exception e) {
                System.err.println("Impossible de charger l'image: " + e.getMessage());
                try {
                    Image defaultImage = new Image(getClass().getResourceAsStream("/images/product.jpg"));
                    productImage.setImage(defaultImage);
                } catch (Exception ex) {
                    System.err.println("Impossible de charger l'image par défaut: " + ex.getMessage());
                }
            }

            imageContainer.getChildren().add(productImage);

            // Informations du produit
            VBox infoContainer = new VBox(5);
            infoContainer.setStyle("-fx-padding: 15;");

            Label nameLabel = new Label(product.getName());
            nameLabel.setStyle("-fx-font-size: 14px; -fx-font-weight: bold; -fx-text-fill: #5C4F3D;");

            Label descriptionLabel = new Label(product.getDescription());
            descriptionLabel.setStyle("-fx-font-size: 12px; -fx-text-fill: #8D7B6A;");

            Label categoryLabel = new Label("Catégorie: " + product.getCategory());
            categoryLabel.setStyle("-fx-font-size: 12px; -fx-text-fill: #8D7B6A;");

            Label stockLabel = new Label("Stock: " + product.getStock());
            stockLabel.setStyle("-fx-font-size: 12px; -fx-text-fill: #8D7B6A;");

            infoContainer.getChildren().addAll(nameLabel, descriptionLabel, categoryLabel, stockLabel);

            // Boutons d'action
            HBox actionContainer = new HBox(10);
            actionContainer.setAlignment(javafx.geometry.Pos.CENTER);
            actionContainer.setStyle("-fx-padding: 0 15 15 15;");

            Button editButton = new Button("Modifier");
            editButton.setStyle("-fx-background-color: #E5DDD1; -fx-text-fill: #7D6E5B; -fx-background-radius: 5; -fx-padding: 5 10;");
            editButton.setOnAction(event -> handleEditProduct(product));

            Button deleteButton = new Button("Supprimer");
            deleteButton.setStyle("-fx-background-color: #F5EEE6; -fx-text-fill: #A18E78; -fx-background-radius: 5; -fx-padding: 5 10;");
            deleteButton.setOnAction(event -> handleDeleteProduct(product));

            actionContainer.getChildren().addAll(editButton, deleteButton);

            // Assembler la carte
            productCard.getChildren().addAll(imageContainer, infoContainer, actionContainer);

            return productCard;

        } catch (Exception e) {
            System.err.println("Erreur lors de la création de la carte produit: " + e.getMessage());
            e.printStackTrace();

            // En cas d'erreur, retourner une carte simplifiée
            VBox errorCard = new VBox();
            errorCard.getChildren().add(new Label("Erreur: " + product.getName()));
            return errorCard;
        }
    }

    private void showEmptyProductsMessage() {
        StackPane emptyMessage = new StackPane();
        emptyMessage.setPrefWidth(700);
        emptyMessage.setPrefHeight(300);

        VBox messageBox = new VBox(10);
        messageBox.setAlignment(javafx.geometry.Pos.CENTER);

        try {
            ImageView iconView = new ImageView(new Image(getClass().getResourceAsStream("/images/product.jpg")));
            iconView.setFitHeight(60);
            iconView.setFitWidth(60);
            iconView.setPreserveRatio(true);

            Label titleLabel = new Label("Aucun produit artisanal disponible");
            titleLabel.setStyle("-fx-font-size: 16px; -fx-text-fill: #8D7B6A;");

            Label subTitleLabel = new Label("Ajoutez votre premier produit pour commencer");
            subTitleLabel.setStyle("-fx-font-size: 14px; -fx-text-fill: #A18E78;");

            messageBox.getChildren().addAll(iconView, titleLabel, subTitleLabel);
        } catch (Exception e) {
            Label errorLabel = new Label("Aucun produit disponible");
            messageBox.getChildren().add(errorLabel);
        }

        emptyMessage.getChildren().add(messageBox);
        productsContainer.getChildren().add(emptyMessage);
    }

    // Méthode pour rafraîchir la liste des produits après une modification
    public void refreshProducts() {
        loadProducts();
        updateStatistics();
        updateNotificationBadge();
    }

    private void updateStatistics() {
        try {
            List<Product> allProducts = productService.recuperer();
            int totalProducts = allProducts.size();
            int inStock = 0;

            // Appel à la nouvelle méthode pour les produits en attente
            int pendingProducts = productService.getTotalProductsPending();

            for (Product product : allProducts) {
                if (product.getStock() > 0) {
                    inStock++;
                }
            }

            // Mettre à jour les labels avec les statistiques
            if (totalProductsLabel != null) {
                totalProductsLabel.setText(String.valueOf(totalProducts));
            }

            if (inStockLabel != null) {
                inStockLabel.setText(String.valueOf(inStock));
            }

            // Mettre à jour le label pour les produits en attente
            if (pendingProductsLabel != null) {
                pendingProductsLabel.setText(String.valueOf(pendingProducts));
            }

        } catch (SQLException e) {
            System.err.println("Erreur lors de la mise à jour des statistiques: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void handleEditProduct(Product product) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/EditProduct.fxml"));
            Parent editProductView = loader.load();

            // Obtenir le contrôleur et passer le produit à modifier
            EditProduct controller = loader.getController();
            controller.setProductData(product);  // Utiliser la méthode correcte setProductData()

            // Créer une nouvelle scène
            Stage stage = new Stage();
            stage.setTitle("Modifier le produit");
            stage.setScene(new Scene(editProductView));

            // Optionnel: définir le comportement après la fermeture
            stage.setOnHidden(e -> refreshProducts());

            // Afficher la fenêtre
            stage.show();

        } catch (IOException e) {
            showAlert(AlertType.ERROR, "Erreur",
                    "Impossible d'ouvrir la page de modification: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void handleDeleteProduct(Product product) {
        try {
            // Demander confirmation
            Alert confirmation = new Alert(AlertType.CONFIRMATION);
            confirmation.setTitle("Confirmation de suppression");
            confirmation.setHeaderText("Supprimer le produit");
            confirmation.setContentText("Êtes-vous sûr de vouloir supprimer le produit "
                    + product.getName() + " ?");

            confirmation.showAndWait().ifPresent(response -> {
                if (response == javafx.scene.control.ButtonType.OK) {
                    try {
                        // Supprimer le produit
                        productService.supprimer(product);

                        // Rafraîchir l'affichage
                        refreshProducts();

                        // Confirmer la suppression
                        showAlert(AlertType.INFORMATION, "Succès",
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
                    "Une erreur est survenue: " + e.getMessage());
            e.printStackTrace();
        }
    }

    @FXML
    private void handleViewProductList() {
        try {
            // Charger la vue de liste des produits
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/ListProductFront.fxml"));
            Parent listView = loader.load();

            // Remplacer la scène actuelle ou ouvrir dans une nouvelle fenêtre
            Scene currentScene = productsContainer.getScene();
            currentScene.setRoot(listView);

        } catch (IOException e) {
            showAlert(AlertType.ERROR, "Erreur",
                    "Impossible d'ouvrir la liste des produits: " + e.getMessage());
            e.printStackTrace();
        }
    }

    @FXML
    private void handleViewStatistics() {
        // Implémentez la logique pour afficher les statistiques détaillées si nécessaire
        showAlert(AlertType.INFORMATION, "Statistiques",
                "Fonctionnalité de statistiques détaillées à venir.");
    }

    private void showAlert(AlertType type, String title, String message) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    public void ajouterProduit(ActionEvent actionEvent) {

    }

    public void handleOrderButtonAction(ActionEvent actionEvent) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/OrdersDashboard.fxml"));
            Parent root = loader.load();

            // Obtenir la scène actuelle et la remplacer par la scène d'ajout
            Stage stage = (Stage) ((Node) actionEvent.getSource()).getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.setTitle("Ajouter un produit");
            stage.show();

        } catch (IOException e) {
            e.printStackTrace();
            showAlert(AlertType.ERROR, "Erreur", "Impossible de charger la page d'ajout de produit : " + e.getMessage());
        }
    }

    @FXML
    private void handleAddProduct(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/AjouterProduct.fxml"));
            Parent root = loader.load();

            // Obtenir la scène actuelle et la remplacer par la scène d'ajout
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.setTitle("Ajouter un produit");
            stage.show();

        } catch (IOException e) {
            e.printStackTrace();
            showAlert(AlertType.ERROR, "Erreur", "Impossible de charger la page d'ajout de produit : " + e.getMessage());
        }
    }



}