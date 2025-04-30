package tn.artflow.controllers;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import tn.artflow.entities.User;
import tn.artflow.services.UserService;

import java.io.File;
import java.sql.SQLException;

public class UpdateUserControllerDash {
    @FXML private TextField nameField;
    @FXML private TextField lastnameField;
    @FXML private TextField emailField;
    @FXML private ComboBox<String> rolesComboBox;
    @FXML private ComboBox<String> bannedComboBox;
    @FXML private TextField photoPathField;

    // Error labels
    @FXML private Label nameError;
    @FXML private Label lastnameError;
    @FXML private Label emailError;
    @FXML private Label roleError;
    @FXML private Label bannedError;

    private User userToUpdate;
    private AfficherUser afficherUserController;

    private final String errorStyle = "-fx-border-color: red; -fx-border-width: 1px; -fx-border-radius: 6; -fx-background-radius: 6;";
    private final String successStyle = "-fx-border-color: green; -fx-border-width: 1px; -fx-border-radius: 6; -fx-background-radius: 6;";

    public void setAfficherUserController(AfficherUser controller) {
        this.afficherUserController = controller;
    }

    public void setUserToUpdateDash(User user) {
        this.userToUpdate = user;

        nameField.setText(user.getName());
        lastnameField.setText(user.getLastname());
        emailField.setText(user.getEmail());

        if (user.getRoles().contains("ADMIN")) {
            rolesComboBox.setValue("admin");
        } else if (user.getRoles().contains("CLIENT")) {
            rolesComboBox.setValue("client");
        }

        bannedComboBox.setValue(user.getIsBanned() ? "Yes" : "No");
    }

    @FXML
    private void initialize() {
        rolesComboBox.getItems().addAll("admin", "client");
        bannedComboBox.getItems().addAll("Yes", "No");
    }

    private boolean validateForm() {
        boolean isValid = true;

        // Name
        if (nameField.getText().trim().length() < 3) {
            nameError.setText("Name must be at least 3 characters.");
            nameField.setStyle(errorStyle);
            isValid = false;
        } else {
            nameError.setText("");
            nameField.setStyle(successStyle);
        }

        // Lastname
        if (lastnameField.getText().trim().length() < 3) {
            lastnameError.setText("Lastname must be at least 3 characters.");
            lastnameField.setStyle(errorStyle);
            isValid = false;
        } else {
            lastnameError.setText("");
            lastnameField.setStyle(successStyle);
        }

        // Email
        if (!emailField.getText().matches("^[\\w.-]+@[\\w.-]+\\.\\w+$")) {
            emailError.setText("Enter a valid email.");
            emailField.setStyle(errorStyle);
            isValid = false;
        } else {
            emailError.setText("");
            emailField.setStyle(successStyle);
        }

        // Role
        if (rolesComboBox.getValue() == null) {
            roleError.setText("Please select a role.");
            rolesComboBox.setStyle(errorStyle);
            isValid = false;
        } else {
            roleError.setText("");
            rolesComboBox.setStyle(successStyle);
        }

        // Banned
        if (bannedComboBox.getValue() == null) {
            bannedError.setText("Please select a status.");
            bannedComboBox.setStyle(errorStyle);
            isValid = false;
        } else {
            bannedError.setText("");
            bannedComboBox.setStyle(successStyle);
        }

        return isValid;
    }

    @FXML
    private void handleUpdateDash() {
        if (!validateForm()) return;

        userToUpdate.setName(nameField.getText());
        userToUpdate.setLastname(lastnameField.getText());
        userToUpdate.setEmail(emailField.getText());

        String selectedRole = rolesComboBox.getValue();
        userToUpdate.setRoles(selectedRole.equals("admin") ? "[\"ROLE_ADMIN\"]" : "[\"ROLE_CLIENT\"]");
        userToUpdate.setIs_Banned(bannedComboBox.getValue().equals("Yes"));

        try {
            new UserService().update(userToUpdate);
            if (afficherUserController != null) {
                afficherUserController.refreshGrid();
            }

            ((Stage) nameField.getScene().getWindow()).close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void handleChoosePhoto() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Choose Photo");
        File file = fileChooser.showOpenDialog(null);
        if (file != null) {
            userToUpdate.setPhoto(file.getAbsolutePath());
            photoPathField.setText(file.getAbsolutePath());
        }
    }
}
