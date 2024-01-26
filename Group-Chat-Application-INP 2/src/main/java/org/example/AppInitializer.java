package org.example;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class AppInitializer extends Application {
    private static double xOffset = 0;
    private static double yOffset = 0;
    @Override
    public void start(Stage stage) throws Exception {
     Parent root = FXMLLoader.load(this.getClass().getResource("/view/Login_form.fxml"));
     Scene scene = new Scene(root);
        stage.setTitle("Login");
        stage.setScene(scene);
        stage.setResizable(false);
        stage.centerOnScreen();
     root.setOnMousePressed(event -> {
         xOffset = event.getSceneX();
         yOffset = event.getSceneY();
     });
     root.setOnMouseDragged(event -> {
         stage.setX(event.getScreenX() - xOffset);
         stage.setY(event.getScreenY() - yOffset);
     });

     stage.show();
    }
    public static void main(String[] args) {
        launch();
    }
}
