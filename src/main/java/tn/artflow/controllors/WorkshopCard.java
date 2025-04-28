package tn.artflow.controllors;

import com.sothawo.mapjfx.Coordinate;
import com.sothawo.mapjfx.MapType;
import com.sothawo.mapjfx.MapView;
import com.sothawo.mapjfx.Marker;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;
import tn.artflow.entities.Workshop;
import tn.artflow.services.WorkshopService;

import java.io.File;
import java.io.IOException;
import java.util.List;

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
    private Workshop workshop;

    public void setData(Workshop w) {
        this.workshop = w;

        titleLabel.setText(w.getTitle());
        dateLabel.setText(w.getDate());
        typeLabel.setText(w.getType());
        locationLabel.setText(w.getLocation());

        String desc = w.getDescription();
        descriptionLabel.setText(desc.length() > 60 ? desc.substring(0, 57) + "..." : desc);

        String imageName = w.getImage();
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
            controller.setWorkshop(workshop);

            Stage stage = new Stage();
            stage.setTitle("Register for: " + workshop.getTitle());
            stage.setScene(new Scene(root));
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    public void chargerPremierAtelierEtAfficher() {
        WorkshopService workshopService = new WorkshopService(); // Assure-toi que le constructeur gère bien la connexion

        List<Workshop> ateliers = workshopService.getAllWorkshops();

        if (!ateliers.isEmpty()) {
            this.workshop = ateliers.get(0); // 👈 ENFIN ! Stocke l'atelier
        } else {
            System.out.println("⚠️ Aucun atelier trouvé.");
        }
    }

    public void handleVoirCarte(ActionEvent event) {
        chargerPremierAtelierEtAfficher(); // charge un atelier dans this.workshop

        MapView mapView = new MapView();
        mapView.setMapType(MapType.OSM);

        BorderPane root = new BorderPane();
        root.setCenter(mapView); // ✅ Important : on met le mapView DIRECTEMENT avant le show

        Scene scene = new Scene(root, 800, 600);
        Stage stage = new Stage();
        stage.setScene(scene);
        stage.setTitle("Localisation de l'atelier");

        stage.setOnCloseRequest(e -> mapView.close());

        // Maintenant qu'on a mis la MapView, on affiche la fenêtre
        stage.show();

        // Et seulement après, on initialise la MapView
        mapView.initialize();

        mapView.initializedProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal) {
                if (workshop == null) {
                    System.out.println("⚠️ Aucun atelier sélectionné !");
                    return;
                }

                float lat = workshop.getLatitude();
                float lon = workshop.getLongitude();

                System.out.println("Latitude: " + lat + ", Longitude: " + lon); // DEBUG

                if (lat != 0.0f || lon != 0.0f) {
                    Coordinate coord = new Coordinate((double) lat, (double) lon);

                    mapView.setZoom(14);
                    mapView.setCenter(coord);

                    Marker marker = Marker.createProvided(Marker.Provided.RED)
                            .setPosition(coord)
                            .setVisible(true);

                    mapView.addMarker(marker);
                } else {
                    System.out.println("⚠️ Coordonnées non définies pour cet atelier.");
                }
            }
        });
    }








}
