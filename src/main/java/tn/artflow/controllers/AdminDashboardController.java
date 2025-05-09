package tn.artflow.controllers;
import com.opencsv.CSVWriter;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.CategoryAxis;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.control.*;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.stage.FileChooser;
import javafx.stage.Modality;
import javafx.stage.Stage;

import tn.artflow.entities.Product;
import tn.artflow.services.ProductService;
 import tn.artflow.tools.EmailSender;
//import tn.artflow.services.EmailService;



import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.sql.SQLException;
import java.util.List;
import java.util.ResourceBundle;
import java.util.Map;

public class AdminDashboardController implements Initializable {
    @FXML
    private Button addProductButton;
    @FXML
    private BarChart<String, Number> categoryChart;
    @FXML
    private CategoryAxis xAxis;
    @FXML
    private NumberAxis yAxis;
    @FXML
    private Button productsButton;
    @FXML
    private StackPane productNotificationBadge;
//    @FXML
//    private EmailService emailService;
    @FXML
    private Label productNotificationCount;
    @FXML
    private FlowPane productsContainer;
    @FXML
    private Label totalProductsLabel;
    @FXML
    private Label inStockLabel;
    @FXML
    private FlowPane productContainer;
    @FXML
    private Label outOfStockLabel;
    @FXML
    private Label pendingProductsLabel;
    @FXML
    private Button exportButton;
    @FXML
    private ComboBox<String> exportComboBox;

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


    private ProductService productService;

    private void initializeExportComboBox() {
        try {
            List<String> categories = productService.getAllCategories();
            ObservableList<String> options = FXCollections.observableArrayList();
            options.add("Tous les produits");
            options.add("Toutes les catégories");
            options.addAll(categories);
            exportComboBox.setItems(options);
            exportComboBox.setValue("Tous les produits");
        } catch (SQLException e) {
            showAlert(AlertType.ERROR, "Erreur", "Impossible de charger les catégories : " + e.getMessage());
            e.printStackTrace();
        }
    }

