package tn.artflow.controllers;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.scene.effect.DropShadow;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.text.TextFlow;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import tn.artflow.entities.User;
import tn.artflow.services.UserService;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.text.SimpleDateFormat;
import java.util.ResourceBundle;
import java.util.UUID;

public class ProfilUserController implements Initializable {

    @FXML private ImageView imgPhoto;
    @FXML private Circle profileCircle;
    @FXML private StackPane profileImageContainer;
    @FXML private Label lblName;
    @FXML private Label lblLastname;
    @FXML private Label lblEmail;
    @FXML private Label lblEmailDetail;
    @FXML private Label lblFullName;
    @FXML private Label lblVerified;
    @FXML private Label verifiedIcon;
    @FXML private Label verifiedBadge;
    @FXML private Label lblDateCreation;
    @FXML private Label lblRole;
    @FXML private Label lblBio;
    @FXML private Label lblPostCount;
    @FXML private Label lblFollowerCount;
    @FXML private Label lblFollowingCount;
    @FXML private TextFlow bioTextFlow;
    @FXML private VBox postsContainer;
    @FXML private VBox noPostsPlaceholder;
    @FXML private VBox emptyGalleryPlaceholder;
    @FXML private FlowPane galleryContainer;
    @FXML private VBox activityContainer;
    @FXML private VBox noActivityPlaceholder;

    private User user;
    private UserService userService;

    private static final String UPLOAD_DIRECTORY = "uploads/profile_images/";

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        userService = new UserService();

