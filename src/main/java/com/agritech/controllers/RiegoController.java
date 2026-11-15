package com.agritech.controllers;

import com.agritech.models.EstadoRiego;
import com.agritech.models.Medida;
import com.agritech.models.Planta;
import com.agritech.services.RiegoService;
import com.agritech.services.AlertasService;
import com.agritech.services.MedidasService;
import com.agritech.services.PlantasService;
import com.agritech.simulation.SimuladorSensor;
import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import javafx.util.Duration;

import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

/** Controlador de la pantalla mínima de riego simulado. */
public class RiegoController {

    private final RiegoService servicioRiego;
    private final PlantasService servicioPlantas;
    private final MedidasService servicioMedidas;
    private final AlertasService servicioAlertas;
    private final SimuladorSensor simulador;
    private Timeline lineaTiempo;
    private int pasoEscenario;
    private boolean actualizandoSelector;

    @FXML
    private Label etiquetaEstado;

    @FXML
    private Button botonAlternar;

    @FXML
    private ComboBox<Planta> selectorPlantas;

    @FXML
    private Label etiquetaUmbral;

    @FXML
    private Label etiquetaHumedad;

    @FXML
    private VBox panelAviso;

    @FXML
    private Label etiquetaAviso;

    @FXML
    private Button botonQuitarAviso;

    public RiegoController(RiegoService servicioRiego, PlantasService servicioPlantas,
                                MedidasService servicioMedidas, AlertasService servicioAlertas,
                                SimuladorSensor simulador) {
        this.servicioRiego = servicioRiego;
        this.servicioPlantas = servicioPlantas;
        this.servicioMedidas = servicioMedidas;
        this.servicioAlertas = servicioAlertas;
        this.simulador = simulador;
    }

    public void inicializarPantalla() {
        try {
            mostrarEstado(servicioRiego.obtenerEstadoActual());
            refrescarPlantas(-1);

        } catch (SQLException excepcion) {
            etiquetaEstado.setText("No se pudo leer el estado");
            botonAlternar.setDisable(true);
            mostrarError("No se pudo cargar el estado del riego.", excepcion);
        }
    }

    @FXML
    private void alternarRiego() {
        try {
            mostrarEstado(servicioRiego.alternarEstado());

        } catch (SQLException excepcion) {
            mostrarError("No se pudo guardar el cambio de estado.", excepcion);
        }
    }

    @FXML
    private void cambiarPlanta() {

        if (!actualizandoSelector) {
            iniciarEscenario();
        }
    }

    @FXML
    private void quitarAviso() {

        if (!botonQuitarAviso.isVisible()) {
            return;
        }

        try {
            Object datoId = botonQuitarAviso.getUserData();
            if (datoId == null) {
                return;
            }
            long idAviso = Long.parseLong(datoId.toString());
            servicioAlertas.descartar(idAviso);
            mostrarAviso(Optional.<com.agritech.models.Alerta>empty(), selectorPlantas.getValue());

        } catch (SQLException excepcion) {
            mostrarError("No se pudo retirar el aviso.", excepcion);
        }
    }

    public void refrescarPlantas(int idSeleccionado) {
        try {
            Planta seleccionadaAnterior = selectorPlantas.getValue();
            int id = idSeleccionado > 0
                    ? idSeleccionado
                    : seleccionadaAnterior == null ? -1 : seleccionadaAnterior.obtenerId();
            List<Planta> plantas = servicioPlantas.obtenerPlantas();
            actualizandoSelector = true;

            try {
                selectorPlantas.getItems().setAll(plantas);

                Planta paraSeleccionar = null;

                for (Planta planta : plantas) {
                    if (planta.obtenerId() == id) {
                        paraSeleccionar = planta;
                        break;
                    }
                }

                if (paraSeleccionar == null && !plantas.isEmpty()) {
                    paraSeleccionar = plantas.get(0);
                }

                selectorPlantas.getSelectionModel().select(paraSeleccionar);

            } finally {
                actualizandoSelector = false;
            }
            iniciarEscenario();

        } catch (SQLException excepcion) {
            mostrarError("No se pudieron cargar las plantas.", excepcion);
        }
    }

