package tn.artflow.controllers;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import tn.artflow.entities.User;
import tn.artflow.services.UserService;
import tn.artflow.tools.EmailSender;
import tn.artflow.tools.EmailVerificationUtil;

import java.io.IOException;
import java.sql.SQLException;

public class VerifyEmailController {

    @FXML private Label titleLabel;
    @FXML private Label messageLabel;
    @FXML private Label emailLabel;
    @FXML private TextField codeField;
    @FXML private Label errorLabel;
    @FXML private Button verifyButton;
    @FXML private Button resendButton;
    @FXML private Button loginButton;

    private String userEmail;
    private String firstName;
    private UserService userService = new UserService();

    // Countdown for resend button
    private int resendCountdown = 0;
    private javafx.animation.Timeline countdownTimeline;

    @FXML
    public void initialize() {
        // Set up code field listener for formatting and validation
        codeField.textProperty().addListener((observable, oldValue, newValue) -> {
            // Only allow digits
            if (!newValue.matches("\\d*")) {
                codeField.setText(newValue.replaceAll("[^\\d]", ""));
            }

            // Limit to 6 digits
            if (newValue.length() > 6) {
                codeField.setText(newValue.substring(0, 6));
            }

            // Enable/disable verify button based on input length
            verifyButton.setDisable(codeField.getText().length() != 6);
        });

        // Set up countdown timeline for resend button
        countdownTimeline = new javafx.animation.Timeline(
                new javafx.animation.KeyFrame(
                        javafx.util.Duration.seconds(1),
                        event -> updateResendCountdown()
                )
        );
        countdownTimeline.setCycleCount(javafx.animation.Animation.INDEFINITE);
    }

    /**
     * Set email information for verification
     */
    public void setEmailInfo(String email, String firstName) {
        this.userEmail = email;
        this.firstName = firstName;

        // Update UI
        emailLabel.setText(email);
        messageLabel.setText("We've sent a verification code to your email. Please check your inbox and enter the 6-digit code below to verify your account.");
    }

    /**
     * Handle verify button click
     */
    @FXML
    private void handleVerify(ActionEvent event) {
        String code = codeField.getText().trim();

        if (code.length() != 6) {
            errorLabel.setText("Please enter a valid 6-digit code");
            return;
        }

        // Verify the code
        if (EmailVerificationUtil.verifyCode(userEmail, code)) {
            try {
                // Update user's verification status in database
                User user = userService.findByEmail(userEmail);
                if (user != null) {
                    user.setIsVerified(true);
                    userService.updateVerificationStatus(user.getId(), true);

                    // Show success message
                    titleLabel.setText("Email Verified!");
                    messageLabel.setText("Your email has been successfully verified. You can now log in to your account.");
                    emailLabel.setVisible(false);
                    codeField.setVisible(false);
                    verifyButton.setVisible(false);
                    resendButton.setVisible(false);
                    errorLabel.setText("");
                    loginButton.setText("Go to Login");

                    // Send welcome email
                    try {
                        EmailSender.sendWelcomeEmail(userEmail, firstName);
                    } catch (Exception e) {
                        // Non-critical error, just log it
                        System.err.println("Failed to send welcome email: " + e.getMessage());
                    }
                } else {
                    errorLabel.setText("User not found. Please try registering again.");
                }
            } catch (SQLException e) {
                errorLabel.setText("Database error: " + e.getMessage());
                e.printStackTrace();
            }
        } else {
            errorLabel.setText("Invalid or expired verification code. Please try again.");
        }
    }

    /**
     * Handle resend button click
     */
    @FXML
    private void handleResend(ActionEvent event) {
        // Generate new verification code
        String newCode = EmailVerificationUtil.generateVerificationCode(userEmail);

        // Send new verification email
        try {
            EmailSender.sendVerificationEmail(userEmail, newCode);
            errorLabel.setText("");
            messageLabel.setText("A new verification code has been sent to your email. Please check your inbox.");

            // Start the countdown (30 seconds)
            startResendCountdown(30);
        } catch (Exception e) {
            errorLabel.setText("Failed to resend verification code: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * Handle login button click
     */
    @FXML
    private void handleLogin(ActionEvent event) {
        try {
            // Load login screen
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/Login.fxml"));
            Parent root = loader.load();

            Stage stage = new Stage();
            stage.setTitle("Login");
            stage.setScene(new Scene(root));
            stage.show();

            // Close current window
            ((javafx.scene.Node)(event.getSource())).getScene().getWindow().hide();

        } catch (IOException e) {
            errorLabel.setText("Error opening login screen: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * Start resend button countdown
     */
    private void startResendCountdown(int seconds) {
        resendCountdown = seconds;
        resendButton.setDisable(true);
        resendButton.setText("Resend Code (" + resendCountdown + ")");
        countdownTimeline.play();
    }

    /**
     * Update resend button countdown
     */
    private void updateResendCountdown() {
        resendCountdown--;

        Platform.runLater(() -> {
            if (resendCountdown > 0) {
                resendButton.setText("Resend Code (" + resendCountdown + ")");
            } else {
                resendButton.setText("Resend Code");
                resendButton.setDisable(false);
                countdownTimeline.stop();
            }
        });
    }

    /**
     * Clean up resources when the controller is no longer needed
     */
    public void cleanup() {
        if (countdownTimeline != null) {
            countdownTimeline.stop();
        }
    }
}