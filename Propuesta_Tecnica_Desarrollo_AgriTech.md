## AGRITECH

## Propuesta técnica de arquitectura y plan de desarrollo

Aplicación de escritorio · Java · JavaFX

Proyecto de Ingeniería

Documento de trabajo para el equipo de desarrollo

## PROPÓSITO

Definir una propuesta compartida para iniciar AgriTech Desktop: tecnologías, arquitectura, módulos, modelo de datos y fases de trabajo. Es una base de diseño que el equipo deberá revisar y aprobar.


## Índice

- 1. Decisión tecnológica

- 2. Alcance de la primera versión

- 3. Arquitectura de funcionamiento

- 4. Pantallas y navegación

- 5. Estructura de carpetas y paquetes

- 6. Modelo de datos

- 7. Simulación y persistencia

- 8. Plan de desarrollo

- 9. Dependencias Maven

- 10. Relación con la arquitectura global

- 11. Trabajo colaborativo y Git

- 12. Riesgos, decisiones pendientes y validación

## Objetivo del documento

Este documento recoge la propuesta técnica para iniciar el desarrollo de la primera aplicación de AgriTech. El equipo ha decidido construir una aplicación de escritorio, utilizar mediciones simuladas como fuente inicial y dejar abierta la decisión definitiva sobre el almacenamiento. Como recomendación, se propone SQLite para disponer de persistencia local sin administrar un servidor.

La aplicación permitirá iniciar sesión, consultar el estado del cultivo, visualizar mediciones y gráficas, revisar históricos, gestionar plantas y alertas y generar informes. La implementación se realizará de forma incremental, validando cada fase antes de avanzar.

## 1. Decisión tecnológica

La primera versión será una aplicación de escritorio desarrollada con Java y JavaFX. No se incorporará inicialmente un backend web independiente ni se implementará la comunicación real con sensores.

| Elemento | Propuesta | Justificación |
| --- | --- | --- |
| Tipo de aplicación Lenguaje | Escritorio Java 21 LTS | Se ajusta al alcance inmediato acordado. Entorno estable para el desarrollo académico. |
| Interfaz Dependencias | JavaFX + FXML + CSS Maven | Separa estructura visual, controladores y estilos. Facilita configurar y compartir el proyecto. |
| Fuente de datos Persistencia recomendada | Simulador Java SQLite | Permite desarrollar sin hardware conectado. Almacenamiento local sin servidor. |
| Gráficas Informes | JavaFX Charts CSV primero; PDF después | Representación de series temporales. Prioriza una exportación sencilla y verificable. |
| Pruebas | JUnit 5 | Validación de lógica y servicios. |

## DECISIÓN SOBRE ALMACENAMIENTO

Se recomienda SQLite desde una fase temprana. La memoria Java pierde los datos al cerrar; CSV/JSON son menos cómodos para consultas históricas y relaciones. SQLite conserva usuarios, plantas, mediciones y alertas en un archivo local.


## 1.1 Alternativas consideradas

| Alternativa | Ventajas | Limitaciones |
| --- | --- | --- |
| Memoria Java CSV / JSON | Muy sencilla para prototipos. Fácil de generar e intercambiar. | Los datos se pierden al cerrar. Menos cómodo para consultas y relaciones. |
| SQLite MySQL | Persistencia local y consultas SQL. Adecuado para escenarios multiusuario. | Requiere definir tablas y repositorios. Necesita servidor y configuración adicional. |

## 2. Alcance de la primera versión

El alcance actual se centra en construir una aplicación de escritorio que permita visualizar y gestionar información ambiental de un huerto. Los datos procederán inicialmente de un simulador, no de sensores físicos.

## 2.1 Funcionalidades previstas

- Inicio y cierre de sesión, con validación de credenciales.

- Dashboard con resumen del estado del cultivo.

- Visualización de temperatura, humedad ambiental, humedad del suelo y luminosidad.

- Gráficas de evolución y consulta de mediciones históricas.

