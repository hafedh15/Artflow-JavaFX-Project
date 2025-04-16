package tn.artflow.services;

import tn.artflow.entities.User;
import tn.artflow.tools.MyDataBase;

import org.mindrot.jbcrypt.BCrypt;
import java.sql.*;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class UserService implements IService<User>{
    Connection cnx;
    String sql;
    public UserService(){
        cnx= MyDataBase.getInstance().getCnx();
    }

    @Override
    public void ajouter(User user) throws SQLException {
        sql="insert into user(name,lastname,roles,password,email,photo,date_Creation,is_Banned,is_Verified)" +
                "values(?,?,?,?,?,?,?,?,?)";
        PreparedStatement ste = cnx.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS); // ✅

        String hashedPassword = BCrypt.hashpw(user.getPassword(), BCrypt.gensalt());

        ste.setString(1,user.getName());
        ste.setString(2,user.getLastname());
        ste.setString(3,user.getRoles());
        ste.setString(4, hashedPassword);
        ste.setString(5,user.getEmail());
        ste.setString(6,user.getPhoto());
        ste.setDate(7, new java.sql.Date(user.getDateCreation().getTime()));
        ste.setBoolean(8,user.getIsBanned());
        ste.setBoolean(9,user.getIsVerified());

        int rowsInserted = ste.executeUpdate();

        if (rowsInserted > 0) {
            ResultSet generatedKeys = ste.getGeneratedKeys();
            if (generatedKeys.next()) {
                int id = generatedKeys.getInt(1);
                user.setId(id); // 🔥 this modifies the object passed from outside
                System.out.println("User added with ID: " + id);
            }
        } else {
            System.out.println("User was not inserted.");
        }
    }

    @Override
    public void modifier(User user) throws SQLException{
        System.out.println("Updating user with ID: " + user.getId());

        sql = "UPDATE user SET name = ?, lastname = ?, email = ?, photo = ? WHERE id = ?";
        PreparedStatement ste = cnx.prepareStatement(sql);

        ste.setString(1, user.getName());
        ste.setString(2, user.getLastname());
        ste.setString(3, user.getEmail());
        ste.setString(4, user.getPhoto());
       // ste.setDate(5, new java.sql.Date(user.getDateCreation().getTime()));
        ste.setInt(5, user.getId());

        int rowsUpdated = ste.executeUpdate();
        if (rowsUpdated > 0) {
            System.out.println("User information updated successfully!");
        } else {
            System.out.println("No rows updated, user might not exist.");
        }
    }

    public void update(User user) throws SQLException{
        String sql = "UPDATE user SET name = ?, lastname = ?, email = ?, photo = ?, roles = ?, is_Banned = ? WHERE id = ?";

        try {
            PreparedStatement ste = cnx.prepareStatement(sql);

            ste.setString(1, user.getName());
            ste.setString(2, user.getLastname());
            ste.setString(3, user.getEmail());
            ste.setString(4, user.getPhoto());
            ste.setString(5, user.getRoles());
            ste.setBoolean(6, user.getIsBanned());
            ste.setInt(7, user.getId());

            int rowsUpdated = ste.executeUpdate();
            if (rowsUpdated > 0) {
                System.out.println("✅ User updated successfully!");
            } else {
                System.out.println("❌ Update failed: User not found.");
            }

        } catch (SQLException e) {
            System.err.println("❌ Error while updating user: " + e.getMessage());
            e.printStackTrace();
        }
    }

//  @Override
//    public void modifier(int id, String nom) throws SQLException {
//        sql= " update user set name='"+nom+"' where id="+id;
//        Statement st = cnx.createStatement();
//        st.executeUpdate(sql);
//        System.out.println("personne modifiée");
//    }




    @Override
    public void supprimer(User user) {
        // SQL statement to delete the user by ID
        String sql = "DELETE FROM user WHERE id = ?";

        try (PreparedStatement ste = cnx.prepareStatement(sql)) {
            // Set the user ID to the SQL statement
            ste.setInt(1, user.getId());

            // Execute the update to delete the user
            int rowsAffected = ste.executeUpdate();

            // Check if a row was deleted (should be 1 if successful)
            if (rowsAffected > 0) {
                System.out.println("User deleted successfully!");
            } else {
                System.out.println("User not found, deletion failed.");
            }

        } catch (SQLException e) {
            System.out.println("Error deleting user: " + e.getMessage());
        }
    }


    @Override
    public List<User> recuperer() throws SQLException {
        sql = "SELECT * FROM user";
        Statement st = cnx.createStatement();
        ResultSet rs = st.executeQuery(sql);
        List<User> users = new ArrayList<>();

        while (rs.next()) {
            int id = rs.getInt("id");
            String name = rs.getString("name");
            String lastname = rs.getString("lastname");

            String email = rs.getString("email");
            Boolean verification = rs.getBoolean("is_Verified");

            Date dateCreation = rs.getDate("date_Creation");

            User user = new User(id,name, lastname, email, dateCreation,verification);
            //   user.setId(id); // Set ID separately if not handled by constructor
            users.add(user);
        }

        return users;
    }


    public List<User> recupererr() throws SQLException {
        sql = "SELECT * FROM user";
        Statement st = cnx.createStatement();
        ResultSet rs = st.executeQuery(sql);
        List<User> users = new ArrayList<>();

        while (rs.next()) {
            int id = rs.getInt("id");
            String name = rs.getString("name");
            String lastname = rs.getString("lastname");
            String roles = rs.getString("roles");
            String password = rs.getString("password");
            String email = rs.getString("email");
            String photo = rs.getString("photo");
            Date dateCreation = rs.getDate("date_Creation");
            Boolean status = rs.getBoolean("is_Banned");
            Boolean verification = rs.getBoolean("is_Verified");


            User user = new User(id,name, lastname,roles,password, email, photo, dateCreation,status,verification);
         //   user.setId(id); // Set ID separately if not handled by constructor
            users.add(user);
        }

        return users;
    }



    public User login(String email, String password) throws SQLException {
        String sql = "SELECT * FROM user WHERE email = ?";
        PreparedStatement pst = cnx.prepareStatement(sql);
        pst.setString(1, email);
        ResultSet rs = pst.executeQuery();

        if (rs.next()) {
            String hashedPassword = rs.getString("password");

            if (BCrypt.checkpw(password, hashedPassword)) {
                int id = rs.getInt("id");
                String name = rs.getString("name");
                String lastname = rs.getString("lastname");
                String roles = rs.getString("roles");
                String photo = rs.getString("photo");
                Date dateCreation = rs.getDate("date_Creation");
                boolean isBanned = rs.getBoolean("is_Banned");
                boolean isVerified = rs.getBoolean("is_Verified");

                User user = new User(id,name, lastname, roles, password, email, photo, dateCreation, isBanned, isVerified);
               // user.setId(id);
                return user;
            }
        }

        return null; // login failed
    }



}
