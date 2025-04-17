package tn.esprit.services;

import tn.esprit.entities.Reponse;
import tn.esprit.tools.MyDataBase;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class ReponseService implements IService<Reponse> {
    private Connection cnx;
    private String sql;

    public ReponseService() {
        cnx = MyDataBase.getInstance().getCnx();
    }

    @Override
    public void ajouter(Reponse reponse) throws SQLException {
        sql = "INSERT INTO reponse (reclamation_id, user_id, message, is_read, created_at) VALUES (?, ?, ?, ?, ?)";
        PreparedStatement ste = cnx.prepareStatement(sql);
        ste.setInt(1, reponse.getReclamationId());
        ste.setInt(2, reponse.getUserId());
        ste.setString(3, reponse.getMessage());
        ste.setBoolean(4, reponse.isRead());
        ste.setTimestamp(5, Timestamp.valueOf(reponse.getCreatedAt()));
        ste.executeUpdate();
        System.out.println("Reponse ajoutée.");
    }

    /*@Override
    public void modifier(int id, String message) throws SQLException {
        sql = "UPDATE reponse SET message='" + message + "' WHERE id=" + id;
        Statement st = cnx.createStatement();
        st.executeUpdate(sql);
        System.out.println("Reponse modifiée.");
    }*/


    @Override
    public void modifier(int id, String message) throws SQLException {
        sql = "UPDATE reponse SET message=? WHERE id=?";
        PreparedStatement ps = cnx.prepareStatement(sql);
        ps.setString(1, message); // handles quotes, special chars safely
        ps.setInt(2, id);
        ps.executeUpdate();
        System.out.println("Reponse modifiée.");
    }


    @Override
    public void supprimer(Reponse reponse) {
        try {
            sql = "DELETE FROM reponse WHERE id=?";
            PreparedStatement ps = cnx.prepareStatement(sql);
            ps.setInt(1, reponse.getId());
            ps.executeUpdate();
            System.out.println("Reponse supprimée.");
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }

    @Override
    public List<Reponse> recuperer() throws SQLException {
        sql = "SELECT * FROM reponse";
        Statement st = cnx.createStatement();
        ResultSet rs = st.executeQuery(sql);
        List<Reponse> reponses = new ArrayList<>();
        while (rs.next()) {
            int id = rs.getInt("id");
            int reclamationId = rs.getInt("reclamation_id");
            int userId = rs.getInt("user_id");
            String message = rs.getString("message");
            boolean isRead = rs.getBoolean("is_read");
            LocalDateTime createdAt = rs.getTimestamp("created_at").toLocalDateTime();
            Reponse r = new Reponse(id, reclamationId, userId, message, isRead, createdAt);
            reponses.add(r);
        }
        return reponses;
    }
}
