package tn.artflow.controllers;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.TextField;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import tn.artflow.entities.User;
import tn.artflow.services.UserService;
import javafx.scene.image.Image;

import javafx.scene.image.ImageView;
import java.io.File;
import java.io.IOException;
import java.sql.SQLException;

public class UpdateUserController {

    @FXML
    private TextField txtName;
    @FXML
    private TextField txtLastname;
    @FXML
    private TextField txtEmail;
    @FXML
    private TextField txtPhoto;

    @FXML
    private ImageView imgProfile;

    private User user;

    private AfficherUser afficherUserController;

    public void setAfficherUserController(AfficherUser controller) {
        this.afficherUserController = controller;
    }


    // Receive the user object to populate the fields with current data
    public void setUser(User user) {
        if (user != null) {
            this.user = user;
            txtName.setText(user.getName());
            txtLastname.setText(user.getLastname());
            txtEmail.setText(user.getEmail());
            txtPhoto.setText(user.getPhoto());
        } else {
            System.out.println("Received null user in UpdateUserController!");
        }
    }

    // Update the user info when the "Update" button is clicked
    @FXML
    private void updateUserInfo() throws SQLException {
        System.out.println("Update button clicked!");  // Debugging output

        if (user != null) {
            // Update the user object with new values from text fields
            user.setName(txtName.getText());
            user.setLastname(txtLastname.getText());
            user.setEmail(txtEmail.getText());
            user.setPhoto(txtPhoto.getText());

            // Update the user in the database
            UserService userService = new UserService();
            userService.modifier(user);  // Ensure this method actually updates the database


            // Optionally show a confirmation message
            System.out.println("User info updated successfully!");
            //openAfficherUserScreen();
            if (afficherUserController != null) {
                afficherUserController.refreshGrid();
            }
            // Close the update window after update
            ((Stage) txtName.getScene().getWindow()).close();

        } else {
            System.out.println("User is null, cannot update.");
        }
    }

    @FXML
    private void browsePhoto(ActionEvent event) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Choose Profile Picture");
        fileChooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("Image Files", "*.png", "*.jpg", "*.jpeg")
        );
        File selectedFile = fileChooser.showOpenDialog(null);
        if (selectedFile != null) {
            txtPhoto.setText(selectedFile.getAbsolutePath());
            imgProfile.setImage(new Image("file:" + selectedFile.getAbsolutePath()));
        }
    }
// bech nrodha tafichi el profil baad maa napdatyy fel front,
// betbi3a bech man3mel fichier e5er kima hedha w
// bech nhotha f blasset hedhi :
//    ligne 76   if (afficherUserController != null) {
//                afficherUserController.refreshGrid();
//            }
    private void openAfficherUserScreen() {
        try {
            // Load the FXML file for the user list screen
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/AfficherUser.fxml"));
            Parent root = loader.load();  // Load the FXML file

            // Get the controller for the AfficherUser screen
            AfficherUser afficherUserController = loader.getController();

            // Pass the controller to another method or keep it for refreshing later
            // Example: If you want to pass it to another view or store it for later usage
            // This could be where you show the new window
            Stage stage = new Stage();
            stage.setScene(new Scene(root));
            stage.setTitle("User List");
            stage.show();

            // Optionally, store the controller reference in the current controller class
            // to be used later (for example, when refreshing the user list after an update)
            this.afficherUserController = afficherUserController;

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
