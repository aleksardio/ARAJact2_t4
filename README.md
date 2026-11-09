# Actividad 2: Control Académico - ARAJ

Aplicación de Programación Web con Spring Boot 4.1.2, Spring Web MVC, Thymeleaf, HTML, CSS y Maven Wrapper. Compila para **Java 17**; puede ejecutarse localmente con JDK 23. Se conserva el paquete `com.ejemplo.act1` y la vista de práctica `/detalles_info`.

## Patrón MVC y datos

En el patrón MVC, `MateriasService` gestiona los datos del modelo, `MateriasMvcController` recibe las solicitudes y coloca datos en `Model`, y las plantillas Thymeleaf generan las vistas HTML. Por ejemplo, el controlador prepara la lista y la vista recorre sus elementos:

```java
model.addAttribute("materias", servicio.listar(categoria));
```

```html
<tr th:each="materia : ${materias}">
```

`MateriaDTO` transporta `id`, `nombre`, `docente`, `categoria` y `semestre` entre el servicio, las vistas, el formulario y JSON. `Materia` se conserva para mantener el formato anterior de `GET /api/materias`. `MateriasController` atiende la API REST; `ProyectoController` y `EjemploController` conservan el endpoint de información del proyecto y la vista de práctica anteriores.

El formulario usa `th:object`, `th:field` y `@ModelAttribute`; el listado usa `th:each`, el filtro `@RequestParam`, el detalle `@PathVariable` y el POST REST `@RequestBody`. `@Value` lee `proyecto.nombre` de `application.properties`. El servicio asigna los identificadores: se ignora cualquier `id` recibido. Nombre, docente y categoría son obligatorios; el semestre debe estar entre 1 y 12. Las categorías de las cuatro materias originales son ejemplos.

**Los datos se comparten entre la interfaz y la API únicamente durante la ejecución; se pierden al reiniciar.** No existe base de datos.

## Ejecución local

En `D:\Spring\act2`, con JDK 17 o superior y acceso a las dependencias Maven:

```powershell
.\mvnw.cmd clean verify
.\mvnw.cmd spring-boot:run
```

En Linux/macOS use `./mvnw`. Abra `http://localhost:8084/materias`. El puerto configurado es **8084**. Compruebe su disponibilidad antes de iniciar. `proyecto.iniciales=ARAJ` sigue configurado para la API heredada.

## Rutas

| Método | Ruta | Uso |
| --- | --- | --- |
| GET | `/materias` | Listado HTML; `?categoria=Redes` filtra por categoría. |
| GET | `/materias/nueva` | Formulario. |
| POST | `/materias/guardar` | Procesa el formulario y redirige al listado. |
| GET | `/materias/{id}` | Detalle; 404 si no existe. |
| GET | `/detalles_info` | Vista de práctica heredada. |
| GET | `/api/proyecto` | Texto de la Actividad 1 e iniciales configuradas. |
| GET | `/api/materias` | JSON heredado: `id`, `nombre`, `docente`, incluyendo nuevas materias. |
| GET | `/api/materias/total` | Total de materias. |
| POST | `/api/materias` | Crea con JSON, responde 201 con DTO completo o 400 si es inválido. |

Para probar el formulario, entre en `/materias/nueva`, complete los cuatro campos y pulse **Guardar materia**. La redirección muestra una confirmación y el registro en el listado; compruebe también el filtro y el detalle.

## POST con Postman o Bruno

Configure `POST http://localhost:8084/api/materias`, cabecera `Content-Type: application/json` y cuerpo raw JSON:

```json
{
  "nombre": "Ingeniería de software",
  "docente": "Pepe Ramirez",
  "categoria": "Software",
  "semestre": 4
}
```

La respuesta válida es HTTP 201 e incluye los cuatro campos y el `id` asignado. La cabecera `Location` apunta al detalle HTML. Errores de validación devuelven HTTP 400 con mensajes por campo; JSON mal formado también devuelve 400.

## Evidencias de funcionamiento

Las siguientes capturas muestran resultados de la aplicación; las anotaciones y directivas mencionadas describen la implementación que produce cada vista, no código visible en las imágenes.

### 1. Listado de materias

El listado muestra las materias antes de registrar Matemáticas Discretas, incluida Inteligencia Artificial creada en la prueba REST. En la implementación MVC, el controlador entrega la colección mediante `Model` y Thymeleaf genera las filas con `th:each`. El encabezado muestra el nombre configurado en `application.properties`, leído por el controlador mediante `@Value`.

![Listado de materias y nombre del proyecto](caps/cap2.png)

### 2. Filtro por categoría

Al seleccionar la categoría Programación, el listado muestra únicamente Programación Web. El controlador recibe el parámetro opcional `categoria` mediante `@RequestParam` y entrega la lista filtrada a la vista.

![Listado filtrado por la categoría Programación](caps/cap3.png)

### 3. Formulario de nueva materia

El formulario contiene los datos de Matemáticas Discretas antes de enviarlos. Thymeleaf enlaza los campos con `MateriaDTO` mediante `th:object` y `th:field`; el controlador recibe el formulario con `@ModelAttribute` y valida los datos.

![Formulario para registrar Matemáticas Discretas](caps/cap4.png)

### 4. Confirmación del registro

Después de guardar, se observa el mensaje de confirmación y la nueva materia en el listado. El controlador guarda el DTO en el servicio y redirige al listado siguiendo Post/Redirect/Get.

![Confirmación y listado con Matemáticas Discretas](caps/cap5.png)

### 5. Detalle de una materia

La vista presenta el identificador, docente, categoría y semestre de Programación Web. En la implementación, `@PathVariable` recibe el ID de `/materias/{id}` para consultar la materia y añadirla al `Model`.

![Detalle de Programación Web](caps/cap6.png)

### 6. Creación mediante Postman

Postman envía JSON a `POST /api/materias` y muestra la respuesta **201 Created**, con los datos de Inteligencia Artificial y el ID asignado. El controlador REST recibe el JSON en un `MateriaDTO` mediante `@RequestBody` y lo guarda en la misma colección utilizada por las vistas.

![Petición POST en Postman y respuesta 201 Created](caps/cap1.png)

## Antes de desplegar

Comprobar la disponibilidad del puerto 8084 en el VPS, revisar la versión de Java instalada y la configuración de red, y planificar el despliegue sin alterar la Actividad 1 en 8082. Este proyecto no configura servicios del VPS ni persistencia.
