package tn.esprit.Contollers;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import tn.esprit.entities.Product;
import tn.esprit.services.ProductService;

import javafx.event.ActionEvent;

import java.io.IOException;
import java.sql.SQLException;
import java.util.List;

public class AfficherProduct {

    @FXML
    private TextField rcategory;

    @FXML
    private TextField rdescription;

    @FXML
    private TextField rimage;

    @FXML
    private TextField rlist;

    @FXML
    private TextField rname;

    @FXML
    private TextField rprice;


    public void setRcategory(String category) {
        this.rcategory.setText(category);
    }

    public void setRlist(String list) {
        this.rlist.setText(list);
    }

    public void setRname(String name) {
        this.rname.setText(name);
    }

    public void setRdescription(String description) {
        this.rdescription.setText(description);
    }

    public void setRprice(double price) {
        this.rprice.setText(String.valueOf(price));
    }

    public void setRimage(String image) {
        this.rimage.setText(image);
    }
    @FXML
    void deleteP(ActionEvent event) {
        try {
            // Récupérer les valeurs des champs de texte
            String name = rname.getText();
            String description = rdescription.getText();
            String category = rcategory.getText();
            double price = 0;
            try {
                price = Double.parseDouble(rprice.getText());
            } catch (NumberFormatException e) {
                System.out.println("Erreur de conversion du prix: " + e.getMessage());
            }
            String image = rimage.getText();

            // Créer une instance de ProductService
            ProductService productService = new ProductService();

            // Récupérer tous les produits
            List<Product> products = productService.recuperer();

            // Chercher un produit correspondant aux critères
            Product productToDelete = null;
            for (Product product : products) {
                // Vérifier si les attributs correspondent
                boolean nameMatch = product.getName() != null && product.getName().equals(name);
                boolean descMatch = product.getDescription() != null && product.getDescription().equals(description);
                boolean catMatch = product.getCategory() != null && product.getCategory().equals(category);
                boolean priceMatch = Math.abs(product.getPrice() - price) < 0.01; // Comparaison de doubles
                boolean imageMatch = product.getImage() != null && product.getImage().equals(image);

                // Si tous les critères importants correspondent, c'est probablement notre produit
                if (nameMatch && descMatch && catMatch && priceMatch) {
                    productToDelete = product;
                    break;
                }
            }

            // Si un produit correspondant a été trouvé, le supprimer
            if (productToDelete != null) {
                // Supprimer le produit par son ID
                productService.supprimerParId(productToDelete.getId());

                // Afficher un message de succès
                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                alert.setTitle("Suppression réussie");
                alert.setHeaderText(null);
                alert.setContentText("Le produit '" + name + "' a été supprimé avec succès.");
                alert.showAndWait();

                // Retourner à la vue appropriée
                try {
                    FXMLLoader loader = new FXMLLoader(getClass().getResource("/AjouterProduct.fxml"));
                    Parent root = loader.load();
                    rname.getScene().setRoot(root);
                } catch (IOException e) {
                    System.out.println("Erreur lors du chargement de la vue: " + e.getMessage());
                }
            } else {
                // Aucun produit correspondant trouvé
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Erreur de suppression");
                alert.setHeaderText(null);
                alert.setContentText("Aucun produit correspondant aux critères n'a été trouvé.");
                alert.showAndWait();
            }
        } catch (SQLException e) {
            // Gérer les erreurs SQL
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Erreur de base de données");
            alert.setHeaderText(null);
            alert.setContentText("Une erreur est survenue lors de la suppression: " + e.getMessage());
            alert.showAndWait();

            System.out.println("Erreur SQL: " + e.getMessage());
        }
    }
    @FXML
    void editP(ActionEvent event) {
        // Your edit implementation here
    }


}
