package tn.artflow.services;

import tn.artflow.entities.Cart;
import tn.artflow.entities.Order;
import tn.artflow.entities.Product;
import tn.artflow.entities.User;
import tn.artflow.tools.MyDataBase;

import java.sql.*;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class OrderService implements IService<Order> {
    Connection cnx;

    public OrderService() {
        cnx = MyDataBase.getInstance().getCnx();
    }


    public List<Product> getProductsByUser(User user) throws SQLException {
        return List.of();
    }

    @Override
    public void ajouter(Order order) throws SQLException {
        String sql = "INSERT INTO `order`(cart_id, user_id, delivery_adress, phone_number, date_order, order_history, paid, payment_inten_id) " +
                "VALUES(?, ?, ?, ?, ?, ?, ?, ?)";
        try (PreparedStatement ps = cnx.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setInt(1, order.getCart().getId());
            User user = tn.artflow.utils.UserSession.getInstance().getUser();

            ps.setInt(2, user.getId());
            ps.setString(3, order.getDeliveryAddress());
            ps.setString(4, order.getPhoneNumber());
            ps.setDate(5, new java.sql.Date(order.getDateOrder().getTime()));
            ps.setString(6, order.getOrderHistory());
            ps.setBoolean(7, false);
            ps.setInt(8, 0);

            ps.executeUpdate();

            try (ResultSet generatedKeys = ps.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    order.setId(generatedKeys.getInt(1));
                }
            }
            System.out.println("Commande ajoutée avec succès. ID: " + order.getId());
        }
    }

    @Override
    public void modifier(Order order) throws SQLException {

    }

    public boolean isCartAlreadyOrdered(int cartId) throws SQLException {
        String query = "SELECT COUNT(*) FROM `order` WHERE cart_id = ?";
        try (PreparedStatement ps = cnx.prepareStatement(query)) {
            ps.setInt(1, cartId);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return rs.getInt(1) > 0;
            }
        }
        return false;
    }








    public void modifier(int id, String nom) throws SQLException {
        // Non implémenté pour Order
    }


    public void modifier(Product product) throws SQLException {

    }


    public void modifierP(Product product) throws SQLException {

    }

    // Méthode pour mettre à jour le statut de paiement d'une commande
    public void mettreAJourStatutPaiement(int orderId, boolean paid) throws SQLException {
        // Vérifier si la commande existe
        if (!orderExists(orderId)) {
            System.out.println("Aucune commande trouvée avec l'ID " + orderId + ".");
            return;
        }

        String sql = "UPDATE `order` SET paid = ? WHERE id = ?";
        try (PreparedStatement ps = cnx.prepareStatement(sql)) {
            ps.setBoolean(1, paid);
            ps.setInt(2, orderId);
            ps.executeUpdate();

            System.out.println("Statut de paiement mis à jour pour la commande ID: " + orderId +
                    (paid ? " (Payée)" : " (Non payée)"));
        }
    }

    // Méthode pour mettre à jour l'adresse de livraison d'une commande
    public void mettreAJourAdresseLivraison(int orderId, String newAddress) throws SQLException {
        // Vérifier si la commande existe
        if (!orderExists(orderId)) {
            System.out.println("Aucune commande trouvée avec l'ID " + orderId + ".");
            return;
        }

        String sql = "UPDATE `order` SET delivery_adress = ? WHERE id = ?";
        try (PreparedStatement ps = cnx.prepareStatement(sql)) {
            ps.setString(1, newAddress);
            ps.setInt(2, orderId);
            ps.executeUpdate();

            System.out.println("Adresse de livraison mise à jour pour la commande ID: " + orderId);
        }
    }

    public boolean orderExists(int orderId) throws SQLException {
        String checkSql = "SELECT COUNT(*) FROM `order` WHERE id = ?";
        try (PreparedStatement checkPs = cnx.prepareStatement(checkSql)) {
            checkPs.setInt(1, orderId);
            try (ResultSet rs = checkPs.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1) > 0;
                }
            }
        }
        return false;
    }

    // Méthode pour mettre à jour l'adresse de livraison et le numéro de téléphone
    public boolean mettreAJourContactLivraison(Order order) throws SQLException {
        // Vérifier si la commande existe
        if (!orderExists(order.getId())) {
            System.out.println("Aucune commande trouvée avec l'ID " + order.getId() + ".");
            return false;
        }

        String sql = "UPDATE `order` SET delivery_adress = ?, phone_number = ? WHERE id = ?";
        try (PreparedStatement ps = cnx.prepareStatement(sql)) {
            ps.setString(1, order.getDeliveryAddress());  // Utilise getDeliveryAddress() au lieu de getAddress()
            ps.setString(2, order.getPhoneNumber());      // Utilise getPhoneNumber() au lieu de getPhone()
            ps.setInt(3, order.getId());

            int rowsAffected = ps.executeUpdate();

            if (rowsAffected > 0) {
                System.out.println("Coordonnées de livraison mises à jour pour la commande ID: " + order.getId());
                return true;
            } else {
                System.out.println("Mise à jour échouée pour la commande ID: " + order.getId());
                return false;
            }
        }
    }
    // Méthode pour supprimer une commande par ID
    public void supprimerParId(int orderId) throws SQLException {
        // Vérifier si la commande existe
        if (!orderExists(orderId)) {
            System.out.println("Aucune commande trouvée avec l'ID " + orderId + ".");
            return;
        }

        String deleteSql = "DELETE FROM `order` WHERE id = ?";
        try (PreparedStatement ps = cnx.prepareStatement(deleteSql)) {
            ps.setInt(1, orderId);
            ps.executeUpdate();
            System.out.println("Commande avec ID " + orderId + " supprimée avec succès.");
        }
    }

    // Méthode pour supprimer une commande
    @Override
    public void supprimer(Order order) throws SQLException {
        if (order == null || order.getId() <= 0) {
            System.out.println("Commande invalide.");
            return;
        }

        supprimerParId(order.getId());
    }

    // Méthode pour récupérer toutes les commandes
    @Override
    public List<Order> recuperer() throws SQLException {
        String sql = "SELECT o.*, u.name AS user_name, u.lastname AS user_lastname " +
                "FROM `order` o JOIN user u ON o.user_id = u.id";
        List<Order> orders = new ArrayList<>();

        try (Statement st = cnx.createStatement();
             ResultSet rs = st.executeQuery(sql)) {
            while (rs.next()) {
                Order order = extractOrderFromResultSet(rs);
                orders.add(order);
            }
        }
        return orders;
    }

    @Override
    public void supprimer(int id) throws SQLException {

    }

    @Override
    public List<Order> getReservationsByUser(User user) throws SQLException {
        return List.of();
    }

    // Méthode pour récupérer une commande par ID
    public Order getOrderById(int orderId) throws SQLException {
        String sql = "SELECT o.*, u.name AS user_name, u.lastname AS user_lastname " +
                "FROM `order` o JOIN user u ON o.user_id = u.id " +
                "WHERE o.id = ?";

        try (PreparedStatement ps = cnx.prepareStatement(sql)) {
            ps.setInt(1, orderId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return extractOrderFromResultSet(rs);
                }
            }
        }
        return null;
    }

    // Méthode pour récupérer les commandes d'un utilisateur
    public List<Order> getOrdersByUser(int userId) throws SQLException {
        String sql = "SELECT o.*, u.name AS user_name, u.lastname AS user_lastname " +
                "FROM `order` o JOIN user u ON o.user_id = u.id " +
                "WHERE o.user_id = ?";
        List<Order> orders = new ArrayList<>();

        try (PreparedStatement ps = cnx.prepareStatement(sql)) {
            ps.setInt(1, userId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Order order = extractOrderFromResultSet(rs);
                    orders.add(order);
                }
            }
        }
        return orders;
    }


    // Méthode pour afficher toutes les commandes avec leurs détails
    public void displayAllOrders() {
        try {
            List<Order> orders = recuperer();

            System.out.println("=== AFFICHAGE DE TOUTES LES COMMANDES ===");

            if (orders.isEmpty()) {
                System.out.println("Aucune commande trouvée.");
            } else {
                for (Order order : orders) {
                    displayOrderDetails(order);
                }
            }
        } catch (SQLException e) {
            System.out.println("Erreur SQL : " + e.getMessage());
            e.printStackTrace();
        }
    }

    // Méthode pour afficher une commande spécifique
    public void displaySpecificOrder(int orderId) {
        try {
            Order order = getOrderById(orderId);

            System.out.println("=== AFFICHAGE DE LA COMMANDE (ID: " + orderId + ") ===");

            if (order == null) {
                System.out.println("Aucune commande trouvée avec l'ID: " + orderId);
            } else {
                displayOrderDetails(order);
            }
        } catch (SQLException e) {
            System.out.println("Erreur SQL : " + e.getMessage());
            e.printStackTrace();
        }
    }

    // Méthode pour afficher les commandes d'un utilisateur
    public void displayOrdersForUser(int userId) {
        try {
            List<Order> orders = getOrdersByUser(userId);

            System.out.println("=== AFFICHAGE DES COMMANDES DE L'UTILISATEUR (ID: " + userId + ") ===");

            if (orders.isEmpty()) {
                System.out.println("Aucune commande trouvée pour l'utilisateur avec l'ID: " + userId);
            } else {
                for (Order order : orders) {
                    displayOrderDetails(order);
                }
            }
        } catch (SQLException e) {
            System.out.println("Erreur SQL : " + e.getMessage());

        }
    }



    // Méthode utilitaire pour extraire une commande d'un ResultSet
    private Order extractOrderFromResultSet(ResultSet rs) throws SQLException {
        Order order = new Order();
        order.setId(rs.getInt("id"));

        // Récupérer le panier associé
        Cart cart = new Cart();
        cart.setId(rs.getInt("cart_id"));
        order.setCart(cart);

        // Récupérer l'utilisateur associé
        User user = new User();
        user.setId(rs.getInt("user_id"));
        user.setName(rs.getString("user_name"));
        user.setLastname(rs.getString("user_lastname"));
        order.setUser(user);

        order.setDeliveryAddress(rs.getString("delivery_adress"));
        order.setPhoneNumber(rs.getString("phone_number"));
        order.setDateOrder(rs.getDate("date_order"));
        order.setOrderHistory(rs.getString("order_history"));
        order.setPaid(rs.getBoolean("paid"));
        order.setPaymentIntentId(rs.getInt("payment_inten_id"));

        return order;
    }



    public List<Product> getProductsByOrderId(int orderId) throws SQLException {
        List<Product> products = new ArrayList<>();

        String sql = "SELECT p.* FROM product p " +
                "JOIN cart_product cp ON p.id = cp.product_id " +
                "JOIN `order` o ON o.cart_id = cp.cart_id " +
                "WHERE o.id = ?";

        try (PreparedStatement ps = cnx.prepareStatement(sql)) {
            ps.setInt(1, orderId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Product product = new Product();
                    product.setId(rs.getInt("id"));
                    product.setName(rs.getString("name"));
                    product.setPrice(rs.getDouble("price"));
                    // ajoute d'autres attributs selon ta classe Product
                    products.add(product);
                }
            }
        }

        return products;
    }

    // Méthode utilitaire pour afficher les détails d'une commande
    private void displayOrderDetails(Order order) {
        System.out.println("\nCommande ID: " + order.getId());
        System.out.println("Utilisateur: " + order.getUser().getName() + " " + order.getUser().getLastname() + " (ID: " + order.getUser().getId() + ")");
        System.out.println("Panier ID: " + order.getCart().getId());
        System.out.println("Adresse de livraison: " + order.getDeliveryAddress());
        System.out.println("Numéro de téléphone: " + order.getPhoneNumber());
        System.out.println("Date de commande: " + order.getDateOrder());
        System.out.println("Historique: " + order.getOrderHistory());
        System.out.println("Statut de paiement: " + (order.getPaid() ? "Payée" : "Non payée"));
        System.out.println("ID d'intention de paiement: " + order.getPaymentIntentId());
        System.out.println("Total estimé: " + order.calculateTotal());
        System.out.println("------------------------------------");
    }
}