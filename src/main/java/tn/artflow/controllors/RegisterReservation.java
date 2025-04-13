package tn.artflow.controllors;

import javafx.fxml.FXML;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import tn.artflow.entities.Reservation;
import tn.artflow.entities.Workshop;
import tn.artflow.entities.User;
import tn.artflow.services.ReservationService;

import java.sql.SQLException;
import java.time.LocalDate;
import java.util.UUID;

public class RegisterReservation {

    @FXML
    private TextField seatsField;

    @FXML
    private TextArea notesArea;

    private Workshop workshop;

    public void setWorkshop(Workshop w) {
        this.workshop = w;
    }

    @FXML
    private void handleSubmit() throws SQLException {
            // Récupérer les valeurs entrées
            int seats = Integer.parseInt(seatsField.getText());
            String notes = notesArea.getText();
            String date = LocalDate.now().toString();
            String code = "RES-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();

            // Créer un objet Reservation
            Reservation reservation = new Reservation();
            reservation.setSeatsReserved(seats);
            reservation.setNotes(notes);
            reservation.setDateReservation(date);
            reservation.setUniqueCode(code);
            reservation.setWorkshop(workshop);

            // Remplace ça avec l'utilisateur connecté
            User currentUser = new User();
            currentUser.setId(1); // Par exemple, l'ID utilisateur est 1
            reservation.setUser(currentUser);

            // Ajouter la réservation via le service
            new ReservationService().ajouter(reservation);

            System.out.println("✅ Réservation enregistrée avec succès !");

            // Fermer la fenêtre après soumission
            Stage stage = (Stage) seatsField.getScene().getWindow(); // Obtenir le stage actuel
            stage.close(); // Fermer la fenêtre


    }
}
