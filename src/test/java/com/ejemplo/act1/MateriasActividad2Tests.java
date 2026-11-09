package com.ejemplo.act1;

import static org.junit.jupiter.api.Assertions.*;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.regex.Pattern;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.test.annotation.DirtiesContext;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT,
        properties = "proyecto.nombre=Nombre de prueba")
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
class MateriasActividad2Tests {
    @LocalServerPort private int port;
    private final HttpClient cliente = HttpClient.newHttpClient();

    private HttpResponse<String> solicitar(String ruta, String tipo, String cuerpo) throws Exception {
        var builder = HttpRequest.newBuilder(URI.create("http://localhost:" + port + ruta));
        if (cuerpo == null) builder.GET();
        else builder.header("Content-Type", tipo).POST(HttpRequest.BodyPublishers.ofString(cuerpo));
        return cliente.send(builder.build(), HttpResponse.BodyHandlers.ofString());
    }

    @Test void listadoFiltroYDetalle() throws Exception {
        var lista = solicitar("/materias", null, null);
        assertEquals(200, lista.statusCode());
        assertTrue(lista.headers().firstValue("content-type").orElse("").contains("text/html"));
        assertTrue(lista.body().contains("Nombre de prueba"));
        assertTrue(lista.body().contains("Programación Web"));
        assertTrue(lista.body().contains("Administracion de redes"));
        assertTrue(lista.body().contains("Software para toma de desiciones"));
        assertTrue(lista.body().contains("Sistemas programables"));
        assertTrue(lista.body().contains("Moises Adrian Hernandez Luis"));
        assertTrue(lista.body().contains("Antonio Erick Wong Diaz"));
        var filtro = solicitar("/materias?categoria=Redes", null, null);
        assertTrue(filtro.body().contains("Administracion de redes"));
        assertFalse(filtro.body().contains("Programación Web"));
        assertTrue(solicitar("/materias?categoria=Inexistente", null, null).body().contains("No hay materias"));
        var detalle = solicitar("/materias/1", null, null);
        assertEquals(200, detalle.statusCode());
        assertTrue(detalle.body().contains("Adelina Martinez Nieto"));
        assertEquals(404, solicitar("/materias/9999", null, null).statusCode());
        assertEquals(200, solicitar("/materias/nueva", null, null).statusCode());
    }

    @Test void materiasInicialesCoincidenConActividadAnterior() throws Exception {
        var respuesta = solicitar("/api/materias", null, null);
        assertEquals(200, respuesta.statusCode());
        var materias = new tools.jackson.databind.json.JsonMapper().readTree(respuesta.body());
        assertTrue(materias.isArray());
        assertEquals(4, materias.size());
        String[][] esperadas = {
                {"Programación Web", "Adelina Martinez Nieto"},
                {"Administracion de redes", "Omar Robledo Cabrera"},
                {"Software para toma de desiciones", "Moises Adrian Hernandez Luis"},
                {"Sistemas programables", "Antonio Erick Wong Diaz"}
        };
        for (int i = 0; i < esperadas.length; i++) {
            var materia = materias.get(i);
            assertEquals(3, materia.size());
            assertEquals(i + 1, materia.path("id").asInt());
            assertEquals(esperadas[i][0], materia.path("nombre").asString());
            assertEquals(esperadas[i][1], materia.path("docente").asString());
        }
        var total = solicitar("/api/materias/total", null, null);
        assertEquals(200, total.statusCode());
        assertEquals("4", total.body());
    }

    @Test void semestresInicialesAparecenEnListadoYDetalles() throws Exception {
        String[] nombres = {"Programación Web", "Administracion de redes",
                "Software para toma de desiciones", "Sistemas programables"};
        int[] semestres = {7, 8, 8, 8};
        var lista = solicitar("/materias", null, null);
        assertEquals(200, lista.statusCode());
        for (int i = 0; i < nombres.length; i++) {
            String fila = "<tr[^>]*>(?:(?!</tr>).)*" + Pattern.quote(nombres[i])
                    + "(?:(?!</tr>).)*<td>" + semestres[i] + "</td>(?:(?!</tr>).)*</tr>";
            assertTrue(Pattern.compile(fila, Pattern.DOTALL).matcher(lista.body()).find(), nombres[i]);
            var detalle = solicitar("/materias/" + (i + 1), null, null);
            assertEquals(200, detalle.statusCode());
            assertTrue(detalle.body().contains(nombres[i]));
            assertTrue(detalle.body().contains("<dt>Semestre</dt><dd>" + semestres[i] + "</dd>"));
        }
    }

    @Test void formularioComparteDatosConRestYValida() throws Exception {
        var invalida = solicitar("/materias/guardar", "application/x-www-form-urlencoded", "nombre=&docente=&categoria=&semestre=99");
        assertEquals(200, invalida.statusCode());
        assertTrue(invalida.body().contains("El nombre es obligatorio"));
        assertTrue(invalida.body().contains("Introduce un semestre válido"));
        var tipoInvalido = solicitar("/materias/guardar", "application/x-www-form-urlencoded", "nombre=A&docente=B&categoria=C&semestre=abc");
        assertEquals(200, tipoInvalido.statusCode());
        assertTrue(tipoInvalido.body().contains("Introduce un semestre válido"));
        var respuesta = solicitar("/materias/guardar", "application/x-www-form-urlencoded", "nombre=MateriaFormulario&docente=Ana&categoria=Arte&semestre=3");
        assertEquals(302, respuesta.statusCode());
        assertEquals("/materias", URI.create(respuesta.headers().firstValue("location").orElseThrow()).getPath().split(";", 2)[0]);
        assertTrue(solicitar("/api/materias", null, null).body().contains("MateriaFormulario"));
        assertEquals("5", solicitar("/api/materias/total", null, null).body());
    }

    @Test void jsonValidoEInvalido() throws Exception {
        var invalida = solicitar("/api/materias", "application/json", "{\"nombre\":\"\",\"semestre\":0}");
        assertEquals(400, invalida.statusCode());
        assertTrue(invalida.body().contains("obligatorio"));
        assertEquals(400, solicitar("/api/materias", "application/json", "{mal").statusCode());
        var creada = solicitar("/api/materias", "application/json", "{\"id\":1,\"nombre\":\"Materia JSON\",\"docente\":\"Ana\",\"categoria\":\"Arte\",\"semestre\":3}");
        assertEquals(201, creada.statusCode());
        var json = new tools.jackson.databind.json.JsonMapper().readTree(creada.body());
        assertEquals(5, json.path("id").asInt());
        assertEquals("Materia JSON", json.path("nombre").asString());
        assertEquals("Ana", json.path("docente").asString());
        assertEquals("Arte", json.path("categoria").asString());
        assertEquals(3, json.path("semestre").asInt());
        assertTrue(solicitar("/materias/5", null, null).body().contains("Materia JSON"));
        assertTrue(solicitar("/api/materias", null, null).body().contains("Materia JSON"));
        assertEquals(200, solicitar("/api/proyecto", null, null).statusCode());
        assertEquals("5", solicitar("/api/materias/total", null, null).body());
    }
}