- Gestión o selección de especies vegetales.

- Comparación de mediciones con rangos orientativos configurados por planta.

- Generación de alertas cuando se superen o incumplan umbrales.

- Generación y exportación de informes, inicialmente CSV y posteriormente PDF.

## 2.2 Fuera del alcance inicial

- Conexión operativa con ESP32 y sensores reales.

- Recepción de telemetría real mediante MQTT.

- Despliegue de FastAPI e integración con InfluxDB.

- Acceso remoto a través de Internet.

- Inteligencia artificial avanzada y aprendizaje automático predictivo.

- Diagnóstico de enfermedades mediante visión artificial.

- Automatización física del riego y control de actuadores reales.

## CRITERIO DE ALCANCE

La primera versión debe demostrar que la aplicación puede gestionar, presentar y exportar mediciones de forma coherente. No debe presentarse como un sistema IoT conectado ni como un sistema que garantice la salud de las plantas.

## 3. Arquitectura de funcionamiento

Se propone una arquitectura modular con separación de responsabilidades. La interfaz no accederá directamente a SQLite ni contendrá la lógica de negocio. Los controladores solicitarán operaciones a los servicios y estos utilizarán repositorios para consultar o persistir información.

| Capa | Responsabilidad | Ejemplos |
| --- | --- | --- |
| Presentación | Mostrar información y recoger acciones. | Vistas FXML y controladores JavaFX. |


## AGRITECH | PROPUESTA TÉCNICA DE DESARROLLO

| Servicios | Aplicar reglas y coordinar operaciones. | Autenticación, mediciones, alertas e informes. |
| --- | --- | --- |
| Repositorios Persistencia | Definir operaciones de acceso a datos. Implementar almacenamiento. | Guardar mediciones y consultar plantas. SQLite y tablas relacionales. |
| Simulación | Producir mediciones ficticias. | SensorSimulator. |

## 3.1 Flujo de datos

```
SensorSimulator
↓
MeasurementService ──→ AlertService
↓
MeasurementRepository
↓
SQLite
↑
Controllers / JavaFX Views
```

El simulador generará una medición y la entregará a un servicio. Este podrá validar los valores, solicitar la generación de alertas y guardar el registro mediante el repositorio. Las vistas consultarán los servicios para mostrar los datos.

## PRINCIPIO DE DISEÑO

La interfaz no debe depender de la implementación concreta del simulador o de SQLite. Se recomienda definir interfaces de repositorio y una abstracción para la fuente de mediciones. Así será más sencillo sustituir el simulador por una API o MQTT.

## 4. Organización de pantallas y navegación

Se recomienda una pantalla de login independiente y una ventana principal con navegación lateral. Las secciones funcionales se cargarán en un área central, evitando abrir una ventana distinta para cada módulo.

| Pantalla / sección | Contenido principal |
| --- | --- |
| Login Dashboard | Usuario, contraseña, validación y acceso. Resumen de indicadores, estado general y últimas alertas. |
| Monitorización Histórico | Valores ambientales y gráficas de actualización periódica. Consulta y filtrado de mediciones por fecha y variable. |
| Mis plantas Alertas | Especies y configuración de rangos de referencia. Avisos, severidad, fecha y estado de resolución. |
| Informes | Selección de periodo, variables y exportación. |

## 4.1 Esquema de navegación

```
LOGIN
↓ autenticación correcta
VENTANA PRINCIPAL
├── Dashboard
├── Monitorización
├── Histórico
├── Mis plantas
```


La ventana principal incluirá el menú lateral, el contenido de la vista seleccionada y una cabecera opcional con el usuario activo y el estado del sistema.

## 5. Estructura de carpetas y paquetes

La siguiente estructura es una propuesta. No es necesario crear todos los archivos el primer día; se incorporarán conforme avance cada fase.

*Proyecto de Ingeniería · Documento de trabajo del equipo | Página 5*


## 5.1 Responsabilidad de los paquetes

