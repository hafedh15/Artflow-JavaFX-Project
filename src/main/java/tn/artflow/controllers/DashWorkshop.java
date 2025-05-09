package tn.artflow.controllers;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.io.IOException;
import java.sql.SQLException;
import java.util.stream.Collectors;

import javafx.event.ActionEvent;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import tn.artflow.entities.Workshop;
import tn.artflow.services.WorkshopService;

public class DashWorkshop {

    @FXML
    private Button GoToArticle;
    @FXML
    private Button GoToUser;
    @FXML
    private Button GoToAtelier;

    @FXML
    private Button GoToReservation;

    @FXML
    private Button GoToComment;

    @FXML
    private Button GoToReclamation;

    @FXML
    private Button GoToReponse;
    @FXML
    private Button orderButton;
    @FXML
    private GridPane gridWorkshop;

    private boolean isSorted = false;

    @FXML
    private TextField searchField;

    private List<Workshop> allWorkshops = new ArrayList<>();

    private void refreshGrid() {
        gridWorkshop.getChildren().clear(); // Clear all nodes
        initialize(); // Reload workshops
    }

    public void initialize() {
        loadWorkshops();
    }

    private void loadWorkshops() {
        WorkshopService ws = new WorkshopService();
        try {
            allWorkshops = ws.recuperer();  // Stocke tous les workshops
            displayWorkshops(allWorkshops);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }


    public void openEditWorkshop(Workshop workshop) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/updateWorkshop.fxml"));
            Parent root = loader.load();

            // Pass the selected workshop to the controller
            UpdateWorkshop controller = loader.getController();
            controller.setWorkshop(workshop);

            Stage stage = new Stage();
            stage.setScene(new Scene(root));
            stage.setTitle("Update Workshop");
            stage.initModality(Modality.APPLICATION_MODAL); // Block until closed
            stage.setOnHidden(e -> refreshGrid()); // ✅ Refresh after closing
            stage.showAndWait(); // Wait until the window is closed

        } catch (IOException e) {
            e.printStackTrace();
        }
    }



