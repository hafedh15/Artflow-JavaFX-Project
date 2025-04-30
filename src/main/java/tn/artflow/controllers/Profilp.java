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
    private ListView<tn.artflow.entities.Product> productListView;

    @FXML
    private ListView<Order> orderHistoryListView;

    @FXML
    private Label noOrdersLabel;

    @FXML
    private Button addProductButton;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        // Chargement des produits
        loadUserProducts();

        // Chargement des commandes
        loadUserOrders();
    }

    private void loadUserProducts() {
        ProductService ps = new ProductService();
        User user = tn.artflow.utils.UserSession.getInstance().getUser();
        //user.setId(1); // ID fixe pour test

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

    // Modification de la méthode loadUserOrders dans la classe Profil.java
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
                            // Ajout du bouton de suppression
                            private Button deleteButton = new Button("Supprimer");
                            private HBox buttonBox = new HBox(10, detailsButton, deleteButton);
                            private VBox orderInfoBox = new VBox(5, orderIdLabel, dateLabel, addressLabel,
                                    phoneLabel, statusLabel, totalLabel);
                            private HBox mainBox = new HBox(10, orderInfoBox, buttonBox);

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

                                // Style et action pour le bouton de suppression
                                deleteButton.getStyleClass().add("delete-button");
                                deleteButton.setOnAction(e -> deleteOrder(getItem()));

                                buttonBox.setAlignment(Pos.CENTER_RIGHT);
                                mainBox.setAlignment(Pos.CENTER_LEFT);
                                mainBox.setPadding(new javafx.geometry.Insets(10));
                                mainBox.getStyleClass().add("order-cell");

                                // Ajouter une disposition verticale pour les boutons
                                VBox buttonsVBox = new VBox(5, detailsButton, deleteButton);
                                buttonsVBox.setAlignment(Pos.CENTER);

                                // Remplacer le buttonBox par le buttonsVBox dans le mainBox
                                mainBox.getChildren().clear();
                                mainBox.getChildren().addAll(orderInfoBox, buttonsVBox);
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
                                    statusLabel.setText("💳 Statut: " + (order.getPaid() ? "Payée" : "En attente de paiement"));
                               //     totalLabel.setText("💰 Total: " + order.calculateTotal() + " €");

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
    // Méthode pour supprimer une commande
    private void deleteOrder(Order order) {
        if (showConfirmationDialog("Confirmation de suppression",
                "Êtes-vous sûr de vouloir supprimer la commande #" + order.getId() + "?")) {
            try {
                OrderService orderService = new OrderService();
                orderService.supprimerParId(order.getId());
                refreshOrderList();
                showAlert(Alert.AlertType.INFORMATION, "Suppression réussie",
                        "La commande a été supprimée avec succès.");
            } catch (SQLException e) {
                e.printStackTrace();
                showAlert(Alert.AlertType.ERROR, "Erreur de suppression",
                        "Impossible de supprimer la commande: " + e.getMessage());
            }
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
            Label paymentStatus = new Label("Paiement: " + (order.getPaid() ? "Payé" : "En attente"));

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

            User user = tn.artflow.utils.UserSession.getInstance().getUser();


            productListView.getItems().clear();
            List<Product> products = ps.getProductsByUser(user);
            productListView.getItems().addAll(products);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // Implémentation de la méthode printOrderHistory
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
                report.append("Statut: ").append(order.getPaid() ? "Payée" : "En attente").append("\n");
                report.append("Total: ").append(order.calculateTotal()).append(" €\n");
                report.append("---------------------------\n\n");
            }

            // Afficher le rapport dans une nouvelle fenêtre (simulation d'impression)
            Dialog<Void> printDialog = new Dialog<>();
            printDialog.setTitle("Impression - Historique des commandes");

            TextArea reportArea = new TextArea(report.toString());
            reportArea.setEditable(false);
            reportArea.setPrefHeight(400);
            reportArea.setPrefWidth(500);

            printDialog.getDialogPane().setContent(reportArea);

            ButtonType printButton = new ButtonType("Imprimer", ButtonBar.ButtonData.OK_DONE);
            ButtonType cancelButton = new ButtonType("Annuler", ButtonBar.ButtonData.CANCEL_CLOSE);
            printDialog.getDialogPane().getButtonTypes().addAll(printButton, cancelButton);

            printDialog.showAndWait();

        } catch (SQLException e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Erreur",
                    "Impossible d'imprimer l'historique des commandes: " + e.getMessage());
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
}