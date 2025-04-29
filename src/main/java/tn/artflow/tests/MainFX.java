package tn.artflow.tests;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.Parent;
import javafx.stage.Stage;

public class MainFX extends Application {

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) {
        try {
            // Charge le fichier FXML (assure-toi qu'il s'appelle AjouterArticle.fxml et qu'il est bien placé)
            Parent root = FXMLLoader.load(getClass().getResource("/AfficherArticle.fxml"));

            Scene scene = new Scene(root);

            primaryStage.setTitle("ajouter un Article");

            primaryStage.setScene(scene);
            primaryStage.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