| Paquete | Responsabilidad |
| --- | --- |
| controllers models | Gestionar eventos de interfaz y coordinar llamadas a servicios. Representar entidades y datos del dominio. |
| services repositories | Contener lógica de negocio y coordinar operaciones. Definir contratos de acceso y consulta de datos. |
| repositories/sqlite simulation | Implementar los contratos utilizando SQLite. Generar mediciones ficticias y escenarios de prueba. |
| utils resources/fxml | Gestionar sesión, inicialización y utilidades. Definir la estructura visual de cada vista. |
| resources/css | Definir colores, tipografía, espaciados y estilos. |

## 5.2 Clases principales

| Clase | Responsabilidad |
| --- | --- |
| Main LoginController | Punto de entrada de JavaFX. Validar formulario y solicitar autenticación. |
| MainController Measurement | Gestionar navegación y contenedor principal. Representar una medición ambiental. |
| MeasurementService SensorSimulator | Validar, consultar y coordinar el registro de mediciones. Generar valores de sensores de demostración. |
| MeasurementRepository SQLiteMeasurementRepository | Definir operaciones para guardar y consultar mediciones. Implementar operaciones de mediciones con SQLite. |
| AlertService ReportService | Evaluar umbrales y generar avisos. Preparar y exportar datos de informes. |

## 6. Modelo de datos

Se propone un modelo inicial con cinco entidades. Los campos podrán ajustarse al concretar las reglas de negocio y el diseño definitivo de la base de datos.

| Entidad | Campos principales | Uso |
| --- | --- | --- |

*Proyecto de Ingeniería · Documento de trabajo del equipo | Página 6*


## AGRITECH | PROPUESTA TÉCNICA DE DESARROLLO

| User Measurement | id, username, passwordHash, email id, temperature, soilHumidity, airHumidity, luminosity, timestamp | Identidad y autenticación. Registro de cada lectura. |
| --- | --- | --- |
| Plant PlantThreshold | id, name, description, imagePath id, plantId, minSoilHumidity, maxSoilHumidity, minTemperature, maxTemperature | Catálogo de especies. Rangos configurables por especie. |
| Alert | id, type, message, severity, timestamp, resolved Avisos generados por reglas. |   |

## 6.1 Consideraciones del modelo

- Las mediciones deben incluir fecha y hora para permitir consultas históricas y gráficas temporales.

- Los umbrales deben estar asociados a una planta y ser configurables.

- Las alertas deben conservar el momento en que se generaron y su estado.

- Las contraseñas deben almacenarse mediante un hash seguro, nunca en texto plano.

- Si se incorpora más de un huerto o dispositivo, convendrá añadir identificadores de huerto y dispositivo a las mediciones.

## PENDIENTE DE DEFINICIÓN

La información disponible no determina si la aplicación gestionará un único huerto o varios, ni si habrá varios usuarios con permisos diferentes. Conviene decidirlo antes de cerrar el esquema SQL.

## 7. Simulación de sensores y persistencia

## 7.1 Simulador

SensorSimulator generará mediciones ficticias para probar la aplicación sin hardware. Se recomienda evitar valores totalmente aleatorios en cada ciclo: las variables deben evolucionar progresivamente para que las gráficas representen escenarios comprensibles.

| Escenario | Comportamiento simulado | Validación |
| --- | --- | --- |
| Normal Humedad baja | Valores relativamente estables dentro de rangos. Descenso gradual de humedad del suelo. | Indicadores y gráficas habituales. Alertas y recomendaciones de revisión del riego. |
| Temperatura elevada Luminosidad baja | Aumento por encima del umbral configurado. Lecturas reducidas durante un periodo. | Avisos por temperatura. Comparación con referencias. |

Como ejemplo de demostración, se puede configurar una actualización cada cinco segundos. Es una propuesta de prueba, no un requisito definitivo.

## 7.2 Persistencia

Cada medición generada se enviará a MeasurementService, que validará los datos y solicitará su almacenamiento a MeasurementRepository. La implementación SQLite conservará los registros para consultarlos después de cerrar y volver a abrir la aplicación.

