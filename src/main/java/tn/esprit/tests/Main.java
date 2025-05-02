package tn.esprit.tests;

import tn.esprit.entities.Cart;
import tn.esprit.entities.Order;
import tn.esprit.entities.Product;
import tn.esprit.entities.User;
import tn.esprit.services.CartService;
import tn.esprit.services.OrderService;
import tn.esprit.services.ProductService;
import tn.esprit.tools.MyDataBase;

import java.sql.SQLException;
import java.util.List;

public class Main {
    public static void main(String[] args) {
        MyDataBase md = MyDataBase.getInstance();
        ProductService ps = new ProductService();
        CartService cs = new CartService();
        OrderService os = new OrderService();
        // Création d'un utilisateur pour l'affecter au produit
        User user = new User();
        user.setId(1); // Supposons que l'utilisateur avec ID 1 existe dans la base
        User user1 = new User();
        user1.setId(2);

        // Création de produits
        Product p = new Product();
        p.setUser(user);
        p.setName("poo");
        p.setDescription("ali");
        p.setPrice(0.0);
        p.setStock(0);
        p.setCategory("Non spécifié");
        p.setImage("");
        p.setStatus("Actif");

        Product p1 = new Product();
        p1.setUser(user);
        p1.setName("Lap");
        p1.setDescription("Ordinateur portable performant");
        p1.setPrice(1200.0);
        p1.setStock(10);
        p1.setCategory("Électronique");
        p1.setImage("laptop.jpg");
        p1.setStatus("Disponible");

        Product p12 = new Product();
        p12.setUser(user);
        p12.setName("lojloj");
        p12.setDescription("Ordinateur portable performant");
        p12.setPrice(1200.0);
        p12.setStock(10);
        p12.setCategory("Électronique");
        p12.setImage("laptop.jpg");
        p12.setStatus("Disponible");
        // Créer l'objet Order

        // Créer d'abord le panier
        Cart cart = new Cart();
      cart.setId(33);
      cart.setUser(user);

// Puis l'utiliser dans l'ordre
        Order order = new Order();
        order.setCart(cart);
        order.setUser(user);
        order.setDeliveryAddress("123 Rue de la Livraison, 75000 Paris");
        order.setPhoneNumber("12345678");
        order.setDateOrder(new java.util.Date());
        order.setOrderHistory("Commande créée");
        order.setPaid("false");
        order.setPaymentIntentId("");

        try {
            // Exemples CRUD
             ps.ajouter(p);
            // ps.modifier(21, "ben ahmed");
            // System.out.println(ps.recuperer());
            // p.setId(24); // ID du produit à supprimer
            // ps.supprimer(p);

            // Ajouter un produit
            // ps.ajouter(p);
            // System.out.println("Produit ajouté : " + p);

            // Afficher les produits
            //        System.out.println("Liste des produits :");
            //      ps.recuperer().forEach(System.out::println);

            // Gestion panier
            cart = new Cart();
            cart.setUser(user);
          //  cs.ajouter(cart);
            cart.setUser(user1);
          //  cs.ajouter(cart);
            // cs.supprimer(cart);
           // cs.ajouterProduitAuPanier(33, 14);
          //  cs.ajouterProduitAuPanier(33, 27);
            // Afficher un panier spécifique (remplacez 1 par l'ID du panier souhaité)
         //   cs.displaySpecificCart(3);
            // Afficher le panier d'un utilisateur spécifique (remplacez 1 par l'ID de l'utilisateur souhaité)
            cs.displayCartForSpecificUser(1);
            cs.supprimerProduitDuPanier(3, 15);
            cs.supprimerProduitDuPanier(3, 14);
            System.out.println("\n--- Test de supprimerParId avec un ID existant ---");
            cs.supprimerParId(3); // Remplacez 3 par un ID qui existe dans votre base
            // Test de supprimerParId avec un ID inexistant
            System.out.println("\n--- Test de supprimerParId avec un ID inexistant ---");
            cs.supprimerParId(999); // ID qui n'existe probablement pas
            // Test de supprimer avec un objet Cart existant
            System.out.println("\n--- Test de supprimer avec un objet Cart existant ---");
            Cart cartToDelete = new Cart();
            cartToDelete.setId(4); // Remplacez 4 par un ID qui existe dans votre base
            cs.supprimer(cartToDelete);


            // Récupérer les produits d’un utilisateur
            List<Product> products = ps.getProductsByUser(user);
            for (Product p4 : products) {
                System.out.println("Produit: " + p4.getName() + ", utilisateur ID: " + p4.getUser().getId());
            }
            cs.displayAllCartsWithProducts();
//order tests
           // os.ajouter(order);
            // ID de la commande à tester
            int orderId = 6;

            // Tester si la commande existe
         boolean exists = os.orderExists(orderId);
            System.out.println("La commande " + orderId + " existe: " + exists);

            if (exists) {
                // Mettre à jour le statut de paiement
                System.out.println("\n--- Test de mise à jour du statut de paiement ---");
                os.mettreAJourStatutPaiement(orderId, true);

                // Mettre à jour l'adresse de livraison
                System.out.println("\n--- Test de mise à jour de l'adresse de livraison ---");
                os.mettreAJourAdresseLivraison(orderId, "123 Nouvelle Rue, Ville");

                // Mettre à jour les coordonnées complètes
                System.out.println("\n--- Test de mise à jour des coordonnées complètes ---");
             //   os.mettreAJourContactLivraison(orderId, "456 Avenue Principale, Ville", "0612345678");
            } else {
                System.out.println("Impossible de tester les fonctions car la commande n'existe pas.");
            }
            os.displayAllOrders();
            os.displaySpecificOrder(6);
            os.displayOrdersForUser(1);
            Order orderById = os.getOrderById(6);
            if (orderById != null) {
                System.out.println("Commande récupérée avec succès. ID: " + orderById.getId());
            } else {
                System.out.println("Aucune commande trouvée avec l'ID 6.");
            }
            os.supprimerParId(6);
        } catch (SQLException e) {
            System.out.println("Erreur SQL : " + e.getMessage());
            e.printStackTrace();
        } catch (Exception e) {
            System.out.println("Erreur générale : " + e.getMessage());
            e.printStackTrace();
        }
    }
}
