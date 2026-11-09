package com.ejemplo.act1.models;

public class MateriaDTO {
    private Integer id;
    private String nombre;
    private String docente;
    private String categoria;
    private Integer semestre;

    public MateriaDTO() { }

    public MateriaDTO(Integer id, String nombre, String docente, String categoria, Integer semestre) {
        this.id = id;
        this.nombre = nombre;
        this.docente = docente;
        this.categoria = categoria;
        this.semestre = semestre;
    }

    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }
    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }
    public String getDocente() { return docente; }
    public void setDocente(String docente) { this.docente = docente; }
    public String getCategoria() { return categoria; }
    public void setCategoria(String categoria) { this.categoria = categoria; }
    public Integer getSemestre() { return semestre; }
    public void setSemestre(Integer semestre) { this.semestre = semestre; }
}
