package com.agritech.controllers;

import com.agritech.services.AlertasService;
import com.agritech.services.MedidasService;
import com.agritech.services.PlantasService;
import com.agritech.services.RiegoService;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import java.io.IOException;

/** Mantiene el menú de la portada mientras cambia el contenido de las páginas públicas. */
public class VentanaPrincipalController {
    private final PlantasService servicioPlantas;
    private final MedidasService servicioMedidas;
    private final AlertasService servicioAlertas;
    private final RiegoService servicioRiego;
    private PanelController controladorPanel;
    private AccesoController controladorAcceso;
    private Parent panel;
    @FXML private HBox barraPortada;
    @FXML private StackPane contentHost;
    @FXML private BorderPane homeView;

    public VentanaPrincipalController(PlantasService servicioPlantas,
            MedidasService servicioMedidas, AlertasService servicioAlertas,
            RiegoService servicioRiego) {
        this.servicioPlantas = servicioPlantas;
        this.servicioMedidas = servicioMedidas;
        this.servicioAlertas = servicioAlertas;
        this.servicioRiego = servicioRiego;
    }

    @FXML
    private void mostrarCaracteristicas() {
        mostrarInformacion("Características", "Características de AgriTech",
                "Consulta la humedad simulada, activa o desactiva el riego "
                        + "y configura las plantas y sus umbrales mínimos.",
                "Esta versión utiliza datos de demostración.");
    }

    @FXML
    private void mostrarFuncionamiento() {
        mostrarInformacion("Cómo funciona", "Cómo funciona AgriTech",
                "1. Inicia sesión con tu nombre y contraseña para abrir la ventana principal.",
                "2. Selecciona Mis plantas en el menú lateral para añadir plantas "
                        + "y configurar su humedad mínima.",
                "3. Abre la pestaña Riego para consultar las lecturas simuladas, "
                        + "ver los avisos y activar o desactivar el riego simulado.");
    }

    @FXML
    private void mostrarPrecios() {
        mostrarInformacion("Precios", "Precios",
                "Todavía no hay planes ni precios definidos para AgriTech.",
                "Puedes explorar las funciones disponibles desde Iniciar sesión.");
    }

    @FXML
    private void mostrarFaq() {
        try {
            FXMLLoader cargador = new FXMLLoader(getClass().getResource("/fxml/faq.fxml"));
            cargador.setController(new FaqController());
            Parent preguntas = cargador.load();
            mostrarPaginaPublica(preguntas, "FAQ");
        } catch (IOException excepcion) {
            mostrarError("No se pudieron abrir las preguntas frecuentes.", excepcion);
        }
    }

    @FXML
    private void verVideo() {
        Alert alerta = new Alert(Alert.AlertType.INFORMATION);
        alerta.initOwner(contentHost.getScene().getWindow());
        alerta.setTitle("Vídeo");
        alerta.setHeaderText(null);
        alerta.setContentText("Todavía no hay un vídeo asociado a la demo.");
        alerta.showAndWait();
    }

    @FXML
    private void abrirLogin() {
        try {
            FXMLLoader cargador = new FXMLLoader(getClass().getResource("/fxml/login.fxml"));
            AccesoController nuevoAcceso = new AccesoController(this::abrirPanel);
            cargador.setController(nuevoAcceso);
            Parent acceso = cargador.load();
            mostrarPaginaPublica(acceso, "Iniciar sesión");
            controladorAcceso = nuevoAcceso;
            acceso.lookup("#campoNombre").requestFocus();
        } catch (IOException excepcion) {
            mostrarError("No se pudo abrir el inicio de sesión.", excepcion);
        }
    }

    private void abrirPanel(String nombre) {
        PanelController nuevoControlador = new PanelController(nombre, servicioPlantas,
                servicioMedidas, servicioAlertas, servicioRiego,
                this::cerrarSesion, this::abrirInformes);
        try {
            FXMLLoader cargador = new FXMLLoader(getClass().getResource("/fxml/panel.fxml"));
            cargador.setController(nuevoControlador);
            Parent nuevoPanel = cargador.load();
            nuevoControlador.inicializarPantalla();
            controladorPanel = nuevoControlador;
            panel = nuevoPanel;
            limpiarAcceso();
            mostrarBarraPortada(false);
            contentHost.getChildren().setAll(panel);
        } catch (IOException excepcion) {
            nuevoControlador.detenerSimulacion();
            mostrarError("No se pudo abrir el panel del huerto.", excepcion);
        }
    }

    @FXML
    private void abrirInformes() {
        try {
            FXMLLoader cargador = new FXMLLoader(getClass().getResource("/fxml/informes.fxml"));
            cargador.setController(new InformesController(this::volverDesdeInformes));
            Parent informes = cargador.load();
            if (controladorPanel == null) {
                mostrarPaginaPublica(informes, "PDF");
            } else {
                // Dentro de la sesión se mantienen la cabecera y el menú lateral del huerto.
                controladorPanel.mostrarInformes(informes);
            }
        } catch (IOException excepcion) {
            mostrarError("No se pudo abrir Informes.", excepcion);
        }
    }

    private void volverDesdeInformes() {
        if (controladorPanel == null) {
            mostrarInicio();
        } else {
            controladorPanel.volverDesdeInformes();
        }
    }

    @FXML
    private void mostrarInicio() {
        mostrarPaginaPublica(homeView, null);
    }

    public void cerrarSesion() {
        if (controladorPanel != null) {
            controladorPanel.detenerSimulacion();
            controladorPanel = null;
            panel = null;
        }
        mostrarInicio();
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
        limpiarAcceso();
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

    private void limpiarAcceso() {
        if (controladorAcceso != null) {
            controladorAcceso.limpiarContrasena();
            controladorAcceso = null;
        }
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
