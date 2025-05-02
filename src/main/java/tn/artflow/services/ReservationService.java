package tn.artflow.services;
import tn.artflow.entities.Comment;
import tn.artflow.entities.Reservation;
import tn.artflow.entities.User;
import tn.artflow.entities.Workshop;
import tn.artflow.tools.MyDataBase;

import java.sql.*;
import java.util.*;
import java.util.Date;

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
    public void modifier(Reservation reservation) throws SQLException {
        String sql = "UPDATE reservation SET seats_reserved = ?, notes = ? WHERE id = ?";
        PreparedStatement ste = cnx.prepareStatement(sql);
            ste.setInt(1, reservation.getSeatsReserved());
            ste.setString(2, reservation.getNotes());
            ste.setInt(3, reservation.getId());
            ste.executeUpdate();
            System.out.println("Workshop modifié");
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

    public List<Reservation> getReservationsByUser(User user) throws SQLException {
        System.out.println("getReservationsByUser called for user ID: " + user.getId());
        System.out.println("Database connection status: " + (cnx != null ? "Connected" : "Not connected"));

        if (cnx == null) {
            throw new SQLException("Database connection is null. Verify your connection setup.");
        }

        List<Reservation> reservations = new ArrayList<>();
        String query = "SELECT r.*, w.title, w.date FROM reservation r\n" +
                "JOIN workshop w ON r.workshop_id = w.id WHERE r.user_id = ?\n";

        System.out.println("Executing SQL: " + query + " with user_id = " + user.getId());

        try (PreparedStatement ps = cnx.prepareStatement(query)) {
            ps.setInt(1, user.getId());

            System.out.println("SQL prepared, executing query...");
            try (ResultSet rs = ps.executeQuery()) {
                System.out.println("Query executed, processing results...");

                while (rs.next()) {
                    Reservation r = new Reservation();
                    r.setId(rs.getInt("id"));
                    r.setSeatsReserved(rs.getInt("seats_reserved"));
                    r.setDateReservation(rs.getString("date_reservation"));
                    r.setNotes(rs.getString("notes"));
                    r.setUniqueCode(rs.getString("unique_code"));


                    Workshop w = new Workshop();
                    w.setId(rs.getInt("workshop_id"));  // Also get the workshop ID
                    w.setTitle(rs.getString("title"));
                    w.setDate(rs.getString("date")); // 👈 ASSURE-TOI QUE CETTE LIGNE EXISTE

                    r.setWorkshop(w);

                    System.out.println("Found reservation: ID=" + r.getId() +
                            ", Workshop=" + w.getTitle() +
                            ", Date=" + r.getDateReservation());

                    reservations.add(r);
                }

                System.out.println("Result processing complete. Total reservations: " + reservations.size());
            }
        } catch (SQLException e) {
            System.err.println("SQL ERROR in getReservationsByUser: " + e.getMessage());
            throw e; // Re-throw to be handled by caller
        }

        return reservations;
    }






}