# Agritech
Agritech proyect for UEM

# Reglas del repositorio para la contribución del proyecto

La rama `main` contiene la versión principal e integrada del proyecto. Para protegerla, los cambios deben desarrollarse en otras ramas y llegar a `main` mediante una *pull request* (PR), siguiendo las reglas descritas aquí.

## Reglas de protección de `main`

### Restringir eliminaciones

Solo las personas con permisos para omitir las reglas (*bypass permissions*) pueden borrar las referencias protegidas. Esto reduce el riesgo de eliminar `main` por accidente; quienes tengan esos permisos siguen pudiendo omitir la restricción.(actualmente ninguno)

### Exigir una pull request antes de fusionar

Todos los commits deben hacerse en una rama distinta de `main` y enviarse mediante una PR antes de poder incorporarse. No se deben subir cambios directamente a `main`.

La PR necesita **2 aprobaciones** para poder fusionarse. Las aprobaciones las deben dar otras personas que revisen los cambios.

### Exigir aprobación del último envío revisable

Si se envían nuevos cambios a una PR después de su revisión, el último envío revisable debe recibir aprobación de alguien distinto de quien lo hizo. Así, una aprobación anterior no cubre automáticamente cambios posteriores. Ejemplo, una persona hace una pull request y resto la aprueba pero antes de hacer el *merge* la última persona añade un cambio. En ese caso se ha de aprobar nuevamente

### Aprobación adicional para PR de Copilot sin colaborador humano

Cuando Copilot abre una PR sin la participación de una persona colaboradora, se exige una aprobación adicional, siempre que ya se requiera al menos una aprobación. En este repositorio, que exige dos, una PR de ese tipo necesita **3 aprobaciones**.

### Bloquear los *force pushes*

Se impiden los envíos forzados a las referencias protegidas, incluso a usuarios con permiso de escritura. Un *force push* puede reescribir el historial compartido y quitar commits existentes.

## Convención para las ramas de trabajo

Para que una funcionalidad o cambio pueda revisarse y aprobarse, la rama debe usar uno de estos prefijos, seguido de una descripción breve:

- `feature/`: funcionalidad nueva completa o prácticamente completa. Ejemplo: `feature/inicio-sesion`.
- `fix/`: corrección de un error o *bug*. Ejemplo: `fix/error-validacion-correo`.
- `enhancement/`: mejora de una funcionalidad que ya existe. Ejemplo: `enhancement/filtros-busqueda`.

Cread la rama desde `main`, trabajad en ella y abrid una PR dirigida a `main`. Usad el prefijo que corresponda al propósito principal del cambio; una rama sin uno de estos prefijos no cumple la convención del equipo.