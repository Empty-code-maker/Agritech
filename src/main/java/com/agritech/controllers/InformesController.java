/** Gestiona las interacciones de la vista de informes. */

package com.agritech.controllers;

import com.agritech.services.InformesService;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.stage.FileChooser;

import java.io.File;
import java.io.IOException;

public class InformesController {

  @FXML
  private VBox vistaInformes;

  @FXML
  private void generarPdf() {
    FileChooser selector = new FileChooser();
    selector.setTitle("Guardar informe");
    selector.setInitialFileName("Informe_AgriTech.pdf");
    selector
      .getExtensionFilters()
      .add(new FileChooser.ExtensionFilter("Archivos PDF", "*.pdf"));
    File archivo = selector.showSaveDialog(
      vistaInformes.getScene().getWindow()
    );

    if (archivo == null) {
      return;
    }

    try {
      // Pedimos al servicio que genere el documento
      InformesService servicio = new InformesService();
      servicio.generar(archivo);
      new Alert(
        Alert.AlertType.INFORMATION,
        "Informe guardado correctamente."
      ).showAndWait();
    } catch (IOException e) {
      new Alert(
        Alert.AlertType.ERROR,
        "No se pudo guardar el informe."
      ).showAndWait();
    }
  }

  private void mostrarMensaje(Alert.AlertType tipo, String mensaje) {
    Alert alerta = new Alert(tipo, mensaje);
    alerta.initOwner(vistaInformes.getScene().getWindow());
    alerta.setHeaderText(null);
    alerta.showAndWait();
  }

  private VentanaPrincipalController ventanaPrincipalController;

  // Recibimos el controlador principal
  public void setVentanaPrincipalController(
    VentanaPrincipalController ventanaPrincipalController
  ) {
    this.ventanaPrincipalController = ventanaPrincipalController;
  }

  // Volvemos a la pantalla de inicio
  @FXML
  private void volverInicio() {
    if (ventanaPrincipalController != null) {
      ventanaPrincipalController.navigateToView("inicio");
    }
  }
}