        // Set up profile image click event
        if (imgPhoto != null) {
            // Create the parent container if it doesn't exist
            if (profileImageContainer == null && profileCircle != null && imgPhoto.getParent() instanceof StackPane) {
                profileImageContainer = (StackPane) imgPhoto.getParent();
            }

            // Add hover effect
            setupProfileImageHoverEffect();

            // Add click event
            imgPhoto.setOnMouseClicked(this::handleProfileImageClick);
        }
    }

    private void setupProfileImageHoverEffect() {
        // Store the original effect
        final DropShadow originalEffect = profileCircle != null ?
                (DropShadow) profileCircle.getEffect() : null;

        // Create hover effect (stronger shadow)
        final DropShadow hoverEffect = new DropShadow();
        hoverEffect.setRadius(15);
        hoverEffect.setColor(Color.rgb(0, 132, 255, 0.5)); // Blue tint

        if (profileImageContainer != null) {
            // Add hover effect indicators
            profileImageContainer.setOnMouseEntered(e -> {
                if (profileCircle != null) {
                    profileCircle.setEffect(hoverEffect);
                }
                imgPhoto.setOpacity(0.9);
                imgPhoto.setCursor(javafx.scene.Cursor.HAND);
            });

            profileImageContainer.setOnMouseExited(e -> {
                if (profileCircle != null) {
                    profileCircle.setEffect(originalEffect);
                }
                imgPhoto.setOpacity(1.0);
                imgPhoto.setCursor(javafx.scene.Cursor.DEFAULT);
            });
        }
    }

    @FXML
    private void handleProfileImageClick(MouseEvent event) {
        if (user == null) {
            showAlert(Alert.AlertType.ERROR, "Error", "Cannot upload image", "No user is currently loaded.");
            return;
        }

        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Select Profile Image");
        fileChooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("Image Files", "*.png", "*.jpg", "*.jpeg", "*.gif")
        );

        Stage stage = (Stage) imgPhoto.getScene().getWindow();
        File selectedFile = fileChooser.showOpenDialog(stage);

        if (selectedFile != null) {
            try {
                // Create upload directory if it doesn't exist
                File uploadDir = new File(UPLOAD_DIRECTORY);
                if (!uploadDir.exists()) {
                    uploadDir.mkdirs();
                }

                // Generate a unique filename to avoid conflicts
                String originalFileName = selectedFile.getName();
                String fileExtension = originalFileName.substring(originalFileName.lastIndexOf('.'));
                String newFileName = UUID.randomUUID().toString() + fileExtension;

                // Destination path
                Path destinationPath = Paths.get(UPLOAD_DIRECTORY + newFileName);

                // Copy the file to our application's directory
                Files.copy(selectedFile.toPath(), destinationPath, StandardCopyOption.REPLACE_EXISTING);

                // Update the user's photo path in the database
                String savedImagePath = destinationPath.toString();
                updateUserProfileImage(savedImagePath);

                // Load and display the new image
                Image newImage = new Image(destinationPath.toUri().toString());
                imgPhoto.setImage(newImage);

                showAlert(Alert.AlertType.INFORMATION, "Success", "Profile image updated",
                        "Your profile image has been successfully updated.");

            } catch (IOException e) {
                showAlert(Alert.AlertType.ERROR, "Error", "Failed to save image",
                        "An error occurred while saving the profile image: " + e.getMessage());
                e.printStackTrace();
            } catch (Exception e) {
                showAlert(Alert.AlertType.ERROR, "Error", "Unexpected error",
                        "An unexpected error occurred: " + e.getMessage());
                e.printStackTrace();
            }
        }
    }

    private void updateUserProfileImage(String imagePath) {
        try {
            // Update user object
            user.setPhoto(imagePath);

            // Update in database
            userService.updatePhoto(user.getId(), imagePath);

        } catch (Exception e) {
            showAlert(Alert.AlertType.ERROR, "Database Error", "Failed to update profile image",
                    "An error occurred while updating the database: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void showAlert(Alert.AlertType alertType, String title, String header, String content) {
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setHeaderText(header);
        alert.setContentText(content);
        alert.showAndWait();
    }

    // This method sets the user object and populates the profile UI
    public void setUser(User user) {
        this.user = user;
        if (this.user != null) {
            loadUserData();
            setupProfileImage();
            loadUserStats();
            loadUserPosts();
            loadUserGallery();
            loadUserActivity();
        } else {
            System.out.println("User is null! Cannot display profile information.");
        }
    }

    private void loadUserData() {
        // Set user basic info
        lblName.setText(user.getName());
        lblLastname.setText(user.getLastname());
        lblEmail.setText(user.getEmail());

        // Set email detail if the field exists
        if (lblEmailDetail != null) {
            lblEmailDetail.setText(user.getEmail());
        }

        // Set full name if the field exists
        if (lblFullName != null) {
            String fullName = user.getName() + " " + user.getLastname();
            lblFullName.setText(fullName);
        }

        // Format and set date
        if (user.getDateCreation() != null) {
            if (lblDateCreation != null) {
                SimpleDateFormat dateFormat = new SimpleDateFormat("MMMM d, yyyy");
                lblDateCreation.setText(dateFormat.format(user.getDateCreation()));
            }
        } else {
            if (lblDateCreation != null) {
                lblDateCreation.setText("N/A");
            }
        }

        // Set verification status
        boolean isVerified = user.getIsVerified();
        if (lblVerified != null) {
            lblVerified.setText(isVerified ? "Verified" : "Not Verified");

            if (isVerified) {
                lblVerified.setStyle("-fx-text-fill: #00b894;");
            } else {
                lblVerified.setStyle("-fx-text-fill: #d63031;");
            }
        }

        // Set verification icons if they exist
        if (verifiedIcon != null) {
            verifiedIcon.setVisible(isVerified);
        }

        if (verifiedBadge != null) {
            verifiedBadge.setVisible(isVerified);
        }

        // Set role if the field exists
        if (lblRole != null) {
            String roles = user.getRoles();
            if (roles != null && !roles.isEmpty()) {
                if (roles.contains("ADMIN")) {
                    lblRole.setText("Administrator");
                    lblRole.setStyle("-fx-text-fill: #e84393; -fx-font-weight: bold;");
                } else if (roles.contains("ARTIST")) {
                    lblRole.setText("Artist");
                    lblRole.setStyle("-fx-text-fill: #0984e3; -fx-font-weight: bold;");
                } else if (roles.contains("CLIENT")) {
                    lblRole.setText("Client");
                } else {
                    lblRole.setText(roles.replace("[\"", "").replace("\"]", "").replace("ROLE_", ""));
                }
            } else {
                lblRole.setText("User");
            }
        }

        // Set bio if the field exists
        if (lblBio != null) {
            lblBio.setText("No bio information provided yet.");
        }
    }

    private void setupProfileImage() {
        try {
            if (imgPhoto != null) {
                // Try to load user's profile photo if available
                if (user.getPhoto() != null && !user.getPhoto().isEmpty()) {
                    try {
                        // Try to load the image from the path
                        Image userImage = new Image("file:" + user.getPhoto(), true);

                        if (!userImage.isError()) {
                            imgPhoto.setImage(userImage);
                        } else {
                            // Try to load as URL
                            userImage = new Image(user.getPhoto(), true);
                            if (!userImage.isError()) {
                                imgPhoto.setImage(userImage);
                            } else {
                                // Load default image
                                tryLoadDefaultImage();
                            }
                        }
                    } catch (Exception e) {
                        System.err.println("Failed to load profile image: " + e.getMessage());
                        tryLoadDefaultImage();
                    }
                } else {
                    tryLoadDefaultImage();
                }

                // Make sure the image is positioned correctly for the circular clip
                if (profileCircle != null) {
                    imgPhoto.setPreserveRatio(false);
                    imgPhoto.setFitWidth(120);
                    imgPhoto.setFitHeight(120);
                }
            }
        } catch (Exception e) {
            System.err.println("Error setting up profile image: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void tryLoadDefaultImage() {
        try {
            // Try to load default image from resources
            Image defaultImage = new Image(getClass().getResourceAsStream("/images/default_profile.png"));
            imgPhoto.setImage(defaultImage);
        } catch (Exception e) {
            System.err.println("Failed to load default profile image: " + e.getMessage());
            // If we can't load the default image, leave it empty
        }
    }

    private void loadUserStats() {
        // Set stats if the fields exist
        if (lblPostCount != null) {
            lblPostCount.setText("0");
        }

        if (lblFollowerCount != null) {
            lblFollowerCount.setText("0");
        }

        if (lblFollowingCount != null) {
            lblFollowingCount.setText("0");
        }
    }

    private void loadUserPosts() {
        // Check if the containers exist
        if (noPostsPlaceholder != null && postsContainer != null) {
            // For now, always show the placeholder
            boolean hasPosts = false;

            noPostsPlaceholder.setVisible(!hasPosts);
            noPostsPlaceholder.setManaged(!hasPosts);
            postsContainer.setVisible(hasPosts);
            postsContainer.setManaged(hasPosts);
        }
    }

    private void loadUserGallery() {
        // Check if the containers exist
        if (emptyGalleryPlaceholder != null && galleryContainer != null) {
            // For now, always show the placeholder
            boolean hasArtwork = false;

            emptyGalleryPlaceholder.setVisible(!hasArtwork);
            emptyGalleryPlaceholder.setManaged(!hasArtwork);
            galleryContainer.setVisible(hasArtwork);
            galleryContainer.setManaged(hasArtwork);
        }
    }

    private void loadUserActivity() {
        // Check if the container exists
        if (noActivityPlaceholder != null) {
            // For now, always show the placeholder
            boolean hasActivity = false;

            noActivityPlaceholder.setVisible(!hasActivity);
            noActivityPlaceholder.setManaged(!hasActivity);
        }
    }

    @FXML
    private void goToUpdatePage(ActionEvent event) {
        try {
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
        } catch (IOException e) {
            System.err.println("Error loading update form: " + e.getMessage());
            e.printStackTrace();
        }
    }
}