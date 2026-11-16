package com.agritech.controllers;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import java.util.function.Consumer;

/** Acceso provisional: acepta cualquier nombre y contraseña que no estén vacíos. */
public class AccesoController {
    private final Consumer<String> alEntrar;
    @FXML private TextField campoNombre;
    @FXML private PasswordField campoContrasena;
    @FXML private Label etiquetaError;

    public AccesoController(Consumer<String> alEntrar) {
        this.alEntrar = alEntrar;
    }

    @FXML
    private void initialize() {
        campoNombre.textProperty().addListener((propiedad, anterior, actual) -> ocultarError());
        campoContrasena.textProperty().addListener((propiedad, anterior, actual) -> ocultarError());
    }

    private void ocultarError() {
        etiquetaError.setVisible(false);
        etiquetaError.setManaged(false);
    }

    @FXML
    private void entrar() {
        String nombre = campoNombre.getText().trim();
        if (nombre.isEmpty() || campoContrasena.getText().trim().isEmpty()) {
            etiquetaError.setText("Escribe un nombre y una contraseña para entrar.");
            etiquetaError.setVisible(true);
            etiquetaError.setManaged(true);
            if (nombre.isEmpty()) {
                campoNombre.requestFocus();
            } else {
                campoContrasena.requestFocus();
            }
            return;
        }
        // La contraseña solo se usa para este formulario y no se guarda.
        campoContrasena.clear();
        alEntrar.accept(nombre);
    }

    public void limpiarContrasena() {
        campoContrasena.clear();
    }
}