```
SensorSimulator → MeasurementService → MeasurementRepository → SQLite
└→ AlertService
```


## 7.3 Sustitución futura del simulador

La fuente de datos debe quedar desacoplada de las vistas. En una evolución posterior, el simulador podrá sustituirse por un componente que consuma una API o reciba información del backend IoT. Los servicios y repositorios se adaptarán según las responsabilidades definitivas.

## 8. Plan de desarrollo por fases

| Fase | Trabajo previsto | Resultado verificable |
| --- | --- | --- |
| 1. Preparación 2. Interfaz y navegación | Crear Maven, configurar JavaFX, paquetes y Main. Login, ventana principal, menú y vistas iniciales. | Ventana básica ejecutable. Navegación entre secciones. |
| 3. Modelos y persistencia 4. Autenticación | Entidades, SQLite, tablas, repositorios y datos iniciales. Validar credenciales y gestionar sesión. | Datos guardados y recuperables. Login funcional. |
| 5. Simulación 6. Dashboard y gráficas | Generar escenarios, validar y guardar mediciones. Indicadores, gráficas y actualización periódica. | Mediciones periódicas almacenadas. Panel conectado al simulador. |
| 7. Histórico y plantas 8. Alertas | Filtros temporales, catálogo y rangos. Evaluar reglas y mostrar avisos. | Consulta histórica y configuración. Alertas ante escenarios de prueba. |
| 9. Informes 10. Validación | Exportación CSV y, si hay tiempo, PDF. Pruebas, revisión visual y correcciones. | Archivo exportado. Versión integrada y demostrable. |

## 8.1 Orden recomendado

La secuencia prioriza primero una aplicación ejecutable y navegable, después la persistencia y el flujo de datos, y finalmente las funcionalidades de análisis y exportación. No se recomienda desarrollar gráficas con datos fijos antes de definir Measurement y la consulta de mediciones.

## 9. Dependencias y configuración Maven

| Dependencia / herramienta | Uso | Momento |
| --- | --- | --- |
| JavaFX Controls JavaFX FXML | Controles de interfaz. Carga de vistas FXML. | Inicio. Inicio. |
| SQLite JDBC JUnit 5 | Conexión con base de datos local. Pruebas unitarias. | Persistencia. Desde servicios y lógica. |
| OpenPDF u otra librería PDF Scene Builder (opcional) | Exportación a PDF. Edición visual de FXML. | Informes. Diseño. |
| Git | Control de versiones y colaboración. | Desde el primer día. |


JavaFX Charts permite desarrollar gráficas básicas sin una librería externa. La versión concreta de JavaFX deberá ser compatible con el JDK elegido y quedar fijada en el pom.xml para todo el equipo.

## 10. Relación con la arquitectura global de AgriTech

La aplicación de escritorio es una primera implementación de la capa de presentación y gestión de información. No reemplaza la arquitectura global descrita en la memoria, que contempla ESP32, Wi-Fi, MQTT, Mosquitto, FastAPI, InfluxDB y Grafana.

| Arquitectura global | Primera aplicación de escritorio |
| --- | --- |
| ESP32 y sensores físicos Wi-Fi y MQTT / Mosquitto | SensorSimulator genera mediciones ficticias. No se implementan en la primera versión. |
| FastAPI InfluxDB | La lógica inicial reside en servicios Java. SQLite se propone como almacenamiento local. |
| Grafana / visualización | La visualización se realiza con JavaFX. |

## EVOLUCIÓN FUTURA

Cuando exista una fuente real de datos, se podrá desarrollar un adaptador para consumir una API o integrar la comunicación prevista en la arquitectura IoT. La evolución deberá evitar acoplar los controladores JavaFX a la tecnología concreta de adquisición.

## 11. Organización del equipo y control de versiones

Se recomienda utilizar un repositorio Git compartido desde el inicio. Cada integrante trabajará en una rama asociada a una funcionalidad y los cambios se integrarán mediante revisiones.

