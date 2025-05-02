package tn.artflow.controllors;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import tn.artflow.entities.Reservation;
import tn.artflow.entities.User;
import tn.artflow.entities.Workshop;
import tn.artflow.services.ReservationService;

import java.net.URL;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;

public class Profil implements Initializable {

    @FXML private TilePane cardsContainer;
    @FXML private AnchorPane editPane;
    @FXML private TextField seatsField;
    @FXML private TextArea notesField;
    @FXML private GridPane calendarGrid;

    private Reservation reservationBeingEdited;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        loadReservations();
    }

    private void loadReservations() {
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

                Label date = new Label("📅 Date: " + r.getWorkshop().getDate());
                Label seats = new Label("🎫 Places: " + r.getSeatsReserved());
                Label notes = new Label("📝 Notes: " + (r.getNotes() != null ? r.getNotes() : "-"));
                Label code = new Label("🔐 Code: " + r.getUniqueCode());

                Button deleteButton = new Button("🗑️ Supprimer");
                deleteButton.setStyle("-fx-background-color: #e74c3c; -fx-text-fill: white;");
                deleteButton.setOnAction(e -> {
                    reservationService.supprimer(r.getId());
                    loadReservations();
                });

                Button editButton = new Button("✏️ Modifier");
                editButton.setStyle("-fx-background-color: #3498db; -fx-text-fill: white;");
                editButton.setOnAction(e -> showEditPane(r));

                card.getChildren().addAll(title, date, seats, notes, code, editButton, deleteButton);
                card.setPrefWidth(260);
                card.setPrefHeight(180);

                cardsContainer.getChildren().add(card);
            }

            generateCalendar(LocalDate.of(2025, 5, 1), reservations); // Choisis le mois ici

        } catch (SQLException e) {
            cardsContainer.getChildren().clear();
            Label errorLabel = new Label("Erreur lors du chargement des réservations : " + e.getMessage());
            errorLabel.setStyle("-fx-background-color: #ffcccc; -fx-padding: 20;");
            cardsContainer.getChildren().add(errorLabel);
        }
    }

    private void generateCalendar(LocalDate startOfMonth, List<Reservation> reservations) {
        calendarGrid.getChildren().clear();

        String[] days = {"Sun", "Mon", "Tue", "Wed", "Thu", "Fri", "Sat"};
        for (int i = 0; i < days.length; i++) {
            Label dayLabel = new Label(days[i]);
            dayLabel.setStyle("-fx-font-weight: bold; -fx-text-fill: #3e2723; -fx-font-size: 14;");
            dayLabel.setPrefSize(50, 30);
            dayLabel.setAlignment(Pos.CENTER);
            calendarGrid.add(dayLabel, i, 0);
        }

        LocalDate firstDay = startOfMonth.withDayOfMonth(1);
        int startCol = firstDay.getDayOfWeek().getValue() % 7;
        int row = 1;
        int col = startCol;
        int lengthOfMonth = firstDay.lengthOfMonth();

        for (int day = 1; day <= lengthOfMonth; day++) {
            LocalDate current = startOfMonth.withDayOfMonth(day);

            VBox cell = new VBox();
            cell.setAlignment(Pos.CENTER);
            cell.setSpacing(5);
            cell.setPrefSize(50, 50);
            cell.setPadding(new Insets(6));
            cell.setStyle("-fx-background-color: #ffffff; -fx-border-color: #ddd; -fx-background-radius: 10;");

            Label dayNum = new Label(String.valueOf(day));
            dayNum.setStyle("-fx-font-size: 14; -fx-text-fill: #3e2723;");
            cell.getChildren().add(dayNum);

            // Highlight reserved days
            boolean reserved = reservations.stream().anyMatch(r -> {
                try {
                    Workshop w = r.getWorkshop();
                    if (w == null || w.getDate() == null) return false;
                    String dateStr = w.getDate().trim();
                    LocalDate workshopDate = LocalDate.parse(dateStr.split(" ")[0]);
                    return workshopDate.equals(current);
                } catch (Exception e) {
                    return false;
                }
            });

            if (reserved) {
                cell.setStyle("-fx-background-color: #d7ccc8; -fx-background-radius: 10; -fx-border-color: transparent;");
                Label badge = new Label("Workshop");
                badge.setStyle("-fx-text-fill: #5d4037; -fx-font-size: 10;");
                cell.getChildren().add(badge);
            }

            // Highlight today
            if (current.equals(LocalDate.now())) {
                cell.setStyle(cell.getStyle() + "-fx-border-color: #8d6e63; -fx-border-width: 2;");
            }

            // Optional: Hover effect
            cell.setOnMouseEntered(e -> cell.setStyle(cell.getStyle() + "-fx-background-color: #f0eae5;"));
            cell.setOnMouseExited(e -> {
                if (reserved) {
                    cell.setStyle("-fx-background-color: #d7ccc8; -fx-background-radius: 10; -fx-border-color: transparent;");
                } else if (current.equals(LocalDate.now())) {
                    cell.setStyle("-fx-background-color: #ffffff; -fx-background-radius: 10; -fx-border-color: #8d6e63; -fx-border-width: 2;");
                } else {
                    cell.setStyle("-fx-background-color: #ffffff; -fx-background-radius: 10; -fx-border-color: #ddd;");
                }
            });

            calendarGrid.add(cell, col, row);

            col++;
            if (col > 6) {
                col = 0;
                row++;
            }
        }
    }

    private void showEditPane(Reservation reservation) {
        reservationBeingEdited = reservation;
        seatsField.setText(String.valueOf(reservation.getSeatsReserved()));
        notesField.setText(reservation.getNotes());
        editPane.setVisible(true);
    }

    @FXML
    private void closeEditPane() {
        editPane.setVisible(false);
        reservationBeingEdited = null;
    }

    @FXML
    private void saveEditedReservation() throws SQLException {
        if (reservationBeingEdited == null) return;

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

        String newNotes = notesField.getText().trim();
        int wordCount = newNotes.isEmpty() ? 0 : newNotes.split("\\s+").length;
        if (wordCount > 5) {
            showError("Notes must not exceed 5 words.");
            return;
        }

        reservationBeingEdited.setSeatsReserved(newSeats);
        reservationBeingEdited.setNotes(newNotes);

        ReservationService rs = new ReservationService();
        rs.modifier(reservationBeingEdited);

        editPane.setVisible(false);
        loadReservations();
    }

    private void showError(String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Erreur");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
