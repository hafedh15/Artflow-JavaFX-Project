package tn.artflow.controllers;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;
import javafx.stage.Stage;
import tn.artflow.entities.Reclamation;
import tn.artflow.entities.Reponse;
import tn.artflow.entities.User;
import tn.artflow.services.ReclamationService;
import tn.artflow.services.ReponseService;

import java.io.IOException;
import java.net.URL;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.ResourceBundle;
import java.util.stream.Collectors;

public class ConversationController implements Initializable {

    @FXML
    private ListView<Reclamation> reclamationListView;
    @FXML
    private ListView<Reponse> chatListView;
    @FXML
    private Label reclamationSubjectLabel;
    @FXML
    private Label reclamationStatusLabel;
    @FXML
    private Label reclamationMessageLabel;
    @FXML
    private TextField responseField;
    @FXML
    private Button sendButton;
    @FXML
    private Label labelResponseError;


    // Navigation Buttons
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
    private Button GoToProduit;

    private static final String ACTIVE_BUTTON_STYLE = "-fx-background-color: #303f9f; -fx-text-fill: white;";
    private static final String INACTIVE_BUTTON_STYLE = "-fx-background-color: transparent; -fx-text-fill: #c5cae9;";
    private static final String ACTIVE_ICON_STYLE = "-fx-text-fill: white;";
    private static final String INACTIVE_ICON_STYLE = "-fx-text-fill: #c5cae9;";

    private final ReclamationService reclamationService = new ReclamationService();
    private final ReponseService reponseService = new ReponseService();

    User user = tn.artflow.utils.UserSession.getInstance().getUser();
    private final int CURRENT_USER_ID = user.getId();
    private final DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("HH:mm");
    @FXML
    private ComboBox<String> filterComboBox;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        loadReclamations();

        // Add filter options including "All"
        filterComboBox.getItems().addAll("All", "Open", "In Progress", "Closed");
        filterComboBox.getSelectionModel().selectFirst();  // Optionally, select the first item by default

