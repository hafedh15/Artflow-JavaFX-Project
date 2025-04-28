package tn.artflow.controllors;

import javafx.animation.FadeTransition;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.layout.TilePane;
import javafx.scene.layout.VBox;
import javafx.scene.layout.AnchorPane;
import javafx.util.Duration;
import tn.artflow.entities.Reservation;
import tn.artflow.entities.User;
import tn.artflow.entities.Workshop;
import tn.artflow.services.ReservationService;

import java.awt.event.ActionEvent;
import java.io.IOException;
import java.net.URL;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;


public class Profil implements Initializable {

    @FXML
    private TilePane cardsContainer;
    @FXML private AnchorPane editPane;
    @FXML private TextField seatsField;
    @FXML private TextArea notesField;

    private Reservation reservationBeingEdited; // Pour garder l’état actuel
    @FXML
    private VBox calendarContainer;


    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        System.out.println("Profil initialization started...");

        // Add visual indicator to make sure TilePane is visible
        cardsContainer.setStyle("-fx-background-color: #f0f0f0; -fx-padding: 15;");

        loadReservations();
    }

    private void loadReservations() {
        System.out.println("Starting to load reservations...");

        ReservationService reservationService = new ReservationService();
        User user = new User();
        user.setId(1); // exemple user

        try {
            List<Reservation> reservations = reservationService.getReservationsByUser(user);
            cardsContainer.getChildren().clear();


            for (Reservation r : reservations) {
                VBox card = new VBox(8);
                card.setStyle("-fx-background-color: #ffffff; -fx-padding: 12;" +
                        "-fx-border-color: #dddddd; -fx-border-radius: 5;" +
                        "-fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.1), 8, 0, 0, 3);");

                Label title = new Label("🧵 Atelier: " + (r.getWorkshop() != null ? r.getWorkshop().getTitle() : "Inconnu"));
                title.setStyle("-fx-font-weight: bold; -fx-font-size: 14px;");

                Label date = new Label("📅 Date: " + r.getDateReservation());
                Label seats = new Label("🎫 Places: " + r.getSeatsReserved());
                Label notes = new Label("📝 Notes: " + (r.getNotes() != null ? r.getNotes() : "-"));
                Label code = new Label("🔐 Code: " + r.getUniqueCode());

                Button deleteButton = new Button("🗑️ Supprimer");
                deleteButton.setStyle("-fx-background-color: #e74c3c; -fx-text-fill: white;");
                deleteButton.setOnAction(e -> {
                    reservationService.supprimer(r.getId());
                    loadReservations(); // recharge après suppression
                });
                Button editButton = new Button("✏️ Modifier");
                editButton.setStyle("-fx-background-color: #3498db; -fx-text-fill: white;");
                editButton.setOnAction(e -> showEditPane(r));


                card.getChildren().addAll(title, date, seats, notes, code, editButton, deleteButton);
                card.setPrefWidth(260);
                card.setPrefHeight(180);

                cardsContainer.getChildren().add(card);
            }

        } catch (SQLException e) {
            System.err.println("Erreur DB: " + e.getMessage());
            cardsContainer.getChildren().clear();
            Label errorLabel = new Label("Erreur lors du chargement des réservations : " + e.getMessage());
            errorLabel.setStyle("-fx-background-color: #ffcccc; -fx-padding: 20;");
            cardsContainer.getChildren().add(errorLabel);
        }
    }




    private void showEditPane(Reservation reservation) {
        reservationBeingEdited = reservation; // On garde la référence
        seatsField.setText(String.valueOf(reservation.getSeatsReserved()));
        notesField.setText(reservation.getNotes());
        editPane.setVisible(true); // Affiche le panneau
    }
    @FXML
    private void closeEditPane() {
        editPane.setVisible(false);
        reservationBeingEdited = null;
    }
    @FXML

    private void saveEditedReservation() throws SQLException {
        if (reservationBeingEdited == null) return;
        // Validate number of seats
        int newSeats;
        try {
            newSeats = Integer.parseInt(seatsField.getText());
            if (newSeats < 1 || newSeats > 4) {
                showError("The number of seats must be between 1 and 4.");
                return;
            }
        } catch (NumberFormatException e) {
            showError("Please enter a valid number for seats.");
            return;
        }
        // Validate number of words in notes
        String newNotes = notesField.getText().trim();
        int wordCount = newNotes.isEmpty() ? 0 : newNotes.split("\\s+").length;
        if (wordCount > 5) {
            showError("Notes must not exceed 5 words.");
            return;
        }

        // If all is good, update the reservation
        reservationBeingEdited.setSeatsReserved(newSeats);
        reservationBeingEdited.setNotes(newNotes);

        ReservationService rs = new ReservationService();
        rs.modifier(reservationBeingEdited);

        editPane.setVisible(false);
        loadReservations();
    }

    private void showError(String message) {
        javafx.scene.control.Alert alert = new javafx.scene.control.Alert(javafx.scene.control.Alert.AlertType.ERROR);
        alert.setTitle("Error");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }




    public void handleShowCalendar(javafx.event.ActionEvent actionEvent) throws SQLException {
        calendarContainer.getChildren().clear();

        // 🔵 Créer l'utilisateur ID = 1
        User user = new User();
        user.setId(1);

        ReservationService reservationService = new ReservationService();
        List<Reservation> reservations = reservationService.getReservationsByUser(user);

        if (reservations.isEmpty()) {
            Label noReservations = new Label("Aucune réservation trouvée.");
            noReservations.setStyle("-fx-font-size: 16px; -fx-text-fill: gray;");
            calendarContainer.getChildren().add(noReservations);
            return;
        }

        // 🔵 Grouper les réservations par mois
        Map<String, List<Reservation>> reservationsByMonth = new LinkedHashMap<>();

        DateTimeFormatter monthFormatter = DateTimeFormatter.ofPattern("MMMM yyyy", Locale.FRENCH); // Ex: Avril 2025

        for (Reservation res : reservations) {
            LocalDate date = LocalDate.parse(res.getDateReservation());
            String month = date.format(monthFormatter);

            reservationsByMonth.computeIfAbsent(month, k -> new ArrayList<>()).add(res);
        }

        // 🔵 Créer l'affichage
        for (Map.Entry<String, List<Reservation>> entry : reservationsByMonth.entrySet()) {
            // Mois titre
            Label monthLabel = new Label(entry.getKey());
            monthLabel.setStyle("-fx-font-size: 20px; -fx-font-weight: bold; -fx-text-fill: #2e86de; -fx-padding: 10 0 5 0;");

            calendarContainer.getChildren().add(monthLabel);

            // Réservations
            for (Reservation res : entry.getValue()) {
                Label resLabel = new Label("🗓️ " + res.getDateReservation() + " : " + res.getWorkshop().getTitle());
                resLabel.setStyle("-fx-background-color: #f7f9fc; -fx-border-color: #d1d8e0; -fx-padding: 10; -fx-border-radius: 8; -fx-background-radius: 8; -fx-font-size: 14px;");
                resLabel.setMaxWidth(Double.MAX_VALUE);
                VBox.setMargin(resLabel, new Insets(5, 0, 5, 0));
                calendarContainer.getChildren().add(resLabel);
            }
        }

        // 🔵 Ajouter une animation douce
        FadeTransition fade = new FadeTransition(Duration.millis(700), calendarContainer);
        fade.setFromValue(0);
        fade.setToValue(1);
        fade.play();
}

}