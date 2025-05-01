package tn.artflow.controllers;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.effect.DropShadow;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import tn.artflow.entities.User;
import tn.artflow.services.UserService;
import tn.artflow.utils.UserSession;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Objects;
import java.util.UUID;
import java.util.stream.Collectors;


public class AfficherUser {

    // FXML Elements
    @FXML
    private GridPane userGrid;
    @FXML
    private TextField searchField;
    @FXML
    private ImageView userProfileImage;
    @FXML
    private StackPane profileImageContainer;
    @FXML
    private StackPane imageOverlay;
    @FXML
    private HBox paginationControls;
    @FXML
    private Label pageInfoLabel;
    @FXML
    private Button prevPageButton;
    @FXML
    private Button nextPageButton;

    // Navigation Buttons
    @FXML
    private Button GoToArticle;
    @FXML
    private Button GoToUser;
    @FXML
    private Button GoToAtelier;
    @FXML
    private Button GoToReservation;
    @FXML
    private Button GoToComment;
    @FXML
    private Button GoToReclamation;
    @FXML
    private Button GoToReponse;
    @FXML
    private Button orderButton;
    @FXML
    private Button GoToProduit;

    // Services
    private final UserService userService = new UserService();

    // User data
    private User loggedInUser;
    private List<User> allUsers;
    private List<User> filteredUsers;

    // Pagination
    private static final int USERS_PER_PAGE = 9;
    private int currentPage = 1;
    private int totalPages = 1;

    // Constants
    private static final String UPLOAD_DIRECTORY = "uploads/profile_images/";

    // CSS Classes and Styles
    private static final String ACTIVE_BUTTON_STYLE = "-fx-background-color: #303f9f; -fx-text-fill: white;";
    private static final String INACTIVE_BUTTON_STYLE = "-fx-background-color: transparent; -fx-text-fill: #c5cae9;";
    private static final String ACTIVE_ICON_STYLE = "-fx-text-fill: white;";
    private static final String INACTIVE_ICON_STYLE = "-fx-text-fill: #c5cae9;";

    private static final String DATA_CELL_STYLE = "-fx-padding: 8 5; -fx-font-size: 13px; -fx-alignment: CENTER_LEFT;";
    private static final String HEADER_CELL_STYLE = "-fx-font-weight: bold; -fx-padding: 15 5 15 5; -fx-font-size: 14px; -fx-text-fill: #3949ab; -fx-border-color: transparent transparent #e8eaf6 transparent; -fx-border-width: 0 0 2 0;";

    private static final String BAN_BUTTON_STYLE = "-fx-background-color: #f44336; -fx-text-fill: white; -fx-background-radius: 20; -fx-min-width: 80px;";
    private static final String UNBAN_BUTTON_STYLE = "-fx-background-color: #4caf50; -fx-text-fill: white; -fx-background-radius: 20; -fx-min-width: 80px;";

    private static final String PAGE_BUTTON_STYLE = "-fx-background-color: #3949ab; -fx-text-fill: white; -fx-background-radius: 20; -fx-min-width: 35px;";
    private static final String PAGE_BUTTON_DISABLED_STYLE = "-fx-background-color: #9fa8da; -fx-text-fill: white; -fx-background-radius: 20; -fx-min-width: 35px;";

    /**
     * Set the currently logged-in user
     */
    public void setLoggedInUser(User user) {
        this.loggedInUser = user;

        // Load the user's profile image if available
        if (user != null && userProfileImage != null) {
            loadUserProfileImage(user);
        }
    }

