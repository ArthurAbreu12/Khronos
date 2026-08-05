package com.khronos;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;

public class MainApp extends Application {

    @Override
    public void start(Stage stage) throws Exception {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/views/main.fxml"));
        Parent root = loader.load();

        Scene scene = new Scene(root, 920, 640);
        scene.getStylesheets().add(getClass().getResource("/com/khronos/style.css").toExternalForm());

        stage.setTitle("Khronos — Planeje, Registre, Controle");
        stage.getIcons().add(new Image(getClass().getResourceAsStream("/com/khronos/images/leopard-logo.png")));
        stage.setScene(scene);
        stage.setMinWidth(760);
        stage.setMinHeight(560);
        stage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
