package com.ejemplo.act1;

import static org.junit.jupiter.api.Assertions.*;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT,
        properties = "proyecto.iniciales=ABC")
class EndpointsTests {

    @LocalServerPort
    private int port;

    private HttpResponse<String> get(String path) throws Exception {
        HttpRequest request = HttpRequest.newBuilder(URI.create("http://localhost:" + port + path))
                .GET().build();
        return HttpClient.newHttpClient().send(request, HttpResponse.BodyHandlers.ofString());
    }

    @Test
    void proyectoIncluyeInicialesConfiguradas() throws Exception {
        var response = get("/api/proyecto");
        assertEquals(200, response.statusCode());
        assertTrue(response.headers().firstValue("content-type").orElse("").startsWith("text/plain"));
        assertTrue(response.body().contains("Actividad 1"));
        assertTrue(response.body().contains("ABC"));
    }

    @Test
    void materiasYTotalSonCoherentes() throws Exception {
        var response = get("/api/materias");
        assertEquals(200, response.statusCode());
        assertTrue(response.headers().firstValue("content-type").orElse("").startsWith("application/json"));
        // Parse the JSON with the application's configured Jackson mapper.
        var mapper = new tools.jackson.databind.json.JsonMapper();
        var materias = mapper.readTree(response.body());
        assertTrue(materias.isArray());
        assertTrue(materias.size() >= 4);
        materias.forEach(materia -> {
            assertTrue(materia.path("id").isIntegralNumber());
            assertTrue(materia.path("nombre").isString());
            assertTrue(materia.path("docente").isString());
        });
        var total = get("/api/materias/total");
        assertEquals(200, total.statusCode());
        assertEquals(materias.size(), Integer.parseInt(total.body()));
    }

    @Test
    void vistaExistenteSigueDisponible() throws Exception {
        var response = get("/detalles_info");
        assertEquals(200, response.statusCode());
        assertTrue(response.body().contains("Informaticonfig"));
        assertTrue(response.body().contains("10.20.0.2"));
    }
}
