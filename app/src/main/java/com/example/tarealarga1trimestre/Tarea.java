package com.example.tarealarga1trimestre;

import java.util.Date;

public class Tarea {
    private String titulo ;
    private String descripcion;
    private int progreso ;
    private Date fechaCreacion;
    private Date fechaObjetivo;
    private Boolean prirotaria;

    public Tarea(String titulo, String descripcion, int progreso, Date fechaCreacion, Date fechaObjetivo, Boolean prirotaria) {
        this.titulo = titulo;
        this.descripcion = descripcion;
        this.progreso = progreso;
        this.fechaCreacion = fechaCreacion;
        this.fechaObjetivo = fechaObjetivo;
        this.prirotaria = prirotaria;
    }
    public Tarea(String titulo, String descripcion, int progreso, Date fechaCreacion, Date fechaObjetivo) {
        this.titulo = titulo;
        this.descripcion = descripcion;
        this.progreso = progreso;
        this.fechaCreacion = fechaCreacion;
        this.fechaObjetivo = fechaObjetivo;
        this.prirotaria = false;
    }

    public String getTitulo() {
        return titulo;
    }

    public void setTitulo(String titulo) {
        this.titulo = titulo;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public int getProgreso() {
        return progreso;
    }

    public void setProgreso(int progreso) {
        this.progreso = progreso;
    }

    public Date getFechaCreacion() {
        return fechaCreacion;
    }

    public void setFechaCreacion(Date fechaCreacion) {
        this.fechaCreacion = fechaCreacion;
    }

    public Date getFechaObjetivo() {
        return fechaObjetivo;
    }

    public void setFechaObjetivo(Date fechaObjetivo) {
        this.fechaObjetivo = fechaObjetivo;
    }

    public Boolean getPrirotaria() {
        return prirotaria;
    }

    public void setPrirotaria(Boolean prirotaria) {
        this.prirotaria = prirotaria;
    }
}
