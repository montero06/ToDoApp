package com.example.tarealarga1trimestre;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.NonNull;

import java.util.Date;

public class Tarea implements Parcelable {
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


    protected Tarea(Parcel in) {
        titulo = in.readString();
        descripcion = in.readString();
        progreso = in.readInt();
        byte tmpPrirotaria = in.readByte();
        prirotaria = tmpPrirotaria == 0 ? null : tmpPrirotaria == 1;
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

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(@NonNull Parcel dest, int flags) {
        dest.writeString(titulo);
        dest.writeString(descripcion);
        dest.writeInt(progreso);
        dest.writeByte((byte) (prirotaria == null ? 0 : prirotaria ? 1 : 2));
    }
}
