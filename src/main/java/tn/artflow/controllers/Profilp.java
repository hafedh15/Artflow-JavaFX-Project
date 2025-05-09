package tn.artflow.controllers;

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
import javafx.scene.control.ButtonBar.ButtonData;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.util.Callback;

import tn.artflow.entities.*;
import tn.artflow.services.*;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.sql.SQLException;
import java.util.List;
import java.util.ResourceBundle;

public class Profilp implements Initializable {

    @FXML
    private ListView<Product> productListView;

    @FXML
    private ListView<Order> orderHistoryListView;

    @FXML
    private Label noOrdersLabel;

    @FXML
    private Label noRequestsLabel;
    @FXML
    private Button addProductButton;
    @FXML
    private ListView<Product> productRequestListView;

    User user = tn.artflow.utils.UserSession.getInstance().getUser();

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        // Chargement des produits
        loadUserProducts();
        // Chargement des demandes de produits en attente
        loadPendingProductRequests();
        // Chargement des commandes
        loadUserOrders();
    }

    // Nouvelle méthode pour charger les demandes de produits en attente
    private void loadPendingProductRequests() {
        ProductService ps = new ProductService();
        try {
            // Utilisation de la méthode getPendingProductsByUser
            List<Product> pendingProducts = ps.getPendingProductsByUser(user.getId()); // ID utilisateur fixé à 1

            if (pendingProducts.isEmpty()) {
                noRequestsLabel.setVisible(true);
                productRequestListView.setVisible(false);
            } else {
                noRequestsLabel.setVisible(false);
                productRequestListView.setVisible(true);
                productRequestListView.getItems().addAll(pendingProducts);

                // Configuration de l'affichage des demandes de produits en attente
                productRequestListView.setCellFactory(new Callback<ListView<Product>, ListCell<Product>>() {
                    @Override
                    public ListCell<Product> call(ListView<Product> listView) {
                        return new ListCell<Product>() {
                            private ImageView imageView = new ImageView();
                            private Label nameLabel = new Label();
                            private Label priceLabel = new Label();
                            private Label stockLabel = new Label();
                            private Label categoryLabel = new Label();
                            private Label descriptionLabel = new Label();
                            private Label statusLabel = new Label();
                            private Button detailsButton = new Button("Détails");
                            private Button cancelButton = new Button("Annuler");
                            private HBox buttonBox = new HBox(10, detailsButton, cancelButton);
                            private VBox infoBox = new VBox(5, nameLabel, priceLabel, stockLabel, categoryLabel, descriptionLabel, statusLabel, buttonBox);
                            private HBox hBox = new HBox(imageView, infoBox);

                            {
                                imageView.setFitWidth(100);
                                imageView.setFitHeight(100);
                                imageView.setPreserveRatio(true);
                                imageView.setSmooth(true);
                                hBox.setSpacing(10);

                                // Style des boutons
                                detailsButton.getStyleClass().add("details-button");
                                cancelButton.getStyleClass().add("delete-button");
                                buttonBox.setAlignment(Pos.CENTER_LEFT);
                                buttonBox.setPadding(new javafx.geometry.Insets(5, 0, 0, 0));

                                // Action des boutons
                                detailsButton.setOnAction(e -> showProductRequestDetails(getItem()));
                                cancelButton.setOnAction(e -> cancelProductRequest(getItem()));

                                // Appliquer des styles
                                nameLabel.getStyleClass().add("product-name");
                                priceLabel.getStyleClass().add("product-price");
                                stockLabel.getStyleClass().add("product-detail");
                                categoryLabel.getStyleClass().add("product-detail");
                                descriptionLabel.getStyleClass().add("product-detail");
                                statusLabel.getStyleClass().add("product-status");

                                hBox.getStyleClass().add("product-request-cell");
                                HBox.setHgrow(infoBox, Priority.ALWAYS);
                            }

                            @Override
                            protected void updateItem(Product p, boolean empty) {
                                super.updateItem(p, empty);
                                if (empty || p == null) {
                                    setText(null);
                                    setGraphic(null);
                                } else {
                                    // Correction pour charger l'image depuis le système de fichier
                                    File imageFile = new File("C:/xampp/htdocs" + p.getImage());
                                    try {
                                        String fileUri = imageFile.toURI().toString();
                                        Image image = new Image(fileUri, true);
                                        imageView.setImage(image);
                                    } catch (Exception e) {
                                        // Fallback à une image par défaut
                                        try {
                                            Image defaultImage = new Image(getClass().getResourceAsStream("/images/default-product.jpg"));
                                            imageView.setImage(defaultImage);
                                        } catch (Exception ex) {
                                            System.err.println("Impossible de charger l'image par défaut");
                                        }
                                        System.err.println("Impossible de charger l'image: " + imageFile.getAbsolutePath());
                                    }

                                    nameLabel.setText("🛒 Produit: " + p.getName());
                                    priceLabel.setText("💶 Prix: " + p.getPrice() + " €");
                                    stockLabel.setText("📦 Stock: " + p.getStock());
                                    categoryLabel.setText("📁 Catégorie: " + p.getCategory());
                                    descriptionLabel.setText("📝 Description: " + p.getDescription());

                                    // Mettre en évidence le statut "en attente"
                                    statusLabel.setText("⏳ Statut: " + p.getStatus());
                                    statusLabel.getStyleClass().add("pending-status");

                                    setGraphic(hBox);
                                }
                            }
                        };
                    }
                });
            }
        } catch (SQLException e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Erreur",
                    "Impossible de charger les demandes de produits: " + e.getMessage());
        }
    }

    // Nouvelle méthode pour afficher les détails d'une demande de produit
    private void showProductRequestDetails(Product product) {
        Dialog<Void> dialog = new Dialog<>();
        dialog.setTitle("Détails de la demande de produit");
        dialog.setHeaderText("Informations complètes de la demande");

        // Contenu du dialogue
        VBox content = new VBox(10);
        content.setPadding(new Insets(20));

        // Information du produit
        Label productName = new Label("Nom du produit: " + product.getName());
        Label productDescription = new Label("Description: " + product.getDescription());
        Label productPrice = new Label("Prix: " + product.getPrice() + " €");
        Label productStock = new Label("Stock: " + product.getStock());
        Label productCategory = new Label("Catégorie: " + product.getCategory());
        Label productStatus = new Label("Statut: " + product.getStatus());
        Label submissionDate = new Label("Date de soumission: À définir"); // À remplacer par la date réelle

        // Ajouter une image du produit
        ImageView productImage = new ImageView();
        productImage.setFitWidth(200);
        productImage.setFitHeight(200);
        productImage.setPreserveRatio(true);

        // Charger l'image du produit
        File imageFile = new File("C:/xampp/htdocs" + product.getImage());
        try {
            String fileUri = imageFile.toURI().toString();
            Image image = new Image(fileUri, true);
            productImage.setImage(image);
        } catch (Exception e) {
            // Fallback à une image par défaut
            try {
                Image defaultImage = new Image(getClass().getResourceAsStream("/images/default-product.jpg"));
                productImage.setImage(defaultImage);
            } catch (Exception ex) {
                System.err.println("Impossible de charger l'image par défaut");
            }
        }

        // Assembler tous les éléments
        content.getChildren().addAll(
                productImage,
                new Separator(),
                productName,
                productDescription,
                productPrice,
                productStock,
                productCategory,
                productStatus,
                submissionDate
        );

        dialog.getDialogPane().setContent(content);

        // Bouton de fermeture
        ButtonType closeButton = new ButtonType("Fermer", ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().add(closeButton);

        dialog.showAndWait();
    }

    // Nouvelle méthode pour annuler une demande de produit
    private void cancelProductRequest(Product product) {
        if (showConfirmationDialog("Confirmation d'annulation",
                "Êtes-vous sûr de vouloir annuler la demande pour le produit " + product.getName() + "?")) {
            try {
                ProductService ps = new ProductService();
                ps.supprimerParId(product.getId());
                refreshProductRequestList();
                showAlert(Alert.AlertType.INFORMATION, "Annulation réussie",
                        "La demande de produit a été annulée avec succès.");
            } catch (SQLException e) {
                e.printStackTrace();
                showAlert(Alert.AlertType.ERROR, "Erreur d'annulation",
                        "Impossible d'annuler la demande de produit: " + e.getMessage());
            }
        }
    }

    // Méthode pour rafraîchir la liste des demandes de produits
    private void refreshProductRequestList() {
        try {
            ProductService ps = new ProductService();

            productRequestListView.getItems().clear();
            List<Product> pendingProducts = ps.getPendingProductsByUser(user.getId()); // ID utilisateur fixé à 1

            if (pendingProducts.isEmpty()) {
                noRequestsLabel.setVisible(true);
                productRequestListView.setVisible(false);
            } else {
                noRequestsLabel.setVisible(false);
                productRequestListView.setVisible(true);
                productRequestListView.getItems().addAll(pendingProducts);
            }
        } catch (SQLException e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Erreur",
                    "Impossible de rafraîchir la liste des demandes de produits: " + e.getMessage());
        }
    }

    private void loadUserProducts() {
        ProductService ps = new ProductService();
        User user = tn.artflow.utils.UserSession.getInstance().getUser();


        try {
            List<Product> products = ps.getProductsByUser(user);
            productListView.getItems().addAll(products);

            // Personnaliser l'affichage des éléments :
            productListView.setCellFactory(new Callback<ListView<Product>, ListCell<Product>>() {
                @Override
                public ListCell<Product> call(ListView<Product> listView) {
                    return new ListCell<Product>() {
                        private ImageView imageView = new ImageView();
                        private Label nameLabel = new Label();
                        private Label priceLabel = new Label();
                        private Label stockLabel = new Label();
                        private Label categoryLabel = new Label();
                        private Label descriptionLabel = new Label();
                        private Label statusLabel = new Label();
                        private Button modifyButton = new Button("Modifier");
                        private Button deleteButton = new Button("Supprimer");
                        private HBox buttonBox = new HBox(10, modifyButton, deleteButton);
                        private VBox infoBox = new VBox(5, nameLabel, priceLabel, stockLabel, categoryLabel, descriptionLabel, statusLabel, buttonBox);
                        private HBox hBox = new HBox(imageView, infoBox);

                        {
                            imageView.setFitWidth(100);
                            imageView.setFitHeight(100);
                            imageView.setPreserveRatio(true);
                            imageView.setSmooth(true);
                            hBox.setSpacing(10);

                            // Style des boutons
                            modifyButton.getStyleClass().add("edit-button");
                            deleteButton.getStyleClass().add("delete-button");
                            buttonBox.setAlignment(Pos.CENTER_LEFT);
                            buttonBox.setPadding(new javafx.geometry.Insets(5, 0, 0, 0));

                            // Action des boutons
                            modifyButton.setOnAction(e -> modifyProduct(getItem()));
                            deleteButton.setOnAction(e -> deleteProduct(getItem()));

                            // Appliquer des styles pour ressembler à la capture d'écran
                            nameLabel.getStyleClass().add("product-name");
                            priceLabel.getStyleClass().add("product-price");
                            stockLabel.getStyleClass().add("product-detail");
                            categoryLabel.getStyleClass().add("product-detail");
                            descriptionLabel.getStyleClass().add("product-detail");
                            statusLabel.getStyleClass().add("product-status");

                            hBox.getStyleClass().add("product-cell");
                            HBox.setHgrow(infoBox, Priority.ALWAYS);
                        }

                        @Override
                        protected void updateItem(Product p, boolean empty) {
                            super.updateItem(p, empty);
                            if (empty || p == null) {
                                setText(null);
                                setGraphic(null);
                            } else {
                                // Correction pour charger l'image depuis le système de fichier
                                File imageFile = new File("C:/xampp/htdocs" + p.getImage());
                                try {
                                    String fileUri = imageFile.toURI().toString();
                                    Image image = new Image(fileUri, true);
                                    imageView.setImage(image);
                                } catch (Exception e) {
                                    // Fallback à une image par défaut
                                    try {
                                        Image defaultImage = new Image(getClass().getResourceAsStream("/images/default-product.jpg"));
                                        imageView.setImage(defaultImage);
                                    } catch (Exception ex) {
                                        System.err.println("Impossible de charger l'image par défaut");
                                    }
                                    System.err.println("Impossible de charger l'image: " + imageFile.getAbsolutePath());
                                }

                                nameLabel.setText("🛒 Produit: " + p.getName());
                                priceLabel.setText("💶 Prix: " + p.getPrice() + " €");
                                stockLabel.setText("📦 Stock: " + p.getStock());
                                categoryLabel.setText("📁 Catégorie: " + p.getCategory());
                                descriptionLabel.setText("📝 Description: " + p.getDescription());
                                statusLabel.setText("✅ Statut: " + p.getStatus());

                                setGraphic(hBox);
                            }
                        }
                    };
                }
            });

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // Modification pour la méthode loadUserOrders() qui affiche les commandes
    private void loadUserOrders() {
        OrderService orderService = new OrderService();
        try {
            List<Order> userOrders = orderService.getOrdersByUser(1); // ID utilisateur fixé à 1

            if (userOrders.isEmpty()) {
                noOrdersLabel.setVisible(true);
                orderHistoryListView.setVisible(false);
            } else {
                noOrdersLabel.setVisible(false);
                orderHistoryListView.setVisible(true);
                orderHistoryListView.getItems().addAll(userOrders);

                // Configuration de l'affichage des commandes
                orderHistoryListView.setCellFactory(new Callback<ListView<Order>, ListCell<Order>>() {
                    @Override
                    public ListCell<Order> call(ListView<Order> listView) {
                        return new ListCell<Order>() {
                            private Label orderIdLabel = new Label();
                            private Label dateLabel = new Label();
                            private Label addressLabel = new Label();
                            private Label phoneLabel = new Label();
                            private Label statusLabel = new Label();
                            private Label totalLabel = new Label();
                            private Button detailsButton = new Button("Détails");
                            private Button deleteButton = new Button("Supprimer");
                            // Ajout du bouton de paiement en ligne


                            private VBox buttonsVBox = new VBox(5, detailsButton, deleteButton);
                            private VBox orderInfoBox = new VBox(5, orderIdLabel, dateLabel, addressLabel,
                                    phoneLabel, statusLabel, totalLabel);
                            private HBox mainBox = new HBox(10, orderInfoBox, buttonsVBox);

                            {
                                // Stylisation des éléments
                                orderIdLabel.getStyleClass().add("order-id");
                                dateLabel.getStyleClass().add("order-detail");
                                addressLabel.getStyleClass().add("order-detail");
                                phoneLabel.getStyleClass().add("order-detail");
                                statusLabel.getStyleClass().add("order-status");
                                totalLabel.getStyleClass().add("order-total");

                                detailsButton.getStyleClass().add("details-button");
                                detailsButton.setOnAction(e -> showOrderDetails(getItem()));

                                deleteButton.getStyleClass().add("delete-button");
                                deleteButton.setOnAction(e -> deleteOrder(getItem()));



                                buttonsVBox.setAlignment(Pos.CENTER);
                                mainBox.setAlignment(Pos.CENTER_LEFT);
                                mainBox.setPadding(new javafx.geometry.Insets(10));
                                mainBox.getStyleClass().add("order-cell");
                            }

                            @Override
                            protected void updateItem(Order order, boolean empty) {
                                super.updateItem(order, empty);
                                if (empty || order == null) {
                                    setText(null);
                                    setGraphic(null);
                                } else {
                                    orderIdLabel.setText("🧾 Commande #" + order.getId());
                                    dateLabel.setText("📅 Date: " + order.getDateOrder());
                                    addressLabel.setText("🏠 Adresse: " + order.getDeliveryAddress());
                                    phoneLabel.setText("📞 Téléphone: " + order.getPhoneNumber());
                                    statusLabel.setText("💳 Statut: " + (order.getPaid() ));
                                    //totalLabel.setText("💰 Total: " + order.calculateTotal() + " €");




                                    setGraphic(mainBox);
                                }
                            }
                        };
                    }
                });
            }
        } catch (SQLException e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Erreur",
                    "Impossible de charger les commandes: " + e.getMessage());
        }
    }

    // Nouvelle méthode pour naviguer vers la page de paiement
    private void navigateToPayment(Order order) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/paiement.fxml"));
            Parent root = loader.load();

            // Vérifier si le contrôleur de la page paiement possède une méthode pour initialiser les données
            Object controller = loader.getController();

            // Si le contrôleur a une méthode setOrderData, on l'utilise
            if (controller instanceof Paiement) {
                Paiement paiementController = (Paiement) controller;
                try {
                    // Essayer d'appeler la méthode setOrderData si elle existe
                    //    paiementController.setOrderData(order);
                } catch (Exception e) {
                    System.err.println("La méthode setOrderData n'existe pas dans PaiementController: " + e.getMessage());
                    // Continuer même sans pouvoir passer les données
                }
            }

            Stage stage = (Stage) orderHistoryListView.getScene().getWindow();
            Scene scene = new Scene(root);
            stage.setScene(scene);
            stage.show();

            // Afficher un message de confirmation
            System.out.println("Redirection vers la page de paiement pour la commande #" + order.getId());

        } catch (IOException e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Erreur",
                    "Impossible de charger la page de paiement: " + e.getMessage());
        }
    }
    private void deleteOrder(Order order) {
        try {
            OrderService orderService = new OrderService();
            // Vérifie si la commande a au moins 7 jours (par exemple)
            if (!orderService.isOrderOlderThanDays(order.getId(), 3)) {
                showAlert(Alert.AlertType.WARNING, "Suppression refusée",
                        "Vous ne pouvez supprimer que les commandes de plus de 3 jours.");
                return;
            }

            if (showConfirmationDialog("Confirmation de suppression",
                    "Êtes-vous sûr de vouloir supprimer la commande #" + order.getId() + "?")) {
                orderService.supprimerParId(order.getId());
                refreshOrderList();
                showAlert(Alert.AlertType.INFORMATION, "Suppression réussie",
                        "La commande a été supprimée avec succès.");
            }
        } catch (SQLException e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Erreur de suppression",
                    "Impossible de supprimer la commande: " + e.getMessage());
        }
    }

    // Méthode pour rafraîchir la liste des commandes
    private void refreshOrderList() {
        try {
            OrderService orderService = new OrderService();

            orderHistoryListView.getItems().clear();
            List<Order> userOrders = orderService.getOrdersByUser(1); // ID utilisateur fixé à 1

            if (userOrders.isEmpty()) {
                noOrdersLabel.setVisible(true);
                orderHistoryListView.setVisible(false);
            } else {
                noOrdersLabel.setVisible(false);
                orderHistoryListView.setVisible(true);
                orderHistoryListView.getItems().addAll(userOrders);
            }
        } catch (SQLException e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Erreur",
                    "Impossible de rafraîchir la liste des commandes: " + e.getMessage());
        }
    }
    // Méthode pour afficher les détails d'une commande
    private void showOrderDetails(Order order) {
        try {
            // Créer une fenêtre de dialogue pour afficher les détails
            Dialog<Void> dialog = new Dialog<>();
            dialog.setTitle("Détails de la commande #" + order.getId());
            dialog.setHeaderText("Informations complètes de la commande");

            // Contenu du dialogue
            VBox content = new VBox(10);
            content.setPadding(new Insets(20));

            // Information de la commande
            Label customerInfo = new Label("Client: " + order.getUser().getName() + " " + order.getUser().getLastname());
            Label orderDate = new Label("Date: " + order.getDateOrder());
            Label deliveryInfo = new Label("Livraison: " + order.getDeliveryAddress() + " - " + order.getPhoneNumber());
            Label paymentStatus = new Label("Paiement: " + ("true".equalsIgnoreCase(order.getPaid()) ? "Payé" : "En attente"));

            // Section des produits
            Label productsTitle = new Label("Produits commandés:");
            productsTitle.setStyle("-fx-font-weight: bold; -fx-font-size: 14px;");

            // Ici, vous devriez récupérer les produits de la commande
            VBox productsBox = new VBox(5);

            OrderService orderService = new OrderService();
            try {
                List<Product> orderedProducts = orderService.getProductsByOrderId(order.getId());


                for (Product product : orderedProducts) {
                    HBox productRow = new HBox(10);
                    Label productName = new Label(product.getName());
                    Label productPrice = new Label(product.getPrice() + " dt");
                    productRow.getChildren().addAll(productName, new Region(), productPrice);
                    HBox.setHgrow(productRow.getChildren().get(1), Priority.ALWAYS);
                    productsBox.getChildren().add(productRow);
                }
            } catch (SQLException e) {
                productsBox.getChildren().add(new Label("Impossible de charger les produits: " + e.getMessage()));
            }



            // Assembler tous les éléments
            content.getChildren().addAll(
                    customerInfo, orderDate, deliveryInfo, paymentStatus,
                    new Separator(), productsTitle, productsBox, new Separator()
            );

            dialog.getDialogPane().setContent(content);

            // Bouton de fermeture
            ButtonType closeButton = new ButtonType("Fermer", ButtonData.OK_DONE);
            dialog.getDialogPane().getButtonTypes().add(closeButton);

            dialog.showAndWait();

        } catch (Exception e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Erreur",
                    "Impossible d'afficher les détails de la commande: " + e.getMessage());
        }
    }

    // Méthode pour ajouter un nouveau produit
    @FXML
    private void addNewProduct(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/AjouterProduct.fxml"));
            Parent root = loader.load();

            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            Scene scene = new Scene(root);
            stage.setScene(scene);
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Erreur",
                    "Impossible de charger le formulaire d'ajout de produit: " + e.getMessage());
        }
    }

    private void modifyProduct(Product product) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/EditProduct.fxml"));
            Parent root = loader.load();

            EditProduct controller = loader.getController();
            controller.setProductData(product);

            Stage stage = (Stage) productListView.getScene().getWindow();
            Scene scene = new Scene(root);
            stage.setScene(scene);
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Erreur",
                    "Impossible de charger la vue de modification: " + e.getMessage());
        }
    }

    private void deleteProduct(Product product) {
        if (showConfirmationDialog("Confirmation de suppression",
                "Êtes-vous sûr de vouloir supprimer le produit " + product.getName() + "?")) {
            try {
                ProductService ps = new ProductService();
                ps.supprimerParId(product.getId());
                refreshProductList();
                showAlert(Alert.AlertType.INFORMATION, "Suppression réussie",
                        "Le produit a été supprimé avec succès.");
            } catch (SQLException e) {
                e.printStackTrace();
                showAlert(Alert.AlertType.ERROR, "Erreur de suppression",
                        "Impossible de supprimer le produit: " + e.getMessage());
            }
        }
    }

    private boolean showConfirmationDialog(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);

        return alert.showAndWait().get().getButtonData().isDefaultButton();
    }

    private void showAlert(Alert.AlertType type, String title, String message) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    // Méthode pour rafraîchir la liste des produits
    private void refreshProductList() {
        try {
            ProductService ps = new ProductService();
            User user = new User();
            user.setId(1); // ID fixe pour test

            productListView.getItems().clear();
            List<Product> products = ps.getProductsByUser(user);
            productListView.getItems().addAll(products);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @FXML
    public void printOrderHistory(ActionEvent actionEvent) {
        try {
            OrderService orderService = new OrderService();
            List<Order> userOrders = orderService.getOrdersByUser(1); // ID utilisateur fixé à 1

            if (userOrders.isEmpty()) {
                showAlert(Alert.AlertType.INFORMATION, "Information",
                        "Aucune commande à imprimer.");
                return;
            }

            // Créer un rapport à imprimer
            StringBuilder report = new StringBuilder();
            report.append("HISTORIQUE DES COMMANDES\n");
            report.append("=========================\n\n");

            for (Order order : userOrders) {
                report.append("Commande #").append(order.getId()).append("\n");
                report.append("Date: ").append(order.getDateOrder()).append("\n");
                report.append("Adresse: ").append(order.getDeliveryAddress()).append("\n");
                report.append("Téléphone: ").append(order.getPhoneNumber()).append("\n");
                report.append("Statut: ").append("true".equalsIgnoreCase(order.getPaid()) ? "Payée" : "En attente").append("\n");
                report.append("Total: ").append(order.calculateTotal()).append(" €\n");
                report.append("---------------------------\n\n");
            }

            // Afficher le rapport dans une nouvelle fenêtre
            Dialog<Void> printDialog = new Dialog<>();
            printDialog.setTitle("Impression - Historique des commandes");

            TextArea reportArea = new TextArea(report.toString());
            reportArea.setEditable(false);
            reportArea.setPrefHeight(400);
            reportArea.setPrefWidth(500);

            // Ajouter un ComboBox pour sélectionner une commande
            ComboBox<Order> orderSelector = new ComboBox<>();
            orderSelector.getItems().addAll(userOrders);
            orderSelector.setPromptText("Sélectionner une commande");

            // Personnaliser l'affichage du ComboBox
            orderSelector.setCellFactory(listView -> new ListCell<Order>() {
                @Override
                protected void updateItem(Order order, boolean empty) {
                    super.updateItem(order, empty);
                    if (empty || order == null) {
                        setText(null);
                    } else {
                        setText("Commande #" + order.getId());
                    }
                }
            });
            orderSelector.setButtonCell(new ListCell<Order>() {
                @Override
                protected void updateItem(Order order, boolean empty) {
                    super.updateItem(order, empty);
                    if (empty || order == null) {
                        setText("Sélectionner une commande");
                    } else {
                        setText("Commande #" + order.getId());
                    }
                }
            });

            // Créer les boutons
            ButtonType payButton = new ButtonType("Payer", ButtonBar.ButtonData.OTHER);
            ButtonType printButton = new ButtonType("Imprimer", ButtonBar.ButtonData.OK_DONE);
            ButtonType cancelButton = new ButtonType("Annuler", ButtonBar.ButtonData.CANCEL_CLOSE);
            printDialog.getDialogPane().getButtonTypes().addAll(payButton, printButton, cancelButton);

            // Activer/désactiver le bouton "Payer" en fonction de la sélection et du statut
            Node payButtonNode = printDialog.getDialogPane().lookupButton(payButton);
            payButtonNode.setDisable(true);
            orderSelector.getSelectionModel().selectedItemProperty().addListener((obs, old, newValue) -> {
                payButtonNode.setDisable(newValue == null || "true".equalsIgnoreCase(newValue.getPaid()));
            });

            // Action pour le bouton "Payer"
            printDialog.setResultConverter(buttonType -> {
                if (buttonType == payButton) {
                    Order selectedOrder = orderSelector.getSelectionModel().getSelectedItem();
                    if (selectedOrder != null) {
                        payOrder(selectedOrder);
                    }
                }
                return null;
            });

            // Assembler le contenu
            VBox content = new VBox(10, orderSelector, reportArea);
            content.setPadding(new Insets(10));
            printDialog.getDialogPane().setContent(content);

            printDialog.showAndWait();

        } catch (SQLException e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Erreur",
                    "Impossible d'imprimer l'historique des commandes: " + e.getMessage());
        }
    }
    private void payOrder(Order order) {
        try {
            OrderService orderService = new OrderService();
            // Mettre à jour l'attribut paid à "true"
            orderService.updatePaidStatus(order.getId(), "Paye");
            showAlert(Alert.AlertType.INFORMATION, "Paiement réussi",
                    "La commande #" + order.getId() + " a été marquée comme payée.");
            refreshOrderList(); // Rafraîchir la liste des commandes
        } catch (SQLException e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Erreur de paiement",
                    "Impossible de marquer la commande comme payée: " + e.getMessage());
        }
    }

    public void editDeliveryInfo(ActionEvent event) {
        try {
            // Récupérer la commande sélectionnée
            Order selectedOrder = orderHistoryListView.getSelectionModel().getSelectedItem();

            if (selectedOrder == null) {
                showAlert(Alert.AlertType.WARNING, "Sélection requise",
                        "Veuillez sélectionner une commande à modifier.");
                return;
            }

            System.out.println("Commande sélectionnée: " + selectedOrder.getId());

            FXMLLoader loader = new FXMLLoader(getClass().getResource("/EditOrder.fxml"));
            Parent root = loader.load();

            // Passer la commande au contrôleur
            EditOrderController controller = loader.getController();
            controller.setOrder(selectedOrder);

            Scene scene = new Scene(root);
            Stage stage = (Stage)((Node)event.getSource()).getScene().getWindow();
            stage.setScene(scene);
            stage.show();
        } catch (Exception e) {
            e.printStackTrace();
            System.err.println("Message d'erreur: " + e.getMessage());
            System.err.println("Cause: " + (e.getCause() != null ? e.getCause().getMessage() : "inconnue"));
            showAlert(Alert.AlertType.ERROR, "Erreur",
                    "Impossible d'ouvrir la page d'édition: " + e.getMessage());
        }
    }

    // Méthodes de navigation
    @FXML
    private void navigateToHome(ActionEvent event) {

    }

    @FXML
    private void navigateToProducts(ActionEvent event) {
        navigateTo(event, "ListProductFront.fxml");
    }

    @FXML
    private void navigateToWorkshops(ActionEvent event) {

    }

    @FXML
    private void navigateToComplaints(ActionEvent event) {

    }

    @FXML
    private void navigateToAbout(ActionEvent event) {

    }

    @FXML
    private void navigateToCart(ActionEvent event) {
        navigateTo(event, "Cart.fxml");
    }

    @FXML
    private void profil(ActionEvent event) {
        // Nous sommes déjà sur la page profil, pas besoin de naviguer
    }

    // Méthode utilitaire pour la navigation
    private void navigateTo(ActionEvent event, String fxmlPath) {
        try {
            Parent root = FXMLLoader.load(getClass().getResource(fxmlPath));
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            Scene scene = new Scene(root);
            stage.setScene(scene);
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
            System.err.println("Impossible de charger la page: " + fxmlPath);
        }
    }

    public void addNewProductRequest(ActionEvent actionEvent) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/AjouterProduct.fxml"));
            Parent root = loader.load();

            Stage stage = (Stage) ((Node) actionEvent.getSource()).getScene().getWindow();
            Scene scene = new Scene(root);
            stage.setScene(scene);
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Erreur",
                    "Impossible de charger le formulaire d'ajout de produit: " + e.getMessage());
        }
    }
    public void Payer(ActionEvent actionEvent) {
        try {
            // Récupérer la commande sélectionnée
            Order selectedOrder = orderHistoryListView.getSelectionModel().getSelectedItem();

            if (selectedOrder == null) {
                showAlert(Alert.AlertType.WARNING, "Sélection requise",
                        "Veuillez sélectionner une commande à payer.");
                return;
            }

            // Vérifier si la commande est déjà payée
            if (selectedOrder.isPaid()) {
                showAlert(Alert.AlertType.WARNING, "Commande déjà payée",
                        "La commande #" + selectedOrder.getId() + " est déjà payée.");
                return;
            }
            OrderService orderService = new OrderService();
            orderService.updatePaidStatus(selectedOrder.getId(), "true");
            // Calculer le montant de la commande

            double totalAmount = orderService.extractTotalFororder(selectedOrder.getId());
            System.out.println("Montant calculé pour la commande #" + selectedOrder.getId() + ": " + totalAmount);

            // Vérifier si le montant est valide
            if (totalAmount <= 0) {
                showAlert(Alert.AlertType.ERROR, "Montant invalide",
                        "Le montant de la commande #" + selectedOrder.getId() + " est invalide: " + totalAmount);
                return;
            }

            // Log pour déboguer
            System.out.println("Navigation vers paiement.fxml pour la commande ID: " + selectedOrder.getId());

            // Charger paiement.fxml
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/paiement.fxml"));
            Parent root = loader.load();

            // Passer l'ID et le montant de la commande au contrôleur
            Paiement controller = loader.getController();
            controller.setOrderId(selectedOrder.getId());

            // Naviguer vers la page de paiement
            Scene scene = new Scene(root);
            Stage stage = (Stage) ((Node) actionEvent.getSource()).getScene().getWindow();
            stage.setScene(scene);
            stage.show();

        } catch (IOException e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Erreur",
                    "Impossible d'ouvrir la page de paiement: " + e.getMessage());
        } catch (SQLException e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Erreur",
                    "Impossible de calculer le montant de la commande: " + e.getMessage());
        }
    }
}