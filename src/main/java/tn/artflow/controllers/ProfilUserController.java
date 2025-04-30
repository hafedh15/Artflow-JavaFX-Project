package tn.artflow.controllers;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.Stage;
import tn.artflow.entities.User;

import java.io.IOException;

public class ProfilUserController {

    @FXML
    private Label lblName;
    @FXML
    private Label lblLastname;
    @FXML
    private Label lblEmail;
    @FXML
    private ImageView imgPhoto;

    @FXML
    private Label lblVerified;

    @FXML
    private Label lblDateCreation;

    private User user;

    // This method sets the user object
    public void setUser(User user) {
        this.user = user;
        if (this.user != null) {
            // Set user information in the profile view
            lblName.setText(user.getName());
            lblLastname.setText(user.getLastname());
            lblEmail.setText(user.getEmail());
            lblVerified.setText(user.getIsVerified() ? "Verified" : "Not Verified");
            lblDateCreation.setText(user.getDateCreation().toString());

            // If there's a photo path, set the image
            if (user.getPhoto() != null) {
                imgPhoto.setImage(new Image("file:" + user.getPhoto())); // Assuming file path
            }
        }
    }

    @FXML
    private void goToUpdatePage(ActionEvent event) throws IOException {
        if (user == null) {
            System.out.println("User is null! Cannot pass user to update page.");
            return; // Don't proceed if the user is null
        }

        // Load the Update User page
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/UpdateUser.fxml"));
        Parent root = loader.load();

        // Get the controller of the UpdateUser.fxml
        UpdateUserController controller = loader.getController();

        // Pass the user to the UpdateUserController
        controller.setUser(user);

        // Open the update user stage
        Stage stage = new Stage();
        stage.setTitle("Update User Info");
        stage.setScene(new Scene(root));
        stage.show();

        // Close the current window (ProfilUser)
        ((javafx.scene.Node) (event.getSource())).getScene().getWindow().hide();
    }
}