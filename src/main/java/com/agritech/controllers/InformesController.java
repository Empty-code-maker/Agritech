package com.agritech.controllers;

import com.agritech.services.InformesService;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import java.io.File;
import java.io.IOException;

/** Conserva la exportación PDF de Alexandru dentro de la navegación compartida. */
public class InformesController {
    @FXML private VBox vistaInformes;

    @FXML
    private void generarPdf() {
        FileChooser selector = new FileChooser();
        selector.setTitle("Guardar informe");
        selector.setInitialFileName("Informe_AgriTech.pdf");
        selector.getExtensionFilters().add(new FileChooser.ExtensionFilter("Archivos PDF", "*.pdf"));
        File archivo = selector.showSaveDialog(vistaInformes.getScene().getWindow());
        if (archivo == null) {
            return;
        }
        try {
            new InformesService().generar(archivo);
            mostrarMensaje(Alert.AlertType.INFORMATION, "Informe guardado correctamente.");
        } catch (IOException excepcion) {
            mostrarMensaje(Alert.AlertType.ERROR, "No se pudo guardar el informe: " + excepcion.getMessage());
        }
    }

    private void mostrarMensaje(Alert.AlertType tipo, String mensaje) {
        Alert alerta = new Alert(tipo, mensaje);
        alerta.initOwner(vistaInformes.getScene().getWindow());
        alerta.setHeaderText(null);
        alerta.showAndWait();
    }
}
