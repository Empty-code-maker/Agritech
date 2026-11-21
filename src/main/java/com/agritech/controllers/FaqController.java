package com.agritech.controllers;

import javafx.fxml.FXML;
import javafx.scene.control.TitledPane;

/** Abre la primera respuesta; la navegación pertenece a la barra común de la portada. */
public class FaqController {
    @FXML private TitledPane preguntaAgriTech;

    @FXML
    private void initialize() {
        preguntaAgriTech.setExpanded(true);
    }
}
