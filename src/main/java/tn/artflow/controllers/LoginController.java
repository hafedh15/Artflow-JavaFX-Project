package tn.artflow.controllers;

import com.google.api.client.auth.oauth2.Credential;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;
import tn.artflow.entities.GoogleSignIn;
import tn.artflow.entities.User;
import tn.artflow.services.UserService;

import java.io.IOException;
import java.sql.SQLException;

public class LoginController {

    @FXML
    private TextField emailField;
    @FXML
    private PasswordField passwordField;
    @FXML
    private Label errorLabel;

    @FXML private Label emailError;
    @FXML private Label passwordError;


    private final UserService userService = new UserService();

    @FXML
    private void handleLogin() {
        clearValidation();

        String email = emailField.getText().trim();
        String password = passwordField.getText().trim();
        boolean isValid = true;

        if (email.isEmpty()) {
            emailError.setText("Email is required.");
            emailField.setStyle("-fx-border-color: red; -fx-border-radius: 5;");
            isValid = false;
        } else {
            emailField.setStyle("-fx-border-color: green; -fx-border-radius: 5;");
        }

        if (password.isEmpty()) {
            passwordError.setText("Password is required.");
            passwordField.setStyle("-fx-border-color: red; -fx-border-radius: 5;");
            isValid = false;
        } else {
            passwordField.setStyle("-fx-border-color: green; -fx-border-radius: 5;");
        }

        if (!isValid) return;

        try {
            User user = userService.login(email, password);
            if (user != null) {
                if (user.getIsBanned()) {
                    errorLabel.setText("You are banned from accessing the application.");
                    return;
                }
                tn.artflow.utils.UserSession.getInstance(user);
                openDashboard();
            } else {
                errorLabel.setText("Invalid credentials.");
            }
        } catch (SQLException e) {
            errorLabel.setText("Database error.");
            e.printStackTrace();
        }
    }

    private void clearValidation() {
        emailError.setText("");
        passwordError.setText("");
        errorLabel.setText("");

        emailField.setStyle(null);
        passwordField.setStyle(null);
    }


    private void openDashboard() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/AfficherUser.fxml"));
            Parent root = loader.load();

            Stage stage = new Stage();
            stage.setTitle("Dashboard - Users");
            stage.setScene(new Scene(root));
            stage.show();

            // Close login window
            Stage loginStage = (Stage) emailField.getScene().getWindow();
            loginStage.close();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void handleForgotPassword(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/ForgotPassword.fxml"));
            Parent root = loader.load();
            Stage stage = new Stage();
            stage.setTitle("Forgot Password");
            stage.setScene(new Scene(root));
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void handleSignUp(ActionEvent event) {
        // Load the signup window
        try {
            Parent root = FXMLLoader.load(getClass().getResource("/AjouterUser.fxml"));
            Stage stage = new Stage();
            stage.setTitle("Sign Up");
            stage.setScene(new Scene(root));
            stage.show();
            ((javafx.scene.Node)(event.getSource())).getScene().getWindow().hide();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    @FXML
    private void handleGoogleLogin(ActionEvent event) {
        try {
            Credential credential = GoogleSignIn.authorize();
            String accessToken = credential.getAccessToken();

            // OPTIONAL: Get user info using Google API
            System.out.println("✅ Google Sign-In Successful. Access Token: " + accessToken);

            // You can now authenticate with Symfony backend if needed

        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("❌ Google Sign-In Failed.");
        }
    }


}
