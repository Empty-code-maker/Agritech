package com.agritech.controllers;

import com.agritech.services.AlertasService;
import com.agritech.services.MedidasService;
import com.agritech.services.PlantasService;
import com.agritech.services.RiegoService;
import com.agritech.simulation.SimuladorSensor;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.Label;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.control.ToggleButton;
import javafx.scene.layout.StackPane;
import java.io.IOException;

/** Ventana principal con menú lateral y las pantallas existentes del huerto. */
public class PanelController {
    private final String nombre;
    private final PlantasService servicioPlantas;
    private final RiegoController controladorRiego;
    private final Runnable alSalir;
    private final Runnable alAbrirInformes;
    private Parent dashboard;
    private Parent monitorizacion;
    private Parent historial;
    @FXML private Label etiquetaUsuario;
    @FXML private Label etiquetaSeccion;
    @FXML private StackPane contenidoPanel;
    @FXML private ToggleButton botonDashboard;
    @FXML private ToggleButton botonMonitorizacion;
    @FXML private ToggleButton botonHistorial;
    @FXML private ToggleButton botonMisPlantas;
    @FXML private TabPane pestanasHuerto;
    @FXML private Tab pestanaRiego;
    @FXML private Tab pestanaPlantas;

    public PanelController(String nombre, PlantasService servicioPlantas,
            MedidasService servicioMedidas, AlertasService servicioAlertas,
            RiegoService servicioRiego, Runnable alSalir, Runnable alAbrirInformes) {
        this.nombre = nombre;
        this.servicioPlantas = servicioPlantas;
        this.alSalir = alSalir;
        this.alAbrirInformes = alAbrirInformes;
        controladorRiego = new RiegoController(servicioRiego, servicioPlantas,
                servicioMedidas, servicioAlertas, new SimuladorSensor());
    }

    public void inicializarPantalla() throws IOException {
        dashboard = FXMLLoader.load(getClass().getResource("/fxml/dashboard.fxml"));
        monitorizacion = FXMLLoader.load(getClass().getResource("/fxml/monitorizacion.fxml"));
        historial = FXMLLoader.load(getClass().getResource("/fxml/historial.fxml"));

        FXMLLoader cargadorRiego = new FXMLLoader(getClass().getResource("/fxml/riego.fxml"));
        cargadorRiego.setController(controladorRiego);
        Parent riego = cargadorRiego.load();
        FXMLLoader cargadorPlantas = new FXMLLoader(getClass().getResource("/fxml/plantas.fxml"));
        PlantaController controladorPlantas = new PlantaController(servicioPlantas,
                controladorRiego::refrescarPlantas);
        cargadorPlantas.setController(controladorPlantas);
        Parent plantas = cargadorPlantas.load();
        etiquetaUsuario.setText("Hola, " + nombre);
        pestanaRiego.setContent(riego);
        pestanaPlantas.setContent(plantas);
        pestanasHuerto.getSelectionModel().select(pestanaPlantas);
        controladorPlantas.inicializarPantalla();
        controladorRiego.inicializarPantalla();
        mostrarDashboard();
    }

    @FXML
    private void mostrarDashboard() {
        mostrarVista("Dashboard", dashboard, botonDashboard);
    }

    @FXML
    private void mostrarMonitorizacion() {
        mostrarVista("Monitorización", monitorizacion, botonMonitorizacion);
    }

    @FXML
    private void mostrarHistorial() {
        mostrarVista("Histórico", historial, botonHistorial);
    }

    @FXML
    private void mostrarMisPlantas() {
        mostrarVista("Mis plantas", pestanasHuerto, botonMisPlantas);
    }

    private void mostrarVista(String titulo, Parent vista, ToggleButton boton) {
        etiquetaSeccion.setText(titulo);
        boton.setSelected(true);
        contenidoPanel.getChildren().setAll(vista);
    }

    public void mostrarInformes(Parent informes) {
        etiquetaSeccion.setText("Informes");
        botonDashboard.getToggleGroup().selectToggle(null);
        contenidoPanel.getChildren().setAll(informes);
    }

    public void volverDesdeInformes() {
        mostrarDashboard();
    }

    public void detenerSimulacion() {
        controladorRiego.detenerSimulacion();
    }

    @FXML
    private void cerrarSesion() {
        alSalir.run();
    }

    @FXML
    private void abrirInformes() {
        alAbrirInformes.run();
    }
}
