package com.agritech.controllers;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.Alert;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.StackPane;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;

import java.io.IOException;
import java.net.URL;

public class MainController {







        @FXML
        private BorderPane mainLayout;

       // Contenedor donde mostramos las pantallas
        @FXML
        private StackPane contentHost;



        @FXML
        private BorderPane homeView;


    @FXML
    private void mostrarCaracteristicas() {

        // Título de la sección
        Label titulo = new Label("Características de AgriTech");
        titulo.getStyleClass().add("seccion-titulo");

        // Texto que explica las funciones principales
        Label descripcion = new Label(
                "AgriTech permite consultar la humedad del suelo, " +
                        "la temperatura, la luminosidad y otros datos del huerto. " +
                        "También muestra gráficos para ver su evolución."
        );

        descripcion.setWrapText(true);
        descripcion.setMaxWidth(600);
        descripcion.getStyleClass().add("seccion-texto");

        // Aclaramos que todavía utilizamos datos de prueba
        Label aviso = new Label(
                "Esta versión utiliza datos de demostración."
        );
        aviso.getStyleClass().add("seccion-aviso");

        // Botón para regresar a la pantalla principal
        Button volver = new Button("Volver al inicio");
        volver.getStyleClass().add("boton-verde");

        volver.setOnAction(event -> navigateToView("inicio"));

        // Colocamos los elementos uno debajo de otro
        VBox seccion = new VBox(
                20, titulo, descripcion, aviso, volver
        );

        seccion.setAlignment(Pos.CENTER_LEFT);
        seccion.getStyleClass().add("seccion-informacion");

        // Mostramos esta sección en la ventana principal
        contentHost.getChildren().setAll(seccion);
    }


    @FXML
        private void mostrarFuncionamiento() {
            mostrarMensaje(
                    "Cómo funciona",
                    "La aplicación carga y representa lecturas locales."
            );
        }

        @FXML
        private void mostrarPrecios() {
            mostrarMensaje(
                    "Precios",
                    "Sección informativa pendiente de definir."
            );
        }

        @FXML
        private void mostrarFaq() {
            mostrarMensaje(
                    "FAQ",
                    "Aquí mostraremos las preguntas frecuentes."
            );
        }

        @FXML
        private void verVideo() {
            mostrarMensaje(
                    "Vídeo",
                    "Todavía no hay un vídeo asociado a la demo."
            );
        }


        @FXML
        private void abrirLogin() {

            // Abrimos la pantalla de inicio de sesión
            navigateToView("login");
        }

      // Decide qué pantalla queremos mostrar
    public void navigateToView(String vista) {

        if ("inicio".equals(vista)) {

            // Volvemos a la pantalla principal
            contentHost.getChildren().setAll(homeView);

        } else {

            // Abrimos la pantalla solicitada
            loadView(vista);
        }
    }


    // Carga el archivo FXML de una pantalla
    public void loadView(String vista) {

        // Creamos la ruta del archivo
        String ruta = "/fxml/" + vista + ".fxml";

        URL recurso = getClass().getResource(ruta);

        // Comprobamos que el archivo existe
        if (recurso == null) {
            mostrarMensaje("Error", "No se encuentra " + ruta);
            return;
        }

        try {

            // Cargamos la pantalla
            FXMLLoader loader = new FXMLLoader(recurso);
            Parent pantalla = loader.load();


            // Conectamos la pantalla de informes con el controlador principal
            if ("reports".equals(vista)) {
                ReportController controladorInformes = loader.getController();
                controladorInformes.setMainController(this);
            }

            // Sustituimos el contenido actual
            contentHost.getChildren().setAll(pantalla);

        } catch (IOException e) {

            mostrarMensaje("Error", "No se pudo abrir " + vista);
            e.printStackTrace();
        }
    }

    // Abre la pantalla de informes
    @FXML
    private void abrirInformes() {
        navigateToView("reports");
    }


    // Permite regresar a la pantalla de inicio
    public void logout() {

        navigateToView("inicio");
    }



    private void mostrarMensaje(
                String titulo,
                String mensaje) {

            Alert alerta = new Alert(
                    Alert.AlertType.INFORMATION
            );

            alerta.setTitle(titulo);
            alerta.setHeaderText(null);
            alerta.setContentText(mensaje);
            alerta.showAndWait();
        }
    }


