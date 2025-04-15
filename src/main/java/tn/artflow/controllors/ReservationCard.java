package tn.artflow.controllors;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import tn.artflow.entities.Reservation;

public class ReservationCard {

    @FXML
    private Label workshopTitleLabel;

    @FXML
    private Label dateLabel;

    @FXML
    private Label seatsLabel;

    @FXML
    private Label notesLabel;

    @FXML
    private Label codeLabel;

    private Reservation reservation;

    private Profil profilController;


    public void setData(Reservation r) {
        System.out.println("Setting data for reservation: " + r.getId());

        if (r.getWorkshop() != null) {
            workshopTitleLabel.setText("🧵 Atelier: " + r.getWorkshop().getTitle());
        } else {
            workshopTitleLabel.setText("🧵 Atelier: [Données manquantes]");
            System.out.println("WARNING: Workshop is null for reservation: " + r.getId());
        }

        dateLabel.setText("📅 Date: " + r.getDateReservation());
        seatsLabel.setText("🎫 Places réservées: " + r.getSeatsReserved());
        notesLabel.setText("📝 Notes: " + (r.getNotes() != null ? r.getNotes() : "-"));
        codeLabel.setText("🔐 Code unique: " + r.getUniqueCode());

        System.out.println("Data set successfully for card");
    }


}