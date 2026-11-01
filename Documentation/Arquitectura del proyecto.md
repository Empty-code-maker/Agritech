## MVC con JavaFX y servicios

En JavaFX, una pantalla suele definirse con FXML y controlarse con una clase controladora. Puedes añadir servicios para separar las funciones de la aplicación y mantener el controlador más pequeño.

Un matiz importante: **añadir una función nueva normalmente requiere conectar esa función con la interfaz**. Si la función se activa desde un botón nuevo, por ejemplo, habrá que añadir el botón y enlazar su acción con el servicio. La idea es que la lógica de cada función quede en su servicio y que el controlador solo la coordine.

### Las partes

- **Vista (FXML):** muestra lecturas, alertas y controles como el botón de riego.
- **Controlador (JavaFX):** recibe eventos de la vista, llama a los servicios y actualiza la pantalla.
- **Modelo:** representa los datos del sistema, por ejemplo una lectura ambiental o el estado del riego.
- **Servicios:** implementan funciones separadas, como actualizar lecturas, exportarlas o alternar el riego.

## ¿Como se vería y usa cada parte?

### Modelos

Los modelos representan los datos y conceptos principales del sistema. Por ejemplo, una lectura de los sensores puede modelarse así:

```java
import java.time.LocalDateTime;

public class LecturaAmbiental {
    private final LocalDateTime fechaHora;
    private final double humedadSuelo;
    private final double temperatura;
    private final double luminosidad;

    public LecturaAmbiental(
            LocalDateTime fechaHora,
            double humedadSuelo,
            double temperatura,
            double luminosidad
    ) {
        this.fechaHora = fechaHora;
        this.humedadSuelo = humedadSuelo;
        this.temperatura = temperatura;
        this.luminosidad = luminosidad;
    }

    public LocalDateTime getFechaHora() {
        return fechaHora;
    }

    public double getHumedadSuelo() {
        return humedadSuelo;
    }

    public double getTemperatura() {
        return temperatura;
    }

    public double getLuminosidad() {
        return luminosidad;
    }
}
```

El estado del riego también puede ser un modelo:

```java
import java.time.LocalDateTime;

public class EstadoRiego {
    private boolean activo;
    private LocalDateTime ultimaActivacion;

    public boolean isActivo() {
        return activo;
    }

    public LocalDateTime getUltimaActivacion() {
        return ultimaActivacion;
    }

    public void activar() {
        activo = true;
        ultimaActivacion = LocalDateTime.now();
    }

    public void desactivar() {
        activo = false;
    }
}
```
### Servicios

Cada servicio se ocupa de una función concreta:

```java
public class RiegoService {
    public void alternarRiego() {
        // Solicita al sistema o dispositivo cambiar el estado del riego.
    }
}
```

```java
public class AlertasService {
    public String evaluar(LecturaAmbiental lectura) {
        if (lectura.humedadSuelo() < 30) {
            return "La humedad del suelo es baja";
        }
        return "";
    }
}
```

### Controlador

El controlador conecta los elementos JavaFX con esos servicios:

```java
public class PanelController {
    @FXML private Label estadoRiegoLabel;
    @FXML private Label alertaLabel;
    @FXML private Button riegoButton;

    private final RiegoService riegoService = new RiegoService();
    private final AlertasService alertasService = new AlertasService();

    @FXML
    private void initialize() {
        riegoButton.setOnAction(event -> {
            riegoService.alternarRiego();
            estadoRiegoLabel.setText("Se solicitó cambiar el estado del riego");
        });
    }

    public void mostrarLectura(LecturaAmbiental lectura) {
        alertaLabel.setText(alertasService.evaluar(lectura));
    }
}
```
### Vista
La vista FXML puede enlazar el botón con un método del controlador:

```xml
<Button text="Alternar riego" onAction="#alternarRiego"/>
```

En ese caso, el controlador define `alternarRiego()` y desde ese método llama a `RiegoService`.

### ¿Como se vería en los ficheros? 

```text
src/main/java/
  model/
    LecturaAmbiental.java
    EstadoRiego.java
  service/
    LecturasService.java
    RiegoService.java
    ExportacionService.java
    AlertasService.java
  controller/
    PanelController.java

src/main/resources/
  views/
    panel.fxml
```

## ¿Cómo se añade una función?

Supongamos que ya existen cinco funciones: leer sensores, alternar riego, evaluar alertas, exportar datos y simular el riego. Cada una puede tener su propio servicio.

Al añadir una sexta función, por ejemplo **programar horarios**, has de crear un servicio:

```java
public class ProgramacionRiegoService {
    public void programar(String horario) {
        // Guarda o aplica el horario de riego.
    }
}
```

Después hay que hacer que la aplicación pueda invocarla. Si se activa desde una pantalla nueva, también se añade el controlador correspondiente que llamará a este servicio. El controlador no debería contener los detalles de cómo se guarda o ejecuta la programación.

## Flujo completo

Datos -> Interfaz

Cuando la app recibe una nueva lectura de humedad y evalua si emitir una alerta:

```text
Sensor o fuente de datos
        ↓
LecturasService obtiene la lectura
        ↓
PanelController recibe la lectura
        ↓
AlertasService evalúa la lectura
        ↓
PanelController recibe el resultado
        ↓
Vista JavaFX actualiza las etiquetas
```

Cuando la persona activa el riego:

Interfaz -> Datos

```text
Botón en FXML
   ↓
PanelController recibe el evento
   ↓
RiegoService ejecuta la operación
   ↓
PanelController actualiza la vista
```

En resumen, ** MVC separa vista, controlador y modelo; los servicios agrupan las funciones**.
Mantén cada servicio enfocado en una responsabilidad y evita que el controlador contenga la lógica detallada de todas las funciones.