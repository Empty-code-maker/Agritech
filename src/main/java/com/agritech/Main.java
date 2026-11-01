package com.agritech;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;

/** Aplicación temporal para comprobar Java 8, JavaFX y Maven. */
public class Main extends Application {

    @Override
    public void start(Stage stage) {
        Label message = new Label("¡Hola, mundo!");
        StackPane root = new StackPane(message);

        stage.setTitle("Prueba de instalación Agritech");
        stage.setScene(new Scene(root, 400, 200));
        stage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
