package tn.artflow.controllers;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import tn.artflow.entities.User;
import tn.artflow.services.UserService;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Parent;
import javafx.stage.Stage;

import java.io.IOException;
import java.sql.SQLException;

public class AjouterUser {

    @FXML
    private TextField txtLastname;

    @FXML
    private TextField txtName;

    @FXML
    private TextField txtemail;

    @FXML
    private PasswordField txtpassword;

    @FXML
    private PasswordField txtConfirmPassword;


    @FXML
    void AjouterUser(ActionEvent event) {

        String Name = txtName.getText();
        String LastName = txtLastname.getText();
        String Password = txtpassword.getText();
        String ConfirmPassword = txtConfirmPassword.getText();
        String Email = txtemail.getText();
        String roles = "[\"ROLE_CLIENT\"]";   // Or however you store roles
        String photo = null;                  // No photo provided
        java.util.Date dateCreation = new java.util.Date(); // Now
        boolean isBanned = false;             // Default not banned
        boolean isVerified = true;

        if (!Password.equals(ConfirmPassword)) {
            System.out.println("Passwords do not match!");
            // Optional: Show alert to user
            javafx.scene.control.Alert alert = new javafx.scene.control.Alert(javafx.scene.control.Alert.AlertType.ERROR);
            alert.setTitle("Error");
            alert.setHeaderText("Password Mismatch");
            alert.setContentText("Password and Confirm Password do not match.");
            alert.showAndWait();
            return;
        }
        User usr = new User(Name,LastName,roles,Password,Email,photo,dateCreation,isBanned,isVerified);
        UserService ps = new UserService();
        try {
            ps.ajouter(usr);

            // ➕ Load and show AfficherUser window
//            FXMLLoader loader = new FXMLLoader(getClass().getResource("/AfficherUser.fxml"));
//            Parent root = loader.load();
//
//            // Optional: You can also pass the user to the controller here if needed
//
//            javafx.stage.Stage stage = new javafx.stage.Stage();
//            stage.setTitle("Liste des Utilisateurs");
//            stage.setScene(new javafx.scene.Scene(root));
//            stage.show();
//
//            // Close the current window (AjouterUser)
//            ((javafx.scene.Node)(event.getSource())).getScene().getWindow().hide();


            FXMLLoader loader = new FXMLLoader(getClass().getResource("/ProfilUser.fxml"));
            Parent root = loader.load();

// Pass the user to the profile controller
            ProfilUserController controller = loader.getController();
           // controller.setUser(tn.artflow.utils.UserSession.getInstance().getUser());
            controller.setUser(usr);

            Stage profileStage = new Stage();
            profileStage.setTitle("Profil Utilisateur");
            profileStage.setScene(new javafx.scene.Scene(root));
            profileStage.show();

            ((javafx.scene.Node)(event.getSource())).getScene().getWindow().hide();

        } catch (SQLException | IOException e) {
            System.out.println(e.getMessage());
            e.printStackTrace();
        }


    }


}
