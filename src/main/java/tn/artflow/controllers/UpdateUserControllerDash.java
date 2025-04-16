package tn.artflow.controllers;

import javafx.event.ActionEvent;
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

    private User userToUpdate;

    private AfficherUser afficherUserController;

    public void setAfficherUserController(AfficherUser controller) {
        this.afficherUserController = controller;
    }


    public void setUserToUpdateDash(User user) {
        this.userToUpdate = user;

        // Fill fields
        nameField.setText(user.getName());
        lastnameField.setText(user.getLastname());
        emailField.setText(user.getEmail());
        String role = user.getRoles();
        if (role.contains("ADMIN")) {
            rolesComboBox.setValue("admin");
        } else if (role.contains("CLIENT")) {
            rolesComboBox.setValue("client");
        }

        bannedComboBox.setValue(user.getIsBanned() ? "Yes" : "No");
    }

    @FXML
    private void initialize() {
        rolesComboBox.getItems().addAll("admin", "client");
        bannedComboBox.getItems().addAll("Yes", "No");
    }

    @FXML
    private void handleUpdateDash(ActionEvent event) {
        userToUpdate.setName(nameField.getText());
        userToUpdate.setLastname(lastnameField.getText());
        userToUpdate.setEmail(emailField.getText());
        String selectedRole = rolesComboBox.getValue();
        if ("admin".equals(selectedRole)) {
            userToUpdate.setRoles("[\"ROLE_ADMIN\"]");
        } else if ("client".equals(selectedRole)) {
            userToUpdate.setRoles("[\"ROLE_CLIENT\"]");
        }

        userToUpdate.setIs_Banned(bannedComboBox.getValue().equals("Yes"));

        try {
            UserService us = new UserService();
            us.update(userToUpdate);
            if (afficherUserController != null) {
                afficherUserController.refreshGrid(); // Refresh list!
            }

            ((Stage) nameField.getScene().getWindow()).close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void handleChoosePhoto(ActionEvent event) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Choose Photo");
        File file = fileChooser.showOpenDialog(null);
        if (file != null) {
            userToUpdate.setPhoto(file.getAbsolutePath());
        }
    }
}
