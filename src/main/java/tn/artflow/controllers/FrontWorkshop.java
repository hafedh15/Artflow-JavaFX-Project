package tn.artflow.controllers;


import javafx.application.Platform;
import javafx.event.ActionEvent;
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

       // Platform.runLater(() -> {
           // String cssFile = getClass().getResource("/styles.css").toExternalForm();
         //   cardsContainer.getScene().getStylesheets().add(cssFile);
     //   });



        loadWorkshop();

    }


    private void loadWorkshop() {
        System.out.println("Chargement des ateliers...");
        cardsContainer.getChildren().clear();

        try {
            List<Workshop> workshops = workshopService.recuperer();
            System.out.println("Ateliers récupérés : " + workshops.size());

            for (Workshop workshop : workshops) {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/WorkshopCard.fxml"));
                Parent card = loader.load();

                WorkshopCard controller = loader.getController();
                controller.setData(workshop);
                controller.setParentController(this);

                cardsContainer.getChildren().add(card);
            }
        } catch (SQLException | IOException e) {
            System.err.println("Erreur lors du chargement des ateliers : " + e.getMessage());
            e.printStackTrace();
        }
    }


}
