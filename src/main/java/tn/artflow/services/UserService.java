package tn.artflow.services;

import tn.artflow.entities.User;
import tn.artflow.tools.MyDataBase;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Date;

public class UserService implements IService<User> {
    Connection cnx;
    String sql;

    public UserService() {
        cnx = MyDataBase.getInstance().getCnx();
    }

    @Override
    public void ajouter(User user) throws SQLException {
        sql = "INSERT INTO user(name, lastname, type, password, email, photo, date_creation) VALUES (?, ?, ?, ?, ?, ?, ?)";

        // Statement.RETURN_GENERATED_KEYS pour récupérer l'ID
        PreparedStatement ste = cnx.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);

        ste.setString(1, user.getName());
        ste.setString(2, user.getLastname());
        ste.setString(3, user.getType());
        ste.setString(4, user.getPassword());
        ste.setString(5, user.getEmail());
        ste.setString(6, user.getPhoto());

        Date creationDate = user.getDateCreation();
        if (creationDate == null) {
            creationDate = new Date();
        }
        ste.setDate(7, new java.sql.Date(creationDate.getTime()));

        ste.executeUpdate();

        // Récupération de l'ID généré par la base
        ResultSet rs = ste.getGeneratedKeys();
        if (rs.next()) {
            int idGénéré = rs.getInt(1);
            user.setId(idGénéré);
            System.out.println("User ajouté avec ID : " + idGénéré);
        } else {
            System.out.println("⚠️ ID utilisateur non récupéré.");
        }
    }
    @Override
    public void modifier(User u) throws SQLException {

    }

    @Override
    public void supprimer(int id) throws SQLException {
        sql = "DELETE FROM user WHERE id = ?";
        PreparedStatement ste = cnx.prepareStatement(sql);
        ste.setInt(1, id);
        ste.executeUpdate();
        System.out.println("User supprimé !");
    }

    @Override
    public List<User> recuperer() throws SQLException {
        List<User> users = new ArrayList<>();
        sql = "SELECT * FROM user";
        Statement ste = cnx.createStatement();
        ResultSet rs = ste.executeQuery(sql);
        while (rs.next()) {
            User user = new User();
            user.setId(rs.getInt("id"));
            user.setName(rs.getString("name"));
            user.setLastname(rs.getString("lastname"));
            user.setEmail(rs.getString("email"));
            user.setPassword(rs.getString("password"));
            user.setType(rs.getString("type")); // ou "roles" si ton champ s'appelle comme ça
            user.setPhoto(rs.getString("photo"));
            user.setDateCreation(rs.getDate("date_creation"));

            users.add(user);
        }
        return users;
    }

    @Override
    public List<User> getReservationsByUser(User user) throws SQLException {
        return List.of();
    }


}
