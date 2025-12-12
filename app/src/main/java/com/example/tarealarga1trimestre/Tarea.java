package com.example.tarealarga1trimestre;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.NonNull;

import java.time.LocalDate;

public class Tarea implements Parcelable {
    private String titulo;
    private String descripcion;
    private int progreso;
    private LocalDate fechaCreacion;
    private LocalDate fechaObjetivo;
    private Boolean prioritaria;

    public Tarea(String titulo, String descripcion, int progreso,
                 LocalDate fechaCreacion, LocalDate fechaObjetivo, Boolean prioritaria) {
        this.titulo = titulo;
        this.descripcion = descripcion;
        this.progreso = progreso;
        this.fechaCreacion = fechaCreacion;
        this.fechaObjetivo = fechaObjetivo;
        this.prioritaria = prioritaria;
    }

    protected Tarea(Parcel in) {
        titulo = in.readString();
        descripcion = in.readString();
        progreso = in.readInt();
        String fechaCreacionStr = in.readString();
        String fechaObjetivoStr = in.readString();
        fechaCreacion = fechaCreacionStr != null ? LocalDate.parse(fechaCreacionStr) : null;
        fechaObjetivo = fechaObjetivoStr != null ? LocalDate.parse(fechaObjetivoStr) : null;
        byte tmpPrioritaria = in.readByte();
        prioritaria = tmpPrioritaria == 0 ? null : tmpPrioritaria == 1;
    }

    public static final Creator<Tarea> CREATOR = new Creator<Tarea>() {
        @Override
        public Tarea createFromParcel(Parcel in) {
            return new Tarea(in);
        }

        @Override
        public Tarea[] newArray(int size) {
            return new Tarea[size];
        }
    };

    public String getTitulo() { return titulo; }
    public void setTitulo(String titulo) { this.titulo = titulo; }

    public String getDescripcion() { return descripcion; }
    public void setDescripcion(String descripcion) { this.descripcion = descripcion; }

    public int getProgreso() { return progreso; }
    public void setProgreso(int progreso) { this.progreso = progreso; }

    public LocalDate getFechaCreacion() { return fechaCreacion; }
    public void setFechaCreacion(LocalDate fechaCreacion) { this.fechaCreacion = fechaCreacion; }

    public LocalDate getFechaObjetivo() { return fechaObjetivo; }
    public void setFechaObjetivo(LocalDate fechaObjetivo) { this.fechaObjetivo = fechaObjetivo; }

    public Boolean getPrioritaria() { return prioritaria; }
    public void setPrioritaria(Boolean prioritaria) { this.prioritaria = prioritaria; }

    @Override
    public int describeContents() { return 0; }

    @Override
    public void writeToParcel(@NonNull Parcel dest, int flags) {
        dest.writeString(titulo);
        dest.writeString(descripcion);
        dest.writeInt(progreso);
        dest.writeString(fechaCreacion != null ? fechaCreacion.toString() : null);
        dest.writeString(fechaObjetivo != null ? fechaObjetivo.toString() : null);
        dest.writeByte((byte) (prioritaria == null ? 0 : prioritaria ? 1 : 2));
    }
}
