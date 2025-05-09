package tn.artflow.controllers;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;
import tn.artflow.entities.User;
import tn.artflow.services.UserService;
import tn.artflow.tools.EmailSender;
import tn.artflow.tools.EmailVerificationUtil;
import tn.artflow.services.GoogleAuthService;

import java.io.IOException;
import java.sql.SQLException;

public class LoginController {

    @FXML private TextField emailField;
    @FXML private PasswordField passwordField;
    @FXML private Label errorLabel;
    @FXML private Label emailError;
    @FXML private Label passwordError;
    @FXML private Button verifyEmailButton;

    private final UserService userService = new UserService();
    private User unverifiedUser = null;

    @FXML
    public void initialize() {
        // Hide verify email button initially
        if (verifyEmailButton != null) {
            verifyEmailButton.setVisible(false);
        }
    }

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
                }if (!user.getIsVerified()) {
                    // User is not verified, show verification option
                    errorLabel.setText("Your email is not verified. Please verify your email to continue.");
                    if (verifyEmailButton != null) {
                        verifyEmailButton.setVisible(true);
                        unverifiedUser = user;
                    }
                    return;
                }

                // User is verified and not banned - proceed with login
                tn.artflow.utils.UserSession.getInstance(user);

                // Send login notification
                try {
                    EmailSender.sendLoginNotification(user.getEmail(), user.getLastname());
                } catch (Exception e) {
                    // Non-critical error, just log it
                    System.err.println("Failed to send login notification: " + e.getMessage());
                }

                openDashboard();
            } else {
                errorLabel.setText("Invalid credentials.");
            }
        } catch (SQLException e) {
            errorLabel.setText("Database error: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void clearValidation() {
        emailError.setText("");
        passwordError.setText("");
        errorLabel.setText("");

        if (verifyEmailButton != null) {
            verifyEmailButton.setVisible(false);
        }

        emailField.setStyle(null);
        passwordField.setStyle(null);
    }

    private void openDashboard() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/Home.fxml"));
            Parent root = loader.load();

            Stage stage = new Stage();
            stage.setTitle("Dashboard - Users");
            stage.setScene(new Scene(root));
            stage.show();

            // Close login window
            Stage loginStage = (Stage) emailField.getScene().getWindow();
            loginStage.close();

        } catch (IOException e) {
            errorLabel.setText("Error opening dashboard: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * Handle verify email button click
     */
    @FXML
    private void handleVerifyEmail() {
        if (unverifiedUser == null) {
            errorLabel.setText("No user to verify. Please try logging in again.");
            return;
        }

        try {
            // Generate a new verification code
            String verificationCode = EmailVerificationUtil.generateVerificationCode(unverifiedUser.getEmail());

            // Send verification email
            EmailSender.sendVerificationEmail(unverifiedUser.getEmail(), verificationCode);

            // Open verification screen
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/VerifyEmail.fxml"));
            Parent root = loader.load();

            // Pass data to controller
            VerifyEmailController controller = loader.getController();
            controller.setEmailInfo(unverifiedUser.getEmail(), unverifiedUser.getName());

            // Show verification screen
            Stage stage = new Stage();
            stage.setTitle("Verify Your Email");
            stage.setScene(new Scene(root));
            stage.show();

            // Close login screen
            Stage loginStage = (Stage) emailField.getScene().getWindow();
            loginStage.close();

        } catch (Exception e) {
            errorLabel.setText("Error sending verification email: " + e.getMessage());
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
            errorLabel.setText("Error opening forgot password screen: " + e.getMessage());
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
            errorLabel.setText("Error opening signup screen: " + e.getMessage());
            e.printStackTrace();
        }
    }

    @FXML
    private void handleGoogleLogin(ActionEvent event) {
        try {
            // Show loading indicator or disable login button
            errorLabel.setText("Connecting to Google...");

            // Create and use the GoogleAuthService
            GoogleAuthService googleAuthService = new GoogleAuthService();

            // Start the Google authentication process
            googleAuthService.startGoogleAuth()
                    .thenAccept(user -> {
                        // This runs when authentication is successful
                        Platform.runLater(() -> {
                            try {
                                // Set user session
                                tn.artflow.utils.UserSession.getInstance(user);

                                // Send login notification
                                try {
                                    EmailSender.sendLoginNotification(user.getEmail(), user.getLastname());
                                } catch (Exception e) {
                                    // Non-critical error, just log it
                                    System.err.println("Failed to send login notification: " + e.getMessage());
                                }

                                // Open dashboard
                                openDashboard();
                            } catch (Exception e) {
                                errorLabel.setText("Error after Google login: " + e.getMessage());
                                e.printStackTrace();
                            }
                        });
                    })
                    .exceptionally(ex -> {
                        // This runs when authentication fails
                        Platform.runLater(() -> {
                            errorLabel.setText("Google login failed: " + ex.getMessage());
                            ex.printStackTrace();
                        });
                        return null;
                    });

        } catch (Exception e) {
            errorLabel.setText("Error starting Google login: " + e.getMessage());
            e.printStackTrace();
        }
    }
}