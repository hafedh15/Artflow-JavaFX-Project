package tn.esprit.services;

import tn.esprit.entities.Product;
import tn.esprit.entities.User;
import tn.esprit.tools.MyDataBase;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ProductService implements IService<Product> {

    Connection cnx;
    String sql;

    public ProductService() {
        cnx = MyDataBase.getInstance().getCnx();
    }
    @Override
    public List<Product> getProductsByUser(User user) throws SQLException {
        List<Product> products = new ArrayList<>();
        String query = "SELECT * FROM product WHERE user_id = ? AND status = 'dispo'";

        try (PreparedStatement ps = cnx.prepareStatement(query)) {
            ps.setInt(1, user.getId());
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Product product = new Product();
                    product.setId(rs.getInt("id"));
                    product.setName(rs.getString("name"));
                    product.setDescription(rs.getString("description"));
                    product.setPrice(rs.getDouble("price"));
                    product.setStock(rs.getInt("stock"));
                    product.setCategory(rs.getString("category"));
                    product.setImage(rs.getString("image"));
                    product.setStatus(rs.getString("status"));

                    product.setUser(user); // Utilise le user passé en paramètre

                    products.add(product);
                }
            }
        }

        return products;
    }
    // Ajoutez cette méthode à votre classe ProductService
    public List<Product> getProductsByCartId(int cartId) throws SQLException {
        List<Product> products = new ArrayList<>();
        String sql = "SELECT p.* FROM product p " +
                "JOIN cart_product ci ON p.id = ci.product_id " +
                "WHERE ci.cart_id = ?";

        try (PreparedStatement ps = cnx.prepareStatement(sql)) {
            ps.setInt(1, cartId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Product product = new Product();
             //       product.setId(rs.getInt("id"));
                    product.setName(rs.getString("name"));
                    product.setDescription(rs.getString("description"));
                    product.setPrice(rs.getDouble("price"));
                    product.setStock(rs.getInt("stock"));
                    product.setImage(rs.getString("image"));
                    product.setCategory(rs.getString("category"));
                    product.setStatus(rs.getString("status"));
        //            product.setUserId(rs.getInt("user_id"));

                    products.add(product);
                }
            }
        }

        return products;
    }
  @Override
    public void ajouter(Product product) throws SQLException {
        // Spécifier explicitement l'ordre des colonnes tel qu'il apparaît dans votre base de données
        String sql = "INSERT INTO product(user_id, name, description, price, stock, category, image, status) " +
                "VALUES(?, ?, ?, ?, ?, ?, ?, ?)";
        cnx.setAutoCommit(true);
        PreparedStatement ste = cnx.prepareStatement(sql);
            // Toujours affecter user_id = 1
            ste.setInt(1, 1);
            ste.setString(2, product.getName());
            ste.setString(3, product.getDescription());
            ste.setDouble(4, product.getPrice());
            // Toujours mettre le stock à 1
            ste.setInt(5, 1);
            ste.setString(6, product.getCategory());
            ste.setString(7, product.getImage());
            // Toujours définir le status comme "dispo"
            ste.setString(8, "attente");
      System.out.println("Préparation terminée, exécution...");
      ste.executeUpdate();
      System.out.println("product ajouté");
    }

    @Override
    public void modifier(int id, String nom) throws SQLException {

    }

    @Override
    public void modifier(Product product) throws SQLException {

    }

    private boolean userIdExists(int userId) throws SQLException {
        if (userId == 0) return true;

        String sql = "SELECT 1 FROM user WHERE id = ? LIMIT 1";
        try (PreparedStatement ps = cnx.prepareStatement(sql)) {
            ps.setInt(1, userId);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next();
            }
        }
    }
    @Override
    public void modifierP(Product product) throws SQLException {
        String sql = "UPDATE product SET name=?, description=?, price=?, category=?, image=? WHERE id=?";

        cnx.setAutoCommit(true);
        PreparedStatement ste = cnx.prepareStatement(sql);

        ste.setString(1, product.getName());
        ste.setString(2, product.getDescription());
        ste.setDouble(3, product.getPrice());
        ste.setString(4, product.getCategory());
        ste.setString(5, product.getImage());
        ste.setInt(6, product.getId());

        System.out.println("Prepared update statement, executing...");
        int rowsAffected = ste.executeUpdate();
        System.out.println("Product updated, " + rowsAffected + " row(s) affected");
    }
   /* @Override
    public void modifier(int id, String nom) throws SQLException {
        sql = "UPDATE product SET name=? WHERE id=?";
        try (PreparedStatement ps = cnx.prepareStatement(sql)) {
            ps.setString(1, nom);
            ps.setInt(2, id);
            ps.executeUpdate();
            System.out.println("Produit modifié");
        }
    }*/
   public void updateProductStatus(int productId, String newStatus) throws SQLException {
       String sql = "UPDATE product SET status=? WHERE id=?";

       try (PreparedStatement ps = cnx.prepareStatement(sql)) {
           ps.setString(1, newStatus);
           ps.setInt(2, productId);

           int rowsAffected = ps.executeUpdate();
           if (rowsAffected > 0) {
               System.out.println("Statut du produit ID " + productId + " mis à jour avec succès: " + newStatus);
           } else {
               System.out.println("Aucun produit mis à jour - ID " + productId + " non trouvé");
           }
       }
   }
    public void supprimer(int id) throws SQLException {
        String req = "DELETE FROM product WHERE id = ?";
        PreparedStatement ps = cnx.prepareStatement(req);
        ps.setInt(1, id);
        ps.executeUpdate();
        System.out.println("Produit supprimé avec ID : " + id);
    }
    public String getUserEmailByProductId(int productId) throws SQLException {
        String query = "SELECT u.email FROM user u JOIN product p ON u.id = p.user_id WHERE p.id = ?";
        String email = null;

        try (PreparedStatement ps = cnx.prepareStatement(query)) { // Changed 'sql' to 'query'
            ps.setInt(1, productId);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                email = rs.getString("email");
            }
        }

        return email;
    }
    @Override
    public void supprimer(Product product) throws SQLException {
        if (product == null || product.getId() <= 0) {
            throw new IllegalArgumentException("Produit invalide ou ID manquant");
        }

        String sql = "DELETE FROM product WHERE id = ?";

        try (PreparedStatement pstmt = cnx.prepareStatement(sql)) {
            pstmt.setInt(1, product.getId());

            int rowsAffected = pstmt.executeUpdate();

            if (rowsAffected == 0) {
                System.out.println("Aucun produit supprimé - ID " + product.getId() + " non trouvé");
            } else {
                System.out.println("Produit ID " + product.getId() + " supprimé avec succès");
            }
        } catch (SQLException e) {
            System.err.println("Erreur lors de la suppression du produit ID " + product.getId());
            throw e;
        }
    }
    public void supprimerParId(int id) throws SQLException {
        if (id <= 0) {
            throw new IllegalArgumentException("ID de produit invalide");
        }

        String sql = "DELETE FROM product WHERE id = ?";

        try (PreparedStatement pstmt = cnx.prepareStatement(sql)) {
            pstmt.setInt(1, id);

            int rowsAffected = pstmt.executeUpdate();

            if (rowsAffected == 0) {
                System.out.println("Aucun produit supprimé - ID " + id + " non trouvé");
            } else {
                System.out.println("Produit ID " + id + " supprimé avec succès");
            }
        } catch (SQLException e) {
            System.err.println("Erreur lors de la suppression du produit ID " + id);
            throw e;
        }
    }


    public List<Product> recuperer() throws SQLException {
        sql = "SELECT p.*, u.id AS user_id, u.name AS user_name, u.lastname AS user_lastname " +
                "FROM product p LEFT JOIN user u ON p.user_id = u.id";
        Statement st = cnx.createStatement();
        ResultSet rs = st.executeQuery(sql);
        List<Product> products = new ArrayList<>();

        while (rs.next()) {
            int id = rs.getInt("id");

            // Gestion correcte du user_id potentiellement NULL
            int userId = rs.getInt("user_id");
            User user = null;
            if (!rs.wasNull()) {
                user = new User();
                user.setId(userId);
                user.setName(rs.getString("user_name"));
                user.setLastname(rs.getString("user_lastname"));
            }

            String name = rs.getString("name");
            String description = rs.getString("description");
            double price = rs.getDouble("price");
            int stock = rs.getInt("stock");
            String category = rs.getString("category");
            String image = rs.getString("image");
            String status = rs.getString("status");

            Product p = new Product(id, user, name, description, price, stock, category, image, status);
            products.add(p);
        }

        return products;
    }
    public List<Product> recupererDispo() throws SQLException {
        List<Product> allProducts = recuperer(); // Uses your original method
        List<Product> availableProducts = new ArrayList<>();

        for (Product product : allProducts) {
            if ("dispo".equals(product.getStatus())) {
                availableProducts.add(product);
            }
        }

        return availableProducts;
    }
    public List<Product> searchProducts(String searchTerm) throws SQLException {
        List<Product> products = new ArrayList<>();

        // Recherche par nom ou description contenant le terme recherché
        String sql = "SELECT p.*, u.id AS user_id, u.name AS user_name, u.lastname AS user_lastname " +
                "FROM product p LEFT JOIN user u ON p.user_id = u.id " +
                "WHERE p.name LIKE ? OR p.description LIKE ? OR p.category LIKE ?";

        try (PreparedStatement ps = cnx.prepareStatement(sql)) {
            String term = "%" + searchTerm + "%";
            ps.setString(1, term);
            ps.setString(2, term);
            ps.setString(3, term);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    int id = rs.getInt("id");

                    // Gestion correcte du user_id potentiellement NULL
                    int userId = rs.getInt("user_id");
                    User user = null;
                    if (!rs.wasNull()) {
                        user = new User();
                        user.setId(userId);
                        user.setName(rs.getString("user_name"));
                        user.setLastname(rs.getString("user_lastname"));
                    }

                    String name = rs.getString("name");
                    String description = rs.getString("description");
                    double price = rs.getDouble("price");
                    int stock = rs.getInt("stock");
                    String category = rs.getString("category");
                    String image = rs.getString("image");
                    String status = rs.getString("status");

                    Product p = new Product(id, user, name, description, price, stock, category, image, status);
                    products.add(p);
                }
            }
        }

        return products;
    }
    public int getTotalProducts() throws SQLException {
        String query = "SELECT COUNT(*) FROM product WHERE status = 'dispo'";
        try (Statement stmt = cnx.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {
            if (rs.next()) {
                return rs.getInt(1);
            }
        }
        return 0;
    }

    public int getTotalProductsPending() throws SQLException {
        String query = "SELECT COUNT(*) FROM product WHERE status = 'attente'";
        try (Statement stmt = cnx.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {
            if (rs.next()) {
                return rs.getInt(1);
            }
        }
        return 0;
    }
    // Méthode modifiée pour filtrer par catégorie et statut "dispo"
    public List<Product> getProductsByCategory(String category) throws SQLException {
        List<Product> products = new ArrayList<>();
        String sql = "SELECT p.*, u.id AS user_id, u.name AS user_name, u.lastname AS user_lastname " +
                "FROM product p LEFT JOIN user u ON p.user_id = u.id " +
                "WHERE p.category LIKE ? AND p.status = 'dispo'";

        try (PreparedStatement ps = cnx.prepareStatement(sql)) {
            ps.setString(1, category);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    int id = rs.getInt("id");

                    // Gestion correcte du user_id potentiellement NULL
                    int userId = rs.getInt("user_id");
                    User user = null;
                    if (!rs.wasNull()) {
                        user = new User();
                        user.setId(userId);
                        user.setName(rs.getString("user_name"));
                        user.setLastname(rs.getString("user_lastname"));
                    }

                    String name = rs.getString("name");
                    String description = rs.getString("description");
                    double price = rs.getDouble("price");
                    int stock = rs.getInt("stock");
                    String productCategory = rs.getString("category");
                    String image = rs.getString("image");
                    String status = rs.getString("status");

                    Product p = new Product(id, user, name, description, price, stock, productCategory, image, status);
                    products.add(p);
                }
            }
        }

        return products;
    }

    // Méthode modifiée pour filtrer par prix et statut "dispo"
    public List<Product> getProductsByPriceRange(double minPrice, double maxPrice) throws SQLException {
        List<Product> products = new ArrayList<>();
        String sql = "SELECT p.*, u.id AS user_id, u.name AS user_name, u.lastname AS user_lastname " +
                "FROM product p LEFT JOIN user u ON p.user_id = u.id " +
                "WHERE p.price >= ? AND p.price <= ? AND p.status = 'dispo'";

        try (PreparedStatement ps = cnx.prepareStatement(sql)) {
            ps.setDouble(1, minPrice);
            ps.setDouble(2, maxPrice);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    int id = rs.getInt("id");

                    // Gestion correcte du user_id potentiellement NULL
                    int userId = rs.getInt("user_id");
                    User user = null;
                    if (!rs.wasNull()) {
                        user = new User();
                        user.setId(userId);
                        user.setName(rs.getString("user_name"));
                        user.setLastname(rs.getString("user_lastname"));
                    }

                    String name = rs.getString("name");
                    String description = rs.getString("description");
                    double price = rs.getDouble("price");
                    int stock = rs.getInt("stock");
                    String category = rs.getString("category");
                    String image = rs.getString("image");
                    String status = rs.getString("status");

                    Product p = new Product(id, user, name, description, price, stock, category, image, status);
                    products.add(p);
                }
            }
        }

        return products;
    }

    // Méthode pour obtenir toutes les catégories des produits disponibles uniquement
    public List<String> getAllCategories() throws SQLException {
        List<String> categories = new ArrayList<>();
        String query = "SELECT DISTINCT category FROM product WHERE status = 'dispo'";

        try (Statement stmt = cnx.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {
            while (rs.next()) {
                categories.add(rs.getString("category"));
            }
        }

        return categories;
    }

    // Nouvelle méthode pour obtenir tous les produits disponibles
    public List<Product> getAvailableProducts() throws SQLException {
        List<Product> products = new ArrayList<>();
        String sql = "SELECT p.*, u.id AS user_id, u.name AS user_name, u.lastname AS user_lastname " +
                "FROM product p LEFT JOIN user u ON p.user_id = u.id " +
                "WHERE p.status = 'dispo'";

        try (PreparedStatement ps = cnx.prepareStatement(sql)) {
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    int id = rs.getInt("id");

                    // Gestion correcte du user_id potentiellement NULL
                    int userId = rs.getInt("user_id");
                    User user = null;
                    if (!rs.wasNull()) {
                        user = new User();
                        user.setId(userId);
                        user.setName(rs.getString("user_name"));
                        user.setLastname(rs.getString("user_lastname"));
                    }

                    String name = rs.getString("name");
                    String description = rs.getString("description");
                    double price = rs.getDouble("price");
                    int stock = rs.getInt("stock");
                    String category = rs.getString("category");
                    String image = rs.getString("image");
                    String status = rs.getString("status");

                    Product p = new Product(id, user, name, description, price, stock, category, image, status);
                    products.add(p);
                }
            }
        }

        return products;
    }
    // Nouvelle méthode pour obtenir tous les produits disponibles
    public List<Product> getattenteeProducts() throws SQLException {
        List<Product> products = new ArrayList<>();
        String sql = "SELECT p.*, u.id AS user_id, u.name AS user_name, u.lastname AS user_lastname " +
                "FROM product p LEFT JOIN user u ON p.user_id = u.id " +
                "WHERE p.status = 'attente'";

        try (PreparedStatement ps = cnx.prepareStatement(sql)) {
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    int id = rs.getInt("id");

                    // Gestion correcte du user_id potentiellement NULL
                    int userId = rs.getInt("user_id");
                    User user = null;
                    if (!rs.wasNull()) {
                        user = new User();
                        user.setId(userId);
                        user.setName(rs.getString("user_name"));
                        user.setLastname(rs.getString("user_lastname"));
                    }

                    String name = rs.getString("name");
                    String description = rs.getString("description");
                    double price = rs.getDouble("price");
                    int stock = rs.getInt("stock");
                    String category = rs.getString("category");
                    String image = rs.getString("image");
                    String status = rs.getString("status");

                    Product p = new Product(id, user, name, description, price, stock, category, image, status);
                    products.add(p);
                }
            }
        }

        return products;
    }
    public List<Product> getPendingProductsByUser(int userId) throws SQLException {
        List<Product> products = new ArrayList<>();
        String sql = "SELECT p.*, u.id AS user_id, u.name AS user_name, u.lastname AS user_lastname " +
                "FROM product p LEFT JOIN user u ON p.user_id = u.id " +
                "WHERE p.user_id = ? AND p.status = 'attente'";

        try (PreparedStatement ps = cnx.prepareStatement(sql)) {
            ps.setInt(1, userId);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    int id = rs.getInt("id");

                    // Création de l'objet User
                    User user = new User();
                    user.setId(userId);
                    user.setName(rs.getString("user_name"));
                    user.setLastname(rs.getString("user_lastname"));

                    String name = rs.getString("name");
                    String description = rs.getString("description");
                    double price = rs.getDouble("price");
                    int stock = rs.getInt("stock");
                    String category = rs.getString("category");
                    String image = rs.getString("image");
                    String status = rs.getString("status");

                    Product p = new Product(id, user, name, description, price, stock, category, image, status);
                    products.add(p);
                }
            }
        }

        return products;
    }

    public int getProductCountByCategory(String category) throws SQLException {
        String query = "SELECT COUNT(*) AS count FROM product WHERE category = ?";
        try (PreparedStatement stmt = cnx.prepareStatement(query)) {  // <- correction ici
            stmt.setString(1, category);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt("count");
                }
            }
        }
        return 0;
    }


    public String getArtisanNameByProductId(int productId) throws SQLException {
        String artisanName = null;
        String query = "SELECT u.name, u.lastname FROM user u " +
                "JOIN product p ON u.id = p.user_id " +
                "WHERE p.id = ?";

        try (PreparedStatement stmt = cnx.prepareStatement(query)) {
            stmt.setInt(1, productId);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    String name = rs.getString("name");
                    String lastname = rs.getString("lastname");

                    // Format the name appropriately
                    if (name != null && lastname != null) {
                        artisanName = name + " " + lastname;
                    } else if (name != null) {
                        artisanName = name;
                    } else if (lastname != null) {
                        artisanName = lastname;
                    } else {
                        artisanName = "Artisan"; // Default if both are null
                    }
                }
            }
        }

        return artisanName;
    }
}

