package tn.artflow.controllors;

import com.google.protobuf.BoolValue;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import tn.artflow.entities.Workshop;
import tn.artflow.services.WorkshopService;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.sql.SQLException;
import java.time.LocalDate;

public class UpdateWorkshop {

    @FXML
    private ComboBox<String> comboType;

    @FXML
    private DatePicker date;

    @FXML
    private TextField txtdescription;

    @FXML
    private TextField txtimage;

    @FXML
    private TextField txtlocation;

    @FXML
    private TextField txttitle;

    @FXML private Label errTitle;
    @FXML private Label errDescription;
    @FXML private Label errImage;
    @FXML private Label errDate;


    @FXML
    void choosefile(ActionEvent event) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Select an Image File");

        // Corriger les filtres d'extension - noter l'astérisque avant le point
        fileChooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("Image Files", "*.png", "*.jpg", "*.jpeg", "*.gif")
        );


        // Définir un répertoire initial pour faciliter la navigation
        fileChooser.setInitialDirectory(new File(System.getProperty("user.home")));

        File selectedFile = fileChooser.showOpenDialog(null);

        if (selectedFile != null) {
            System.out.println("Fichier sélectionné : " + selectedFile.getAbsolutePath());
            try {
                // Destination path in XAMPP htdocs folder
                String destinationPath = "C:/xampp/htdocs/images/workshops/";
                File destDir = new File(destinationPath);
                if (!destDir.exists()) {
                    destDir.mkdirs();
                    System.out.println("Répertoire créé : " + destinationPath);
                }

                String newFileName = System.currentTimeMillis() + "_" + selectedFile.getName();
                File destinationFile = new File(destDir, newFileName);

                Files.copy(selectedFile.toPath(), destinationFile.toPath(), StandardCopyOption.REPLACE_EXISTING);
                System.out.println("Fichier copié vers : " + destinationFile.getAbsolutePath());

                // URL relative que vous allez stocker dans la base de données
                String webURL = "/images/workshops/" + newFileName;
                txtimage.setText(webURL);
                System.out.println("URL définie : " + webURL);

            } catch (IOException e) {
                System.out.println("Erreur de copie: " + e.getMessage());
                e.printStackTrace();
            }
        } else {
            System.out.println("Aucun fichier sélectionné");
        }
    }


    private Workshop currentWorkshop;

    public void setWorkshop(Workshop workshop) {
        this.currentWorkshop = workshop;

        // Pre-fill form
        txttitle.setText(workshop.getTitle());
        txtdescription.setText(workshop.getDescription());
        txtlocation.setText(workshop.getLocation());
        txtimage.setText(workshop.getImage());
        comboType.getItems().addAll("Online", "In Person");
        comboType.setValue(workshop.getType());


    }

    @FXML
    void updadeWorkshop(ActionEvent event) {
        // Réinitialiser les messages d'erreur
        errTitle.setText("");
        errDescription.setText("");
        errImage.setText("");
        errDate.setText("");

        boolean isValid = true;

        String title = txttitle.getText().trim();
        if (title.isEmpty()) {
            errTitle.setText("Title cannot be empty.");
            isValid = false;
        }

        String description = txtdescription.getText().trim();
        if (description.length() > 200) {
            errDescription.setText("Max 200 characters.");
            isValid = false;
        }

        String imagePath = txtimage.getText().trim().toLowerCase();
        if (!(imagePath.endsWith(".jpg") || imagePath.endsWith(".jpeg") || imagePath.endsWith(".png"))) {
            errImage.setText("Must be jpg/jpeg/png.");
            isValid = false;
        }

        LocalDate selectedDate = date.getValue();
        if (selectedDate == null || selectedDate.isBefore(LocalDate.now())) {
            errDate.setText("Date must not be in the past.");
            isValid = false;
        }

        if (!isValid) return;

        // Si tout est valide, on continue
        currentWorkshop.setTitle(title);
        currentWorkshop.setDescription(description);
        currentWorkshop.setLocation(txtlocation.getText().trim());
        currentWorkshop.setImage(imagePath);
        currentWorkshop.setType(comboType.getValue());
        currentWorkshop.setDate(selectedDate.toString());

        try {
            WorkshopService ws = new WorkshopService();
            ws.modifier(currentWorkshop);
            ((Stage) txttitle.getScene().getWindow()).close();
        } catch (SQLException e) {
            e.printStackTrace();
            showAlert("An error occurred while saving the workshop.");
        }
    }

    private void showAlert(String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Validation Error");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    @FXML
    void cancelUpdate(ActionEvent event) {
        // Close the window
        Stage stage = (Stage) txttitle.getScene().getWindow();
        stage.close();
    }




}
