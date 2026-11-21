package com.agritech;

import com.agritech.controllers.VentanaPrincipalController;
import com.agritech.repositories.sqlite.SQLiteAlertasRepository;
import com.agritech.repositories.sqlite.SQLiteMedidasRepository;
import com.agritech.repositories.sqlite.SQLitePlantasRepository;
import com.agritech.repositories.sqlite.SQLiteRiegoRepository;
import com.agritech.services.AlertasService;
import com.agritech.services.MedidasService;
import com.agritech.services.PlantasService;
import com.agritech.services.RiegoService;
import com.agritech.utils.InicializadorBaseDatos;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;
import java.sql.SQLException;

/** Abre la portada y conecta sus pantallas con los servicios del huerto. */
public class AplicacionAgriTech extends Application {
    private VentanaPrincipalController controlador;

    @Override
    public void start(Stage escenario) throws IOException, SQLException {
        InicializadorBaseDatos.inicializar();
        controlador = new VentanaPrincipalController(
                new PlantasService(new SQLitePlantasRepository()),
                new MedidasService(new SQLiteMedidasRepository()),
                new AlertasService(new SQLiteAlertasRepository()),
                new RiegoService(new SQLiteRiegoRepository()));
        FXMLLoader cargador = new FXMLLoader(getClass().getResource("/fxml/main.fxml"));
        cargador.setController(controlador);
        Parent portada = cargador.load();
        escenario.setTitle("AgriTech");
        escenario.setMinWidth(1000);
        escenario.setMinHeight(700);
        escenario.setScene(new Scene(portada, 1100, 740));
        escenario.show();
    }

    @Override
    public void stop() {
        if (controlador != null) {
            controlador.cerrarSesion();
        }
    }

    public static void main(String[] argumentos) throws SQLException {
        if (argumentos.length == 1 && "--inicializar-bd".equals(argumentos[0])) {
            InicializadorBaseDatos.inicializar();
            System.out.println("Base de datos SQLite inicializada en: "
                    + System.getProperty("user.dir") + "/datos/agritech.db");
            return;
        }
        launch(argumentos);
    }
}
