package com.ejemplo.act1.controllers;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class ProyectoController {

    @Value("${proyecto.iniciales:XX}")
    private String iniciales;

    @GetMapping(value = "/api/proyecto", produces = MediaType.TEXT_PLAIN_VALUE)
    public String proyecto() {
        return "Actividad 1 de Programación Web - Estudiante: " + iniciales;
    }
}
