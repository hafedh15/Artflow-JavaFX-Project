package tn.artflow.controllors;

import javafx.scene.control.Label;
import javafx.scene.layout.GridPane;
import tn.artflow.entities.Reservation;
import tn.artflow.entities.Workshop;
import tn.artflow.services.ReservationService;
import tn.artflow.services.WorkshopService;

import java.sql.SQLException;
import java.util.List;

public class DashReservation {
    public GridPane gridReservation;

    public void initialize() {
        ReservationService ws = new ReservationService();
        try {
            List<Reservation> reservations = ws.recuperer();

            // Add header row for reservations
            gridReservation.addRow(0,
                    new Label("ID"),
                    new Label("Date Reservation"),
                    new Label("Seats Reserved"),
                    new Label("Notes"),
                    new Label("Unique Code")
            );

            int row = 1;
            for (Reservation w : reservations) {
                // Add row to grid with reservation data
                gridReservation.addRow(row++,
                        new Label(String.valueOf(w.getId())),
                        new Label(w.getDateReservation()), // Assuming getDate() is the reservation date
                        new Label(String.valueOf(w.getSeatsReserved())), // Assuming there's a getSeatsReserved method
                        new Label(w.getNotes()), // Assuming there's a getNotes method
                        new Label(w.getUniqueCode()) // Assuming there's a getUniqueCode method
                );
            }
        } catch (SQLException e) {
            e.printStackTrace();
            // Consider adding proper error handling here
        }
    }

    private void refreshGrid() {
        gridReservation.getChildren().clear(); // Clear all nodes
        initialize(); // Reload reservations
    }
}