package com.ejemplo.act1.services;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.TreeSet;

import com.ejemplo.act1.models.MateriaDTO;
import org.springframework.stereotype.Service;
import org.springframework.validation.Errors;

@Service
public class MateriasService {
    private final List<MateriaDTO> materias = new ArrayList<>();
    private int siguienteId = 1;

    public MateriasService() {
        guardar(new MateriaDTO(null, "Programación Web", "Adelina Martinez Nieto", "Programación", 7));
        guardar(new MateriaDTO(null, "Administracion de redes", "Omar Robledo Cabrera", "Redes", 8));
        guardar(new MateriaDTO(null, "Software para toma de desiciones", "Moises Adrian Hernandez Luis", "Software", 8));
        guardar(new MateriaDTO(null, "Sistemas programables", "Antonio Erick Wong Diaz", "Sistemas", 8));
    }

    private MateriaDTO copia(MateriaDTO m) {
        return new MateriaDTO(m.getId(), m.getNombre(), m.getDocente(), m.getCategoria(), m.getSemestre());
    }

    public synchronized MateriaDTO guardar(MateriaDTO entrada) {
        MateriaDTO materia = new MateriaDTO(siguienteId++, entrada.getNombre().trim(),
                entrada.getDocente().trim(), entrada.getCategoria().trim(), entrada.getSemestre());
        materias.add(materia);
        return copia(materia);
    }

    public synchronized List<MateriaDTO> listar(String categoria) {
        return materias.stream().filter(m -> categoria == null || categoria.isBlank()
                || m.getCategoria().equalsIgnoreCase(categoria.trim())).map(this::copia).toList();
    }

    public synchronized Optional<MateriaDTO> buscar(int id) {
        return materias.stream().filter(m -> m.getId() == id).findFirst().map(this::copia);
    }

    public synchronized List<String> categorias() {
        TreeSet<String> categorias = new TreeSet<>(String.CASE_INSENSITIVE_ORDER);
        materias.forEach(m -> categorias.add(m.getCategoria()));
        return List.copyOf(categorias);
    }

    public void validar(MateriaDTO materia, Errors errores) {
        if (materia.getNombre() == null || materia.getNombre().isBlank())
            errores.rejectValue("nombre", "obligatorio", "El nombre es obligatorio.");
        if (materia.getDocente() == null || materia.getDocente().isBlank())
            errores.rejectValue("docente", "obligatorio", "El docente es obligatorio.");
        if (materia.getCategoria() == null || materia.getCategoria().isBlank())
            errores.rejectValue("categoria", "obligatorio", "La categoría es obligatoria.");
        if (!errores.hasFieldErrors("semestre") && (materia.getSemestre() == null
                || materia.getSemestre() < 1 || materia.getSemestre() > 12))
            errores.rejectValue("semestre", "invalido", "El semestre debe ser un número entre 1 y 12.");
    }
}