//    public void initialize() {
//        WorkshopService ws = new WorkshopService();
//        try {
//            List<Workshop> workshops = ws.recuperer();
//
//            // Add header row
//            gridWorkshop.addRow(0,
//                    new Label("ID"),
//                    new Label("Title"),
//                    new Label("Description"),
//                    new Label("Date"),
//                    new Label("Type"),
//                    new Label("Location"),
//                    new Label("Actions")  // For buttons
//            );
//
//            int row = 1;
//            for (Workshop w : workshops) {
//                // Delete button
//                Button deleteButton = new Button("Delete");
//                deleteButton.setStyle("-fx-background-color: #e74c3c; -fx-text-fill: white;");
//                deleteButton.setOnAction(e -> {
//                    ws.supprimer(w.getId());
//                    refreshGrid();
//                });
//
//                // Edit button
//                Button editButton = new Button("Edit");
//                editButton.setStyle("-fx-background-color: #3498db; -fx-text-fill: white;");
//                editButton.setOnAction(e -> openEditWorkshop(w)); // Open form with values
//
//
//                // Add row to grid with both buttons
//                gridWorkshop.addRow(row++,
//                        new Label(String.valueOf(w.getId())),
//                        new Label(w.getTitle()),
//                        new Label(w.getDescription()),
//                        new Label(w.getDate()),
//                        new Label(w.getType()),
//                        new Label(w.getLocation()),
//                        new javafx.scene.layout.HBox(10, editButton, deleteButton)  // Add both in HBox
//                );
//            }
//
//        } catch (SQLException e) {
//            e.printStackTrace();
//        }
//    }

    @FXML
    void addworkshop(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/AddWorkshop.fxml"));
            Parent root = loader.load();

            Stage stage = new Stage();
            stage.setTitle("Add Workshop");
            stage.setScene(new Scene(root));
            stage.initModality(Modality.APPLICATION_MODAL); // Block until closed
            stage.setOnHidden(e -> refreshGrid()); // ✅ Refresh after closing
            stage.showAndWait();

        } catch (IOException e) {
            e.printStackTrace();
        }

    }


    @FXML
    void goToArticle(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/AfficherArticle.fxml"));
            Parent root = loader.load();
            GoToArticle.getScene().setRoot(root);  // Même principe ici
        } catch (IOException e) {

            e.printStackTrace();
        }
    }

    @FXML
    void goToReservation(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/dashReservation.fxml"));
            Parent root = loader.load();
            GoToArticle.getScene().setRoot(root);  // Même principe ici
        } catch (IOException e) {

            e.printStackTrace();
        }
    }
    @FXML
    void goToAtelier(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/dashWorkshop.fxml"));
            Parent root = loader.load();
            GoToArticle.getScene().setRoot(root);  // Même principe ici
        } catch (IOException e) {

            e.printStackTrace();
        }
    }
    @FXML
    void goToComment(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/AfficheComment.fxml"));
            Parent root = loader.load();
            GoToArticle.getScene().setRoot(root);  // Même principe ici
        } catch (IOException e) {

            e.printStackTrace();
        }
    }
    @FXML
    void goToReclamation(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/AfficherReclamation.fxml"));
            Parent root = loader.load();
            GoToArticle.getScene().setRoot(root);  // Même principe ici
        } catch (IOException e) {

            e.printStackTrace();
        }
    }
    @FXML
    void goToReponse(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/conversation.fxml"));
            Parent root = loader.load();
            GoToArticle.getScene().setRoot(root);  // Même principe ici
        } catch (IOException e) {

            e.printStackTrace();
        }
    }

    @FXML
    void goToProduit(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/Dashboard.fxml"));
            Parent root = loader.load();
            GoToArticle.getScene().setRoot(root);  // Même principe ici
        } catch (IOException e) {

            e.printStackTrace();
        }
    }

    @FXML
    void goToUser(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/AfficherUser.fxml"));
            Parent root = loader.load();
            GoToArticle.getScene().setRoot(root);  // Même principe ici
        } catch (IOException e) {

            e.printStackTrace();
        }
    }

    public void handleOrderButtonAction(ActionEvent actionEvent) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/OrdersDashboard.fxml"));
            Parent root = loader.load();

            // Obtenir la scène actuelle et la remplacer par la scène d'ajout
            Stage stage = (Stage) ((Node) actionEvent.getSource()).getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.setTitle("Ajouter un produit");
            stage.show();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    public void searchWorkshops() {
        String keyword = searchField.getText().toLowerCase();

        List<Workshop> filtered = allWorkshops.stream()
                .filter(w -> w.getTitle().toLowerCase().contains(keyword))
                .collect(Collectors.toList());

        displayWorkshops(filtered);
    }

    @FXML
    public void sortByDate() {
        if (!isSorted) {
            List<Workshop> sorted = new ArrayList<>(allWorkshops);

            sorted.sort(Comparator.comparing(w -> {
                String datePart = w.getDate().split(" ")[0];
                return LocalDate.parse(datePart);
            }));

            displayWorkshops(sorted);
            isSorted = true;
        } else {
            displayWorkshops(allWorkshops);
            isSorted = false;
        }
    }

    private void displayWorkshops(List<Workshop> workshops) {
        gridWorkshop.getChildren().clear();

        // En-tête
        gridWorkshop.addRow(0,
                new Label("ID"), new Label("Title"), new Label("Description"),
                new Label("Date"), new Label("Type"), new Label("Location"),
                new Label("Actions")
        );

        int row = 1;
        for (Workshop w : workshops) {
            Button deleteButton = new Button("Delete");
            deleteButton.setStyle("-fx-background-color: #e74c3c; -fx-text-fill: white;");
            deleteButton.setOnAction(e -> {
                new WorkshopService().supprimer(w.getId());
                loadWorkshops();
            });

            Button editButton = new Button("Edit");
            editButton.setStyle("-fx-background-color: #3498db; -fx-text-fill: white;");
            editButton.setOnAction(e -> openEditWorkshop(w));

            gridWorkshop.addRow(row++,
                    new Label(String.valueOf(w.getId())),
                    new Label(w.getTitle()),
                    new Label(w.getDescription()),
                    new Label(w.getDate()),
                    new Label(w.getType()),
                    new Label(w.getLocation()),
                    new HBox(10, editButton, deleteButton)
            );
        }
    }

}





