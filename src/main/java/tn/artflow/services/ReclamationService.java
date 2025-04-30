package tn.artflow.services;

import tn.artflow.entities.Reclamation;
import tn.artflow.entities.User;
import tn.artflow.tools.MyDataBase;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class ReclamationService implements IService<Reclamation> {
    private Connection cnx;
    private String sql;

    public ReclamationService() {
        cnx = MyDataBase.getInstance().getCnx();
    }

    public void modifierStatus(int id, String status) throws SQLException {
        sql = "UPDATE reclamation SET status=? WHERE id=?";
        PreparedStatement ste = cnx.prepareStatement(sql);
        ste.setString(1, status);
        ste.setInt(2, id);
        ste.executeUpdate();
        System.out.println("Reclamation ID " + id + " status updated to: " + status);
    }

    @Override
    public void ajouter(Reclamation reclamation) throws SQLException {
        sql = "INSERT INTO reclamation (user_id, subject, message, status, is_marked, created_at) VALUES (?, ?, ?, ?, ?, ?)";
        PreparedStatement ste = cnx.prepareStatement(sql);
        ste.setInt(1, reclamation.getUserId());
        ste.setString(2, reclamation.getSubject());
        ste.setString(3, reclamation.getMessage());
        ste.setString(4, reclamation.getStatus());
        ste.setBoolean(5, reclamation.isMarked());
        ste.setTimestamp(6, Timestamp.valueOf(reclamation.getCreatedAt()));
        ste.executeUpdate();
        System.out.println("Reclamation ajoutée.");
    }

    @Override
    public void modifier(Reclamation reclamation) throws SQLException {

    }

    public void modifier(int id, String nom) throws SQLException {
        // "nom" used as "subject" here
        sql = "UPDATE reclamation SET subject='" + nom + "' WHERE id=" + id;
        Statement st = cnx.createStatement();
        st.executeUpdate(sql);
        System.out.println("Reclamation modifiée.");
    }

    @Override
    public void supprimer(Reclamation reclamation) {
        try {
            sql = "DELETE FROM reclamation WHERE id=?";
            PreparedStatement ps = cnx.prepareStatement(sql);
            ps.setInt(1, reclamation.getId());
            ps.executeUpdate();
            System.out.println("Reclamation supprimée.");
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }

    @Override
    public List<Reclamation> recuperer() throws SQLException {
        sql = "SELECT * FROM reclamation";
        Statement st = cnx.createStatement();
        ResultSet rs = st.executeQuery(sql);
        List<Reclamation> reclamations = new ArrayList<>();
        while (rs.next()) {
            int id = rs.getInt("id");
            int userId = rs.getInt("user_id");
            String subject = rs.getString("subject");
            String message = rs.getString("message");
            String status = rs.getString("status");
            boolean isMarked = rs.getBoolean("is_marked");
            LocalDateTime createdAt = rs.getTimestamp("created_at").toLocalDateTime();
            Reclamation r = new Reclamation(id, userId, subject, message, status, isMarked, createdAt);
            reclamations.add(r);
        }
        return reclamations;
    }

    @Override
    public void supprimer(int id) throws SQLException {

    }

    @Override
    public List<Reclamation> getReservationsByUser(User user) throws SQLException {
        return List.of();
    }
}
