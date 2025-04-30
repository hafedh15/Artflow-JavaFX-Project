package tn.artflow.controllers;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene; // ❗ Tu avais oublié d'importer Scene
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.Stage;
import tn.artflow.entities.Workshop;

import java.io.File;
import java.io.IOException;

public class WorkshopCard {

    @FXML
    private ImageView imageView;

    @FXML
    private Label titleLabel;

    @FXML
    private Label dateLabel;

    @FXML
    private Label typeLabel;

    @FXML
    private Label locationLabel;

    @FXML
    private Label descriptionLabel;
    private FrontWorkshop parentController;


    // 🔴 AJOUT : Pour garder une référence à l’atelier courant
    private Workshop workshop;

    public void setData(Workshop w) {
        this.workshop = w; // 🔴 Ne pas oublier de stocker l'atelier pour plus tard

        titleLabel.setText(w.getTitle());
        dateLabel.setText(w.getDate());
        typeLabel.setText(w.getType());
        locationLabel.setText(w.getLocation());

        String desc = w.getDescription();
        descriptionLabel.setText(desc.length() > 60 ? desc.substring(0, 57) + "..." : desc);

        String imageName = w.getImage(); // Ex: "/images/workshops/example.jpg"
        String imageFullPath = "C:/xampp/htdocs" + imageName;

        File imageFile = new File(imageFullPath);
        Image image = imageFile.exists()
                ? new Image(imageFile.toURI().toString())
                : new Image(getClass().getResourceAsStream("/images/default-workshop.png"));

        imageView.setImage(image);
    }

  public void setParentController(FrontWorkshop controller) {
        this.parentController = controller;
    }

    public void registerReservation(ActionEvent actionEvent) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/RegisterReservation.fxml"));
            Parent root = loader.load();

            RegisterReservation controller = loader.getController();
            controller.setWorkshop(workshop); // 🔵 transmettre l'atelier

            Stage stage = new Stage();
            stage.setTitle("Register for: " + workshop.getTitle());
            stage.setScene(new Scene(root));
            stage.show();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