    private void iniciarEscenario() {

        if (lineaTiempo != null) {
            lineaTiempo.stop();
        }

        Planta planta = selectorPlantas.getValue();

        if (planta == null) {
            etiquetaUmbral.setText("Añade una planta para iniciar la simulación.");
            etiquetaHumedad.setText("Humedad simulada: —");
            panelAviso.setVisible(false);
            panelAviso.setManaged(false);
            return;
        }

        pasoEscenario = 0;
        etiquetaUmbral.setText(String.format(java.util.Locale.ROOT,
                "Umbral mínimo: %.1f %%", planta.obtenerUmbral().obtenerHumedadSueloMinima()));
        registrarLectura();

        lineaTiempo = new Timeline(new KeyFrame(Duration.seconds(5), evento -> registrarLectura()));
        lineaTiempo.setCycleCount(Animation.INDEFINITE);
        lineaTiempo.play();
    }

    private void registrarLectura() {
        
        Planta planta = selectorPlantas.getValue();

        if (planta == null) {
            return;
        }

        try {
            Medida lecturaSimulada = simulador.generarMedida(planta, pasoEscenario);
            Medida lecturaGuardada = servicioMedidas.registrar(lecturaSimulada);
            pasoEscenario++;
            etiquetaHumedad.setText(String.format(java.util.Locale.ROOT,
                    "Humedad simulada: %.1f %%", lecturaGuardada.obtenerHumedadSuelo()));
            Optional<com.agritech.models.Alerta> aviso = servicioAlertas.evaluar(lecturaGuardada,
                    planta.obtenerUmbral().obtenerHumedadSueloMinima());
            mostrarAviso(aviso, planta);
            
        } catch (SQLException excepcion) {
            mostrarError("No se pudo guardar la lectura simulada.", excepcion);
        }
    }

    private void mostrarAviso(Optional<com.agritech.models.Alerta> aviso, Planta planta) {
        if (!aviso.isPresent()) {
            panelAviso.setVisible(true);
            panelAviso.setManaged(true);
            botonQuitarAviso.setVisible(false);
            botonQuitarAviso.setManaged(false);
            etiquetaAviso.setText("Sin avisos activos.");
            return;
        }

        com.agritech.models.Alerta alerta = aviso.get();
        etiquetaAviso.setText(String.format(java.util.Locale.ROOT,
                "Humedad baja en %s: %.1f %% (mínimo %.1f %%).",
                planta.obtenerNombre(), alerta.obtenerValorObservado(), alerta.obtenerValorUmbral()));
        botonQuitarAviso.setUserData(String.valueOf(alerta.obtenerId()));
        botonQuitarAviso.setVisible(true);
        botonQuitarAviso.setManaged(true);
        panelAviso.setVisible(true);
        panelAviso.setManaged(true);
    }

    private void mostrarEstado(EstadoRiego estado) {
        etiquetaEstado.setText(estado.estaActivo() ? "RIEGO ACTIVADO" : "RIEGO DESACTIVADO");
        etiquetaEstado.getStyleClass().remove("estado-activo");
        etiquetaEstado.getStyleClass().remove("estado-inactivo");
        etiquetaEstado.getStyleClass().add(estado.estaActivo() ? "estado-activo" : "estado-inactivo");
        botonAlternar.setText(estado.estaActivo() ? "Desactivar riego" : "Activar riego");
    }

    private void mostrarError(String mensaje, SQLException excepcion) {
        Alert alerta = new Alert(Alert.AlertType.ERROR);
        alerta.setTitle("Error de persistencia");
        alerta.setHeaderText(mensaje);
        alerta.setContentText(excepcion.getMessage());
        alerta.showAndWait();
    }
}
