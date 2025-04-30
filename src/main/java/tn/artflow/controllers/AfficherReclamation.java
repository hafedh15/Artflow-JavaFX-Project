package tn.artflow.controllers;

import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;
import tn.artflow.entities.Reclamation;
import tn.artflow.services.ReclamationService;

import java.io.IOException;
import java.sql.SQLException;

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
    private TableColumn<Reclamation, String> statusColumn; // The status column will now contain ComboBoxes
    @FXML
    private TableColumn<Reclamation, java.time.LocalDateTime> dateColumn;



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

    private final ReclamationService reclamationService = new ReclamationService();

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
                                // Optionally, provide feedback to the user
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
        statusColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getStatus())); // Still need to fetch the status for initial display

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

    private void loadData() {
        try {
            tableView.setItems(FXCollections.observableArrayList(reclamationService.recuperer()));
        } catch (SQLException e) {
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
}