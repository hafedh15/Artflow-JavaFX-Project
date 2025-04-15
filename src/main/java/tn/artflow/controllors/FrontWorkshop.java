package tn.artflow.controllors;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.*;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.TilePane;
import tn.artflow.entities.Reservation;
import tn.artflow.entities.User;
import tn.artflow.entities.Workshop;
import tn.artflow.services.ReservationService;
import tn.artflow.services.WorkshopService;

import java.io.IOException;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

public class FrontWorkshop {

    @FXML
    private TilePane cardsContainer;


    private WorkshopService workshopService;

    @FXML
    public void initialize() {

        workshopService = new WorkshopService();

        Platform.runLater(() -> {
            String cssFile = getClass().getResource("/styles.css").toExternalForm();
            cardsContainer.getScene().getStylesheets().add(cssFile);
        });



        loadWorkshop();

    }


    private void loadWorkshop() {
        try {
            System.out.println("Chargement des ateliers...");
            List<Workshop> workshops = workshopService.recuperer();

            System.out.println("Ateliers récupérés : " + workshops.size());
            cardsContainer.getChildren().clear();

            for (Workshop workshop : workshops) {
                try {
                    System.out.println("Atelier : " + workshop.getTitle());

                    FXMLLoader loader = new FXMLLoader(getClass().getResource("/WorkshopCard.fxml"));
                    Parent productCard = loader.load();

                    WorkshopCard cardController = loader.getController();
                    cardController.setData(workshop);
                    cardController.setParentController(this);

                    cardsContainer.getChildren().add(productCard);
                } catch (IOException e) {
                    System.out.println("Erreur chargement carte : " + e.getMessage());
                    e.printStackTrace();
                }
            }

        } catch (SQLException e) {
            System.out.println("Erreur SQL : " + e.getMessage());
            e.printStackTrace();
        }
    }


    // Méthode pour rafraîchir la liste des produits (appelée après suppression)
    public void refreshProducts() {
        loadWorkshop();
    }

    private void showAlert(Alert.AlertType type, String title, String message) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }




}
