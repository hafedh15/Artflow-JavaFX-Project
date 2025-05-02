package tn.artflow.controllors;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import tn.artflow.entities.Workshop;
import tn.artflow.services.WorkshopService;

import javafx.event.ActionEvent;

import java.io.IOException;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

public class DashWorkshop {

    private boolean isSorted = false;

    @FXML
    private GridPane gridWorkshop;

    @FXML
    private TextField searchField;

    private List<Workshop> allWorkshops = new ArrayList<>();

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

    @FXML
    void addworkshop(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/AddWorkshop.fxml"));
            Parent root = loader.load();

            Stage stage = new Stage();
            stage.setTitle("Add Workshop");
            stage.setScene(new Scene(root));
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.setOnHidden(e -> loadWorkshops());
            stage.showAndWait();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void openEditWorkshop(Workshop workshop) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/updateWorkshop.fxml"));
            Parent root = loader.load();

            UpdateWorkshop controller = loader.getController();
            controller.setWorkshop(workshop);

            Stage stage = new Stage();
            stage.setScene(new Scene(root));
            stage.setTitle("Update Workshop");
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.setOnHidden(e -> loadWorkshops());
            stage.showAndWait();

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

}
