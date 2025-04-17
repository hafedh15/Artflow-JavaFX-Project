package tn.esprit.Contollers;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.scene.control.Label;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import tn.esprit.entities.Order;
import tn.esprit.entities.Product;
import tn.esprit.services.OrderService;

import java.net.URL;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.ResourceBundle;

public class OrderDashboardController implements Initializable {

    @FXML
    private FlowPane ordersContainer;

    @FXML
    private Label totalOrdersLabel;

    private OrderService orderService;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        orderService = new OrderService();
        loadOrders();
    }

    private void loadOrders() {
        try {
            // Clear the container
            ordersContainer.getChildren().clear();

            // Get all orders
            List<Order> orders = orderService.recuperer();

            // Update the total orders label
            totalOrdersLabel.setText(String.valueOf(orders.size()));

            // Create a card for each order
            for (Order order : orders) {
                ordersContainer.getChildren().add(createOrderCard(order));
            }

        } catch (SQLException e) {
            System.err.println("Erreur lors du chargement des commandes: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private VBox createOrderCard(Order order) {
        VBox orderCard = new VBox();
        orderCard.setPrefWidth(400);
        orderCard.setMinHeight(200);
        orderCard.getStyleClass().add("order-card");
        orderCard.setStyle("-fx-background-color: white; -fx-background-radius: 10; " +
                "-fx-border-color: #E8DECD; -fx-border-radius: 10; -fx-padding: 15; " +
                "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.05), 5, 0, 0, 2);");

        // Header with order ID and date
        HBox header = new HBox();
        header.setSpacing(10);

        Label orderIdLabel = new Label("Commande #" + order.getId());
        orderIdLabel.setStyle("-fx-font-weight: bold; -fx-font-size: 16px; -fx-text-fill: #5C4F3D;");

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
        Label dateLabel = new Label(dateFormat.format(order.getDateOrder()));
        dateLabel.setStyle("-fx-text-fill: #8D7B6A;");

        header.getChildren().addAll(orderIdLabel, spacer, dateLabel);

        // Client info
        VBox clientInfo = new VBox(5);
        clientInfo.setPadding(new Insets(10, 0, 10, 0));

        Label clientLabel = new Label("Client: " + order.getUser().getName() + " " + order.getUser().getLastname());
        clientLabel.setStyle("-fx-text-fill: #5C4F3D;");

        Label addressLabel = new Label("Adresse: " + order.getDeliveryAddress());
        addressLabel.setStyle("-fx-text-fill: #8D7B6A;");

        Label phoneLabel = new Label("Téléphone: " + order.getPhoneNumber());
        phoneLabel.setStyle("-fx-text-fill: #8D7B6A;");

        clientInfo.getChildren().addAll(clientLabel, addressLabel, phoneLabel);

        // Products in the order
        VBox productsInfo = new VBox(5);
        try {
            List<Product> products = orderService.getProductsByOrderId(order.getId());
            Label productsLabel = new Label("Produits: " + products.size());
            productsLabel.setStyle("-fx-text-fill: #5C4F3D;");
            productsInfo.getChildren().add(productsLabel);

            // Add up to 3 products to show in the card
            int count = 0;
            for (Product product : products) {
                if (count < 3) {
                    Label productLabel = new Label("• " + product.getName() + " - " + product.getPrice() + " €");
                    productLabel.setStyle("-fx-text-fill: #8D7B6A;");
                    productsInfo.getChildren().add(productLabel);
                    count++;
                } else {
                    Label moreProductsLabel = new Label("• Et " + (products.size() - 3) + " autres...");
                    moreProductsLabel.setStyle("-fx-text-fill: #8D7B6A; -fx-font-style: italic;");
                    productsInfo.getChildren().add(moreProductsLabel);
                    break;
                }
            }
        } catch (SQLException e) {
            Label errorLabel = new Label("Erreur lors du chargement des produits");
            errorLabel.setStyle("-fx-text-fill: #AA0000;");
            productsInfo.getChildren().add(errorLabel);
        }

        // Footer with payment status and total
        HBox footer = new HBox();
        footer.setSpacing(10);
        footer.setPadding(new Insets(10, 0, 0, 0));

        Label statusLabel = new Label(order.getPaid() ? "Payée" : "Non payée");
        statusLabel.setStyle(order.getPaid()
                ? "-fx-background-color: #D4EDDA; -fx-text-fill: #155724; -fx-padding: 5 10; -fx-background-radius: 5;"
                : "-fx-background-color: #F8D7DA; -fx-text-fill: #721C24; -fx-padding: 5 10; -fx-background-radius: 5;");

        Region footerSpacer = new Region();
        HBox.setHgrow(footerSpacer, Priority.ALWAYS);

       // Label totalLabel = new Label("Total: " + order.calculateTotal() + " €");
     //   totalLabel.setStyle("-fx-font-weight: bold; -fx-text-fill: #5C4F3D;");

     //   footer.getChildren().addAll(statusLabel, footerSpacer, totalLabel);

        // Add action buttons
        HBox actions = new HBox();
        actions.setSpacing(10);
        actions.setPadding(new Insets(10, 0, 0, 0));



        // Add all components to the card
        orderCard.getChildren().addAll(header, clientInfo, productsInfo, footer, actions);

        return orderCard;
    }

    private void showOrderDetails(Order order) {
        // Implement this method to show order details in a popup or new screen
        System.out.println("Affichage des détails de la commande #" + order.getId());
        // Here you would typically open a new window or dialog with full order details
    }

    public void handleOrderButtonAction(ActionEvent actionEvent) {
    }
}