    /**
     * Initialize method called when FXML is loaded
     */
    @FXML
    public void initialize() {
        try {
            // Get logged in user from session
            loggedInUser = UserSession.getInstance().getUser();

            // Set up profile image click handler and fix the display issue
            if (profileImageContainer != null) {
                setupProfileImageHandlers();

                // Fix the image centering issue
                if (userProfileImage != null) {
                    userProfileImage.setPreserveRatio(false); // Ensure the image fills the entire circle
                    userProfileImage.setFitWidth(80);
                    userProfileImage.setFitHeight(80);
                }
            }

            // Set up pagination controls
            if (prevPageButton != null && nextPageButton != null) {
                prevPageButton.setOnAction(event -> goToPreviousPage());
                nextPageButton.setOnAction(event -> goToNextPage());
            }

            // Mark the active sidebar button
            markActiveButton(GoToUser);

            // Load all users data
            loadAllUsers();

            // Load current user's profile image
            if (loggedInUser != null && userProfileImage != null) {
                loadUserProfileImage(loggedInUser);
            }

        } catch (SQLException e) {
            showAlert(Alert.AlertType.ERROR, "Database Error",
                    "Could not load users", e.getMessage());
            e.printStackTrace();
        }
    }



    /**
     * Load all users and set up pagination
     */
    private void loadAllUsers() throws SQLException {
        allUsers = userService.recupererr();
        filteredUsers = allUsers; // Start with all users

        // Calculate total pages
        totalPages = (int) Math.ceil((double) filteredUsers.size() / USERS_PER_PAGE);
        if (totalPages == 0) totalPages = 1; // Always at least one page, even if empty

        // Set current page to 1
        currentPage = 1;

        // Update pagination controls
        updatePaginationControls();

        // Load the first page of users
        loadCurrentPage();
    }

    /**
     * Update pagination controls
     */
    private void updatePaginationControls() {
        if (pageInfoLabel != null) {
            pageInfoLabel.setText("Page " + currentPage + " / " + totalPages);
        }

        if (prevPageButton != null) {
            prevPageButton.setDisable(currentPage <= 1);
            prevPageButton.setStyle(currentPage <= 1 ? PAGE_BUTTON_DISABLED_STYLE : PAGE_BUTTON_STYLE);
        }

        if (nextPageButton != null) {
            nextPageButton.setDisable(currentPage >= totalPages);
            nextPageButton.setStyle(currentPage >= totalPages ? PAGE_BUTTON_DISABLED_STYLE : PAGE_BUTTON_STYLE);
        }
    }

    /**
     * Navigate to previous page
     */
    private void goToPreviousPage() {
        if (currentPage > 1) {
            currentPage--;
            loadCurrentPage();
            updatePaginationControls();
        }
    }

    /**
     * Navigate to next page
     */
    private void goToNextPage() {
        if (currentPage < totalPages) {
            currentPage++;
            loadCurrentPage();
            updatePaginationControls();
        }
    }

    /**
     * Load the current page of users
     */
    private void loadCurrentPage() {
        int startIndex = (currentPage - 1) * USERS_PER_PAGE;
        int endIndex = Math.min(startIndex + USERS_PER_PAGE, filteredUsers.size());

        // Get the subset of users for the current page
        List<User> currentPageUsers = filteredUsers.subList(startIndex, endIndex);

        // Display these users
        populateGrid(currentPageUsers);
    }

    /**
     * Set up profile image click and hover handlers
     */
    private void setupProfileImageHandlers() {
        // Store the original effect
        final DropShadow hoverEffect = new DropShadow();
        hoverEffect.setRadius(15);
        hoverEffect.setColor(Color.rgb(255, 255, 255, 0.5));

        // Add hover effect handlers
        profileImageContainer.setOnMouseEntered(e -> {
            userProfileImage.setEffect(hoverEffect);
            userProfileImage.setOpacity(0.8);

            // Show the overlay if it exists
            if (imageOverlay != null) {
                imageOverlay.setOpacity(0.7);
            }
        });

        profileImageContainer.setOnMouseExited(e -> {
            userProfileImage.setEffect(null);
            userProfileImage.setOpacity(1.0);

            // Hide the overlay
            if (imageOverlay != null) {
                imageOverlay.setOpacity(0.0);
            }
        });

        // Set click handler for image upload
        profileImageContainer.setOnMouseClicked(this::handleProfileImageClick);
    }

