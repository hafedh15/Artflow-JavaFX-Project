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
    private final ReponseService reponseService = new ReponseService();

    User user = tn.artflow.utils.UserSession.getInstance().getUser();
    private final int CURRENT_USER_ID = user.getId();
    private final DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("HH:mm");

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        loadReclamations();
        reclamationListView.getSelectionModel().selectedItemProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal != null) {
                showConversation(newVal);
                labelResponseError.setVisible(false);
            }
        });
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

            chatListView.getItems().setAll(reponses);
            chatListView.setCellFactory(listView -> new ReponseListCell());
            chatListView.scrollTo(reponses.size() - 1);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void sendResponse() {
        Reclamation selected = reclamationListView.getSelectionModel().getSelectedItem();
        String responseText = responseField.getText().trim();

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
            showConversation(selected);
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
                    showConversation(reclamationListView.getSelectionModel().getSelectedItem());
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
