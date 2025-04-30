package tn.artflow.controllers;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;
import tn.artflow.entities.Product;
import tn.artflow.services.ProductService;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.sql.SQLException;
import java.util.List;
import java.util.ResourceBundle;

public class AdminDashboardController implements Initializable {
    @FXML
    private Button addProductButton;

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
    private Button GoToArticle;
    @FXML
    private Button GoToUser;
    @FXML
    private Button GoToAtelier;

    @FXML
    private Button GoToReservation;

    @FXML
    private Button GoToComment;

    @FXML
    private Button GoToReclamation;

    @FXML
    private Button GoToReponse;

    @FXML
    private Button orderButton;


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

            // Mettre à jour les statistiques
            updateStatistics();
        });
    }

    private void loadProducts() {
        try {
            // Vider le conteneur avant de charger les produits
            productsContainer.getChildren().clear();

            // Récupérer tous les produits
            List<Product> products = productService.recuperer();

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
    }

    // Méthode pour mettre à jour les statistiques dans l'interface
    private void updateStatistics() {
        try {
            List<Product> allProducts = productService.recuperer();
            int totalProducts = allProducts.size();
            int inStock = 0;


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

    @FXML
    void goToArticle(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/AfficherArticle.fxml"));
            Parent root = loader.load();
            GoToArticle.getScene().setRoot(root);  // Même principe ici
        } catch (IOException e) {

            e.printStackTrace();
        }
    }

    @FXML
    void goToReservation(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/dashReservation.fxml"));
            Parent root = loader.load();
            GoToArticle.getScene().setRoot(root);  // Même principe ici
        } catch (IOException e) {

            e.printStackTrace();
        }
    }
    @FXML
    void goToAtelier(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/dashWorkshop.fxml"));
            Parent root = loader.load();
            GoToArticle.getScene().setRoot(root);  // Même principe ici
        } catch (IOException e) {

            e.printStackTrace();
        }
    }
    @FXML
    void goToComment(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/AfficheComment.fxml"));
            Parent root = loader.load();
            GoToArticle.getScene().setRoot(root);  // Même principe ici
        } catch (IOException e) {

            e.printStackTrace();
        }
    }
    @FXML
    void goToReclamation(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/AfficherReclamation.fxml"));
            Parent root = loader.load();
            GoToArticle.getScene().setRoot(root);  // Même principe ici
        } catch (IOException e) {

            e.printStackTrace();
        }
    }
    @FXML
    void goToReponse(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/conversation.fxml"));
            Parent root = loader.load();
            GoToArticle.getScene().setRoot(root);  // Même principe ici
        } catch (IOException e) {

            e.printStackTrace();
        }
    }

    @FXML
    void goToProduit(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/Dashboard.fxml"));
            Parent root = loader.load();
            GoToArticle.getScene().setRoot(root);  // Même principe ici
        } catch (IOException e) {

            e.printStackTrace();
        }
    }

    @FXML
    void goToUser(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/AfficherUser.fxml"));
            Parent root = loader.load();
            GoToArticle.getScene().setRoot(root);  // Même principe ici
        } catch (IOException e) {

            e.printStackTrace();
        }
    }



}