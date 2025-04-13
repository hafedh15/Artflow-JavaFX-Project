package tn.artflow.controllors;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import java.util.List;
import java.io.IOException;
import java.sql.SQLException;
import javafx.event.ActionEvent;
import javafx.scene.layout.GridPane;
import javafx.stage.Modality;
import javafx.stage.Stage;
import tn.artflow.entities.Workshop;
import tn.artflow.services.WorkshopService;

public class DashWorkshop {

    @FXML
    private GridPane gridWorkshop;
    private void refreshGrid() {
        gridWorkshop.getChildren().clear(); // Clear all nodes
        initialize(); // Reload workshops
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



    public void initialize() {
        WorkshopService ws = new WorkshopService();
        try {
            List<Workshop> workshops = ws.recuperer();

            // Add header row
            gridWorkshop.addRow(0,
                    new Label("ID"),
                    new Label("Title"),
                    new Label("Description"),
                    new Label("Date"),
                    new Label("Type"),
                    new Label("Location"),
                    new Label("Actions")  // For buttons
            );

            int row = 1;
            for (Workshop w : workshops) {
                // Delete button
                Button deleteButton = new Button("Delete");
                deleteButton.setStyle("-fx-background-color: #e74c3c; -fx-text-fill: white;");
                deleteButton.setOnAction(e -> {
                    ws.supprimer(w.getId());
                    refreshGrid();
                });

                // Edit button
                Button editButton = new Button("Edit");
                editButton.setStyle("-fx-background-color: #3498db; -fx-text-fill: white;");
                editButton.setOnAction(e -> openEditWorkshop(w)); // Open form with values


                // Add row to grid with both buttons
                gridWorkshop.addRow(row++,
                        new Label(String.valueOf(w.getId())),
                        new Label(w.getTitle()),
                        new Label(w.getDescription()),
                        new Label(w.getDate()),
                        new Label(w.getType()),
                        new Label(w.getLocation()),
                        new javafx.scene.layout.HBox(10, editButton, deleteButton)  // Add both in HBox
                );
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

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







}



