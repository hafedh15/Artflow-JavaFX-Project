package tn.artflow.services;

import tn.artflow.entities.Cart;
import tn.artflow.entities.Product;
import tn.artflow.entities.User;
import tn.artflow.tools.MyDataBase;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class CartService implements IService<Cart> {
    Connection cnx;

    public CartService() {
        cnx = MyDataBase.getInstance().getCnx();
    }


    public List<Product> getProductsByUser(User user) throws SQLException {
        String cartIdQuery = "SELECT id FROM cart WHERE user_id = ?";
        try (PreparedStatement ps = cnx.prepareStatement(cartIdQuery)) {
            ps.setInt(1, user.getId());
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                int cartId = rs.getInt("id");
                return getProductsByCartId(cartId);
            }
        }
        return new ArrayList<>();
    }
    public void updateCartTotalPrice(int cartId) throws SQLException {
        String sql = "SELECT SUM(p.price) FROM product p " +
                "JOIN cart_product cp ON p.id = cp.product_id " +
                "WHERE cp.cart_id = ?";
        try (PreparedStatement ps = cnx.prepareStatement(sql)) {
            ps.setInt(1, cartId);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                double totalPrice = rs.getDouble(1);
                String updateSql = "UPDATE cart SET total_price = ? WHERE id = ?";
                try (PreparedStatement updatePs = cnx.prepareStatement(updateSql)) {
                    updatePs.setDouble(1, totalPrice);
                    updatePs.setInt(2, cartId);
                    updatePs.executeUpdate();
                }
            }
        }
    }
    public void retirerProduitDuPanier(int cartId, int productId) throws SQLException {
        String sql = "DELETE FROM cart_product WHERE cart_id = ? AND product_id = ?";
        try (PreparedStatement ps = cnx.prepareStatement(sql)) {
            ps.setInt(1, cartId);
            ps.setInt(2, productId);
            int rowsAffected = ps.executeUpdate();
            if (rowsAffected > 0) {
                System.out.println("Produit retiré du panier.");
                updateCartTotalPrice(cartId);
            } else {
                System.out.println("Aucune correspondance trouvée dans le panier.");
            }
        }
    }
    public boolean userHasCart(int userId) throws SQLException {
        String sql = "SELECT COUNT(*) FROM cart WHERE user_id = ?";
        try (PreparedStatement ps = cnx.prepareStatement(sql)) {
            ps.setInt(1, userId);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return rs.getInt(1) > 0;
            }
        }
        return false;
    }
    public int getCartIdByUserId(int userId) throws SQLException {
        String sql = "SELECT id FROM cart WHERE user_id = ?";
        try (PreparedStatement ps = cnx.prepareStatement(sql)) {
            ps.setInt(1, userId);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return rs.getInt("id");
            }
        }
        return -1; // Retourne -1 si aucun panier trouvé
    }
    public void displayProductsByUserId(int userId) throws SQLException {
        if (!userHasCart(userId)) {
            System.out.println("Cet utilisateur n'a pas de panier.");
            return;
        }

        int cartId = getCartIdByUserId(userId);
        List<Product> products = getProductsByCartId(cartId);

        if (products.isEmpty()) {
            System.out.println("Le panier est vide.");
        } else {
            System.out.println("Produits dans le panier de l'utilisateur ID " + userId + ":");
            for (Product product : products) {
                System.out.println(product); // Assure-toi que toString() est bien défini dans Product
            }
        }
    }


    @Override
    public void ajouter(Cart cart) throws SQLException {
        // Vérifier si l'utilisateur a déjà un panier
        String checkQuery = "SELECT COUNT(*) FROM cart WHERE user_id = ?";
        try (PreparedStatement checkStmt = cnx.prepareStatement(checkQuery)) {
            checkStmt.setInt(1, cart.getUser().getId());
            ResultSet rs = checkStmt.executeQuery();
            if (rs.next() && rs.getInt(1) > 0) {
                System.out.println("L'utilisateur (ID: " + cart.getUser().getId() + ") a déjà un panier.");
                return; // Important: Sortir de la méthode pour éviter de créer un nouveau panier
            }
        }

        // Cette partie ne s'exécute que si l'utilisateur n'a pas de panier
        String sql = "INSERT INTO cart(user_id, total_price) VALUES(?, ?)";
        try (PreparedStatement ste = cnx.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ste.setInt(1, cart.getUser().getId());
            ste.setDouble(2, cart.getTotalPrice());
            ste.executeUpdate();

            try (ResultSet generatedKeys = ste.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    cart.setId(generatedKeys.getInt(1));
                }
            }
            System.out.println("Panier ajouté avec succès. ID: " + cart.getId());
        }
    }

    @Override
    public void modifier(Cart cart) throws SQLException {

    }

    public void updateCartTotal(Cart cart) throws SQLException {
        String sql = "UPDATE cart SET total_price = ? WHERE id = ?";
        try (PreparedStatement ste = cnx.prepareStatement(sql)) {
            ste.setDouble(1, cart.getTotalPrice());
            ste.setInt(2, cart.getId());
            ste.executeUpdate();
            System.out.println("Total du panier mis à jour : " + cart.getTotalPrice() + " TND");
        }
    }


    public void modifier(int id, String nom) throws SQLException {

    }

    public void modifier(Product product) throws SQLException {

    }

    public void modifierP(Product product) throws SQLException {

    }

    public void supprimerParId(int cartId) throws SQLException {
        // Vérifier d'abord si le panier existe
        String checkSql = "SELECT COUNT(*) FROM cart WHERE id = ?";
        try (PreparedStatement checkPs = cnx.prepareStatement(checkSql)) {
            checkPs.setInt(1, cartId);
            ResultSet rs = checkPs.executeQuery();
            rs.next();
            int count = rs.getInt(1);

            if (count == 0) {
                System.out.println("Aucun panier trouvé avec l'ID " + cartId + ".");
                return;
            }

            // Le panier existe, procéder à la suppression
            String deleteSql = "DELETE FROM cart WHERE id = ?";
            try (PreparedStatement deletePs = cnx.prepareStatement(deleteSql)) {
                deletePs.setInt(1, cartId);
                deletePs.executeUpdate();
                System.out.println("Panier avec ID " + cartId + " supprimé avec succès.");
            }
        }
    }

    public void supprimer(Cart cart) throws SQLException {
        if (cart == null || cart.getId() <= 0) {
            System.out.println("Panier invalide.");
            return;
        }

        // Vérifier d'abord si le panier existe
        String checkSql = "SELECT COUNT(*) FROM cart WHERE id = ?";
        try (PreparedStatement checkPs = cnx.prepareStatement(checkSql)) {
            checkPs.setInt(1, cart.getId());
            ResultSet rs = checkPs.executeQuery();
            rs.next();
            int count = rs.getInt(1);

            if (count == 0) {
                System.out.println("Ce panier n'existe pas dans la base de données.");
                return;
            }

            // Le panier existe, procéder à la suppression
            String deleteSql = "DELETE FROM cart WHERE id = ?";
            try (PreparedStatement deletePs = cnx.prepareStatement(deleteSql)) {
                deletePs.setInt(1, cart.getId());
                deletePs.executeUpdate();
                System.out.println("Panier supprimé avec succès.");
            }
        }
    }


    @Override
  public List<Cart> recuperer() throws SQLException {
        String sql = "SELECT c.*, u.name AS user_name FROM cart c JOIN user u ON c.user_id = u.id";
        List<Cart> carts = new ArrayList<>();

        try (Statement st = cnx.createStatement(); ResultSet rs = st.executeQuery(sql)) {
            while (rs.next()) {
                int id = rs.getInt("id");
                double totalPrice = rs.getDouble("total_price");
                User user = new User(rs.getInt("user_id"), rs.getString("user_name"));
                Cart cart = new Cart(id, user);
                cart.setTotalPrice(totalPrice);
                carts.add(cart);
            }
        }
        return carts;
    }

    @Override
    public void supprimer(int id) throws SQLException {

    }

    @Override
    public List<Cart> getReservationsByUser(User user) throws SQLException {
        return List.of();
    }

    // Ajoutez cette méthode à votre classe CartService
    public List<Product> getProductsByCartId(int cartId) throws SQLException {
        String sql = "SELECT p.*, cp.cart_id, u.id AS owner_id, u.name AS owner_name, " +
                "u.lastname AS owner_lastname, u.roles AS owner_type, u.email AS owner_email " +
                "FROM product p " +
                "JOIN cart_product cp ON p.id = cp.product_id " +
                "JOIN user u ON p.user_id = u.id " +
                "WHERE cp.cart_id = ?";

        List<Product> products = new ArrayList<>();

        try (PreparedStatement ps = cnx.prepareStatement(sql)) {
            ps.setInt(1, cartId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    // Créer l'utilisateur propriétaire du produit
                    User productOwner = new User();
                    productOwner.setId(rs.getInt("owner_id"));
                    productOwner.setName(rs.getString("owner_name"));
                    productOwner.setLastname(rs.getString("owner_lastname"));
                    productOwner.setRoles(rs.getString("owner_type"));
                    productOwner.setEmail(rs.getString("owner_email"));

                    // Créer le produit
                    Product product = new Product();
                    product.setId(rs.getInt("id"));
                    product.setName(rs.getString("name"));
                    product.setDescription(rs.getString("description"));
                    product.setPrice(rs.getDouble("price"));
                    product.setStock(rs.getInt("stock"));
                    product.setCategory(rs.getString("category"));
                    product.setImage(rs.getString("image"));
                    product.setStatus(rs.getString("status"));
                    product.setUser(productOwner);

                    products.add(product);
                }
            }
        }

        return products;
    }
    public void displayAllCartsWithProducts() {
        try {
            // Récupérer tous les paniers avec leurs utilisateurs
            String cartQuery = "SELECT c.id as cart_id, c.total_price, u.id as user_id, u.name, u.lastname " +
                    "FROM cart c JOIN user u ON c.user_id = u.id";

            PreparedStatement cartStmt = cnx.prepareStatement(cartQuery);
            ResultSet cartResult = cartStmt.executeQuery();

            System.out.println("=== AFFICHAGE DE TOUS LES PANIERS AVEC LEURS DÉTAILS ===");

            while (cartResult.next()) {
                int cartId = cartResult.getInt("cart_id");
                double totalPrice = cartResult.getDouble("total_price");
                int userId = cartResult.getInt("user_id");
                String userName = cartResult.getString("name");
                String userLastname = cartResult.getString("lastname");

                System.out.println("\nPanier ID: " + cartId + " - Utilisateur: " + userName + " " + userLastname + " (ID: " + userId + ")");
                System.out.println("Prix total: " + totalPrice);

                // Récupérer les produits de ce panier
                List<Product> products = getProductsByCartId(cartId);

                if (products.isEmpty()) {
                    System.out.println("Ce panier ne contient aucun produit.");
                } else {
                    System.out.println("Produits dans ce panier:");
                    for (Product product : products) {
                        System.out.println("- Produit ID: " + product.getId() + ", Nom: " + product.getName() + ", Prix: " + product.getPrice());
                    }
                }
            }

            cartResult.close();
            cartStmt.close();

        } catch (SQLException e) {
            System.out.println("Erreur SQL : " + e.getMessage());
            e.printStackTrace();
        }
    }
    public void displayCartForSpecificUser(int userId) {
        try {
            // Récupérer le panier de l'utilisateur spécifié
            String cartQuery = "SELECT c.id as cart_id, c.total_price, u.id as user_id, u.name, u.lastname " +
                    "FROM cart c JOIN user u ON c.user_id = u.id " +
                    "WHERE u.id = ?";

            PreparedStatement cartStmt = cnx.prepareStatement(cartQuery);
            cartStmt.setInt(1, userId);
            ResultSet cartResult = cartStmt.executeQuery();

            System.out.println("=== AFFICHAGE DU PANIER DE L'UTILISATEUR (ID: " + userId + ") ===");

            if (cartResult.next()) {
                int cartId = cartResult.getInt("cart_id");
                double totalPrice = cartResult.getDouble("total_price");
                String userName = cartResult.getString("name");
                String userLastname = cartResult.getString("lastname");

                System.out.println("\nPanier ID: " + cartId + " - Utilisateur: " + userName + " " + userLastname + " (ID: " + userId + ")");
                System.out.println("Prix total: " + totalPrice);

                // Récupérer les produits de ce panier
                List<Product> products = getProductsByCartId(cartId);

                if (products.isEmpty()) {
                    System.out.println("Ce panier ne contient aucun produit.");
                } else {
                    System.out.println("Produits dans ce panier:");
                    for (Product product : products) {
                        System.out.println("- Produit ID: " + product.getId() + ", Nom: " + product.getName() + ", Prix: " + product.getPrice());
                    }
                }
            } else {
                System.out.println("Aucun panier trouvé pour l'utilisateur avec l'ID: " + userId);
            }

            cartResult.close();
            cartStmt.close();

        } catch (SQLException e) {
            System.out.println("Erreur SQL : " + e.getMessage());
            e.printStackTrace();
        }
    }
    public void displaySpecificCart(int cartId) {
        try {
            // Récupérer le panier spécifié
            String cartQuery = "SELECT c.id as cart_id, c.total_price, u.id as user_id, u.name, u.lastname " +
                    "FROM cart c JOIN user u ON c.user_id = u.id " +
                    "WHERE c.id = ?";

            PreparedStatement cartStmt = cnx.prepareStatement(cartQuery);
            cartStmt.setInt(1, cartId);
            ResultSet cartResult = cartStmt.executeQuery();

            System.out.println("=== AFFICHAGE DU PANIER (ID: " + cartId + ") ===");

            if (cartResult.next()) {
                double totalPrice = cartResult.getDouble("total_price");
                int userId = cartResult.getInt("user_id");
                String userName = cartResult.getString("name");
                String userLastname = cartResult.getString("lastname");

                System.out.println("\nPanier ID: " + cartId + " - Utilisateur: " + userName + " " + userLastname + " (ID: " + userId + ")");
                System.out.println("Prix total: " + totalPrice);

                // Récupérer les produits de ce panier
                List<Product> products = getProductsByCartId(cartId);

                if (products.isEmpty()) {
                    System.out.println("Ce panier ne contient aucun produit.");
                } else {
                    System.out.println("Produits dans ce panier:");
                    for (Product product : products) {
                        System.out.println("- Produit ID: " + product.getId() + ", Nom: " + product.getName() + ", Prix: " + product.getPrice());
                    }
                }
            } else {
                System.out.println("Aucun panier trouvé avec l'ID: " + cartId);
            }

            cartResult.close();
            cartStmt.close();

        } catch (SQLException e) {
            System.out.println("Erreur SQL : " + e.getMessage());
            e.printStackTrace();
        }
    }

   public void ajouterProduitAuPanier(int cartId, int productId) throws SQLException {
       // Vérifier si le panier existe
       String checkCartQuery = "SELECT COUNT(*) FROM cart WHERE id = ?";
       try (PreparedStatement checkCartStmt = cnx.prepareStatement(checkCartQuery)) {
           checkCartStmt.setInt(1, cartId);
           ResultSet cartRs = checkCartStmt.executeQuery();
           if (cartRs.next() && cartRs.getInt(1) == 0) {
               System.out.println(" Le panier avec l'ID " + cartId + " n'existe pas.");
               return;
           }
       }

       // Vérifier si le produit existe
       String checkProductQuery = "SELECT COUNT(*) FROM product WHERE id = ?";
       try (PreparedStatement checkProductStmt = cnx.prepareStatement(checkProductQuery)) {
           checkProductStmt.setInt(1, productId);
           ResultSet productRs = checkProductStmt.executeQuery();
           if (productRs.next() && productRs.getInt(1) == 0) {
               System.out.println(" Le produit avec l'ID " + productId + " n'existe pas.");
               return;
           }
       }

       // Vérifier si le produit est déjà dans le panier
       String checkQuery = "SELECT COUNT(*) FROM cart_product WHERE cart_id = ? AND product_id = ?";
       try (PreparedStatement checkStmt = cnx.prepareStatement(checkQuery)) {
           checkStmt.setInt(1, cartId);
           checkStmt.setInt(2, productId);
           ResultSet rs = checkStmt.executeQuery();
           if (rs.next() && rs.getInt(1) > 0) {
               System.out.println("Ce produit (ID: " + productId + ") est déjà dans le panier (ID: " + cartId + ").");
               return; // Sortir de la méthode sans ajouter le produit
           }
       }

       // Si le panier et le produit existent et que le produit n'est pas dans le panier, l'ajouter
       String sql = "INSERT INTO cart_product(cart_id, product_id) VALUES(?, ?)";
       try (PreparedStatement ps = cnx.prepareStatement(sql)) {
           ps.setInt(1, cartId);
           ps.setInt(2, productId);
           ps.executeUpdate();
           System.out.println("Produit (ID: " + productId + ") ajouté au panier (ID: " + cartId + ").");
       }
   }

 public void supprimerProduitDuPanier(int cartId, int productId) throws SQLException {
     // Vérifier si le produit existe dans le panier
     String checkQuery = "SELECT COUNT(*) FROM cart_product WHERE cart_id = ? AND product_id = ?";
     try (PreparedStatement checkStmt = cnx.prepareStatement(checkQuery)) {
         checkStmt.setInt(1, cartId);
         checkStmt.setInt(2, productId);
         ResultSet rs = checkStmt.executeQuery();
         if (rs.next() && rs.getInt(1) == 0) {
             System.out.println("Ce produit (ID: " + productId + ") n'existe pas dans le panier (ID: " + cartId + ").");
             return; // Sortir de la méthode sans tenter de supprimer
         }
     }

     // Si le produit existe dans le panier, le supprimer
     String sql = "DELETE FROM cart_product WHERE cart_id = ? AND product_id = ?";
     try (PreparedStatement ps = cnx.prepareStatement(sql)) {
         ps.setInt(1, cartId);
         ps.setInt(2, productId);
         int rowsAffected = ps.executeUpdate();
         if (rowsAffected > 0) {
             System.out.println("Produit (ID: " + productId + ") supprimé du panier (ID: " + cartId + ").");
         }
     }
 }

    public Cart getPanierParUserId(int userId) throws SQLException {
        String query = "SELECT c.*, u.name, u.lastname, u.roles, u.email FROM cart c " +
                "JOIN user u ON c.user_id = u.id " +
                "WHERE u.id = ?";

        try (PreparedStatement stmt = cnx.prepareStatement(query)) {
            stmt.setInt(1, userId);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                // Créer l'objet User
                User user = new User();
                user.setId(userId);
                user.setName(rs.getString("name"));
                user.setLastname(rs.getString("lastname"));
                user.setRoles(rs.getString("roles"));
                user.setEmail(rs.getString("email"));

                // Créer l'objet Cart
                Cart cart = new Cart();
                cart.setId(rs.getInt("id"));
                cart.setTotalPrice(rs.getDouble("total_price"));
                cart.setUser(user);

                return cart;
            }
        }

        return null; // Retourne null si aucun panier n'est trouvé pour cet utilisateur
    }


}