        filterComboBox.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue != null) {
                filterReclamations(newValue);
            }
        });

        // Listener for when a reclamation is selected
        reclamationListView.getSelectionModel().selectedItemProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal != null) {
                showConversation(newVal);
                labelResponseError.setVisible(false);
            }
        });
    }


    // The filterReclamations method
    private void filterReclamations(String status) {
        try {
            // Retrieve all reclamations
            List<Reclamation> allReclamations = reclamationService.recuperer();

            // If the status is "All", show all reclamations
            if (status.equalsIgnoreCase("All")) {
                reclamationListView.getItems().setAll(allReclamations);
            } else {
                // Filter the reclamations based on the selected status
                List<Reclamation> filteredReclamations = allReclamations.stream()
                        .filter(reclamation -> reclamation.getStatus().equalsIgnoreCase(status))
                        .collect(Collectors.toList());

                // Update the ListView with the filtered list
                reclamationListView.getItems().setAll(filteredReclamations);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    private void loadReclamations() {
        try {
            List<Reclamation> reclamations = reclamationService.recuperer();
            reclamationListView.getItems().setAll(reclamations);
            reclamationListView.setCellFactory(param -> new ListCell<>() {
                private final Label subjectLabel = new Label();
                private final Label timeLabel = new Label();
                private final Label userLabel = new Label();
                private final VBox vbox = new VBox(subjectLabel, new HBox(userLabel, timeLabel));

                {
                    subjectLabel.setFont(Font.font(14));
                    timeLabel.setFont(Font.font(10));
                    timeLabel.setStyle("-fx-text-fill: gray;");
                    userLabel.setFont(Font.font(10));
                    userLabel.setStyle("-fx-font-weight: bold; -fx-text-fill: #0078D7;");
                    vbox.setSpacing(2);
                    vbox.setPadding(new Insets(5));
                }

                @Override
                protected void updateItem(Reclamation item, boolean empty) {
                    super.updateItem(item, empty);
                    if (empty || item == null) {
                        setText(null);
                        setGraphic(null);
                    } else {
                        subjectLabel.setText(item.getSubject());
                        timeLabel.setText(item.getCreatedAt().format(timeFormatter));
                        userLabel.setText("User " + item.getUserId());
                        setGraphic(vbox);
                    }
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void showConversation(Reclamation reclamation) {
        reclamationSubjectLabel.setText(reclamation.getSubject());
        reclamationStatusLabel.setText(reclamation.getStatus());
        reclamationMessageLabel.setText(reclamation.getMessage());

        try {
            List<Reponse> reponses = reponseService.recuperer().stream()
                    .filter(r -> r.getReclamationId() == reclamation.getId())
                    .collect(Collectors.toList());

            chatListView.getItems().setAll(reponses); // clear old messages and display the new
            chatListView.setCellFactory(listView -> new ReponseListCell());
            chatListView.scrollTo(reponses.size() - 1); // last message
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void sendResponse() {
        Reclamation selected = reclamationListView.getSelectionModel().getSelectedItem(); // On envoie une réponse uniquement si une réclamation est sélectionnée.
        String responseText = responseField.getText().trim(); // Récupère le texte tapé dans le champ de réponse, en supprimant les espaces au début et à la fin

        if (selected == null || responseText.isEmpty()) {
            labelResponseError.setText("Le message ne doit pas être vide.");
            labelResponseError.setVisible(true);
            return;
        }

        labelResponseError.setVisible(false);

        Reponse reponse = new Reponse(
                selected.getId(),
                CURRENT_USER_ID,
                responseText,
                false,
                LocalDateTime.now()
        );

        try {
            reponseService.ajouter(reponse);
            responseField.clear();
            showConversation(selected); //Recharge la conversation pour afficher la nouvelle réponse juste ajoutée.


        } catch (Exception e) {
            e.printStackTrace();
            showError("Erreur", "Une erreur est survenue lors de l'envoi.");
        }
    }

    private void confirmAndDeleteMessage(Reponse reponseToDelete) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Confirmation de suppression");
        alert.setHeaderText("Supprimer ce message ?");
        alert.setContentText("Cette action est irréversible.");

        alert.showAndWait().ifPresent(response -> {
            if (response == ButtonType.OK) {
                try {
                    reponseService.supprimer(reponseToDelete);
                    showConversation(reclamationListView.getSelectionModel().getSelectedItem()); // Puis on réaffiche la conversation liée à la réclamation sélectionnée, pour mettre à jour la liste de messages.
                } catch (Exception e) {
                    e.printStackTrace();
                    showError("Erreur de suppression", "Impossible de supprimer ce message.");
                }
            }
        });
    }

    private void showError(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    class ReponseListCell extends ListCell<Reponse> {
        private boolean isEditing = false;

        @Override
        protected void updateItem(Reponse item, boolean empty) {
            super.updateItem(item, empty);
            if (empty || item == null) {
                setText(null);
                setGraphic(null);
                return;
            }

            if (!isEditing) {
                Text messageText = new Text(item.getMessage());
                Text timeText = new Text(" (" + item.getCreatedAt().format(timeFormatter) + ")");
                timeText.setFont(Font.font(10));
                timeText.setFill(Color.GRAY);
                messageText.setFill(Color.BLACK);

                TextFlow textFlow = new TextFlow(messageText, timeText);
                textFlow.setPadding(new Insets(10));
                textFlow.setMaxWidth(300);
                textFlow.setStyle("-fx-background-radius: 15; -fx-background-color: "
                        + (item.getUserId() == CURRENT_USER_ID ? "#CFE9FF" : "#F1F0F0") + ";");

                HBox messageContainer = new HBox(10, textFlow);
                messageContainer.setAlignment(item.getUserId() == CURRENT_USER_ID ? Pos.CENTER_RIGHT : Pos.CENTER_LEFT);

                if (item.getUserId() == CURRENT_USER_ID) {
                    Text editIcon = new Text("✏️");
                    editIcon.setFont(Font.font("Segoe UI Emoji", 10)); // Or another emoji-compatible font

                    Button editButton = new Button();
                    editButton.setGraphic(editIcon);
                    editButton.setOnAction(e -> {
                        isEditing = true;
                        updateItem(item, false);
                    });

                    Text deleteIcon = new Text("🗑️");
                    deleteIcon.setFont(Font.font("Segoe UI Emoji", 10));

                    Button deleteButton = new Button();
                    deleteButton.setGraphic(deleteIcon);
                    deleteButton.setOnAction(e -> confirmAndDeleteMessage(item));

                    messageContainer.getChildren().addAll(editButton, deleteButton);
                }

                setGraphic(messageContainer);
            } else {
                TextField editField = new TextField(item.getMessage());
                Button saveButton = new Button("✅");
                Button cancelButton = new Button("❌");

                saveButton.setOnAction(e -> {
                    String newText = editField.getText().trim();
                    if (!newText.isEmpty()) {
                        item.setMessage(newText);
                        item.setCreatedAt(LocalDateTime.now());
                        try {
                            reponseService.modifier(item);
                            isEditing = false;
                            updateItem(item, false);
                        } catch (Exception ex) {
                            ex.printStackTrace();
                        }
                    }
                });

                cancelButton.setOnAction(e -> {
                    isEditing = false;
                    updateItem(item, false);
                });

                HBox editBox = new HBox(10, editField, saveButton, cancelButton);
                editBox.setAlignment(Pos.CENTER_RIGHT);
                setGraphic(editBox);
            }
        }
    }
//    private void markActiveButton(Button activeButton) {
//        // List of all navigation buttons
//        Button[] allButtons = {
//                GoToUser, GoToProduit, GoToArticle, GoToAtelier,
//                GoToReservation, GoToComment, GoToReclamation,
//                GoToReponse, orderButton
//        };
//
//        // Reset all buttons to inactive state
//        for (Button button : allButtons) {
//            if (button != null) {
//                button.setStyle(button.getStyle().replace(ACTIVE_BUTTON_STYLE, INACTIVE_BUTTON_STYLE));
//
//                // Find the label (icon) within the button's graphic
//                if (button.getGraphic() instanceof Label) {
//                    Label iconLabel = (Label) button.getGraphic();
//                    iconLabel.setStyle(iconLabel.getStyle().replace(ACTIVE_ICON_STYLE, INACTIVE_ICON_STYLE));
//                }
//            }
//        }
//
//        // Set the active button
//        if (activeButton != null) {
//            activeButton.setStyle(activeButton.getStyle().replace(INACTIVE_BUTTON_STYLE, ACTIVE_BUTTON_STYLE));
//
//            // Find the label (icon) within the button's graphic
//            if (activeButton.getGraphic() instanceof Label) {
//                Label iconLabel = (Label) activeButton.getGraphic();
//                iconLabel.setStyle(iconLabel.getStyle().replace(INACTIVE_ICON_STYLE, ACTIVE_ICON_STYLE));
//            }
//        }
//    }

    // Navigation methods remain the same as before
    // ...

    /**
     * Navigate to Articles
     */
    @FXML
    void goToArticle(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/AfficherArticle.fxml"));
            Parent root = loader.load();

            // Mark the articles button as active before switching
          //  markActiveButton(GoToArticle);

            GoToArticle.getScene().setRoot(root);
        } catch (IOException e) {
            showAlert(Alert.AlertType.ERROR, "Navigation Error",
                    "Could not navigate to Articles", e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * Navigate to Reservations
     */
    @FXML
    void goToReservation(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/dashReservation.fxml"));
            Parent root = loader.load();

            // Mark the reservations button as active before switching
          //  markActiveButton(GoToReservation);

            GoToArticle.getScene().setRoot(root);
        } catch (IOException e) {
            showAlert(Alert.AlertType.ERROR, "Navigation Error",
                    "Could not navigate to Reservations", e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * Navigate to Workshops
     */
    @FXML
    void goToAtelier(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/dashWorkshop.fxml"));
            Parent root = loader.load();

            // Mark the workshops button as active before switching
          //  markActiveButton(GoToAtelier);

            GoToArticle.getScene().setRoot(root);
        } catch (IOException e) {
            showAlert(Alert.AlertType.ERROR, "Navigation Error",
                    "Could not navigate to Workshops", e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * Navigate to Comments
     */
    @FXML
    void goToComment(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/AfficheComment.fxml"));
            Parent root = loader.load();

            // Mark the comments button as active before switching
        //    markActiveButton(GoToComment);

            GoToArticle.getScene().setRoot(root);
        } catch (IOException e) {
            showAlert(Alert.AlertType.ERROR, "Navigation Error",
                    "Could not navigate to Comments", e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * Navigate to Complaints
     */
    @FXML
    void goToReclamation(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/AfficherReclamation.fxml"));
            Parent root = loader.load();

            // Mark the complaints button as active before switching
         //   markActiveButton(GoToReclamation);

            GoToArticle.getScene().setRoot(root);
        } catch (IOException e) {
            showAlert(Alert.AlertType.ERROR, "Navigation Error",
                    "Could not navigate to Complaints", e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * Handle orders button click
     */
    public void handleOrderButtonAction(ActionEvent actionEvent) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/OrdersDashboard.fxml"));
            Parent root = loader.load();

            // Mark the orders button as active before switching
         //   markActiveButton(orderButton);

            // Switch to the orders screen
            Stage stage = (Stage) ((Node) actionEvent.getSource()).getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.setTitle("Gestion des Commandes");
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Error",
                    "Navigation Error", "Could not load the orders page.");
        }
    }


    /**
     * Navigate to Responses
     */
    @FXML
    void goToReponse(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/conversation.fxml"));
            Parent root = loader.load();

            // Mark the responses button as active before switching
          //  markActiveButton(GoToReponse);

            GoToArticle.getScene().setRoot(root);
        } catch (IOException e) {
            showAlert(Alert.AlertType.ERROR, "Navigation Error",
                    "Could not navigate to Responses", e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * Navigate to Products
     */
    @FXML
    void goToProduit(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/Dashboard.fxml"));
            Parent root = loader.load();

            // Mark the products button as active before switching
          //  markActiveButton(GoToProduit);

            GoToArticle.getScene().setRoot(root);
        } catch (IOException e) {
            showAlert(Alert.AlertType.ERROR, "Navigation Error",
                    "Could not navigate to Products", e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * Navigate back to User Management
     */
    @FXML
    void goToUser(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/AfficherUser.fxml"));
            Parent root = loader.load();

            // Mark the users button as active before switching
           // markActiveButton(GoToUser);

            GoToArticle.getScene().setRoot(root);
        } catch (IOException e) {
            showAlert(Alert.AlertType.ERROR, "Navigation Error",
                    "Could not navigate to Users", e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * Show an alert dialog
     */
    private void showAlert(Alert.AlertType alertType, String title, String header, String content) {
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setHeaderText(header);
        alert.setContentText(content);
        alert.showAndWait();
    }
}