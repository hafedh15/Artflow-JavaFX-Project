package tn.artflow.controllers;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Stage;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import tn.artflow.tools.PasswordResetTokenStore;

import java.io.IOException;

public class TokenValidationController {

    @FXML
    private TextField tokenField;

    @FXML
    private Label errorLabel;

    @FXML
    private Button validateTokenButton; // make sure fx:id="validateTokenButton"

    @FXML
    private void hoverValidateButton() {
        validateTokenButton.setStyle("-fx-background-color: #1976D2; -fx-text-fill: white; -fx-background-radius: 8; -fx-font-size: 14px; -fx-padding: 10 20; -fx-cursor: hand; -fx-font-weight: bold;");
    }

    @FXML
    private void unhoverValidateButton() {
        validateTokenButton.setStyle("-fx-background-color: #2196F3; -fx-text-fill: white; -fx-background-radius: 8; -fx-font-size: 14px; -fx-padding: 10 20; -fx-cursor: hand; -fx-font-weight: bold;");
    }


    @FXML
    private void handleValidateToken() {
        String token = tokenField.getText().trim();
        String email = PasswordResetTokenStore.getEmailByToken(token);

        if (email != null) {
            // Valid token -> Open ResetPassword page
            openResetPasswordWindow(token);

            // Close token validation window
            Stage currentStage = (Stage) tokenField.getScene().getWindow();
            currentStage.close();
        } else {
            errorLabel.setText("Invalid or expired token.");
        }
    }

    private void openResetPasswordWindow(String token) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/ResetPassword.fxml"));
            Parent root = loader.load();

            ResetPasswordController controller = loader.getController();
            controller.initData(token);

            Stage stage = new Stage();
            stage.setTitle("Reset Your Password");
            stage.setScene(new Scene(root));
            stage.show();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