| Rama de ejemplo | Ámbito |
| --- | --- |
| main | Versión estable e integrada. |
| feature/login feature/dashboard | Autenticación y sesión. Dashboard e indicadores. |
| feature/simulator feature/database | Generación de mediciones. SQLite y repositorios. |
| feature/reports | Exportación de informes. |

## 11.1 Buenas prácticas

- Acordar nombres de paquetes, clases, atributos y convenciones antes de repartir tareas.

- Evitar que dos integrantes modifiquen simultáneamente los mismos archivos sin coordinación.

- Mantener el proyecto compilable en la rama de integración.

- Realizar commits pequeños y descriptivos.

- Documentar decisiones técnicas y cambios relevantes en las actas.

- Probar cada funcionalidad antes de fusionarla.

- No subir contraseñas, configuración privada ni datos personales al repositorio.


## 11.2 Reparto de trabajo

El reparto concreto debe acordarse según la disponibilidad y conocimientos de los integrantes. Conviene dividir el trabajo por módulos con interfaces claras y asignar una persona responsable de integración y revisión técnica. La distribución no debe impedir que todos conozcan el flujo general.

## 12. Riesgos, decisiones pendientes y validación

## 12.1 Riesgos técnicos iniciales

| Riesgo | Consecuencia | Medida preventiva |
| --- | --- | --- |
| Acoplamiento entre interfaz y datos Simulación poco realista | Cambios en la fuente obligan a modificar vistas. Servicios y repositorios separados. Gráficas y alertas poco útiles. | Escenarios controlados y progresivos. |
| Alcance excesivo Conflictos de integración | No se termina una versión integrada. Errores al combinar ramas. | Priorizar login, navegación, persistencia y dashboard. Git, commits pequeños y revisión. |
| Reglas de riego simplificadas Pérdida de datos | Recomendaciones potencialmente engañosas. Histórico incompleto. | Rangos orientativos y límites explícitos. Validación y pruebas de persistencia. |

## 12.2 Decisiones que debe confirmar el equipo

- Aprobar SQLite como almacenamiento local de la primera versión.

- Determinar si se gestionará un único huerto o varios.

- Definir si habrá un único usuario de demostración o varios perfiles.

- Acordar las especies iniciales y las fuentes de sus rangos orientativos.

- Definir el intervalo de simulación y los escenarios de prueba.

- Concretar qué campos y formatos incluirán los informes.

- Determinar qué funcionalidades son imprescindibles para la entrega y cuáles quedan como ampliaciones.

## 12.3 Criterios mínimos de validación

| Área | Criterio de aceptación propuesto |
| --- | --- |
| Inicio de sesión Navegación | Credenciales válidas permiten acceder; las inválidas muestran un error. Todas las secciones previstas se abren desde la ventana principal. |
| Simulación Persistencia | Se generan mediciones fechadas y dentro de límites configurados. Las mediciones siguen disponibles tras reiniciar la aplicación. |
| Dashboard Gráficas | Los indicadores reflejan la última medición guardada. Las series se construyen a partir de mediciones históricas. |
| Alertas Informes | Un escenario fuera de umbral genera el aviso correspondiente. El archivo exportado contiene las mediciones del filtro seleccionado. |


## Conclusión y siguiente paso

La propuesta establece una base técnica modular para desarrollar AgriTech Desktop con Java y JavaFX. El uso de un simulador permite avanzar sin depender del hardware, mientras que SQLite aporta persistencia local y facilita probar la consulta histórica. La separación entre presentación, servicios y repositorios prepara la aplicación para evolucionar hacia una fuente de datos real.

## PRIMER HITO DEL EQUIPO

Crear el proyecto Maven, configurar JavaFX, establecer la estructura inicial de paquetes y ejecutar una primera ventana. Una vez validado este hito, se comenzará el diseño del login y la navegación principal.

AGRITECH · Agricultura inteligente, cultivos saludables
