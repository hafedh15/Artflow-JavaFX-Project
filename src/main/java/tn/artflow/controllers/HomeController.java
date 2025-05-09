package tn.artflow.controllers;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.TilePane;
import javafx.stage.Stage;
import java.net.URL;
import java.io.IOException;
import javafx.scene.control.MenuButton;
import javafx.scene.control.MenuItem;
import tn.artflow.services.*;
import tn.artflow.entities.*;
import tn.artflow.controllers.*;

public class HomeController {

    @FXML
    private AnchorPane contentArea;
    @FXML
    private TilePane productContainer;

    private Object currentUser; // Utilisez le type approprié à votre modèle d'utilisateur

    public void initData(Object user) {
        this.currentUser = user;
        // Initialiser l'interface en fonction de l'utilisateur si nécessaire
    }

    @FXML
    private void handleAfficherEvent() {
        // Correction du nom du fichier (faute d'orthographe)
        loadUI("/frontWorkshop.fxml");
    }

    @FXML
    private void handleAfficherTextile() {
        // Correction du nom du fichier (faute d'orthographe)
        loadUI("/showtextile.fxml");
    }
    @FXML
    private void handleAfficherCollection() {
        // Correction du nom du fichier (faute d'orthographe)
        loadUI("/show1.fxml");
    }


    @FXML
    private void handleAfficherCeramique() {
        // Correction du nom du fichier (faute d'orthographe)
        loadUI("/show.fxml");
    }

    @FXML
    private void handleAfficherArticle() {
        loadUI("/AfficheArticleFront.fxml");
    }

    @FXML
    private void handleAjouterReclamation() {
        loadUI("/AjouterReclamation.fxml");
    }

    @FXML
    private void handleAjouterReponse() {
        loadUI("/ClientConversation.fxml");
    }

    @FXML
    private void handleAfficherProduct() {
        loadUI("/ListProductFront.fxml");
    }

    @FXML
    private void handleAfficherCart() {
        loadUI("/Cart.fxml");
    }

    @FXML
    private void handleProfil() {
        loadUI("/profilp.fxml");
    }

    @FXML
    private void handleAfficherMusique() {
        loadUI("/AfficherMusiqueEtPlaylists.fxml");
    }

    @FXML
    private void handleAfficherRecommendations() {
        try {
            System.out.println("Tentative de chargement de Recommendations.fxml");
            URL recommendationsUrl = getClass().getResource("/Recommendations.fxml");
            if (recommendationsUrl == null) {
                // Essayer d'autres chemins possibles
                recommendationsUrl = getClass().getResource("/Views/Recommendations.fxml");
                if (recommendationsUrl == null) {
                    recommendationsUrl = getClass().getResource("/FXML/Recommendations.fxml");
                    if (recommendationsUrl == null) {
                        recommendationsUrl = getClass().getClassLoader().getResource("Recommendations.fxml");
                        if (recommendationsUrl == null) {
                            throw new IOException("Impossible de trouver le fichier Recommendations.fxml dans les ressources");
                        }
                    }
                }
            }
            System.out.println("URL de la ressource trouvée : " + recommendationsUrl);

            // Charger la vue des recommandations
            FXMLLoader loader = new FXMLLoader(recommendationsUrl);
            Parent recommendationsView = loader.load();

            // Vider le contenu actuel
            contentArea.getChildren().clear();

            // Adapter la vue à la taille du contentArea
            AnchorPane.setTopAnchor(recommendationsView, 0.0);
            AnchorPane.setRightAnchor(recommendationsView, 0.0);
            AnchorPane.setBottomAnchor(recommendationsView, 0.0);
            AnchorPane.setLeftAnchor(recommendationsView, 0.0);

            // Ajouter la vue des recommandations dans le contentArea
            contentArea.getChildren().add(recommendationsView);
            System.out.println("Recommendations.fxml chargé avec succès");
        } catch (IOException e) {
            System.out.println("❌ Erreur lors du chargement des recommandations : " + e.getMessage());
            e.printStackTrace();
            showErrorAlert("Erreur de chargement", "Impossible de charger la vue des recommandations", e.getMessage());
        }
    }