    @FXML
    private void handleExportButtonClick(ActionEvent event) {
        try {
            String selectedOption = exportComboBox.getValue();
            if (selectedOption == null || selectedOption.isEmpty()) {
                showAlert(AlertType.WARNING, "Avertissement", "Veuillez sélectionner une option d'exportation.");
                return;
            }

            FileChooser fileChooser = new FileChooser();
            fileChooser.setTitle("Exporter les produits vers CSV");
            fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Fichiers CSV", "*.csv"));
            fileChooser.setInitialFileName("products_export_" + selectedOption.toLowerCase().replace(" ", "_") + ".csv");

            Stage stage = (Stage) exportButton.getScene().getWindow();
            File file = fileChooser.showSaveDialog(stage);

            if (file != null) {
                productService.exportProductsToCSV(file.getAbsolutePath(), selectedOption);
                showAlert(AlertType.INFORMATION, "Succès", "Produits exportés avec succès vers " + file.getAbsolutePath());
            }
        } catch (SQLException | IOException e) {
            showAlert(AlertType.ERROR, "Erreur", "Impossible d'exporter les produits : " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void loadCategoryChart() {
        try {
            Map<String, Integer> categoryCounts = productService.getProductCountsByCategory();
            XYChart.Series<String, Number> series = new XYChart.Series<>();
            series.setName("Produits Disponibles");

            int index = 0;
            for (Map.Entry<String, Integer> entry : categoryCounts.entrySet()) {
                XYChart.Data<String, Number> data = new XYChart.Data<>(entry.getKey(), entry.getValue());
                int finalIndex = index;
                data.nodeProperty().addListener((obs, oldNode, newNode) -> {
                    if (newNode != null) {
                        newNode.setStyle("-fx-bar-fill: " + getColorForIndex(finalIndex) + ";");
                    }
                });
                series.getData().add(data);
                index++;
            }

            categoryChart.getData().clear();
            categoryChart.getData().add(series);

            categoryChart.setBarGap(2);
            categoryChart.setCategoryGap(10);
            categoryChart.setTitle("Nombre de Produits par Catégorie");
            xAxis.setLabel("Catégorie");
            yAxis.setLabel("Nombre de Produits");
            yAxis.setAutoRanging(true);

        } catch (SQLException e) {
            System.err.println("Erreur lors du chargement du graphique: " + e.getMessage());
            showAlert(AlertType.ERROR, "Erreur", "Impossible de charger les statistiques par catégorie: " + e.getMessage());
        }
    }

    private String getColorForIndex(int index) {
        String[] colors = {
                "#8D7B6A", "#E76F51", "#B0BEC5", "#A18E78", "#F4A261", "#D5D8DC", "#5C4F3D", "#FF8A65"
        };
        return colors[index % colors.length];
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        productService = new ProductService();

        Platform.runLater(() -> {
            try {
                if (productsContainer != null && productsContainer.getScene() != null) {
                    String cssFile = getClass().getResource("/styles.css").toExternalForm();
                    productsContainer.getScene().getStylesheets().add(cssFile);
                } else {
                    System.err.println("productsContainer est null ou n'est pas encore attaché à une scène");
                }
            } catch (Exception e) {
                System.err.println("Erreur lors du chargement du CSS: " + e.getMessage());
            }

            loadProducts();
            updateNotificationBadge();
            updateStatistics();
            loadCategoryChart();
            initializeExportComboBox();
        });
    }

    private void updateNotificationBadge() {
        try {
            int pendingCount = productService.getTotalProductsPending();
            productNotificationCount.setText(String.valueOf(pendingCount));
            productNotificationBadge.setVisible(pendingCount > 0);
            productNotificationBadge.setManaged(pendingCount > 0);
        } catch (SQLException e) {
            System.err.println("Erreur lors de la mise à jour du badge de notification: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private HBox createProductListItem(Product product) {
        HBox productItem = new HBox(15);
        productItem.setPadding(new Insets(10));
        productItem.setStyle("-fx-background-color: white; -fx-border-color: #E8DECD; -fx-border-radius: 5; " +
                "-fx-background-radius: 5; -fx-effect: dropshadow(gaussian, rgba(0,0,0,0.05), 3, 0, 0, 1);");

        ImageView productImage = new ImageView();
        productImage.setFitHeight(60);
        productImage.setFitWidth(60);
        productImage.setPreserveRatio(true);

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

        VBox infoContainer = new VBox(5);
        infoContainer.setAlignment(Pos.CENTER_LEFT);

        Label nameLabel = new Label(product.getName());
        nameLabel.setStyle("-fx-font-size: 14px; -fx-font-weight: bold; -fx-text-fill: #5C4F3D;");

        Label priceLabel = new Label(product.getPrice() + " TND");
        priceLabel.setStyle("-fx-font-size: 12px; -fx-text-fill: #8D7B6A;");

        Label categoryLabel = new Label(product.getCategory());
        categoryLabel.setStyle("-fx-font-size: 12px; -fx-text-fill: #8D7B6A;");

        infoContainer.getChildren().addAll(nameLabel, priceLabel, categoryLabel);

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
            Platform.runLater(() -> {
                try {
                    showPendingProductsInScrollableList(productService.getattenteeProducts());
                } catch (SQLException ex) {
                    showAlert(AlertType.ERROR, "Erreur", "Impossible de recharger la liste: " + ex.getMessage());
                }
            });
        });

        Button rejectButton = new Button("Refuser");
        rejectButton.setStyle("-fx-background-color: #EF9A9A; -fx-text-fill: white; -fx-background-radius: 5; -fx-padding: 5 10;");
        rejectButton.setOnAction(e -> {
            handleProductApproval(product, false);
            productItem.setVisible(false);
            productItem.setManaged(false);
            Platform.runLater(() -> {
                try {
                    showPendingProductsInScrollableList(productService.getattenteeProducts());
                } catch (SQLException ex) {
                    showAlert(AlertType.ERROR, "Erreur", "Impossible de recharger la liste: " + ex.getMessage());
                }
            });
        });

        buttonsContainer.getChildren().addAll(viewButton, approveButton, rejectButton);
        productItem.getChildren().addAll(productImage, infoContainer, buttonsContainer);
        HBox.setHgrow(infoContainer, Priority.ALWAYS);

        return productItem;
    }

    private void showProductDetails(Product product) {
        Stage dialog = new Stage();
        dialog.initModality(Modality.APPLICATION_MODAL);
        dialog.setTitle("Détails du produit");

        VBox detailsContainer = new VBox(15);
        detailsContainer.setPadding(new Insets(20));
        detailsContainer.setAlignment(Pos.CENTER);

        ImageView productImage = new ImageView();
        productImage.setFitHeight(150);
        productImage.setFitWidth(150);
        productImage.setPreserveRatio(true);

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

        HBox buttonsContainer = new HBox(15);
        buttonsContainer.setAlignment(Pos.CENTER);

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
        detailsContainer.getChildren().addAll(productImage, nameLabel, descriptionLabel,
                categoryLabel, priceLabel, stockLabel, buttonsContainer);

        Scene dialogScene = new Scene(detailsContainer, 400, 500);
        dialog.setScene(dialogScene);
        dialog.show();
    }

    @FXML
    private void handleProductsButtonClick(ActionEvent event) {
        try {
            List<Product> pendingProducts = productService.getattenteeProducts();

            if (pendingProducts.isEmpty()) {
                showAlert(AlertType.INFORMATION, "Information", "Aucun produit en attente d'approbation.");
                return;
            }

            showPendingProductsInScrollableList(pendingProducts);
            Platform.runLater(() -> refreshProducts());
        } catch (SQLException e) {
            showAlert(AlertType.ERROR, "Erreur",
                    "Impossible de charger les produits en attente: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void showPendingProductsInScrollableList(List<Product> pendingProducts) {
        try {
            Stage dialog = new Stage();
            dialog.initModality(Modality.APPLICATION_MODAL);
            dialog.setTitle("Produits en attente d'approbation");

            VBox mainContainer = new VBox(15);
            mainContainer.setPadding(new Insets(20));
            mainContainer.setAlignment(Pos.CENTER);
            mainContainer.setStyle("-fx-background-color: #F9F5F0;");

            Label titleLabel = new Label("Liste des produits en attente");
            titleLabel.setStyle("-fx-font-size: 22px; -fx-font-weight: bold; -fx-text-fill: #5C4F3D; " +
                    "-fx-padding: 5 0 15 0; -fx-border-color: transparent transparent #E76F51 transparent; " +
                    "-fx-border-width: 0 0 2 0; -fx-padding: 0 0 10 0;");

            ScrollPane scrollPane = new ScrollPane();
            scrollPane.setFitToWidth(true);
            scrollPane.setStyle("-fx-background-color: transparent; -fx-background: transparent; " +
                    "-fx-border-color: #EAE0D5; -fx-border-radius: 5;");
            scrollPane.setPrefHeight(400);

            VBox productsContainer = new VBox(10);
            productsContainer.setPadding(new Insets(10));
            productsContainer.setStyle("-fx-background-color: transparent;");

            for (Product product : pendingProducts) {
                HBox productItem = createStyledProductListItem(product);
                productsContainer.getChildren().add(productItem);
            }

            scrollPane.setContent(productsContainer);

            Button closeButton = new Button("Fermer");
            closeButton.setStyle("-fx-background-color: #D5C7B6; -fx-text-fill: #5C4F3D; -fx-background-radius: 5; " +
                    "-fx-padding: 10 30; -fx-font-weight: bold; -fx-cursor: hand; " +
                    "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.1), 2, 0, 0, 1);");

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

            mainContainer.getChildren().addAll(titleLabel, scrollPane, closeButton);

            Scene dialogScene = new Scene(mainContainer, 550, 550);
            dialog.setScene(dialogScene);
            dialog.show();

        } catch (Exception e) {
            showAlert(AlertType.ERROR, "Erreur", "Erreur lors de l'affichage de la liste: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private HBox createStyledProductListItem(Product product) {
        HBox productItem = new HBox(15);
        productItem.setPadding(new Insets(12));
        productItem.setStyle("-fx-background-color: white; -fx-border-color: #EAE0D5; -fx-border-radius: 8; " +
                "-fx-background-radius: 8; -fx-effect: dropshadow(gaussian, rgba(0,0,0,0.05), 3, 0, 0, 1);");

        StackPane imageWrapper = new StackPane();
        imageWrapper.setStyle("-fx-background-color: #F5EEE6; -fx-background-radius: 5; -fx-padding: 5;");
        imageWrapper.setPrefWidth(70);
        imageWrapper.setPrefHeight(70);

        ImageView productImage = new ImageView();
        productImage.setFitHeight(60);
        productImage.setFitWidth(60);
        productImage.setPreserveRatio(true);

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

        VBox infoContainer = new VBox(5);
        infoContainer.setAlignment(Pos.CENTER_LEFT);
        HBox.setHgrow(infoContainer, Priority.ALWAYS);

        Label nameLabel = new Label(product.getName());
        nameLabel.setStyle("-fx-font-size: 14px; -fx-font-weight: bold; -fx-text-fill: #5C4F3D;");

        Label priceLabel = new Label(product.getPrice() + " TND");
        priceLabel.setStyle("-fx-font-size: 12px; -fx-text-fill: #E76F51; -fx-font-weight: bold;");

        Label categoryLabel = new Label(product.getCategory());
        categoryLabel.setStyle("-fx-font-size: 12px; -fx-text-fill: #7D7168;");

        infoContainer.getChildren().addAll(nameLabel, priceLabel, categoryLabel);

        HBox buttonsContainer = new HBox(8);
        buttonsContainer.setAlignment(Pos.CENTER_RIGHT);

        Button viewButton = new Button("Voir");
        viewButton.setStyle("-fx-background-color: #F5EEE6; -fx-text-fill: #7D6E5B; -fx-background-radius: 5; " +
                "-fx-padding: 5 12; -fx-cursor: hand;");

        viewButton.setOnMouseEntered(e ->
                viewButton.setStyle("-fx-background-color: #EAE0D5; -fx-text-fill: #5C4F3D; -fx-background-radius: 5; " +
                        "-fx-padding: 5 12; -fx-cursor: hand;"));
        viewButton.setOnMouseExited(e ->
                viewButton.setStyle("-fx-background-color: #F5EEE6; -fx-text-fill: #7D6E5B; -fx-background-radius: 5; " +
                        "-fx-padding: 5 12; -fx-cursor: hand;"));
        viewButton.setOnAction(e -> showProductDetails(product));

        Button approveButton = new Button("Accepter");
        approveButton.setStyle("-fx-background-color: #A5D6A7; -fx-text-fill: white; -fx-background-radius: 5; " +
                "-fx-padding: 5 12; -fx-cursor: hand;");
        approveButton.setOnMouseEntered(e ->
                approveButton.setStyle("-fx-background-color: #8BC34A; -fx-text-fill: white; -fx-background-radius: 5; " +
                        "-fx-padding: 5 12; -fx-cursor: hand;"));
        approveButton.setOnMouseExited(e ->
                approveButton.setStyle("-fx-background-color: #A5D6A7; -fx-text-fill: white; -fx-background-radius: 5; " +
                        "-fx-padding: 5 12; -fx-cursor: hand;"));
        approveButton.setOnAction(e -> {
            handleProductApproval(product, true);
            productItem.setVisible(false);
            productItem.setManaged(false);
            Platform.runLater(() -> {
                try {
                    showPendingProductsInScrollableList(productService.getattenteeProducts());
                } catch (SQLException ex) {
                    showAlert(AlertType.ERROR, "Erreur", "Impossible de recharger la liste: " + ex.getMessage());
                }
            });
        });

        Button rejectButton = new Button("Refuser");
        rejectButton.setStyle("-fx-background-color: #EF9A9A; -fx-text-fill: white; -fx-background-radius: 5; " +
                "-fx-padding: 5 12; -fx-cursor: hand;");
        rejectButton.setOnMouseEntered(e ->
                rejectButton.setStyle("-fx-background-color: #E57373; -fx-text-fill: white; -fx-background-radius: 5; " +
                        "-fx-padding: 5 12; -fx-cursor: hand;"));
        rejectButton.setOnMouseExited(e ->
                rejectButton.setStyle("-fx-background-color: #EF9A9A; -fx-text-fill: white; -fx-background-radius: 5; " +
                        "-fx-padding: 5 12; -fx-cursor: hand;"));
        rejectButton.setOnAction(e -> {
            handleProductApproval(product, false);
            productItem.setVisible(false);
            productItem.setManaged(false);
            Platform.runLater(() -> {
                try {
                    showPendingProductsInScrollableList(productService.getattenteeProducts());
                } catch (SQLException ex) {
                    showAlert(AlertType.ERROR, "Erreur", "Impossible de recharger la liste: " + ex.getMessage());
                }
            });
        });

        buttonsContainer.getChildren().addAll(viewButton, approveButton, rejectButton);
        productItem.getChildren().addAll(imageWrapper, infoContainer, buttonsContainer);

        return productItem;
    }

    private void showProductApprovalDialog(Product product, List<Product> allPendingProducts, int currentIndex) {
        Stage dialog = new Stage();
        dialog.initModality(Modality.APPLICATION_MODAL);
        dialog.setTitle("Approbation de produit");

        VBox dialogVbox = new VBox(15);
        dialogVbox.setStyle("-fx-background-color: #F9F5F0; -fx-background-radius: 10; -fx-border-color: #EAE0D5; " +
                "-fx-border-radius: 10; -fx-border-width: 2; -fx-effect: dropshadow(gaussian, rgba(0,0,0,0.1), 5, 0, 0, 0);");
        dialogVbox.setAlignment(Pos.CENTER);
        dialogVbox.setPadding(new Insets(20));

        StackPane imageContainer = new StackPane();
        imageContainer.setStyle("-fx-background-color: #F5EEE6; -fx-background-radius: 8; -fx-padding: 10;");
        imageContainer.setPrefWidth(220);
        imageContainer.setPrefHeight(170);

        ImageView productImage = new ImageView();
        productImage.setFitHeight(150);
        productImage.setFitWidth(150);
        productImage.setPreserveRatio(true);

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

        imageContainer.getChildren().add(productImage);

        Label titleLabel = new Label("Produit en attente d'approbation");
        titleLabel.setStyle("-fx-font-size: 18px; -fx-font-weight: bold; -fx-text-fill: #5C4F3D; -fx-padding: 0 0 10 0; " +
                "-fx-border-color: transparent transparent #EAE0D5 transparent; -fx-border-width: 0 0 1 0; -fx-padding: 0 0 8 0;");

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

        HBox progressContainer = new HBox();
        progressContainer.setAlignment(Pos.CENTER);
        progressContainer.setStyle("-fx-padding: 10 0;");

        Label progressLabel = new Label((currentIndex + 1) + " / " + allPendingProducts.size());
        progressLabel.setStyle("-fx-font-size: 14px; -fx-text-fill: #A8A29E; -fx-background-color: #F5EEE6; " +
                "-fx-padding: 5 15; -fx-background-radius: 20;");

        progressContainer.getChildren().add(progressLabel);

        HBox buttonsBox = new HBox(15);
        buttonsBox.setAlignment(Pos.CENTER);
        buttonsBox.setPadding(new Insets(10, 0, 0, 0));

        Button approveButton = new Button("Accepter");
        approveButton.setStyle("-fx-background-color: #A5D6A7; -fx-text-fill: white; -fx-background-radius: 5; " +
                "-fx-padding: 10 25; -fx-font-weight: bold; -fx-cursor: hand; -fx-effect: dropshadow(gaussian, rgba(0,0,0,0.1), 2, 0, 0, 1);");
        approveButton.setOnMouseEntered(e ->
                approveButton.setStyle("-fx-background-color: #8BC34A; -fx-text-fill: white; -fx-background-radius: 5; " +
                        "-fx-padding: 10 25; -fx-font-weight: bold; -fx-cursor: hand; -fx-effect: dropshadow(gaussian, rgba(0,0,0,0.2), 3, 0, 0, 1);"));
        approveButton.setOnMouseExited(e ->
                approveButton.setStyle("-fx-background-color: #A5D6A7; -fx-text-fill: white; -fx-background-radius: 5; " +
                        "-fx-padding: 10 25; -fx-font-weight: bold; -fx-cursor: hand; -fx-effect: dropshadow(gaussian, rgba(0,0,0,0.1), 2, 0, 0, 1);"));

        Button rejectButton = new Button("Refuser");
        rejectButton.setStyle("-fx-background-color: #EF9A9A; -fx-text-fill: white; -fx-background-radius: 5; " +
                "-fx-padding: 10 25; -fx-font-weight: bold; -fx-cursor: hand; -fx-effect: dropshadow(gaussian, rgba(0,0,0,0.1), 2, 0, 0, 1);");
        rejectButton.setOnMouseEntered(e ->
                rejectButton.setStyle("-fx-background-color: #E57373; -fx-text-fill: white; -fx-background-radius: 5; " +
                        "-fx-padding: 10 25; -fx-font-weight: bold; -fx-cursor: hand; -fx-effect: dropshadow(gaussian, rgba(0,0,0,0.2), 3, 0, 0, 1);"));
        rejectButton.setOnMouseExited(e ->
                rejectButton.setStyle("-fx-background-color: #EF9A9A; -fx-text-fill: white; -fx-background-radius: 5; " +
                        "-fx-padding: 10 25; -fx-font-weight: bold; -fx-cursor: hand; -fx-effect: dropshadow(gaussian, rgba(0,0,0,0.1), 2, 0, 0, 1);"));

        buttonsBox.getChildren().addAll(approveButton, rejectButton);
        dialogVbox.getChildren().addAll(titleLabel, imageContainer, infoContainer, progressContainer, buttonsBox);

        approveButton.setOnAction(e -> {
            handleProductApproval(product, true);
            dialog.close();
            if (currentIndex + 1 < allPendingProducts.size()) {
                showProductApprovalDialog(allPendingProducts.get(currentIndex + 1), allPendingProducts, currentIndex + 1);
            } else {
                showAlert(AlertType.INFORMATION, "Information", "Tous les produits ont été traités.");
                Platform.runLater(() -> {
                    refreshProducts();
                    try {
                        showPendingProductsInScrollableList(productService.getattenteeProducts());
                    } catch (SQLException ex) {
                        showAlert(AlertType.ERROR, "Erreur", "Impossible de recharger la liste: " + ex.getMessage());
                    }
                });
            }
        });

        rejectButton.setOnAction(e -> {
            handleProductApproval(product, false);
            dialog.close();
            if (currentIndex + 1 < allPendingProducts.size()) {
                showProductApprovalDialog(allPendingProducts.get(currentIndex + 1), allPendingProducts, currentIndex + 1);
            } else {
                showAlert(AlertType.INFORMATION, "Information", "Tous les produits ont été traités.");
                Platform.runLater(() -> {
                    refreshProducts();
                    try {
                        showPendingProductsInScrollableList(productService.getattenteeProducts());
                    } catch (SQLException ex) {
                        showAlert(AlertType.ERROR, "Erreur", "Impossible de recharger la liste: " + ex.getMessage());
                    }
                });
            }
        });

        Scene dialogScene = new Scene(dialogVbox, 400, 650);
        dialog.setScene(dialogScene);
        dialog.show();
    }

    private void handleProductApproval(Product product, boolean isApproved) {
        try {
            ProductService productService = new ProductService();
            String userEmail = productService.getUserEmailByProductId(product.getId());
            String artisanName = productService.getArtisanNameByProductId(product.getId());

            if (isApproved) {
                productService.updateProductStatus(product.getId(), "dispo");
                Alert alert = new Alert(AlertType.INFORMATION);
                alert.setTitle("Succès");
                alert.setHeaderText(null);
                alert.setContentText("Le produit \"" + product.getName() + "\" a été approuvé avec succès.");

                DialogPane dialogPane = alert.getDialogPane();
                dialogPane.setStyle("-fx-background-color: #F9F5F0; -fx-border-color: #EAE0D5; -fx-border-width: 2;");
                Button okButton = (Button) dialogPane.lookupButton(ButtonType.OK);
                okButton.setStyle("-fx-background-color: #A5D6A7; -fx-text-fill: white; -fx-background-radius: 5;");

                if (userEmail != null && !userEmail.isEmpty()) {
                    // Utiliser la nouvelle classe EmailSender au lieu de EmailService
                    EmailSender.sendProductApprovalEmail(userEmail, product.getName(), artisanName);
                }
                alert.showAndWait();
            } else {
                productService.updateProductStatus(product.getId(), "refuse");
                Alert alert = new Alert(AlertType.INFORMATION);
                alert.setTitle("Information");
                alert.setHeaderText(null);
                alert.setContentText("Le produit \"" + product.getName() + "\" a été refusé.");

                if (userEmail != null && !userEmail.isEmpty()) {
                    // Utiliser la nouvelle classe EmailSender au lieu de EmailService
                    EmailSender.sendProductRejectionEmail(userEmail, product.getName(), artisanName);
                }

                DialogPane dialogPane = alert.getDialogPane();
                dialogPane.setStyle("-fx-background-color: #F9F5F0; -fx-border-color: #EAE0D5; -fx-border-width: 2;");
                Button okButton = (Button) dialogPane.lookupButton(ButtonType.OK);
                okButton.setStyle("-fx-background-color: #EF9A9A; -fx-text-fill: white; -fx-background-radius: 5;");

                alert.showAndWait();
            }

            Platform.runLater(() -> refreshProducts());

        } catch (SQLException e) {
            showAlert(AlertType.ERROR, "Erreur",
                    "Erreur lors du traitement du produit: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void loadProducts() {
        try {
            productsContainer.getChildren().clear();
            List<Product> products = productService.recupererDispo();

            if (products.isEmpty()) {
                showEmptyProductsMessage();
                return;
            }

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
            VBox productCard = new VBox();
            productCard.setPrefHeight(280);
            productCard.setPrefWidth(220);
            productCard.setStyle("-fx-background-color: white; -fx-border-color: #E8DECD; -fx-border-radius: 10; " +
                    "-fx-background-radius: 10; -fx-effect: dropshadow(gaussian, rgba(0,0,0,0.05), 3, 0, 0, 1);");

            StackPane imageContainer = new StackPane();
            imageContainer.setPrefHeight(140);
            imageContainer.setStyle("-fx-background-color: #F9F6F2; -fx-background-radius: 10 10 0 0;");

            ImageView productImage = new ImageView();
            productImage.setFitHeight(120);
            productImage.setFitWidth(120);
            productImage.setPreserveRatio(true);

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

            imageContainer.getChildren().add(productImage);

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

            HBox actionContainer = new HBox(10);
            actionContainer.setAlignment(Pos.CENTER);
            actionContainer.setStyle("-fx-padding: 0 15 15 15;");

            Button editButton = new Button("Modifier");
            editButton.setStyle("-fx-background-color: #E5DDD1; -fx-text-fill: #7D6E5B; -fx-background-radius: 5; -fx-padding: 5 10;");
            editButton.setOnAction(event -> handleEditProduct(product));

            Button deleteButton = new Button("Supprimer");
            deleteButton.setStyle("-fx-background-color: #F5EEE6; -fx-text-fill: #A18E78; -fx-background-radius: 5; -fx-padding: 5 10;");
            deleteButton.setOnAction(event -> handleDeleteProduct(product));

            actionContainer.getChildren().addAll(editButton, deleteButton);
            productCard.getChildren().addAll(imageContainer, infoContainer, actionContainer);

            return productCard;

        } catch (Exception e) {
            System.err.println("Erreur lors de la création de la carte produit: " + e.getMessage());
            e.printStackTrace();
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
        messageBox.setAlignment(Pos.CENTER);

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

    public void refreshProducts() {
        loadProducts();
        updateStatistics();
        updateNotificationBadge();
        loadCategoryChart();
        initializeExportComboBox();
    }

    private void updateStatistics() {
        try {
            List<Product> allProducts = productService.recuperer();
            int totalProducts = allProducts.size();
            int inStock = 0;
            int pendingProducts = productService.getTotalProductsPending();

            for (Product product : allProducts) {
                if (product.getStock() > 0) {
                    inStock++;
                }
            }

            if (totalProductsLabel != null) {
                totalProductsLabel.setText(String.valueOf(totalProducts));
            }

            if (inStockLabel != null) {
                inStockLabel.setText(String.valueOf(inStock));
            }

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

            EditProduct controller = loader.getController();
            controller.setProductData(product);

            Stage stage = new Stage();
            stage.setTitle("Modifier le produit");
            stage.setScene(new Scene(editProductView));
            stage.setOnHidden(e -> refreshProducts());
            stage.show();

        } catch (IOException e) {
            showAlert(AlertType.ERROR, "Erreur",
                    "Impossible d'ouvrir la page de modification: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void handleDeleteProduct(Product product) {
        try {
            Alert confirmation = new Alert(AlertType.CONFIRMATION);
            confirmation.setTitle("Confirmation de suppression");
            confirmation.setHeaderText("Supprimer le produit");
            confirmation.setContentText("Êtes-vous sûr de vouloir supprimer le produit "
                    + product.getName() + " ?");

            confirmation.showAndWait().ifPresent(response -> {
                if (response == ButtonType.OK) {
                    try {
                        productService.supprimer(product);
                        refreshProducts();
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
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/ListProductFront.fxml"));
            Parent listView = loader.load();
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

    @FXML
    private void handleAddProduct(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/AjouterProduct.fxml"));
            Parent root = loader.load();

            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.setTitle("Ajouter un produit");
            stage.show();

        } catch (IOException e) {
            e.printStackTrace();
            showAlert(AlertType.ERROR, "Erreur", "Impossible de charger la page d'ajout de produit : " + e.getMessage());
        }
    }

    public void ajouterProduit(ActionEvent actionEvent) {
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
        }
    }
}