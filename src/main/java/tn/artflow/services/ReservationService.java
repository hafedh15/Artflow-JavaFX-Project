package tn.artflow.services;
import tn.artflow.entities.Comment;
import tn.artflow.entities.Reservation;
import tn.artflow.entities.User;
import tn.artflow.entities.Workshop;
import tn.artflow.tools.MyDataBase;

import java.sql.*;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class ReservationService implements IService<Reservation> {

    private Connection cnx;
    private String sql;

    public ReservationService() {
        cnx = MyDataBase.getInstance().getCnx();
    }

    @Override
    public void ajouter(Reservation reservation) throws SQLException {
        sql = "INSERT INTO reservation(user_id, workshop_id, seats_reserved, notes, date_reservation, unique_code) VALUES (?, ?, ?, ?, ?, ?)";
        PreparedStatement ste = cnx.prepareStatement(sql);
        ste.setInt(1, reservation.getUser().getId());
        ste.setInt(2, reservation.getWorkshop().getId());
        ste.setInt(3, reservation.getSeatsReserved());
        ste.setString(4, reservation.getNotes());
        ste.setString(5, reservation.getDateReservation());
        ste.setString(6, reservation.getUniqueCode());
        ste.executeUpdate();
        System.out.println("Réservation ajoutée !");
    }

    @Override
    public void modifier(Reservation r) throws SQLException {

    }

    @Override
    public void supprimer(int id) {
        String sql = "DELETE FROM reservation WHERE id = ?";
        try {
            PreparedStatement ste = cnx.prepareStatement(sql);
            ste.setInt(1, id);
            ste.executeUpdate();
            System.out.println("Reservation supprimée");
        } catch (SQLException ex) {
            System.out.println(ex.getMessage());
        }
    }


    @Override
    public List<Reservation> recuperer() throws SQLException {
        List<Reservation> reservations = new ArrayList<>();
        sql = "SELECT r.id, r.seats_reserved, r.notes, r.date_reservation, r.unique_code, r.user_id, r.workshop_id " +
                "FROM reservation r";  // Requête simplifiée pour ne récupérer que les champs de Reservation

        PreparedStatement preparedStatement = cnx.prepareStatement(sql);
        ResultSet rs = preparedStatement.executeQuery();

        while (rs.next()) {
            Reservation reservation = new Reservation();
            reservation.setId(rs.getInt("id"));
            reservation.setSeatsReserved(rs.getInt("seats_reserved"));
            reservation.setNotes(rs.getString("notes"));
            reservation.setDateReservation(rs.getString("date_reservation"));
            reservation.setUniqueCode(rs.getString("unique_code"));

            reservations.add(reservation);


        }

        return reservations;
    }


}