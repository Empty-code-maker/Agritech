# Inicio de sesión y navegación

El punto de entrada sigue siendo `AplicacionAgriTech`. Con Java 8 que incluya
JavaFX, se ejecuta desde la raíz del proyecto con:

```sh
mvn compile exec:java
```

La portada procede de la rama `feature/ventana-principal-menu` de Alexandru
(commit `505caf5`). Se adaptaron sus controladores y la exportación PDF a los
nombres en español y a JavaFX 8 de la versión principal. Los estilos de portada
están en `portada.css`; `main.css` conserva los de las pantallas de Manuel.

El flujo es:

1. Pulsar **Iniciar sesión** en la portada. Los botones **Probar gratis** y
   **Probar Demo Gratuita** también abren el mismo formulario.
2. Escribir un nombre y una contraseña. Por ahora se acepta cualquier valor
   que no esté vacío ni contenga únicamente espacios. No se comprueban cuentas,
   no se guarda la contraseña y no se crea ningún usuario en SQLite.
3. Entrar a la ventana principal. El menú lateral ofrece **Dashboard**,
   **Monitorización**, **Histórico** y **Mis plantas**, y la cabecera muestra el
   nombre de usuario y que el sistema es local. Dashboard es la vista inicial.
   Las tres primeras secciones cargan sus propios archivos FXML, todavía vacíos.
   **Mis plantas** abre el contenido de Manuel y muestra primero **Plantas y
   umbrales**; desde la pestaña **Riego** se accede al control del riego. Las
   plantas, las lecturas, las alertas y el estado del riego utilizan su
   persistencia SQLite existente. Cambiar de sección conserva los formularios y
   la selección de plantas, sin crear nuevos temporizadores.
4. **Informes** conserva la exportación inicial de Alexandru: un PDF con título,
   todavía sin las mediciones. Antes del login mantiene la barra de la portada;
   dentro de la sesión se abre en el área central del huerto y conserva la
   cabecera y el menú lateral. Se puede volver a Mis plantas desde ese menú.
5. **Cerrar sesión** regresa a la portada, descarta el nombre y detiene el
   temporizador de lecturas. Volver a entrar crea un panel nuevo y no duplica
   simulaciones. Cerrar la aplicación también detiene el temporizador.

El formulario está en `fxml/login.fxml` y lo gestiona `AccesoController`.
`VentanaPrincipalController` coordina los cambios de pantalla; `PanelController`
reutiliza las vistas y los servicios de Riego y Plantas.

La barra superior se define una sola vez en `main.fxml`, fuera de `contentHost`.
Se mantiene al navegar por **Características**, **Cómo funciona**, **Precios**,
**FAQ**, **Informes** y el formulario de acceso. Las páginas cambian únicamente
el contenido central. Pulsar la marca **AgriTech**, situada a la derecha,
devuelve a la portada; no se necesitan botones independientes para volver.
Salir del formulario desde la barra borra la contraseña introducida.

El botón **FAQ** de la portada abre `fxml/faq.fxml`: una página desplazable con
15 preguntas agrupadas en primeros pasos, plantas y riego, lecturas y avisos,
y datos y sesión. `FaqController` despliega la primera respuesta; la navegación
pertenece a la barra común. Las preguntas y respuestas se editan directamente
en FXML.

Para incorporar autenticación real, hay que sustituir la validación provisional
de `AccesoController` por la comprobación de credenciales y la gestión de sesión
que implemente el equipo.
