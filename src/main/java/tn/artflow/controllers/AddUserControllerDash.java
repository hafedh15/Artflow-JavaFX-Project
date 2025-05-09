package tn.artflow.controllers;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import tn.artflow.entities.User;
import tn.artflow.services.UserService;

import java.io.File;
import java.util.Arrays;
import java.util.Date;

public class AddUserControllerDash {

    @FXML private TextField nameField;
    @FXML private TextField lastnameField;
    @FXML private TextField emailField;
    @FXML private PasswordField passwordField;
    @FXML private PasswordField confirmPasswordField;
    @FXML private ComboBox<String> roleComboBox;
    @FXML private TextField photoField;

    @FXML private Label nameError;
    @FXML private Label lastnameError;
    @FXML private Label emailError;
    @FXML private Label passwordError;
    @FXML private Label confirmPasswordError;
    @FXML private Label roleError;
    @FXML private Label photoError;

    private final String errorStyle = "-fx-border-color: red; -fx-border-width: 1px; -fx-border-radius: 6; -fx-background-radius: 6;";
    private final String successStyle = "-fx-border-color: green; -fx-border-width: 1px; -fx-border-radius: 6; -fx-background-radius: 6;";




    private AfficherUser afficherUserController;

    public void setAfficherUserController(AfficherUser afficherUserController) {
        this.afficherUserController = afficherUserController;
    }


    private final UserService userService = new UserService();

    @FXML
    public void initialize() {
        roleComboBox.getItems().addAll("Client", "Admin");

    }

    @FXML
    private void handleBrowse() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Select a Photo");
        File selectedFile = fileChooser.showOpenDialog(null);
        if (selectedFile != null) {
            photoField.setText(selectedFile.getAbsolutePath());
        }
    }

    @FXML
    private void handleAddUser() {
        // Clear all previous error messages
        nameError.setText("");
        lastnameError.setText("");
        emailError.setText("");
        passwordError.setText("");
        confirmPasswordError.setText("");
        roleError.setText("");
        photoError.setText("");

        // Get form values
        String name = nameField.getText().trim();
        String lastname = lastnameField.getText().trim();
        String email = emailField.getText().trim();
        String password = passwordField.getText();
        String confirmPassword = confirmPasswordField.getText();
        String selectedRole = roleComboBox.getValue();
        String photo = photoField.getText().trim();

        boolean isValid = true;

        // Name validation
        if (name.isEmpty()) {
            nameError.setText("First name is required.");
            nameField.setStyle(errorStyle);
            isValid = false;
        } else if (name.length() < 3) {
            nameError.setText("Must be at least 3 characters.");
            nameField.setStyle(errorStyle);
            isValid = false;
        } else {
            nameField.setStyle(successStyle);
        }

        // Lastname validation
        if (lastname.isEmpty()) {
            lastnameError.setText("Last name is required.");
            lastnameField.setStyle(errorStyle);
            isValid = false;
        } else if (lastname.length() < 3) {
            lastnameError.setText("Must be at least 3 characters.");
            lastnameField.setStyle(errorStyle);
            isValid = false;
        } else {
            lastnameField.setStyle(successStyle);
        }

        // Email validation
        if (email.isEmpty()) {
            emailError.setText("Email is required.");
            emailField.setStyle(errorStyle);
            isValid = false;
        } else if (!email.matches("^[\\w.-]+@[\\w.-]+\\.[a-zA-Z]{2,}$")) {
            emailError.setText("Invalid email format.");
            emailField.setStyle(errorStyle);
            isValid = false;
        } else {
            emailField.setStyle(successStyle);
        }

        // Password validation
        if (password.isEmpty()) {
            passwordError.setText("Password is required.");
            passwordField.setStyle(errorStyle);
            isValid = false;
        } else if (password.length() < 6) {
            passwordError.setText("At least 6 characters.");
            passwordField.setStyle(errorStyle);
            isValid = false;
        } else {
            passwordField.setStyle(successStyle);
        }

        // Confirm Password validation
        if (confirmPassword.isEmpty()) {
            confirmPasswordError.setText("Please confirm password.");
            confirmPasswordField.setStyle(errorStyle);
            isValid = false;
        } else if (!confirmPassword.equals(password)) {
            confirmPasswordError.setText("Passwords do not match.");
            confirmPasswordField.setStyle(errorStyle);
            isValid = false;
        } else {
            confirmPasswordField.setStyle(successStyle);
        }

        // Role validation
        if (selectedRole == null) {
            roleError.setText("Please select a role.");
            roleComboBox.setStyle(errorStyle);
            isValid = false;
        } else {
            roleComboBox.setStyle(successStyle);
        }

        // Photo validation
        if (photo.isEmpty()) {
            photoError.setText("Please choose a photo.");
            photoField.setStyle(errorStyle);
            isValid = false;
        } else {
            photoField.setStyle(successStyle);
        }

        if (!isValid) return;

        // All validations passed, proceed
        String role = "[\"ROLE_CLIENT\"]";
        if ("Admin".equals(selectedRole)) {
            role = "[\"ROLE_ADMIN\"]";
        }

        try {
            User user = new User(name, lastname, role, password, email, photo, new Date(), false, true);
            userService.ajouter(user);
            showAlert("Success", "User added successfully.");

            Stage stage = (Stage) nameField.getScene().getWindow();
            stage.close();

            if (afficherUserController != null) {
                afficherUserController.refreshGrid();
            }

        } catch (Exception e) {
            showAlert("Error", "Failed to add user: " + e.getMessage());
        }
    }

//    private void validateTitre() {
//        String titre = titreTF.getText().trim();
//        if (titre.isEmpty() || titre.length() < 5 || titre.length() > 100) {
//            titreStatus.setText("❌ Le titre doit contenir entre 5 et 100 caractères");
//            titreStatus.setStyle("-fx-text-fill: red;");
//        } else {
//            titreStatus.setText("✅");
//            titreStatus.setStyle("-fx-text-fill: green;");
//        }
//    }

//    private boolean validateFields() {
//        validateTitre();
//        validateCategorie();
//        validateContenu();
//        validateAuteur();
//        validateDate();
//
//        boolean allValid = titreStatus.getText().equals("✅")
//                && categorieStatus.getText().equals("✅")
//                && contenuStatus.getText().equals("✅")
//                && nomauteurStatus.getText().equals("✅")
//                && dateStatus.getText().equals("✅");
//
//        if (!allValid) {
//            showError("Merci de corriger les erreurs avant d'ajouter l'article.");
//        }
//
//        return allValid;
//    }

    private void showAlert(String title, String content) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }
}
