package tn.artflow.tests;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

public class MainFx extends Application {

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) {
       /* FXMLLoader loader = new FXMLLoader(
                getClass()
                        .getResource("/AddWorkshop.fxml")
        );
        try {
            Parent root = loader.load();
            Scene scene = new Scene(root);
            primaryStage.setScene(scene);
            primaryStage.setTitle("Add Workshop");
            primaryStage.show();
        } catch (IOException e) {
            System.out.println(e.getMessage());
        }*/
        //FXMLLoader loader = new FXMLLoader(getClass().getResource("/dashWorkshop.fxml"));

        FXMLLoader loader = new FXMLLoader(getClass().getResource("/dashReservation.fxml"));

        try {
            Parent root = loader.load();
            Scene scene = new Scene(root);
            primaryStage.setScene(scene);
            primaryStage.setTitle("Workshop List");
            primaryStage.show();
        } catch (IOException e) {
            System.out.println(e.getMessage());
        }

    }
}