    /**
     * Handle clicking on profile image to upload a new one
     */
    private void handleProfileImageClick(MouseEvent event) {
        if (loggedInUser == null) {
            showAlert(Alert.AlertType.ERROR, "Error",
                    "Cannot upload image", "No user is currently logged in.");
            return;
        }

        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Select Profile Image");
        fileChooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("Image Files", "*.png", "*.jpg", "*.jpeg", "*.gif")
        );

        Stage stage = (Stage) userProfileImage.getScene().getWindow();
        File selectedFile = fileChooser.showOpenDialog(stage);

        if (selectedFile != null) {
            try {
                // Create upload directory if it doesn't exist
                File uploadDir = new File(UPLOAD_DIRECTORY);
                if (!uploadDir.exists()) {
                    uploadDir.mkdirs();
                }

                // Generate a unique filename
                String originalFileName = selectedFile.getName();
                String fileExtension = originalFileName.substring(originalFileName.lastIndexOf('.'));
                String newFileName = UUID.randomUUID().toString() + fileExtension;

                // Destination path
                Path destinationPath = Paths.get(UPLOAD_DIRECTORY + newFileName);

                // Copy the file
                Files.copy(selectedFile.toPath(), destinationPath, StandardCopyOption.REPLACE_EXISTING);

                // Update the user's photo path in the database
                String savedImagePath = destinationPath.toString();
                loggedInUser.setPhoto(savedImagePath);

                // Update user in database
                userService.updatePhoto(loggedInUser.getId(), savedImagePath);

                // Update the displayed image
                Image newImage = new Image(destinationPath.toUri().toString());
                userProfileImage.setImage(newImage);

                showAlert(Alert.AlertType.INFORMATION, "Success",
                        "Profile image updated", "Your profile image has been successfully updated.");

            } catch (Exception e) {
                showAlert(Alert.AlertType.ERROR, "Error",
                        "Failed to update profile image", e.getMessage());
                e.printStackTrace();
            }
        }
    }

    /**
     * Load a user's profile image
     */
    private void loadUserProfileImage(User user) {
        try {
            if (user.getPhoto() != null && !user.getPhoto().isEmpty()) {
                try {
                    // Try to load as file path
                    Image userImage = new Image("file:" + user.getPhoto(), true);

                    if (!userImage.isError()) {
                        userProfileImage.setImage(userImage);
                    } else {
                        // Try to load as URL
                        userImage = new Image(user.getPhoto(), true);
                        if (!userImage.isError()) {
                            userProfileImage.setImage(userImage);
                        } else {
                            // Load default image
                            loadDefaultProfileImage();
                        }
                    }
                } catch (Exception e) {
                    System.err.println("Failed to load profile image: " + e.getMessage());
                    loadDefaultProfileImage();
                }
            } else {
                loadDefaultProfileImage();
            }
        } catch (Exception e) {
            System.err.println("Error loading profile image: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * Load the default profile image
     */
    private void loadDefaultProfileImage() {
        try {
            Image defaultImage = new Image(Objects.requireNonNull(
                    getClass().getResourceAsStream("/images/user.jpg")));
            userProfileImage.setImage(defaultImage);
        } catch (Exception e) {
            System.err.println("Failed to load default profile image: " + e.getMessage());
        }
    }

    /**
     * Handle orders button click
     */
    public void handleOrderButtonAction(ActionEvent actionEvent) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/OrdersDashboard.fxml"));
            Parent root = loader.load();

            // Mark the orders button as active before switching
            markActiveButton(orderButton);

            // Switch to the orders screen
            Stage stage = (Stage) ((Node) actionEvent.getSource()).getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.setTitle("Gestion des Commandes");
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Error",
                    "Navigation Error", "Could not load the orders page.");
        }
    }

    /**
     * Populate the user grid with data
     */
    private void populateGrid(List<User> users) {
        userGrid.getChildren().clear();

        // Create styled header cells
        userGrid.add(createHeader("Prénom"), 0, 0);
        userGrid.add(createHeader("Nom"), 1, 0);
        userGrid.add(createHeader("Rôle"), 2, 0);
        userGrid.add(createHeader("Email"), 3, 0);
        userGrid.add(createHeader("Date d'inscription"), 4, 0);
        userGrid.add(createHeader("Vérifié"), 5, 0);
        userGrid.add(createHeader("Statut"), 6, 0);
        userGrid.add(createHeader("Actions"), 7, 0);

        int row = 1;

        for (User user : users) {
            Label nameLabel = createDataCell(user.getName());
            Label lastnameLabel = createDataCell(user.getLastname());
            Label rolesLabel = createStyledRoleCell(user.getRoles());
            Label emailLabel = createDataCell(user.getEmail());

            // Format date properly
            String formattedDate = "N/A";
            if (user.getDateCreation() != null) {
                SimpleDateFormat sdf = new SimpleDateFormat("dd MMM yyyy");
                formattedDate = sdf.format(user.getDateCreation());
            }
            Label dateLabel = createDataCell(formattedDate);

            // Verified status with icon
            Label verifiedLabel = new Label(user.getIsVerified() ? "✓" : "✗");
            verifiedLabel.setStyle(DATA_CELL_STYLE + (user.getIsVerified()
                    ? "-fx-text-fill: #4caf50; -fx-font-size: 16px; -fx-font-weight: bold;"
                    : "-fx-text-fill: #f44336; -fx-font-size: 16px; -fx-font-weight: bold;"));

            // Ban/Unban button with consistent size
            Button banToggleBtn = new Button(user.getIsBanned() ? "Réactiver" : "Bloquer");
            banToggleBtn.setStyle(user.getIsBanned() ? UNBAN_BUTTON_STYLE : BAN_BUTTON_STYLE);
            banToggleBtn.setPrefHeight(30);

            banToggleBtn.setOnAction(e -> {
                user.setIs_Banned(!user.getIsBanned());
                try {
                    userService.update(user);
                    banToggleBtn.setText(user.getIsBanned() ? "Réactiver" : "Bloquer");
                    banToggleBtn.setStyle(user.getIsBanned() ? UNBAN_BUTTON_STYLE : BAN_BUTTON_STYLE);
                } catch (SQLException ex) {
                    showAlert(Alert.AlertType.ERROR, "Database Error",
                            "Failed to update user status", ex.getMessage());
                    ex.printStackTrace();
                }
            });

            // Action buttons with icons
            HBox actionPane = new HBox(10);
            actionPane.setStyle("-fx-alignment: CENTER_LEFT;");

            Button updateBtn = createIconButton("✏️", "#3949ab");
            Button deleteBtn = createIconButton("🗑️", "#f44336");
            Button viewBtn = createIconButton("👁️", "#00897b");

            updateBtn.setOnAction(e -> handleUpdateDash(user));
            deleteBtn.setOnAction(e -> handleDelete(user));
            viewBtn.setOnAction(e -> handleViewProfile(user));

            actionPane.getChildren().addAll(viewBtn, updateBtn, deleteBtn);

            // Add all cells to the grid
            userGrid.add(nameLabel, 0, row);
            userGrid.add(lastnameLabel, 1, row);
            userGrid.add(rolesLabel, 2, row);
            userGrid.add(emailLabel, 3, row);
            userGrid.add(dateLabel, 4, row);
            userGrid.add(verifiedLabel, 5, row);
            userGrid.add(banToggleBtn, 6, row);
            userGrid.add(actionPane, 7, row);

            row++;
        }
    }

    /**
     * Create a styled header cell for the table
     */
    private Label createHeader(String text) {
        Label label = new Label(text);
        label.setStyle(HEADER_CELL_STYLE);
        label.setMaxWidth(Double.MAX_VALUE);
        return label;
    }

    /**
     * Create a standard data cell
     */
    private Label createDataCell(String text) {
        Label label = new Label(text);
        label.setStyle(DATA_CELL_STYLE);
        label.setMaxWidth(Double.MAX_VALUE);
        return label;
    }

    /**
     * Create a styled role cell with appropriate colors
     */
    private Label createStyledRoleCell(String roles) {
        Label label = new Label(formatRoleName(roles));

        String style = DATA_CELL_STYLE;

        if (roles.contains("ADMIN")) {
            style += "-fx-text-fill: #f44336; -fx-font-weight: bold;";
        } else if (roles.contains("ARTIST")) {
            style += "-fx-text-fill: #2196f3; -fx-font-weight: bold;";
        } else if (roles.contains("CLIENT")) {
            style += "-fx-text-fill: #4caf50; -fx-font-weight: bold;";
        }

        label.setStyle(style);
        label.setMaxWidth(Double.MAX_VALUE);
        return label;
    }

    /**
     * Format role name for display
     */
    private String formatRoleName(String roles) {
        if (roles == null || roles.isEmpty()) {
            return "User";
        }

        if (roles.contains("ADMIN")) {
            return "Admin";
        } else if (roles.contains("ARTIST")) {
            return "Artiste";
        } else if (roles.contains("CLIENT")) {
            return "Client";
        }

        return roles.replace("[\"", "")
                .replace("\"]", "")
                .replace("ROLE_", "");
    }

    /**
     * Create an icon button for actions
     */
    private Button createIconButton(String iconText, String color) {
        Button button = new Button();
        button.setText(iconText);
        button.setStyle(String.format(
                "-fx-background-color: transparent; -fx-font-size: 16px; -fx-text-fill: %s; " +
                        "-fx-cursor: hand; -fx-padding: 5 8;", color));

        // Add hover effect
        button.setOnMouseEntered(e ->
                button.setStyle(String.format(
                        "-fx-background-color: #f5f5f5; -fx-font-size: 16px; -fx-text-fill: %s; " +
                                "-fx-cursor: hand; -fx-padding: 5 8; -fx-background-radius: 100;", color)));

        button.setOnMouseExited(e ->
                button.setStyle(String.format(
                        "-fx-background-color: transparent; -fx-font-size: 16px; -fx-text-fill: %s; " +
                                "-fx-cursor: hand; -fx-padding: 5 8;", color)));

        return button;
    }

    /**
     * Handle user update
     */
    private void handleUpdateDash(User user) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/UpdateUserDash.fxml"));
            Parent root = loader.load();

            // Pass user to controller
            UpdateUserControllerDash controller = loader.getController();
            controller.setUserToUpdateDash(user);
            controller.setAfficherUserController(this);

            Stage stage = new Stage();
            stage.setTitle("Modifier l'utilisateur");
            stage.setScene(new Scene(root));
            stage.show();

        } catch (IOException e) {
            showAlert(Alert.AlertType.ERROR, "Error",
                    "Navigation Error", "Could not load the update user page.");
            e.printStackTrace();
        }
    }

    /**
     * Handle user search
     */
    @FXML
    private void rechercherUtilisateurs() {
        String keyword = searchField.getText().trim().toLowerCase();
        try {
            if (keyword.isEmpty()) {
                filteredUsers = allUsers;
            } else {
                filteredUsers = allUsers.stream()
                        .filter(user -> user.getName().toLowerCase().contains(keyword) ||
                                user.getLastname().toLowerCase().contains(keyword) ||
                                user.getEmail().toLowerCase().contains(keyword))
                        .collect(Collectors.toList());
            }

            // Reset to first page when searching
            currentPage = 1;

            // Recalculate total pages
            totalPages = (int) Math.ceil((double) filteredUsers.size() / USERS_PER_PAGE);

            // Update pagination controls
            updatePaginationControls();

            // Load the first page of filtered results
            loadCurrentPage();

        } catch (Exception e) {
            showAlert(Alert.AlertType.ERROR, "Search Error",
                    "Could not search for users", e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * Handle user deletion
     */
    private void handleDelete(User user) {
        Alert confirmAlert = new Alert(Alert.AlertType.CONFIRMATION);
        confirmAlert.setTitle("Confirmation");
        confirmAlert.setHeaderText("Supprimer l'utilisateur");
        confirmAlert.setContentText("Êtes-vous sûr de vouloir supprimer cet utilisateur?\nCette action est irréversible.");

        confirmAlert.showAndWait().ifPresent(response -> {
            if (response == ButtonType.OK) {
                try {
                    userService.supprimer(user);

                    // Reload all users after deletion
                    loadAllUsers();

                    showAlert(Alert.AlertType.INFORMATION, "Succès",
                            "Utilisateur supprimé", "L'utilisateur a été supprimé avec succès.");
                } catch (SQLException e) {
                    showAlert(Alert.AlertType.ERROR, "Erreur",
                            "Échec de la suppression", "Impossible de supprimer l'utilisateur: " + e.getMessage());
                    e.printStackTrace();
                }
            }
        });
    }

    /**
     * Refresh the user grid
     */
    public void refreshGrid() {
        try {
            // Reload all users
            loadAllUsers();
        } catch (SQLException e) {
            showAlert(Alert.AlertType.ERROR, "Database Error",
                    "Refresh Error", "Could not refresh users: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * Open the add user window
     */
    @FXML
    private void openAddUserWindow() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/AddUserDash.fxml"));
            Parent root = loader.load();

            // Get and configure the controller
            AddUserControllerDash controller = loader.getController();
            controller.setAfficherUserController(this);

            // Set up and show the stage
            Stage stage = new Stage();
            stage.setTitle("Ajouter un utilisateur");
            stage.setScene(new Scene(root));
            stage.show();

        } catch (IOException e) {
            showAlert(Alert.AlertType.ERROR, "Navigation Error",
                    "Could not open add user window", e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * Handle view profile for the current user
     */
    @FXML
    private void handleViewProfil(ActionEvent event) {
        try {
            User currentUser = UserSession.getInstance().getUser();
            if (currentUser == null) {
                showAlert(Alert.AlertType.ERROR, "Error",
                        "No user logged in", "There is no user currently logged in.");
                return;
            }

            FXMLLoader loader = new FXMLLoader(getClass().getResource("/ProfilUser.fxml"));
            Parent root = loader.load();

            // Get the controller and pass the user data
            ProfilUserController controller = loader.getController();
            controller.setUser(currentUser);

            Stage stage = new Stage();
            stage.setTitle("Mon Profil");
            stage.setScene(new Scene(root));
            stage.show();
        } catch (IOException e) {
            showAlert(Alert.AlertType.ERROR, "Navigation Error",
                    "Could not open profile", e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * Handle view profile for a specific user in the table
     */
    private void handleViewProfile(User user) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/ProfilUser.fxml"));
            Parent root = loader.load();

            // Get the controller and pass the user data
            ProfilUserController controller = loader.getController();
            controller.setUser(user);

            Stage stage = new Stage();
            stage.setTitle("Profil Utilisateur");
            stage.setScene(new Scene(root));
            stage.show();

        } catch (IOException e) {
            showAlert(Alert.AlertType.ERROR, "Navigation Error",
                    "Could not open user profile", e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * Mark the active sidebar button and reset the others
     */
    private void markActiveButton(Button activeButton) {
        // List of all navigation buttons
        Button[] allButtons = {
                GoToUser, GoToProduit, GoToArticle, GoToAtelier,
                GoToReservation, GoToComment, GoToReclamation,
                GoToReponse, orderButton
        };

        // Reset all buttons to inactive state
        for (Button button : allButtons) {
            if (button != null) {
                button.setStyle(button.getStyle().replace(ACTIVE_BUTTON_STYLE, INACTIVE_BUTTON_STYLE));

                // Find the label (icon) within the button's graphic
                if (button.getGraphic() instanceof Label) {
                    Label iconLabel = (Label) button.getGraphic();
                    iconLabel.setStyle(iconLabel.getStyle().replace(ACTIVE_ICON_STYLE, INACTIVE_ICON_STYLE));
                }
            }
        }

        // Set the active button
        if (activeButton != null) {
            activeButton.setStyle(activeButton.getStyle().replace(INACTIVE_BUTTON_STYLE, ACTIVE_BUTTON_STYLE));

            // Find the label (icon) within the button's graphic
            if (activeButton.getGraphic() instanceof Label) {
                Label iconLabel = (Label) activeButton.getGraphic();
                iconLabel.setStyle(iconLabel.getStyle().replace(INACTIVE_ICON_STYLE, ACTIVE_ICON_STYLE));
            }
        }
    }

    // Navigation methods remain the same as before
    // ...

    /**
     * Navigate to Articles
     */
    @FXML
    void goToArticle(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/AfficherArticle.fxml"));
            Parent root = loader.load();

            // Mark the articles button as active before switching
            markActiveButton(GoToArticle);

            GoToArticle.getScene().setRoot(root);
        } catch (IOException e) {
            showAlert(Alert.AlertType.ERROR, "Navigation Error",
                    "Could not navigate to Articles", e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * Navigate to Reservations
     */
    @FXML
    void goToReservation(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/dashReservation.fxml"));
            Parent root = loader.load();

            // Mark the reservations button as active before switching
            markActiveButton(GoToReservation);

            GoToArticle.getScene().setRoot(root);
        } catch (IOException e) {
            showAlert(Alert.AlertType.ERROR, "Navigation Error",
                    "Could not navigate to Reservations", e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * Navigate to Workshops
     */
    @FXML
    void goToAtelier(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/dashWorkshop.fxml"));
            Parent root = loader.load();

            // Mark the workshops button as active before switching
            markActiveButton(GoToAtelier);

            GoToArticle.getScene().setRoot(root);
        } catch (IOException e) {
            showAlert(Alert.AlertType.ERROR, "Navigation Error",
                    "Could not navigate to Workshops", e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * Navigate to Comments
     */
    @FXML
    void goToComment(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/AfficheComment.fxml"));
            Parent root = loader.load();

            // Mark the comments button as active before switching
            markActiveButton(GoToComment);

            GoToArticle.getScene().setRoot(root);
        } catch (IOException e) {
            showAlert(Alert.AlertType.ERROR, "Navigation Error",
                    "Could not navigate to Comments", e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * Navigate to Complaints
     */
    @FXML
    void goToReclamation(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/AfficherReclamation.fxml"));
            Parent root = loader.load();

            // Mark the complaints button as active before switching
            markActiveButton(GoToReclamation);

            GoToArticle.getScene().setRoot(root);
        } catch (IOException e) {
            showAlert(Alert.AlertType.ERROR, "Navigation Error",
                    "Could not navigate to Complaints", e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * Navigate to Responses
     */
    @FXML
    void goToReponse(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/conversation.fxml"));
            Parent root = loader.load();

            // Mark the responses button as active before switching
            markActiveButton(GoToReponse);

            GoToArticle.getScene().setRoot(root);
        } catch (IOException e) {
            showAlert(Alert.AlertType.ERROR, "Navigation Error",
                    "Could not navigate to Responses", e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * Navigate to Products
     */
    @FXML
    void goToProduit(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/Dashboard.fxml"));
            Parent root = loader.load();

            // Mark the products button as active before switching
            markActiveButton(GoToProduit);

            GoToArticle.getScene().setRoot(root);
        } catch (IOException e) {
            showAlert(Alert.AlertType.ERROR, "Navigation Error",
                    "Could not navigate to Products", e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * Navigate back to User Management
     */
    @FXML
    void goToUser(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/AfficherUser.fxml"));
            Parent root = loader.load();

            // Mark the users button as active before switching
            markActiveButton(GoToUser);

            GoToArticle.getScene().setRoot(root);
        } catch (IOException e) {
            showAlert(Alert.AlertType.ERROR, "Navigation Error",
                    "Could not navigate to Users", e.getMessage());
            e.printStackTrace();
        }
    }



    /**
     * Show an alert dialog
     */
    private void showAlert(Alert.AlertType alertType, String title, String header, String content) {
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setHeaderText(header);
        alert.setContentText(content);
        alert.showAndWait();
    }


}