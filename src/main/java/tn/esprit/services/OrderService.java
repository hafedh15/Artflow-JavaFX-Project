package tn.esprit.services;

import tn.esprit.entities.Cart;
import tn.esprit.entities.Order;
import tn.esprit.entities.Product;
import tn.esprit.entities.User;
import tn.esprit.tools.MyDataBase;

import java.sql.*;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class OrderService implements IService<Order> {
    Connection cnx;

    public OrderService() {
        cnx = MyDataBase.getInstance().getCnx();
    }

    @Override
    public List<Product> getProductsByUser(User user) throws SQLException {
        return List.of();
    }

    public void mettreAJourStatutPaiement(Integer id, boolean paid) throws SQLException {
        String sql = "UPDATE `order` SET paid = ? WHERE id = ?";
        try (PreparedStatement ps = cnx.prepareStatement(sql)) {
            ps.setBoolean(1, paid); // ✅ Utiliser setBoolean ici
            ps.setInt(2, id);

            int rowsUpdated = ps.executeUpdate();
            System.out.println("Statut de paiement mis à jour pour la commande ID: " + id + ", lignes affectées: " + rowsUpdated);
        }
    }

    @Override
    public void ajouter(Order order) throws SQLException {
        String sql = "INSERT INTO `order`(cart_id, user_id, delivery_adress, phone_number, date_order, order_history, paid, payment_inten_id) " +
                "VALUES(?, ?, ?, ?, ?, ?, ?, ?)";
        try (PreparedStatement ps = cnx.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setInt(1, order.getCart().getId());
            ps.setInt(2, 1);
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


    public void marquerCommandesPayeesParUtilisateur(int userId) throws SQLException {
        // Log avant l'exécution
        System.out.println("Tentative de marquer les commandes comme payées pour l'utilisateur ID: " + userId);

        // Vérifier d'abord si des commandes existent pour cet utilisateur
        String checkSql = "SELECT COUNT(*) FROM `order` WHERE user_id = ?";
        try (PreparedStatement checkPs = cnx.prepareStatement(checkSql)) {
            checkPs.setInt(1, userId);
            try (ResultSet rs = checkPs.executeQuery()) {
                if (rs.next()) {
                    int count = rs.getInt(1);
                    System.out.println("Nombre de commandes trouvées pour l'utilisateur: " + count);
                }
            }
        }

        // Afficher l'état actuel des commandes
        String statusSql = "SELECT id, paid FROM `order` WHERE user_id = ?";
        try (PreparedStatement statusPs = cnx.prepareStatement(statusSql)) {
            statusPs.setInt(1, userId);
            try (ResultSet rs = statusPs.executeQuery()) {
                System.out.println("État actuel des commandes:");
                while (rs.next()) {
                    System.out.println("Commande ID: " + rs.getInt("id") + ", Paid: " + rs.getObject("paid"));
                }
            }
        }

        // La requête de mise à jour
        String sql = "UPDATE `order` SET paid = ? WHERE user_id = ? AND (paid IS NULL OR paid = false)";

        try (PreparedStatement ps = cnx.prepareStatement(sql)) {
            ps.setBoolean(1, true);  // Utiliser setBoolean et non setString
            ps.setInt(2, userId);

            int lignesModifiees = ps.executeUpdate();

            System.out.println("Requête exécutée: " + sql);
            System.out.println("Paramètres: {1: true, 2: " + userId + "}");
            System.out.println("Nombre de lignes modifiées: " + lignesModifiees);

            // Vérifier à nouveau l'état après mise à jour
            try (PreparedStatement afterPs = cnx.prepareStatement(statusSql)) {
                afterPs.setInt(1, userId);
                try (ResultSet rs = afterPs.executeQuery()) {
                    System.out.println("État des commandes après mise à jour:");
                    while (rs.next()) {
                        System.out.println("Commande ID: " + rs.getInt("id") + ", Paid: " + rs.getObject("paid"));
                    }
                }
            }
        } catch (SQLException e) {
            System.err.println("❌ Erreur SQL complète: " + e.getMessage());
            throw e;
        }
    }


    @Override
    public void modifier(int id, String nom) throws SQLException {
        // Non implémenté pour Order
    }

    @Override
    public void modifier(Product product) throws SQLException {

    }

    @Override
    public void modifierP(Product product) throws SQLException {

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

    public double extractTotalFromText(String orderSummary) {
        double total = 0.0;

        if (orderSummary != null && !orderSummary.isEmpty()) {
            String[] lines = orderSummary.split("\n");
            for (String line : lines) {
                if (line.trim().startsWith("Total :")) {
                    try {
                        String totalStr = line.split(":")[1].trim();
                        totalStr = totalStr.replace("dt", "").trim();
                        total = Double.parseDouble(totalStr);
                        break;
                    } catch (Exception e) {
                        System.out.println("Erreur lors de l'extraction du total: " + e.getMessage());
                    }
                }
            }
        }

        return total;
    }
    public double extractTotalForUser(int userId) throws SQLException {
        double total = 0.0;

        // Récupérer toutes les commandes de l'utilisateur
        List<Order> userOrders = getOrdersByUser(userId);

        if (userOrders.isEmpty()) {
            System.out.println("Aucune commande trouvée pour l'utilisateur avec ID: " + userId);
            return total;
        }

        // Pour chaque commande, essayer d'extraire le total
        for (Order order : userOrders) {
            String orderHistory = order.getOrderHistory();
            if (orderHistory != null && !orderHistory.isEmpty()) {
                // Chercher la ligne qui contient "Total :"
                String[] lines = orderHistory.split("\n");
                for (String line : lines) {
                    if (line.trim().startsWith("Total :")) {
                        try {
                            // Extraire la valeur numérique
                            String totalStr = line.split(":")[1].trim();
                            totalStr = totalStr.replace("dt", "").trim();
                            total += Double.parseDouble(totalStr);
                        } catch (Exception e) {
                            System.out.println("Erreur lors de l'extraction du total: " + e.getMessage());
                        }
                    }
                }
            }
        }

        return total;
    }
    public double extractTotalFororder(int orderId) throws SQLException {
        double total = 0.0;

        // Récupérer la commande spécifique
        Order order = getOrderById(orderId); // Assume this method exists to get a single order by ID

        if (order == null) {
            System.out.println("Aucune commande trouvée pour l'ID: " + orderId);
            return total;
        }

        String orderHistory = order.getOrderHistory();
        if (orderHistory != null && !orderHistory.isEmpty()) {
            // Chercher la ligne qui contient "Total :"
            String[] lines = orderHistory.split("\n");
            for (String line : lines) {
                if (line.trim().startsWith("Total :")) {
                    try {
                        // Extraire la valeur numérique
                        String totalStr = line.split(":")[1].trim();
                        totalStr = totalStr.replace("dt", "").trim();
                        total = Double.parseDouble(totalStr);
                    } catch (Exception e) {
                        System.out.println("Erreur lors de l'extraction du total: " + e.getMessage());
                    }
                }
            }
        }

        return total;
    }

    public void marquerCommandeCommePayeePourUtilisateur(int userId) throws SQLException {
        String sql = "UPDATE `order` SET paid = true WHERE user_id = ? AND paid = false";

        try (PreparedStatement ps = cnx.prepareStatement(sql)) {
            ps.setInt(1, userId);

            int rowsAffected = ps.executeUpdate();

            if (rowsAffected > 0) {
                System.out.println("La commande non payée de l'utilisateur avec ID " + userId + " a été marquée comme payée.");
            } else {
                System.out.println("Aucune commande non payée trouvée pour l'utilisateur avec ID " + userId + ".");
            }
        } catch (SQLException e) {
            System.err.println("Erreur lors de la mise à jour du statut de paiement: " + e.getMessage());
            throw e;
        }
    }

    private String extraireHistoriqueCommande(String historiqueTexte) {
        StringBuilder builder = new StringBuilder();
        double total = 0.0;

        String[] lignes = historiqueTexte.split("\n");
        for (String ligne : lignes) {
            if (ligne.contains(":") && ligne.contains("dt") && !ligne.toLowerCase().contains("total")) {
                builder.append(ligne.trim()).append("\n");
                String prixTexte = ligne.substring(ligne.lastIndexOf(":") + 1).replace("dt", "").trim();
                try {
                    total += Double.parseDouble(prixTexte);
                } catch (NumberFormatException ignored) {}
            }
        }

        builder.append("\nTotal : ").append(String.format("%.2f", total)).append(" dt");
        return builder.toString();
    }

    public double extractTotalFromOrderHistory(int orderId) throws SQLException {
        double total = 0.0;

        // Vérifier si la commande existe
        if (!orderExists(orderId)) {
            System.out.println("Aucune commande trouvée avec l'ID " + orderId + ".");
            return total;
        }

        // Récupérer la commande
        Order order = getOrderById(orderId);
        if (order == null) {
            System.out.println("Impossible de récupérer la commande avec l'ID " + orderId + ".");
            return total;
        }

        String orderHistory = order.getOrderHistory();

        // Si l'historique est vide, calculer le total via la méthode existante
        if (orderHistory == null || orderHistory.isEmpty()) {
            return order.calculateTotal();
        }

        // Essayer d'extraire le total à partir de l'historique
        try {
            // Chercher un pattern comme "Total: XX.XX" dans l'historique
            String[] parts = orderHistory.split("Total:");
            if (parts.length > 1) {
                String totalPart = parts[1].trim();
                // Extraire les chiffres du total (ignorer les devises ou autres caractères)
                String totalValue = totalPart.replaceAll("[^0-9.,]", "").replace(",", ".");
                total = Double.parseDouble(totalValue);
            } else {
                // Si le pattern n'est pas trouvé, utiliser la méthode calculateTotal
                total = order.calculateTotal();
            }
        } catch (NumberFormatException e) {
            System.out.println("Erreur lors de l'extraction du total depuis l'historique: " + e.getMessage());
            // En cas d'erreur, utiliser la méthode calculateTotal comme fallback
            total = order.calculateTotal();
        }

        return total;
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
        order.setPaid(rs.getString("paid"));
        order.setPaymentIntentId(rs.getString("payment_inten_id"));

        return order;
    }


    public boolean updatePaymentIntentId(int orderId, String paymentIntentId) throws SQLException {
        String sql = "UPDATE `order` SET payment_inten_id = ? WHERE id = ?";
        try (PreparedStatement ps = cnx.prepareStatement(sql)) {
            ps.setString(1, paymentIntentId);
            ps.setInt(2, orderId);

            int rowsAffected = ps.executeUpdate();
            if (rowsAffected > 0) {
                System.out.println("PaymentIntentId mis à jour pour la commande ID: " + orderId);
                return true;
            } else {
                System.out.println("Aucune commande mise à jour (ID: " + orderId + ")");
                return false;
            }
        }
    }
    public Order getOrderByPaymentIntentId(String paymentIntentId) throws SQLException {
        String sql = "SELECT o.*, u.name AS user_name, u.lastname AS user_lastname " +
                "FROM `order` o JOIN user u ON o.user_id = u.id " +
                "WHERE o.payment_inten_id = ?";

        try (PreparedStatement ps = cnx.prepareStatement(sql)) {
            ps.setString(1, paymentIntentId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return extractOrderFromResultSet(rs);
                }
            }
        }
        return null;
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
    public boolean isOrderOlderThanDays(int orderId, int days) throws SQLException {
        String query = "SELECT DATEDIFF(NOW(), date_order) AS days_passed FROM `order` WHERE id = ?";
        try (PreparedStatement ps = cnx.prepareStatement(query)) {
            ps.setInt(1, orderId);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                int daysPassed = rs.getInt("days_passed");
                return daysPassed >= days;
            }
            return false;
        }
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
        System.out.println("Statut de paiement: " + ("true".equalsIgnoreCase(order.getPaid()) ? "Payée" : "Non payée"));
        System.out.println("ID d'intention de paiement: " + order.getPaymentIntentId());
        System.out.println("Total estimé: " + order.calculateTotal());
        System.out.println("------------------------------------");
    }

    public void updatePaidStatus(int orderId, String paid) throws SQLException {
        String query = "UPDATE `order` SET paid = ? WHERE id = ?";
        try (PreparedStatement ps = cnx.prepareStatement(query)) {
            ps.setString(1, paid);
            ps.setInt(2, orderId);
            int rowsUpdated = ps.executeUpdate();
            System.out.println("Statut de paiement mis à jour pour la commande ID: " + orderId +
                    ", valeur paid: " + paid + ", lignes affectées: " + rowsUpdated);
            if (rowsUpdated == 0) {
                System.out.println("Aucune commande trouvée avec l'ID: " + orderId);
            }
        } catch (SQLException e) {
            System.err.println("Erreur lors de la mise à jour du statut de paiement: " + e.getMessage());
            throw e;
        }
    }


}