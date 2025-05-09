package tn.artflow.controllers;

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
import tn.artflow.entities.Reclamation;
import tn.artflow.entities.Reponse;
import tn.artflow.entities.User;
import tn.artflow.services.ReclamationService;
import tn.artflow.services.ReponseService;
import tn.artflow.services.DialogflowClient;
import java.net.URL;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.ResourceBundle;
import java.util.stream.Collectors;

public class ClientConversation implements Initializable {

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

    User user = tn.artflow.utils.UserSession.getInstance().getUser();
    private final int CURRENT_USER_ID = user.getId(); // Replace this with dynamic user ID retrieval if needed
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
            List<Reclamation> reclamations = reclamationService.recuperer().stream()
                    .filter(r -> r.getUserId() == CURRENT_USER_ID)
                    .collect(Collectors.toList());

            reclamationListView.getItems().setAll(reclamations);
            reclamationListView.setCellFactory(param -> new ListCell<>() {
                private final Label subjectLabel = new Label();
                private final Label timeLabel = new Label();
                private final VBox vbox = new VBox(subjectLabel, timeLabel);

                {
                    subjectLabel.setFont(Font.font(14));
                    timeLabel.setFont(Font.font(10));
                    timeLabel.setStyle("-fx-text-fill: gray;");
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

   /* @FXML
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
*/

    @FXML
    private void sendResponse() {
        Reclamation selected = reclamationListView.getSelectionModel().getSelectedItem();
        String responseText = responseField.getText().trim();

        if (selected == null) {
            labelResponseError.setText("Veuillez sélectionner une réclamation.");
            labelResponseError.setVisible(true);
            return;
        }

        if (responseText.isEmpty()) {
            labelResponseError.setText("Le message ne doit pas être vide.");
            labelResponseError.setVisible(true);
            return;
        }

        labelResponseError.setVisible(false);

        Reponse userResponse = new Reponse(
                selected.getId(),
                CURRENT_USER_ID,
                responseText,
                false,
                LocalDateTime.now()
        );

        try {
            // Save user message
            reponseService.ajouter(userResponse);
            responseField.clear();
            responseField.requestFocus();
            showConversation(selected);

            // === Call Dialogflow bot ===
            String botReply = DialogflowClient.detectIntent(responseText);

            // Save bot response with userId = 0
            Reponse botResponse = new Reponse(
                    selected.getId(),
                    143, // BOT user ID
                    "BOT: "+ botReply,
                    false,
                    LocalDateTime.now()
            );

            reponseService.ajouter(botResponse);
            showConversation(selected);

        } catch (Exception e) {
            e.printStackTrace();
            showError("Erreur", "Une erreur est survenue lors de l'envoi.");
        }
    }

    private void showError(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    class ReponseListCell extends ListCell<Reponse> {
        @Override
        protected void updateItem(Reponse item, boolean empty) {
            super.updateItem(item, empty);
            if (empty || item == null) {
                setText(null);
                setGraphic(null);
                return;
            }

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

            setGraphic(messageContainer);
        }
    }
}