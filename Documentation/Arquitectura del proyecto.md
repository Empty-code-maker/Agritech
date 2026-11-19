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

    public LocalDateTime obtenerFechaHora() {
        return fechaHora;
    }

    public double obtenerHumedadSuelo() {
        return humedadSuelo;
    }

    public double obtenerTemperatura() {
        return temperatura;
    }

    public double obtenerLuminosidad() {
        return luminosidad;
    }
}
```

```java
import java.time.LocalDateTime;

public final class EstadoRiego {
    private final boolean activo;
    private final LocalDateTime fechaHoraUltimoCambio;

    public EstadoRiego(boolean activo, LocalDateTime fechaHoraUltimoCambio) {
        this.activo = activo;
        this.fechaHoraUltimoCambio = fechaHoraUltimoCambio;
    }

    public boolean estaActivo() {
        return activo;
    }

    public LocalDateTime obtenerFechaHoraUltimoCambio() {
        return fechaHoraUltimoCambio;
    }
}
```
### Servicios

Cada servicio se ocupa de una función concreta:

```java
public class RiegoService {
    private final RiegoRepository repositorioRiego;

    public RiegoService(RiegoRepository repositorioRiego) {
        this.repositorioRiego = repositorioRiego;
    }

    public EstadoRiego alternarEstado() throws SQLException {
        EstadoRiego estadoActual = repositorioRiego.obtenerEstadoActual();
        EstadoRiego estadoActualizado = new EstadoRiego(
                !estadoActual.estaActivo(), LocalDateTime.now());
        repositorioRiego.guardarEstado(estadoActualizado);
        return estadoActualizado;
    }
}
```

```java
public class AlertasService {
    public String evaluar(LecturaAmbiental lectura) {
        if (lectura.obtenerHumedadSuelo() < 30) {
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
    @FXML private Label etiquetaEstado;
    @FXML private Label etiquetaAlerta;
    @FXML private Button botonAlternar;

    private final RiegoService servicioRiego;
    private final AlertasService servicioAlertas = new AlertasService();

    public PanelController(RiegoService servicioRiego) {
        this.servicioRiego = servicioRiego;
    }

    private void alternarRiego() {
        // El controlador delega el cambio al servicio y actualiza la etiqueta.
    }

    public void mostrarLectura(LecturaAmbiental lectura) {
        etiquetaAlerta.setText(servicioAlertas.evaluar(lectura));
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
src/main/java/com/agritech/
  AplicacionAgriTech.java
  controllers/RiegoController.java
  models/EstadoRiego.java
  services/RiegoService.java
  repositories/RiegoRepository.java
  repositories/sqlite/SQLiteRiegoRepository.java
  repositories/sqlite/ConexionSQLite.java
  utils/InicializadorBaseDatos.java

src/main/resources/
  fxml/riego.fxml
  css/main.css
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
