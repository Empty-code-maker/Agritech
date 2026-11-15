/** Gestiona las interacciones de la vista de informes. */

package com.agritech.controllers;

import com.agritech.services.ReportService;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.stage.FileChooser;

import java.io.File;
import java.io.IOException;

public class ReportController {


    // Referencia al controlador principal
    private MainController mainController;


    @FXML
    private void generarPdf() {

        // Permitimos al usuario elegir dónde guardar el PDF
        FileChooser selector = new FileChooser();
        selector.setTitle("Guardar informe");
        selector.setInitialFileName("Informe_AgriTech.pdf");

        // Solo mostramos archivos de tipo PDF
        selector.getExtensionFilters().add(
                new FileChooser.ExtensionFilter("Archivos PDF", "*.pdf")
        );

        File archivo = selector.showSaveDialog(null);

        // Si el usuario cancela, no hacemos nada
        if (archivo == null) {
            return;
        }

        try {
            // Pedimos al servicio que genere el documento
            ReportService servicio = new ReportService();
            servicio.generar(archivo);

            new Alert(Alert.AlertType.INFORMATION,
                    "Informe guardado correctamente.").showAndWait();

        } catch (IOException e) {
            new Alert(Alert.AlertType.ERROR,
                    "No se pudo guardar el informe.").showAndWait();
        }
    }

            // Recibimos el controlador principal
            public void setMainController(MainController mainController) {
                this.mainController = mainController;
            }

            // Volvemos a la pantalla de inicio
            @FXML
            private void volverInicio() {
                if (mainController != null) {
                    mainController.navigateToView("inicio");
                }
            }

}


