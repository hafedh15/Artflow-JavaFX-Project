package tn.artflow.controllers;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import tn.artflow.entities.User;
import tn.artflow.services.UserService;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

import java.io.IOException;
import java.sql.SQLException;

public class AjouterUser {

    @FXML private TextField txtLastname;
    @FXML private TextField txtName;
    @FXML private TextField txtemail;
    @FXML private PasswordField txtpassword;
    @FXML private PasswordField txtConfirmPassword;

    @FXML private javafx.scene.control.Label nameError;
    @FXML private javafx.scene.control.Label lastnameError;
    @FXML private javafx.scene.control.Label emailError;
    @FXML private javafx.scene.control.Label passwordError;
    @FXML private javafx.scene.control.Label confirmPasswordError;

    @FXML
    private Button btnSignUp, btnGoogleSignUp, btnGoLogin;

    @FXML private ImageView googleIcon; // ImageView to hold the Google icon

    @FXML
    public void initialize() {
        // Load the image
        googleIcon.setImage(new Image(getClass().getResourceAsStream("/ImageUser/google.png")));
    }

    @FXML
    private void hoverSignUp() {
        btnSignUp.setStyle("-fx-background-color: #45A049; -fx-text-fill: white; -fx-font-weight: bold; -fx-background-radius: 8; -fx-padding: 12 0; -fx-pref-width: 100%;");
    }

    @FXML
    private void unhoverSignUp() {
        btnSignUp.setStyle("-fx-background-color: #4CAF50; -fx-text-fill: white; -fx-font-weight: bold; -fx-background-radius: 8; -fx-padding: 12 0; -fx-pref-width: 100%;");
    }

    @FXML
    private void hoverGoogle() {
        btnGoogleSignUp.setStyle("-fx-background-color: #ccc2c0; -fx-text-fill: white; -fx-font-weight: bold; -fx-background-radius: 8; -fx-padding: 12 0; -fx-pref-width: 100%;");
    }

    @FXML
    private void unhoverGoogle() {
        btnGoogleSignUp.setStyle("-fx-background-color: #ccc2c0; -fx-text-fill: white; -fx-font-weight: bold; -fx-background-radius: 8; -fx-padding: 12 0; -fx-pref-width: 100%;");
    }


    private final String errorStyle = "-fx-border-color: red; -fx-border-width: 1px; -fx-border-radius: 6; -fx-background-radius: 6;";
    private final String successStyle = "-fx-border-color: green; -fx-border-width: 1px; -fx-border-radius: 6; -fx-background-radius: 6;";

    @FXML
    void AjouterUser(ActionEvent event) {
        clearErrors();

        String Name = txtName.getText().trim();
        String LastName = txtLastname.getText().trim();
        String Password = txtpassword.getText().trim();
        String ConfirmPassword = txtConfirmPassword.getText().trim();
        String Email = txtemail.getText().trim();

        boolean isValid = true;

        if (Name.isEmpty()) {
            nameError.setText("Name is required.");
            txtName.setStyle(errorStyle);
            isValid = false;
        } else {
            txtName.setStyle(successStyle);
        }

        if (LastName.isEmpty()) {
            lastnameError.setText("Last name is required.");
            txtLastname.setStyle(errorStyle);
            isValid = false;
        } else {
            txtLastname.setStyle(successStyle);
        }

        if (Email.isEmpty() || !Email.matches("^[\\w.-]+@[\\w.-]+\\.[a-zA-Z]{2,6}$")) {
            emailError.setText("Enter a valid email.");
            txtemail.setStyle(errorStyle);
            isValid = false;
        } else {
            txtemail.setStyle(successStyle);
        }

        if (Password.isEmpty()) {
            passwordError.setText("Password is required.");
            txtpassword.setStyle(errorStyle);
            isValid = false;
        } else {
            txtpassword.setStyle(successStyle);
        }

        if (!Password.equals(ConfirmPassword)) {
            confirmPasswordError.setText("Passwords do not match.");
            txtConfirmPassword.setStyle(errorStyle);
            isValid = false;
        } else {
            txtConfirmPassword.setStyle(successStyle);
        }

        if (!isValid) return;

        String roles = "[\"ROLE_CLIENT\"]";
        String photo = null;
        java.util.Date dateCreation = new java.util.Date();
        boolean isBanned = false;
        boolean isVerified = true;

        User usr = new User(Name, LastName, roles, Password, Email, photo, dateCreation, isBanned, isVerified);
        UserService ps = new UserService();

        try {
            ps.ajouter(usr);

            // After successful sign up, redirect to Login.fxml
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/Login.fxml"));
            Parent root = loader.load();

            Stage stage = new Stage();
            stage.setTitle("Login");
            stage.setScene(new Scene(root));
            stage.show();

            // Close the current Sign Up window
            ((javafx.scene.Node)(event.getSource())).getScene().getWindow().hide();

        } catch (SQLException | IOException e) {
            System.out.println(e.getMessage());
            e.printStackTrace();
        }
    }

    @FXML
    void goToLogin(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/Login.fxml"));
            Parent root = loader.load();

            Stage stage = new Stage();
            stage.setTitle("Login");
            stage.setScene(new Scene(root));
            stage.show();

            ((javafx.scene.Node)(event.getSource())).getScene().getWindow().hide();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    void signUpWithGoogle(ActionEvent event) {
        System.out.println("Sign Up with Google Clicked (to be implemented)");
        // You can later implement OAuth login here if you want!
    }

    private void clearErrors() {
        nameError.setText("");
        lastnameError.setText("");
        emailError.setText("");
        passwordError.setText("");
        confirmPasswordError.setText("");

        txtName.setStyle(null);
        txtLastname.setStyle(null);
        txtemail.setStyle(null);
        txtpassword.setStyle(null);
        txtConfirmPassword.setStyle(null);
    }
}
