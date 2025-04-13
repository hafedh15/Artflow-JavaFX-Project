package tn.artflow.controllors;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
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
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class AddWorkshop {

    public Label titleErrorLabel;
    public Label imageErrorLabel;
    public Label locationErrorLabel;
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

    @FXML
    private ComboBox<String> comboType;

    @FXML
    public void initialize() {
        // Populate combo box with options
        comboType.getItems().addAll("Online", "In Person");
    }

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



    @FXML
    void addWorkshop(ActionEvent event) {


        titleErrorLabel.setText("");
        imageErrorLabel.setText("");
        locationErrorLabel.setText("");

        String title = txttitle.getText();
        String description = txtdescription.getText();
        String image = txtimage.getText();
        String type = comboType.getValue(); // Get selected type from dropdown
        String location = txtlocation.getText();
        LocalDate dateValue = date.getValue(); // Supposons que 'date' est un DatePicker ou une valeur de date sélectionnée

        // Ajouter une heure prédéfinie (par exemple, 14:30)
        LocalDateTime dateTimeValue = dateValue.atTime(14, 30); // Ajoute l'heure 14:30 à la date

        // Convertir en String au format voulu (par exemple : "2025-04-12 14:30")
        String dateString = dateTimeValue.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"));


        if (title.length() > 5) {
            titleErrorLabel.setText("Title must not exceed 5 letters.");
            return;
        }

        if (!(image.endsWith(".jpg") || image.endsWith(".jpeg") || image.endsWith(".png"))) {
            imageErrorLabel.setText("Image must be .jpg, .jpeg or .png format.");
            return;
        }
        if (!location.matches("(?=.*[a-zA-Z])(?=.*[0-9]).+")) {
            locationErrorLabel.setText("Location must contain letters and numbers.");
            return;
        }


        Workshop w = new Workshop(title, description, image, dateString, type, location);

        WorkshopService ws = new WorkshopService();
        try {
            ws.ajouter(w);
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }


}
