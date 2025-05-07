package tn.esprit.controllers;

import javafx.application.Platform;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.PieChart;
import javafx.scene.chart.XYChart;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import tn.esprit.entities.Reclamation;
import tn.esprit.services.ReclamationService;

import java.sql.SQLException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class AfficherReclamation {
    @FXML
    private TableColumn<Reclamation, Void> actionsColumn;
    @FXML
    private TableView<Reclamation> tableView;
    @FXML
    private TableColumn<Reclamation, Integer> idColumn;
    @FXML
    private TableColumn<Reclamation, Integer> userIdColumn;
    @FXML
    private TableColumn<Reclamation, String> messageColumn;
    @FXML
    private TableColumn<Reclamation, String> statusColumn;
    @FXML
    private TableColumn<Reclamation, java.time.LocalDateTime> dateColumn;

    // Statistics components
    @FXML
    private Label totalReclamationsLabel;
    @FXML
    private Label openReclamationsLabel;
    @FXML
    private Label inProgressReclamationsLabel;
    @FXML
    private Label closedReclamationsLabel;
    @FXML
    private PieChart statusPieChart;
    @FXML
    private BarChart<String, Number> timeBarChart;

    private final ReclamationService reclamationService = new ReclamationService();
    private ObservableList<Reclamation> reclamations;

    @FXML
    public void initialize() {
        idColumn.setCellValueFactory(new PropertyValueFactory<>("id"));
        userIdColumn.setCellValueFactory(new PropertyValueFactory<>("userId"));
        messageColumn.setCellValueFactory(new PropertyValueFactory<>("message"));
        dateColumn.setCellValueFactory(new PropertyValueFactory<>("createdAt"));

        // Set up the Status column to display a ComboBox
        statusColumn.setCellFactory(col -> {
            return new TableCell<Reclamation, String>() {
                private final ComboBox<String> statusComboBox = new ComboBox<>(FXCollections.observableArrayList("open", "in progress", "closed"));

                {
                    statusComboBox.setMaxWidth(Double.MAX_VALUE);
                    statusComboBox.setOnAction(event -> {
                        Reclamation reclamation = getTableView().getItems().get(getIndex());
                        String newStatus = statusComboBox.getValue();
                        if (reclamation != null && !newStatus.equals(reclamation.getStatus())) {
                            try {
                                reclamationService.modifierStatus(reclamation.getId(), newStatus);
                                reclamation.setStatus(newStatus); // Update the model
                                System.out.println("Reclamation ID " + reclamation.getId() + " status updated to: " + newStatus);

                                // Update statistics after status change
                                updateStatistics();

                            } catch (SQLException e) {
                                Alert errorAlert = new Alert(Alert.AlertType.ERROR);
                                errorAlert.setTitle("Erreur de mise à jour");
                                errorAlert.setHeaderText("Une erreur est survenue lors de la mise à jour du statut.");
                                errorAlert.setContentText("Erreur: " + e.getMessage());
                                errorAlert.showAndWait();
                                e.printStackTrace();
                                // Revert the ComboBox value if the update fails
                                statusComboBox.setValue(reclamation.getStatus());
                            }
                        }
                    });
                }

                @Override
                protected void updateItem(String item, boolean empty) {
                    super.updateItem(item, empty);
                    if (empty || item == null) {
                        setGraphic(null);
                    } else {
                        statusComboBox.setValue(item);
                        setGraphic(statusComboBox);
                    }
                }
            };
        });
        statusColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getStatus()));

        // Set up the Actions column with the Delete button
        actionsColumn.setCellFactory(param -> new TableCell<>() {
            private final Button deleteBtn = new Button("🗑️");

            {
                deleteBtn.setStyle(
                        "-fx-background-color: transparent;" +
                                "-fx-text-fill: #f44336;" +
                                "-fx-cursor: hand;" +
                                "-fx-font-family: 'Segoe UI Emoji';"
                );

                deleteBtn.setOnAction(event -> {
                    Reclamation reclamationToDelete = getTableView().getItems().get(getIndex());
                    Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
                    alert.setTitle("Confirmation de suppression");
                    alert.setHeaderText(null);
                    alert.setContentText("Êtes-vous sûr de vouloir supprimer la réclamation avec l'ID " + reclamationToDelete.getId() + "?");

                    alert.showAndWait().ifPresent(response -> {
                        if (response == ButtonType.OK) {
                            try {
                                reclamationService.supprimer(reclamationToDelete);
                                tableView.getItems().remove(reclamationToDelete);
                                System.out.println("Reclamation with ID " + reclamationToDelete.getId() + " deleted.");

                                // Update statistics after deletion
                                updateStatistics();

                            } catch (Exception e) {
                                Alert errorAlert = new Alert(Alert.AlertType.ERROR);
                                errorAlert.setTitle("Erreur de suppression");
                                errorAlert.setHeaderText("Une erreur est survenue lors de la suppression.");
                                errorAlert.setContentText("Erreur: " + e.getMessage());
                                errorAlert.showAndWait();
                                e.printStackTrace();
                            }
                        }
                    });
                });
            }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) {
                    setGraphic(null);
                } else {
                    setGraphic(deleteBtn);
                }
            }
        });

        loadData();
    }

    /**
     * Applies styles to chart nodes using Platform.runLater to ensure the chart is fully rendered
     */
    private void applyChartStyles() {
        // Style the bar chart
        if (timeBarChart != null && timeBarChart.getData() != null) {
            Platform.runLater(() -> {
                for (XYChart.Series<String, Number> series : timeBarChart.getData()) {
                    if (series.getNode() != null) {
                        series.getNode().setStyle("-fx-bar-fill: #C8B6A6;");
                    }

                    // Style individual data points
                    for (XYChart.Data<String, Number> data : series.getData()) {
                        if (data.getNode() != null) {
                            data.getNode().setStyle("-fx-bar-fill: #C8B6A6;");
                        }
                    }
                }
            });
        }

        // Style the pie chart
        if (statusPieChart != null && statusPieChart.getData() != null) {
            Platform.runLater(() -> {
                for (PieChart.Data data : statusPieChart.getData()) {
                    String color;
                    switch (data.getName()) {
                        case "Ouvertes":
                            color = "#da5828";
                            break;
                        case "En cours":
                            color = "#e19418";
                            break;
                        case "Fermées":
                            color = "#5B8D5B";
                            break;
                        default:
                            color = "#7D6E5B";
                    }

                    if (data.getNode() != null) {
                        data.getNode().setStyle("-fx-pie-color: " + color + ";");
                    }
                }
            });
        }
    }

    private void loadData() {
        try {
            reclamations = FXCollections.observableArrayList(reclamationService.recuperer());
            tableView.setItems(reclamations);

            // Initialize statistics after loading data
            updateStatistics();

        } catch (SQLException e) {
            e.printStackTrace();
            showErrorAlert("Erreur de chargement", "Une erreur est survenue lors du chargement des données.", e.getMessage());
        }
    }

    /**
     * Updates all statistics components with the current data
     */
    private void updateStatistics() {
        updateSummaryCards();
        updateStatusPieChart();
        updateTimeBarChart();

        // Apply styles after chart data has been updated
        // Use Platform.runLater to ensure the charts are fully rendered
        Platform.runLater(() -> {
            // Add a small delay to ensure the charts have time to render
            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            applyChartStyles();
        });
    }

    /**
     * Updates the summary cards showing counts of different status types
     */
    private void updateSummaryCards() {
        long total = reclamations.size();
        long openCount = reclamations.stream().filter(r -> "open".equals(r.getStatus())).count();
        long inProgressCount = reclamations.stream().filter(r -> "in progress".equals(r.getStatus())).count();
        long closedCount = reclamations.stream().filter(r -> "closed".equals(r.getStatus())).count();

        totalReclamationsLabel.setText(String.valueOf(total));
        openReclamationsLabel.setText(String.valueOf(openCount));
        inProgressReclamationsLabel.setText(String.valueOf(inProgressCount));
        closedReclamationsLabel.setText(String.valueOf(closedCount));
    }

    /**
     * Updates the pie chart showing the distribution of reclamation statuses
     */
    private void updateStatusPieChart() {
        // Clear previous data
        statusPieChart.getData().clear();

        // Count by status
        Map<String, Long> statusCounts = reclamations.stream()
                .collect(Collectors.groupingBy(
                        Reclamation::getStatus,
                        Collectors.counting()
                ));

        // Create pie chart data
        ObservableList<PieChart.Data> pieChartData = FXCollections.observableArrayList();

        // Add data with appropriate colors
        if (statusCounts.containsKey("open")) {
            PieChart.Data openSlice = new PieChart.Data("Ouvertes", statusCounts.get("open"));
            pieChartData.add(openSlice);
        }

        if (statusCounts.containsKey("in progress")) {
            PieChart.Data inProgressSlice = new PieChart.Data("En cours", statusCounts.get("in progress"));
            pieChartData.add(inProgressSlice);
        }

        if (statusCounts.containsKey("closed")) {
            PieChart.Data closedSlice = new PieChart.Data("Fermées", statusCounts.get("closed"));
            pieChartData.add(closedSlice);
        }

        statusPieChart.setData(pieChartData);
        // We don't use the skinProperty listener anymore - styling is done in applyChartStyles
    }

    /**
     * Updates the bar chart showing reclamations over time
     */
    private void updateTimeBarChart() {
        // Clear previous data
        timeBarChart.getData().clear();

        // Group reclamations by month
        Map<String, Integer> reclamationsByMonth = new HashMap<>();
        DateTimeFormatter monthFormatter = DateTimeFormatter.ofPattern("MMM yyyy");

        for (Reclamation reclamation : reclamations) {
            LocalDateTime date = reclamation.getCreatedAt();
            String monthYear = date.format(monthFormatter);
            reclamationsByMonth.put(monthYear, reclamationsByMonth.getOrDefault(monthYear, 0) + 1);
        }

        // Create series for the bar chart
        XYChart.Series<String, Number> series = new XYChart.Series<>();
        series.setName("Nombre de réclamations");

        // Sort map by date
        reclamationsByMonth.entrySet().stream()
                .sorted(Map.Entry.comparingByKey())
                .forEach(entry -> series.getData().add(new XYChart.Data<>(entry.getKey(), entry.getValue())));

        timeBarChart.getData().add(series);
        // We don't style here anymore - styling is done in applyChartStyles
    }

    /**
     * Displays an error alert with the given information
     */
    private void showErrorAlert(String title, String header, String content) {
        Alert errorAlert = new Alert(Alert.AlertType.ERROR);
        errorAlert.setTitle(title);
        errorAlert.setHeaderText(header);
        errorAlert.setContentText("Erreur: " + content);
        errorAlert.showAndWait();
    }
}