    private void loadUI(String fxmlPath) {
        try {
            System.out.println("Tentative de chargement de " + fxmlPath);
            URL resourceUrl = getClass().getResource(fxmlPath);

            // Si le chemin direct ne fonctionne pas, essayer d'autres chemins possibles
            if (resourceUrl == null) {
                System.out.println("URL null pour " + fxmlPath + ", essai d'autres chemins...");

                // Essayer sans le slash initial
                String pathWithoutSlash = fxmlPath.startsWith("/") ? fxmlPath.substring(1) : fxmlPath;
                resourceUrl = getClass().getClassLoader().getResource(pathWithoutSlash);

                // Essayer dans le dossier Views
                if (resourceUrl == null) {
                    String viewsPath = "/Views" + fxmlPath;
                    resourceUrl = getClass().getResource(viewsPath);
                    System.out.println("Essai avec " + viewsPath);
                }

                // Essayer dans le dossier FXML
                if (resourceUrl == null) {
                    String fxmlDirPath = "/FXML" + fxmlPath;
                    resourceUrl = getClass().getResource(fxmlDirPath);
                    System.out.println("Essai avec " + fxmlDirPath);
                }

                // Si toujours null, échec
                if (resourceUrl == null) {
                    throw new IOException("Impossible de trouver le fichier " + fxmlPath + " dans les ressources");
                }
            }

            System.out.println("URL de la ressource trouvée : " + resourceUrl);

            // Utilisez un FXMLLoader pour plus de contrôle
            FXMLLoader loader = new FXMLLoader(resourceUrl);
            try {
                Parent root = loader.load();

                // Vider et ajouter le nouveau contenu
                contentArea.getChildren().clear();

                // Adapter la vue à la taille du contentArea
                AnchorPane.setTopAnchor(root, 0.0);
                AnchorPane.setRightAnchor(root, 0.0);
                AnchorPane.setBottomAnchor(root, 0.0);
                AnchorPane.setLeftAnchor(root, 0.0);

                contentArea.getChildren().add(root);
                System.out.println(fxmlPath + " chargé avec succès");
            } catch (Exception e) {
                System.out.println("Erreur spécifique lors du chargement : " + e.getMessage());
                e.printStackTrace();

                if (loader.getController() != null) {
                    System.out.println("Contrôleur créé : " + loader.getController().getClass().getName());
                } else {
                    System.out.println("Aucun contrôleur n'a été créé");
                }

                showErrorAlert("Erreur de chargement", "Impossible de charger " + fxmlPath, e.getMessage());
                throw e;
            }
        } catch (IOException e) {
            System.out.println("❌ Erreur complète lors du chargement de " + fxmlPath + " : " + e);
            e.printStackTrace();
            showErrorAlert("Erreur de chargement", "Impossible de charger " + fxmlPath, e.getMessage());
        }
    }

    private void showErrorAlert(String title, String header, String content) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(header);
        alert.setContentText(content);
        alert.showAndWait();
    }

    public void handleOpenDashboard() {
        try {
            // Charge le FXML (ajustez le chemin selon votre structure)
            URL fxmlUrl = getClass().getResource("/AdminEvents.fxml");
            if (fxmlUrl == null) {
                // Essayer d'autres chemins possibles
                fxmlUrl = getClass().getResource("/Views/AdminEvents.fxml");
                if (fxmlUrl == null) {
                    fxmlUrl = getClass().getResource("/FXML/AdminEvents.fxml");
                    if (fxmlUrl == null) {
                        throw new IOException("Fichier FXML non trouvé : AdminEvents.fxml");
                    }
                }
            }

            FXMLLoader loader = new FXMLLoader(fxmlUrl);
            Parent root = loader.load();

            // Crée la fenêtre
            Stage stage = new Stage();
            stage.setScene(new Scene(root));
            stage.setTitle("Dashboard");
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
            showErrorAlert("Erreur Dashboard", "Impossible d'ouvrir le dashboard", e.getMessage());
        }
    }

    public void goToProfil(ActionEvent actionEvent) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/profil.fxml"));
            Parent root = loader.load();
            Stage stage = new Stage();
            stage.setTitle("Profil");
            stage.setScene(new Scene(root));
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    public void profil(ActionEvent actionEvent) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/profilp.fxml"));
            Parent profilPage = loader.load();

            // Get the stage from the event source
            Stage stage = (Stage) ((javafx.scene.Node) actionEvent.getSource()).getScene().getWindow();

            // Set the new scene
            stage.setScene(new Scene(profilPage));
            stage.show();
        } catch (IOException e) {
            showAlert(Alert.AlertType.ERROR, "Erreur",
                    "Impossible de charger la page profil: " + e.getMessage());
            e.printStackTrace();
        }
    }

    @FXML
    public void openCart(ActionEvent actionEvent) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/Cart.fxml"));
            Parent cartPage = loader.load();

            // Get the stage from the event source
            Stage stage = (Stage) ((javafx.scene.Node) actionEvent.getSource()).getScene().getWindow();

            // Set the new scene
            stage.setScene(new Scene(cartPage));
            stage.show();
        } catch (IOException e) {
            showAlert(Alert.AlertType.ERROR, "Erreur",
                    "Impossible de charger la page panier: " + e.getMessage());
            e.printStackTrace();
        }
    }
    private void showAlert(Alert.AlertType type, String title, String message) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

}