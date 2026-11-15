
package com.agritech;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

public class Main extends Application {

    @Override
    public void start(Stage stage) throws IOException {

        // 1. Localizamos el archivo FXML
        FXMLLoader loader = new FXMLLoader(
                Main.class.getResource("/fxml/main.fxml")
        );

        // 2. Cargamos la interfaz y creamos la escena
        Scene scene = new Scene(loader.load(), 1100, 650);

        // 3. Configuramos la ventana
        stage.setTitle("AgriTech - Huerto inteligente");

        // 4. Asignamos la escena correcta
        stage.setScene(scene);

        // 5. Mostramos la ventana
        stage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
