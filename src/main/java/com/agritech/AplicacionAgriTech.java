package com.agritech;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.stage.Stage;

import com.agritech.controllers.RiegoController;
import com.agritech.controllers.PlantaController;
import com.agritech.repositories.sqlite.SQLiteAlertasRepository;
import com.agritech.repositories.sqlite.SQLiteRiegoRepository;
import com.agritech.repositories.sqlite.SQLiteMedidasRepository;
import com.agritech.repositories.sqlite.SQLitePlantasRepository;
import com.agritech.services.AlertasService;
import com.agritech.services.RiegoService;
import com.agritech.services.MedidasService;
import com.agritech.services.PlantasService;
import com.agritech.simulation.SimuladorSensor;
import com.agritech.utils.InicializadorBaseDatos;

import java.io.IOException;
import java.sql.SQLException;

/** Punto de entrada de la aplicación AgriTech. */
public class AplicacionAgriTech extends Application {

    @Override
    public void start(Stage escenario) throws IOException, SQLException {
        InicializadorBaseDatos.inicializar();

        PlantasService servicioPlantas = new PlantasService(new SQLitePlantasRepository());
        MedidasService servicioMedidas = new MedidasService(new SQLiteMedidasRepository());
        AlertasService servicioAlertas = new AlertasService(new SQLiteAlertasRepository());
        RiegoService servicioRiego = new RiegoService(new SQLiteRiegoRepository());
        RiegoController controladorRiego = new RiegoController(servicioRiego,
                servicioPlantas, servicioMedidas, servicioAlertas, new SimuladorSensor());

        FXMLLoader cargadorRiego = new FXMLLoader(AplicacionAgriTech.class.getResource("/fxml/riego.fxml"));
        cargadorRiego.setController(controladorRiego);
        Parent vistaRiego = cargadorRiego.load();
        controladorRiego.inicializarPantalla();

        FXMLLoader cargadorPlantas = new FXMLLoader(AplicacionAgriTech.class.getResource("/fxml/plantas.fxml"));
        PlantaController controladorPlantas = new PlantaController(servicioPlantas,
                controladorRiego::refrescarPlantas);
        cargadorPlantas.setController(controladorPlantas);
        Parent vistaPlantas = cargadorPlantas.load();
        controladorPlantas.inicializarPantalla();

        Tab ventanaRiego = new Tab("Riego", vistaRiego);
        ventanaRiego.setClosable(false);
        Tab ventanaPlantas = new Tab("Plantas y umbrales", vistaPlantas);
        ventanaPlantas.setClosable(false);
        TabPane ventanas = new TabPane();
        ventanas.getTabs().addAll(ventanaRiego, ventanaPlantas);

        escenario.setTitle("AgriTech — Riego");
        escenario.setScene(new Scene(ventanas, 680, 540));
        escenario.show();
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
