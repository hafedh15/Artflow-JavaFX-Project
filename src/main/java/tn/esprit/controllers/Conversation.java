package tn.esprit.controllers;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;
import tn.esprit.entities.Reclamation;
import tn.esprit.entities.Reponse;
import tn.esprit.services.ReclamationService;
import tn.esprit.services.ReponseService;

import java.net.URL;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.ResourceBundle;
import java.util.stream.Collectors;

public class Conversation implements Initializable {

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

    private final ReclamationService reclamationService = new ReclamationService();
    private final ReponseService reponseService = new ReponseService();

    private final int CURRENT_USER_ID = 1;
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
}
