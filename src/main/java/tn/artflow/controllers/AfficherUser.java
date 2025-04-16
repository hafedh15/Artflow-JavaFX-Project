package tn.artflow.controllers;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;
import tn.artflow.entities.User;
import tn.artflow.services.UserService;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.GridPane;

import java.io.IOException;
import java.sql.SQLException;
import java.util.List;




public class AfficherUser {

    @FXML
    private GridPane userGrid;

    private User loggedInUser;

    public void setLoggedInUser(User user) {
        this.loggedInUser = user;
    }


    private final UserService userService = new UserService();

    @FXML
    public void initialize() {
        try {
            List<User> users = userService.recupererr(); // or recupererr() if you want more fields
            populateGrid(users);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void populateGrid(List<User> users) {
        userGrid.getChildren().clear(); // Clear previous content

        // Create and add styled headers
        userGrid.add(createHeader("Name"), 0, 0);
        userGrid.add(createHeader("Lastname"), 1, 0);
        userGrid.add(createHeader("Role"), 2, 0);
        userGrid.add(createHeader("Email"), 3, 0);
        userGrid.add(createHeader("Photo"), 4, 0);
        userGrid.add(createHeader("Date Created"), 5, 0);
        userGrid.add(createHeader("Verified"), 6, 0);
        userGrid.add(createHeader("Banned"), 7, 0);
        userGrid.add(createHeader("Actions"), 8, 0);

        int row = 1;

        for (User user : users) {
            Label nameLabel = new Label(user.getName());
            Label lastnameLabel = new Label(user.getLastname());
            Label rolesLabel = new Label(user.getRoles());
            Label emailLabel = new Label(user.getEmail());
            Label photoLabel = new Label(user.getPhoto());
            Label dateLabel = new Label(user.getDateCreation().toString());
            Label verifiedLabel = new Label(user.getIsVerified() ? "Yes" : "No");
            Label bannedLabel = new Label(user.getIsBanned() ? "Yes" : "No");

            // Style each data label (optional for a clean look)
//            styleCell(nameLabel);
//            styleCell(lastnameLabel);
//            styleCell(rolesLabel);
//            styleCell(emailLabel);
//            styleCell(photoLabel);
//            styleCell(dateLabel);
//            styleCell(verifiedLabel);
//            styleCell(bannedLabel);

            // Action buttons
            Button updateBtn = new Button("Update");
            Button deleteBtn = new Button("Delete");
            Button profileBtn = new Button("View");

            updateBtn.setStyle("-fx-background-color: #4CAF50; -fx-text-fill: white; -fx-font-size: 11px;");
            deleteBtn.setStyle("-fx-background-color: #f44336; -fx-text-fill: white; -fx-font-size: 11px;");
            profileBtn.setStyle("-fx-background-color: #2196F3; -fx-text-fill: white; -fx-font-size: 11px;");

            updateBtn.setOnAction(e -> handleUpdateDash(user));
            deleteBtn.setOnAction(e -> handleDelete(user));
            profileBtn.setOnAction(e -> handleViewProfile(user));


            GridPane actionPane = new GridPane();
            actionPane.setHgap(5);
            actionPane.add(updateBtn, 0, 0);
            actionPane.add(deleteBtn, 1, 0);
            actionPane.add(profileBtn, 2, 0);

            // Add row to grid
            userGrid.add(nameLabel, 0, row);
            userGrid.add(lastnameLabel, 1, row);
            userGrid.add(rolesLabel, 2, row);
            userGrid.add(emailLabel, 3, row);
            userGrid.add(photoLabel, 4, row);
            userGrid.add(dateLabel, 5, row);
            userGrid.add(verifiedLabel, 6, row);
            userGrid.add(bannedLabel, 7, row);
            userGrid.add(actionPane, 8, row);

            row++;
        }
    }

    private Label createHeader(String text) {
        Label label = new Label(text);
        label.setStyle("-fx-font-weight: bold;; -fx-padding: 10;  -fx-font-size: 14px;");
        label.setMaxWidth(Double.MAX_VALUE);
        return label;
    }

//    private void styleCell(Label label) {
//        label.setStyle("-fx-padding: 6 12 6 12; -fx-font-size: 13px; -fx-alignment: CENTER-LEFT;");
//        label.setMaxWidth(Double.MAX_VALUE);
//    }



    private void handleUpdateDash(User user) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/UpdateUserDash.fxml"));
            Parent root = loader.load();

            // Pass user to controller
            UpdateUserControllerDash controller = loader.getController();
            controller.setUserToUpdateDash(user);
            controller.setAfficherUserController(this);
            Stage stage = new Stage();
            stage.setTitle("Update User");
            stage.setScene(new Scene(root));
            stage.show();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    private void handleDelete(User user) {
        try {
            userService.supprimer(user);
            populateGrid(userService.recupererr()); // Refresh
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    public void refreshGrid() {
        try {
            List<User> updatedUsers = userService.recupererr();
            populateGrid(updatedUsers);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }


    @FXML
    private void openAddUserWindow() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/AddUserDash.fxml"));
            Parent root = loader.load();

            AddUserControllerDash controller = loader.getController();
            controller.setAfficherUserController(this);

            Stage stage = new Stage();
            stage.setTitle("Add User");
            stage.setScene(new Scene(root));
            stage.show(); // wait until the user adds the data

            // optionally refresh the GridPane here after adding
            //refreshGrid();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // hedhi profile user in session
    @FXML
    private void handleViewProfil(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/ProfilUser.fxml"));
            Parent root = loader.load();

            // Get the controller and pass the user data if needed
            ProfilUserController controller = loader.getController();
            controller.setUser(tn.artflow.utils.UserSession.getInstance().getUser());

            Stage stage = new Stage();
            stage.setTitle("User Profile");
            stage.setScene(new Scene(root));
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

// w hedhi profil mta3 kol user f table
    private void handleViewProfile(User user) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/ProfilUser.fxml"));
            Parent root = loader.load();

            ProfilUserController controller = loader.getController();
            controller.setUser(user); // Pass user to profile

            Stage stage = new Stage();
            stage.setTitle("User Profile");
            stage.setScene(new Scene(root));
            stage.show();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void openAfficherUserScreen() {
        try {
            // Load the FXML file for the user list screen
            FXMLLoader loader = new FXMLLoader(getClass().getResource("AfficherUser.fxml"));
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
//            this.afficherUserController = afficherUserController;

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}




//public class AfficherUser {
//
//    @FXML
//    private TableView<User> userTable;
//    @FXML
//    private TableColumn<User, String> colName;
//    @FXML
//    private TableColumn<User, String> colLastName;
//    @FXML
//    private TableColumn<User, String> colEmail;
//    @FXML
//    private TableColumn<User, Boolean> colVerified;
//    @FXML
//    private TableColumn<User, java.util.Date> colDateCreation;
//
//    public void initialize() {
//        colName.setCellValueFactory(new PropertyValueFactory<>("name"));
//        colLastName.setCellValueFactory(new PropertyValueFactory<>("lastname"));
//        colEmail.setCellValueFactory(new PropertyValueFactory<>("email"));
//        colVerified.setCellValueFactory(new PropertyValueFactory<>("isVerified"));
//        colDateCreation.setCellValueFactory(new PropertyValueFactory<>("dateCreation"));
//
//        UserService ps = new UserService();
//        try {
//            List<User> list = ps.recuperer(); // You must implement this in your service
//            ObservableList<User> observableList = FXCollections.observableArrayList(list);
//            userTable.setItems(observableList);
//        } catch (SQLException e) {
//            System.out.println("Error loading users: " + e.getMessage());
//        }
//    }
//}
