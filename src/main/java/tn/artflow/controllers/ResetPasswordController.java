package tn.artflow.controllers;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Stage;
import tn.artflow.services.UserService;
import tn.artflow.tools.PasswordResetTokenStore;

public class ResetPasswordController {

    @FXML
    private PasswordField newPasswordField;

    @FXML
    private PasswordField confirmPasswordField;

    @FXML
    private Label errorLabel;

    @FXML
    private Label successLabel;

    private String token;
    private String email;

    @FXML
    private Button resetButton; // Add fx:id="resetButton" in FXML if needed

    @FXML
    private void hoverResetButton() {
        resetButton.setStyle("-fx-background-color: #45a049; -fx-text-fill: white; -fx-background-radius: 5; -fx-font-size: 14px; -fx-padding: 10; -fx-pref-width: 100%; -fx-font-weight: bold; -fx-cursor: hand;");
    }

    @FXML
    private void unhoverResetButton() {
        resetButton.setStyle("-fx-background-color: #4CAF50; -fx-text-fill: white; -fx-background-radius: 5; -fx-font-size: 14px; -fx-padding: 10; -fx-pref-width: 100%; -fx-font-weight: bold; -fx-cursor: hand;");
    }


    private final UserService userService = new UserService();

    public void initData(String token) {
        this.token = token;
        this.email = PasswordResetTokenStore.getEmailByToken(token);
    }

    @FXML
    private void handleResetPassword() {
        errorLabel.setText("");
        successLabel.setText("");

        String newPass = newPasswordField.getText();
        String confirmPass = confirmPasswordField.getText();

        if (newPass.isEmpty() || confirmPass.isEmpty()) {
            errorLabel.setText("Please fill in all fields.");
            return;
        }

        if (!newPass.equals(confirmPass)) {
            errorLabel.setText("Passwords do not match.");
            return;
        }

        if (email == null) {
            errorLabel.setText("Invalid or expired token.");
            return;
        }

        boolean success = userService.updatePasswordByEmail(email, newPass);
        if (success) {
            successLabel.setText("Password reset successful!");
            PasswordResetTokenStore.removeToken(token);

            // Close the Reset Password window after success
            Stage stage = (Stage) newPasswordField.getScene().getWindow();
            stage.close();

        } else {
            errorLabel.setText("Password reset failed.");
        }
    }
}
