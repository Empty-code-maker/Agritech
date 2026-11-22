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
import javafx.scene.layout.HBox;
import java.awt.Desktop;
import java.net.URI;


import com.agritech.services.InformesService;
import javafx.stage.FileChooser;
import java.io.File;

import java.io.IOException;
import java.net.URL;

public class VentanaPrincipalController {

        @FXML
        private BorderPane mainLayout;

       // Contenedor donde mostramos las pantallas
        @FXML
        private StackPane contentHost;

        @FXML
        private BorderPane homeView;

        @FXML
        private HBox barraPortada;

        // Acciones para abrir las pantallas de riego y plantas
        private Runnable navegarRiego;
        private Runnable navegarPlantas;

        // Recibimos las acciones desde la aplicación principal
        public void configurarNavegacion(Runnable riego, Runnable plantas) {
            navegarRiego = riego;
            navegarPlantas = plantas;
        }

    // Muestra una pantalla dentro de nuestra interfaz principal
        public void mostrarVista(Parent vista) {

            // Sustituimos el contenido sin cambiar de ventana
            contentHost.getChildren().setAll(vista);
        }

    // Abrimos la pantalla de riego
        @FXML
        private void abrirRiego() {
            if (navegarRiego != null) {
                navegarRiego.run();
            }
        }

        // Abrimos la pantalla de plantas
        @FXML
        private void abrirPlantas() {
            if (navegarPlantas != null) {
                navegarPlantas.run();
            }
        }

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
        // Título de la sección
        Label titulo = new Label("Cómo funciona AgriTech");
        titulo.getStyleClass().add("seccion-titulo");

        // Explicamos las funciones de la aplicación
        Label descripcion = new Label(
                "1. Plantas: permite gestionar las plantas y sus umbrales.\n\n" +
                        "2. Riego: muestra la humedad simulada y permite activar el riego.\n\n" +
                        "3. Informes: permite generar y guardar archivos PDF.\n\n" +
                        "4. Datos: utilizamos SQLite y prepararemos la conexión con una API."
        );

        descripcion.setWrapText(true);
        descripcion.setMaxWidth(650);
        descripcion.getStyleClass().add("seccion-texto");

// Botón para descargar la guía en PDF
        Button descargar = new Button("Descargar guía PDF");
        descargar.getStyleClass().add("boton-verde");

// Al pulsarlo, guardamos el documento
        descargar.setOnAction(event -> guardarGuiaPdf());


        // Botón para regresar a la pantalla principal
        Button volver = new Button("Volver al inicio");
        volver.getStyleClass().add("boton-verde");
        volver.setOnAction(event -> navigateToView("inicio"));

        // Organizamos los elementos verticalmente

        VBox seccion = new VBox(25, titulo, descripcion, descargar, volver);

        seccion.setAlignment(Pos.CENTER_LEFT);
        seccion.getStyleClass().add("seccion-informacion");

        // Mostramos el contenido sin abrir otra ventana
        contentHost.getChildren().setAll(seccion);
        }


    // Permite elegir dónde guardar la guía PDF
    private void guardarGuiaPdf() {

        FileChooser selector = new FileChooser();
        selector.setTitle("Guardar guía de AgriTech");
        selector.setInitialFileName("Guia_AgriTech.pdf");

        // Solo permitimos seleccionar archivos PDF
        selector.getExtensionFilters().add(
                new FileChooser.ExtensionFilter("Archivos PDF", "*.pdf")
        );

        // Abrimos la ventana para elegir el destino
        File archivo = selector.showSaveDialog(
                mainLayout.getScene().getWindow()
        );

        // Si el usuario cancela, no hacemos nada
        if (archivo == null) {
            return;
        }

        try {
            // Utilizamos el servicio que ya hemos programado
            InformesService servicio = new InformesService();
            servicio.generarGuia(archivo);

            mostrarMensaje("Guía PDF", "La guía se ha guardado correctamente.");

        } catch (IOException e) {
            new Alert(Alert.AlertType.ERROR,
                    "No se pudo guardar la guía PDF.").showAndWait();
        }
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

            // Enlace al vídeo sobre el huerto inteligente
            String enlace = "https://www.youtube.com/watch?v=VgmKTAXLPw4";

            try {
                // Comprobamos que podemos abrir el navegador
                if (Desktop.isDesktopSupported()
                        && Desktop.getDesktop().isSupported(Desktop.Action.BROWSE)) {

                    // Abrimos el vídeo en el navegador
                    Desktop.getDesktop().browse(URI.create(enlace));

                } else {
                    mostrarMensaje("Vídeo", "No se puede abrir el navegador.");
                }

            } catch (IOException | SecurityException e) {
                mostrarMensaje("Error", "No se pudo abrir el vídeo.");
            }
        }

        @FXML
        private void abrirLogin() {

            // Abrimos la pantalla de inicio de sesión
            navigateToView("login");
        }

            // Volvemos a la pantalla principal
            @FXML
            private void abrirInicio() {
                navigateToView("inicio");
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
            if ("informes".equals(vista)) {
                InformesController controladorInformes = loader.getController();
                controladorInformes.setVentanaPrincipalController(this);
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
    private void mostrarInicio() {
        mostrarPaginaPublica(homeView, null);
    }

    public void cerrarSesion() {
        mostrarInicio();
    }

    @FXML
    private void abrirInformes() {
        navigateToView("informes");
    }

    private void mostrarInformacion(String opcion, String titulo, String... parrafos) {
        VBox seccion = new VBox(20);
        seccion.setAlignment(Pos.CENTER_LEFT);
        seccion.getStyleClass().add("seccion-informacion");
        Label encabezado = new Label(titulo);
        encabezado.getStyleClass().add("seccion-titulo");
        seccion.getChildren().add(encabezado);
        for (String parrafo : parrafos) {
            Label texto = new Label(parrafo);
            texto.setWrapText(true);
            texto.setMaxWidth(760);
            texto.getStyleClass().add("seccion-texto");
            seccion.getChildren().add(texto);
        }
        mostrarPaginaPublica(seccion, opcion);
    }

    private void mostrarPaginaPublica(Parent vista, String opcion) {
        mostrarBarraPortada(true);
        for (javafx.scene.Node nodo : barraPortada.getChildren()) {
            if (nodo instanceof Button) {
                Button boton = (Button) nodo;
                boton.getStyleClass().remove("pagina-activa");
                if (boton.getText().equals(opcion)) {
                    boton.getStyleClass().add("pagina-activa");
                }
            }
        }
        contentHost.getChildren().setAll(vista);
    }

    private void mostrarBarraPortada(boolean visible) {
        barraPortada.setVisible(visible);
        barraPortada.setManaged(visible);
    }

    private void mostrarMensaje(String titulo, String mensaje) {
        Alert alerta = new Alert(Alert.AlertType.INFORMATION);
        alerta.initOwner(contentHost.getScene().getWindow());
        alerta.setTitle(titulo);
        alerta.setHeaderText(null);
        alerta.setContentText(mensaje);
        alerta.showAndWait();
    }

    private void mostrarError(String mensaje, IOException excepcion) {
        Alert alerta = new Alert(Alert.AlertType.ERROR);
        alerta.initOwner(contentHost.getScene().getWindow());
        alerta.setTitle("No se pudo abrir la pantalla");
        alerta.setHeaderText(mensaje);
        alerta.setContentText(excepcion.getMessage());
        alerta.showAndWait();
    }
}
