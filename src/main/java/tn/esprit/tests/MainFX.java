package tn.esprit.tests;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

public class MainFX extends Application {

    public static void main(String[] args) {
        launch(args);
    }

    @Override
   public void start(Stage primaryStage) {
        try {
            // Load the FXML file for Reclamation
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/AjouterReclamation.fxml"));

            // Load the root from the FXML
            Parent root = loader.load();

            // Create a scene and set it on the stage
            Scene scene = new Scene(root);

            // Set the stage title and scene
            primaryStage.setScene(scene);
            primaryStage.setTitle("Ajouter Reclamation"); // Update title to match Reclamation

            // Show the stage
            primaryStage.show();
        } catch (IOException e) {
            e.printStackTrace(); // Handle errors related to FXML loading
        }
    }

    /*public void start(Stage primaryStage) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/AfficherReclamation.fxml"));
            Parent root = loader.load();
            Scene scene = new Scene(root);
            primaryStage.setScene(scene);
            primaryStage.setTitle("Afficher Réclamations");
            primaryStage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }*/


  /*  public void start(Stage primaryStage) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/Conversation.fxml"));
            Parent root = loader.load();
            Scene scene = new Scene(root);
            primaryStage.setScene(scene);
            primaryStage.setTitle("Interface de Conversation");
            primaryStage.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }*/
}
