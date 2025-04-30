package tn.artflow.controllers;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.Stage;
import tn.artflow.entities.Article;
import tn.artflow.entities.Reservation;
import tn.artflow.entities.User;
import tn.artflow.entities.Workshop;
import tn.artflow.services.ReservationService;

import java.io.File;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.UUID;

public class RegisterReservation {

    @FXML
    private TextField seatsField;

    @FXML
    private ImageView imageView;

    @FXML
    private TextArea notesArea;
    @FXML
    private Label seatsErrorLabel;

    @FXML
    private Label notesErrorLabel;


    @FXML
    private Label workshopTitle;

    @FXML
    private Label workshopDate;

    @FXML
    private Label workshopLocation;

    @FXML
    private Label workshopType;
    @FXML
    private Label workshopDescription;

    @FXML
    private ImageView workshopImage;

    private Workshop workshop;

    public void setWorkshop(Workshop w) {
        this.workshop = w;

        workshopTitle.setText("📚 " + w.getTitle());
        workshopDescription.setText("📚 " + w.getDescription());
        workshopDate.setText("📅 " + w.getDate());
        workshopLocation.setText("📍 " + w.getLocation());
        workshopType.setText("🧾 Type: " + w.getType());

        String imageName = w.getImage(); // Ex: "/images/workshops/example.jpg"
        String imageFullPath = "C:/xampp/htdocs" + imageName;

        File imageFile = new File(imageFullPath);
        Image image = imageFile.exists()
                ? new Image(imageFile.toURI().toString())
                : new Image(getClass().getResourceAsStream("/images/default-workshop.png"));

        imageView.setImage(image);

    }

    @FXML
    private void handleSubmit() {
        // Clear previous errors
        seatsErrorLabel.setText("");
        notesErrorLabel.setText("");

        boolean isValid = true;

        // Validate seats
        String seatText = seatsField.getText().trim();
        int seats = 0;
        try {
            seats = Integer.parseInt(seatText);
            if (seats < 1 || seats > 4) {
                seatsErrorLabel.setText("You can reserve 1 to 4 seats only.");
                isValid = false;
            }
        } catch (NumberFormatException e) {
            seatsErrorLabel.setText("Please enter a valid number.");
            isValid = false;
        }

        // Validate notes
        String notes = notesArea.getText().trim();
        if (notes.length() > 20) {
            notesErrorLabel.setText("Notes cannot exceed 20 characters.");
            isValid = false;
        }

        if (!isValid) return;

        try {
            String date = LocalDate.now().toString();
            String code = "RES-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();

            Reservation reservation = new Reservation();
            reservation.setSeatsReserved(seats);
            reservation.setNotes(notes);
            reservation.setDateReservation(date);
            reservation.setUniqueCode(code);
            reservation.setWorkshop(workshop);

            User currentUser = tn.artflow.utils.UserSession.getInstance().getUser();
            reservation.setUser(currentUser);

           // reservation.setUser(currentUser);

            new ReservationService().ajouter(reservation);

            System.out.println("✅ Reservation successful!");
            Stage stage = (Stage) seatsField.getScene().getWindow();
            stage.close();

        } catch (SQLException e) {
            seatsErrorLabel.setText("Something went wrong. Try again.");
        }
    }





    @FXML
    void cancelUpdate(ActionEvent event) {
        // Close the window
        Stage stage = (Stage) notesArea.getScene().getWindow();
        stage.close();
    }


}
