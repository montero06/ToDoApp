package com.example.tarealarga1trimestre.Manager;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import java.time.LocalDate;

@Entity(tableName = "tareas")
public class Tarea implements Parcelable {
    @PrimaryKey(autoGenerate = true)
    private long id;
    @NonNull
    private String titulo;
    private String descripcion;
    @ColumnInfo(defaultValue = "0")
    private int progreso;
    @NonNull
    @ColumnInfo(defaultValue = "CURRENT_DATE")
    private LocalDate fechaCreacion;
    @NonNull
    @ColumnInfo(defaultValue = "CURRENT_DATE")
    private LocalDate fechaObjetivo;
    @ColumnInfo(defaultValue = "0")
    private boolean prioritaria;
    private String urlDoc;
    private String urlImg;
    private String urlAud;
    private String urlVid;

    public Tarea() {
        this.titulo = "";
        this.fechaCreacion = LocalDate.now();
        this.fechaObjetivo = LocalDate.now();
        this.progreso = 0;
        this.prioritaria = false;
    }

    public Tarea(@NonNull String titulo, String descripcion, int progreso,
                 @NonNull LocalDate fechaCreacion, @NonNull LocalDate fechaObjetivo,
                 boolean prioritaria, String urlDoc, String urlImg, String urlAud, String urlVid) {
        this.titulo = titulo;
        this.descripcion = descripcion;
        this.progreso = progreso;
        this.fechaCreacion = fechaCreacion;
        this.fechaObjetivo = fechaObjetivo;
        this.prioritaria = prioritaria;
        this.urlDoc = urlDoc;
        this.urlImg = urlImg;
        this.urlAud = urlAud;
        this.urlVid = urlVid;
    }

    protected Tarea(Parcel in) {
        id = in.readLong();
        titulo = in.readString();
        descripcion = in.readString();
        progreso = in.readInt();
        String fechaCreacionStr = in.readString();
        String fechaObjetivoStr = in.readString();
        fechaCreacion = fechaCreacionStr != null ? LocalDate.parse(fechaCreacionStr) : LocalDate.now();
        fechaObjetivo = fechaObjetivoStr != null ? LocalDate.parse(fechaObjetivoStr) : LocalDate.now();
        prioritaria = in.readByte() != 0;
        urlDoc = in.readString();
        urlImg = in.readString();
        urlAud = in.readString();
        urlVid = in.readString();
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

    public long getId() { return id; }
    public void setId(long id) { this.id = id; }

    @NonNull
    public String getTitulo() { return titulo; }
    public void setTitulo(@NonNull String titulo) { this.titulo = titulo; }

    public String getDescripcion() { return descripcion; }
    public void setDescripcion(String descripcion) { this.descripcion = descripcion; }

    public int getProgreso() { return progreso; }
    public void setProgreso(int progreso) { this.progreso = progreso; }

    @NonNull
    public LocalDate getFechaCreacion() { return fechaCreacion; }
    public void setFechaCreacion(@NonNull LocalDate fechaCreacion) { this.fechaCreacion = fechaCreacion; }

    @NonNull
    public LocalDate getFechaObjetivo() { return fechaObjetivo; }
    public void setFechaObjetivo(@NonNull LocalDate fechaObjetivo) { this.fechaObjetivo = fechaObjetivo; }

    public boolean isPrioritaria() { return prioritaria; }
    public void setPrioritaria(boolean prioritaria) { this.prioritaria = prioritaria; }

    public String getUrlDoc() { return urlDoc; }
    public void setUrlDoc(String urlDoc) { this.urlDoc = urlDoc; }

    public String getUrlImg() { return urlImg; }
    public void setUrlImg(String urlImg) { this.urlImg = urlImg; }

    public String getUrlAud() { return urlAud; }
    public void setUrlAud(String urlAud) { this.urlAud = urlAud; }

    public String getUrlVid() { return urlVid; }
    public void setUrlVid(String urlVid) { this.urlVid = urlVid; }

    @Override
    public int describeContents() { return 0; }

    @Override
    public void writeToParcel(@NonNull Parcel dest, int flags) {
        dest.writeLong(id);
        dest.writeString(titulo);
        dest.writeString(descripcion);
        dest.writeInt(progreso);
        dest.writeString(fechaCreacion.toString());
        dest.writeString(fechaObjetivo.toString());
        dest.writeByte((byte) (prioritaria ? 1 : 0));
        dest.writeString(urlDoc);
        dest.writeString(urlImg);
        dest.writeString(urlAud);
        dest.writeString(urlVid);
    }
}
