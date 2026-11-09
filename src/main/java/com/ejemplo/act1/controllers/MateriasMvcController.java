package com.ejemplo.act1.controllers;

import com.ejemplo.act1.models.MateriaDTO;
import com.ejemplo.act1.services.MateriasService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
public class MateriasMvcController {
    private final MateriasService servicio;
    private final String nombreProyecto;

    public MateriasMvcController(MateriasService servicio, @Value("${proyecto.nombre}") String nombreProyecto) {
        this.servicio = servicio;
        this.nombreProyecto = nombreProyecto;
    }

    @ModelAttribute("nombreProyecto")
    public String nombreProyecto() { return nombreProyecto; }

    @GetMapping("/materias")
    public String listar(@RequestParam(value = "categoria", required = false) String categoria, Model model) {
        model.addAttribute("materias", servicio.listar(categoria));
        model.addAttribute("categorias", servicio.categorias());
        model.addAttribute("categoriaSeleccionada", categoria == null ? "" : categoria);
        return "materias-lista";
    }

    @GetMapping("/materias/nueva")
    public String nueva(Model model) {
        model.addAttribute("materia", new MateriaDTO());
        return "materias-formulario";
    }

    @PostMapping("/materias/guardar")
    public String guardar(@ModelAttribute("materia") MateriaDTO materia, BindingResult errores,
                          RedirectAttributes redirect) {
        servicio.validar(materia, errores);
        if (errores.hasErrors()) return "materias-formulario";
        servicio.guardar(materia);
        redirect.addFlashAttribute("confirmacion", "Materia guardada correctamente.");
        return "redirect:/materias";
    }

    @GetMapping("/materias/{id}")
    public String detalle(@PathVariable int id, Model model) {
        model.addAttribute("materia", servicio.buscar(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Materia no encontrada")));
        return "materias-detalle";
    }
}
