# Instalación de Java 8 con JavaFX y Maven en Windows

Esta guía describe la instalación de Azul Zulu JDK FX 8 de 64 bits y Apache Maven en Windows 10/11. Maven 3.10.0 requiere JDK 8 o superior y es compatible con este proyecto configurado para Java 8.

## 1. Instalar Azul Zulu JDK FX 8

Descarga Azul Zulu desde el [selector oficial de Azul](https://www.azul.com/downloads/?version=java-8-lts&os=windows&architecture=x86-64-bit&package=jdk-fx#zulu). Selecciona estos valores:

- **Java Version:** Java 8 (LTS)
- **Operating System:** Windows
- **Architecture:** x86 64-bit
- **Java Package:** JDK FX
- **Formato recomendado:** MSI

> El paquete debe decir **JDK FX**. El paquete **JDK** normal incluye el compilador, pero no JavaFX. La descarga de Azul mostrada actualmente corresponde a Zulu 8u504-b01; consulta las notas de versión de Azul para comprobar la versión de JavaFX incluida.

### Instalación con MSI

1. Ejecuta el archivo `.msi` descargado. Si Windows pide permisos de administrador, acéptalos.
2. Sigue el asistente y conserva la carpeta propuesta. La ruta habitual es `C:\Program Files\Zulu\zulu-8\`.
3. En las opciones del instalador, activa **Add to PATH** y **Set JAVA_HOME variable**.
4. Finaliza la instalación.
5. Abre una ventana nueva de CMD y comprueba Java y el compilador:

   ```bat
   java -version
   javac -version
   echo %JAVA_HOME%
   ```

   Ambos comandos de versión deben mostrar Java 8. `JAVA_HOME` debe apuntar a la carpeta del JDK, por ejemplo `C:\Program Files\Zulu\zulu-8`, sin `\bin` ni `\jre` al final.

### Si descargaste ZIP en lugar de MSI

1. Extrae el ZIP en una carpeta estable, por ejemplo `C:\Program Files\Zulu\zulu-8\`.
2. Configura `JAVA_HOME` con esa carpeta y añade `%JAVA_HOME%\bin` al principio de `Path` siguiendo la sección 3.
3. Abre una consola nueva y ejecuta los comandos de comprobación anteriores.

## 2. Instalar Maven

Si Maven ya está instalado y `mvn -version` funciona, puedes saltar esta sección. Para instalarlo desde cero:

1. Descarga el archivo **Binary zip archive** desde la [página oficial de descargas de Maven](https://maven.apache.org/download.cgi). Maven 3.10.0 requiere JDK 8 o superior.
2. Extrae el contenido en `C:\Maven\`. La carpeta resultante podría ser `C:\Maven\apache-maven-3.10.0\`.
3. Añade la siguiente entrada a `Path`:

   ```text
   C:\Maven\apache-maven-3.10.0\bin
   ```

   Ajusta la ruta si instalaste otra versión.

4. Abre una ventana nueva de CMD y ejecuta:

   ```bat
   mvn -version
   ```

   La salida debe indicar Maven 3.x y que está usando Java 8 de Azul. `MAVEN_HOME` no es obligatorio si `Path` contiene la carpeta `bin` de Maven.

## 3. Configurar `JAVA_HOME` y `Path` en Windows

El instalador MSI puede crear estas variables automáticamente. Si no lo hizo:

1. Abre **Inicio**, busca **Editar las variables de entorno del sistema** y selecciona **Variables de entorno…**.
2. En **Variables del sistema**, crea o edita `JAVA_HOME` con la ruta del JDK, por ejemplo:

   ```text
   C:\Program Files\Zulu\zulu-8
   ```

3. Edita la variable `Path` y añade al principio:

   ```text
   %JAVA_HOME%\bin
   ```

4. Si instalaste Maven, añade también la ruta a su carpeta `bin`, por ejemplo:

   ```text
   C:\Maven\apache-maven-3.10.0\bin
   ```

5. Acepta todos los cuadros de diálogo y abre una consola nueva. Los cambios de variables de entorno no se aplican a las consolas que ya estaban abiertas.

## 4. Comprobación final

En una ventana nueva de CMD, ejecuta:

```bat
where java
where javac
java -version
javac -version
mvn -version
echo %JAVA_HOME%
```

Comprueba que `where java` y `where javac` muestran primero el directorio `bin` de Azul, que Java y `javac` corresponden a Java 8 y que `mvn -version` indica Azul como proveedor y una ruta dentro de su instalación. Si aparece primero una ruta antigua de Oracle, corrige el orden de `Path`.

Para comprobar que JavaFX 8 está incluido en la instalación, verifica que existe este archivo:

```bat
dir "%JAVA_HOME%\jre\lib\ext\jfxrt.jar"
```

También se puede consultar la [documentación de instalación de Azul para Windows](https://docs.azul.com/core/install/windows) y las [notas de versión de Azul](https://docs.azul.com/core/release-notes-full).

## 5. Desinstalar Oracle Java (opcional)

Desinstala Oracle solo después de que Zulu y Maven pasen las comprobaciones anteriores.

1. Abre **Configuración → Aplicaciones → Aplicaciones instaladas**.
2. Busca **Java 8 Update 381 (32-bit)** o la entrada equivalente de Oracle.
3. Selecciona **Desinstalar** y confirma.
4. Si aparece **Java Auto Updater** de Oracle, también puedes desinstalarlo.
5. No borres manualmente las carpetas de Java. Revisa `Path` y elimina únicamente entradas antiguas de Oracle, como `C:\Program Files (x86)\Common Files\Oracle\Java\javapath`, si aún están presentes.
6. Abre otra consola nueva y repite la comprobación final.

## Enlaces

- [Descargas de Azul Zulu JDK FX 8 para Windows x64](https://www.azul.com/downloads/?version=java-8-lts&os=windows&architecture=x86-64-bit&package=jdk-fx#zulu)
- [Instalación de Azul Zulu en Windows](https://docs.azul.com/core/install/windows)
- [Descargas oficiales de Apache Maven](https://maven.apache.org/download.cgi)
- [Instalación oficial de Maven](https://maven.apache.org/install.html)
