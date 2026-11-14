package com.agritech.controllers;

import com.agritech.models.Planta;
import com.agritech.services.PlantasService;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;

import java.sql.SQLException;
import java.util.List;
import java.util.function.Consumer;

/** Permite añadir plantas y editar su umbral mínimo de humedad. */
public class PlantaController {

    private final PlantasService servicioPlantas;
    private final Consumer<Integer> alGuardar;

    @FXML
    private ListView<Planta> listaPlantas;

    @FXML
    private TextField campoNombre;

    @FXML
    private TextField campoUmbral;

    @FXML
    private Label etiquetaMensaje;

    public PlantaController(PlantasService servicioPlantas, Consumer<Integer> alGuardar) {
        this.servicioPlantas = servicioPlantas;
        this.alGuardar = alGuardar;
    }

    public void inicializarPantalla() {
        listaPlantas.getSelectionModel().selectedItemProperty().addListener(
                (propiedad, anterior, seleccionada) -> mostrarPlantaSeleccionada(seleccionada));
        refrescarLista(-1);
    }

    @FXML
    private void nuevaPlanta() {
        listaPlantas.getSelectionModel().clearSelection();
        campoNombre.clear();
        campoUmbral.setText("30");
        etiquetaMensaje.setText("Introduce el nombre y el umbral mínimo.");
        campoNombre.requestFocus();
    }

    @FXML
    private void crearPlanta() {
        try {
            Planta planta = servicioPlantas.crearPlanta(campoNombre.getText(), leerUmbral());
            etiquetaMensaje.setText("Planta añadida.");
            refrescarLista(planta.obtenerId());
            alGuardar.accept(planta.obtenerId());

        } catch (IllegalArgumentException excepcion) {
            mostrarError(excepcion.getMessage());

        } catch (SQLException excepcion) {
            mostrarError("No se pudo guardar la planta: " + excepcion.getMessage());
        }
    }

    @FXML
    private void guardarCambios() {
        Planta seleccionada = listaPlantas.getSelectionModel().getSelectedItem();

        if (seleccionada == null) {
            mostrarError("Selecciona una planta para modificar su nombre o umbral.");
            return;
        }

        try {
            servicioPlantas.actualizarPlanta(seleccionada, campoNombre.getText(), leerUmbral());
            etiquetaMensaje.setText("Cambios guardados.");
            refrescarLista(seleccionada.obtenerId());
            alGuardar.accept(seleccionada.obtenerId());

        } catch (IllegalArgumentException excepcion) {
            mostrarError(excepcion.getMessage());

        } catch (SQLException excepcion) {
            mostrarError("No se pudieron guardar los cambios: " + excepcion.getMessage());
        }
    }

    private double leerUmbral() {
        try {
            return Double.parseDouble(campoUmbral.getText().trim().replace(',', '.'));

        } catch (NumberFormatException excepcion) {
            throw new IllegalArgumentException("Escribe el umbral como un número, por ejemplo 30.");
        }
    }

    private void refrescarLista(int idSeleccionado) {
        try {
            List<Planta> plantas = servicioPlantas.obtenerPlantas();
            listaPlantas.getItems().setAll(plantas);

            for (Planta planta : plantas) {
                if (planta.obtenerId() == idSeleccionado) {
                    listaPlantas.getSelectionModel().select(planta);
                    return;
                }
            }

            if (!plantas.isEmpty()) {
                listaPlantas.getSelectionModel().selectFirst();
            }

        } catch (SQLException excepcion) {
            mostrarError("No se pudo cargar el catálogo: " + excepcion.getMessage());
        }
    }

    private void mostrarPlantaSeleccionada(Planta planta) {
        
        if (planta == null) {
            return;
        }

        campoNombre.setText(planta.obtenerNombre());
        campoUmbral.setText(String.valueOf(planta.obtenerUmbral().obtenerHumedadSueloMinima()));
        etiquetaMensaje.setText("Umbral aplicado a las lecturas simuladas de esta planta.");
    }

    private void mostrarError(String mensaje) {
        etiquetaMensaje.setText(mensaje);
        Alert alerta = new Alert(Alert.AlertType.ERROR);
        alerta.setTitle("No se pudieron guardar los datos");
        alerta.setHeaderText(mensaje);
        alerta.setContentText(null);
        alerta.showAndWait();
    }
}
