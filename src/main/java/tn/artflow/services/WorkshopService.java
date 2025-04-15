package tn.artflow.services;


import tn.artflow.entities.User;
import tn.artflow.entities.Workshop;
import tn.artflow.tools.MyDataBase;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Date;

public class WorkshopService implements IService<Workshop> {
    Connection cnx;
    String sql;

    public WorkshopService() {
        cnx = MyDataBase.getInstance().getCnx();
    }
    @Override
    public void ajouter(Workshop workshop) throws SQLException {
        sql = "INSERT INTO workshop(title, description, image, date, type, location) VALUES (?, ?, ?, ?, ?, ?)";
        PreparedStatement ste = cnx.prepareStatement(sql);
        ste.setString(1, workshop.getTitle());
        ste.setString(2, workshop.getDescription());
        ste.setString(3, workshop.getImage());
        ste.setString(4, workshop.getDate());
        ste.setString(5, workshop.getType());
        ste.setString(6, workshop.getLocation());

        ste.executeUpdate();
        System.out.println("Workshop ajouté");
    }

    @Override
    public void modifier(Workshop w) throws SQLException {
        String sql = "UPDATE workshop SET title = ?, description = ?, image = ?, date = ?, type = ?, location = ? WHERE id = ?";
        PreparedStatement ste = cnx.prepareStatement(sql);

        ste.setString(1, w.getTitle());
        ste.setString(2, w.getDescription());
        ste.setString(3, w.getImage());
        ste.setString(4, w.getDate());
        ste.setString(5, w.getType());
        ste.setString(6, w.getLocation());
        ste.setInt(7, w.getId());

        ste.executeUpdate();
        System.out.println("Workshop modifié");
    }


    @Override
    public void supprimer(int id) {
        String sql = "DELETE FROM workshop WHERE id = ?";

        try {
            PreparedStatement ste = cnx.prepareStatement(sql);
            ste.setInt(1, id);
            // Set the ID safely
            ste.executeUpdate();
            System.out.println("Workshop supprimé");
        } catch (SQLException ex) {
            System.out.println(ex.getMessage());
        }
    }


    @Override
    public List<Workshop> recuperer() throws SQLException {
        sql = "SELECT * FROM workshop";
        Statement st = cnx.createStatement();
        ResultSet rs = st.executeQuery(sql);
        List<Workshop> workshops = new ArrayList<>();
        while (rs.next()) {
            int id = rs.getInt("id");
            String title = rs.getString("title");
            String description = rs.getString("description");
            String image = rs.getString("image");
            String date = rs.getString("date");
            String type = rs.getString("type");
            String location = rs.getString("location");
            Workshop w = new Workshop(id,title, description, image, date, type, location);
            workshops.add(w);
        }
        return workshops;
    }

    @Override
    public List<Workshop> getReservationsByUser(User user) {
        return List.of();
    }


}
