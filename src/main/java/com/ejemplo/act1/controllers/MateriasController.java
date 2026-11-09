package com.ejemplo.act1.controllers;

import java.util.List;

import com.ejemplo.act1.models.Materia;
import com.ejemplo.act1.models.MateriaDTO;
import com.ejemplo.act1.services.MateriasService;
import java.net.URI;
import java.util.Map;
import java.util.stream.Collectors;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/materias")
public class MateriasController {

    private final MateriasService servicio;

    public MateriasController(MateriasService servicio) {
        this.servicio = servicio;
    }

    @GetMapping
    public List<Materia> materias() {
        return servicio.listar(null).stream()
                .map(m -> new Materia(m.getId(), m.getNombre(), m.getDocente())).toList();
    }

    @GetMapping("/total")
    public int total() {
        return servicio.listar(null).size();
    }

    @PostMapping
    public ResponseEntity<?> crear(@RequestBody MateriaDTO materia) {
        var errores = new BeanPropertyBindingResult(materia, "materia");
        servicio.validar(materia, errores);
        if (errores.hasErrors()) {
            var mensajes = errores.getFieldErrors().stream().collect(Collectors.toMap(
                    e -> e.getField(), e -> e.getDefaultMessage(), (primero, otro) -> primero));
            return ResponseEntity.badRequest().body(Map.of("errores", mensajes));
        }
        MateriaDTO creada = servicio.guardar(materia);
        return ResponseEntity.created(URI.create("/materias/" + creada.getId())).body(creada);
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<?> jsonInvalido() {
        return ResponseEntity.badRequest().body(Map.of("error", "JSON inválido: revise los campos y sus tipos."));
    }
}
