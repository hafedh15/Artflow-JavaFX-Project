package tn.artflow.controllers;

import jakarta.mail.MessagingException;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;
import tn.artflow.tools.EmailUtil;
import tn.artflow.tools.PasswordResetTokenStore;

import java.io.IOException;
import java.util.UUID;

public class ForgotPasswordController {

    @FXML
    private TextField emailField;

    @FXML
    private Label emailError;

    @FXML
    private Label successLabel;

    @FXML
    private Button sendResetButton; // make sure to set fx:id="sendResetButton" on the button in FXML

    @FXML
    private void hoverSendButton() {
        sendResetButton.setStyle("-fx-background-color: #45a049; -fx-text-fill: white; -fx-background-radius: 8; -fx-font-size: 14px; -fx-padding: 10 20; -fx-cursor: hand; -fx-font-weight: bold;");
    }

    @FXML
    private void unhoverSendButton() {
        sendResetButton.setStyle("-fx-background-color: #4CAF50; -fx-text-fill: white; -fx-background-radius: 8; -fx-font-size: 14px; -fx-padding: 10 20; -fx-cursor: hand; -fx-font-weight: bold;");
    }


    @FXML
    private void handleSendResetLink() {
        emailError.setText("");
        successLabel.setText("");

        String email = emailField.getText().trim();

        if (email.isEmpty()) {
            emailError.setText("Email is required.");
            return;
        }

        // Generate a unique token
        String token = UUID.randomUUID().toString();
        PasswordResetTokenStore.storeToken(email, token);

        // Construct reset link (for demo: a dummy URL)
        String resetLink =  token;

        //  String subject = "Password Reset Request";
        //  String body = "Hello,\n\nTo reset your password, please click the link below:\n" + resetLink + "\n\nIf you didn't request this, ignore this email.";

        try {
            EmailUtil.sendResetEmail(email, token);
            successLabel.setText("Reset link sent! Check your inbox.");

// Open the Token Validation window
            openTokenValidationWindow();

// Close the Forgot Password window
            Stage currentStage = (Stage) emailField.getScene().getWindow();
            currentStage.close();


        } catch (MessagingException e) {
            e.printStackTrace();
            emailError.setText("Failed to send email. Try again.");
        }
    }
        private void openTokenValidationWindow() {
            try {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/TokenValidation.fxml"));
                Parent root = loader.load();

                Stage stage = new Stage();
                stage.setTitle("Enter Token");
                stage.setScene(new Scene(root));
                stage.show();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }



    public void openResetWindowFromToken(String token) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/ResetPassword.fxml"));
            Parent root = loader.load();

            ResetPasswordController controller = loader.getController();
            controller.initData(token);

            Stage stage = new Stage();
            stage.setTitle("Reset Password");
            stage.setScene(new Scene(root));
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
