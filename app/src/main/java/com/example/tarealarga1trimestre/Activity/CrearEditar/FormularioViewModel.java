package com.example.tarealarga1trimestre.Activity.CrearEditar;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class FormularioViewModel extends ViewModel {

    public final MutableLiveData<String> titulo = new MutableLiveData<>("");
    public final MutableLiveData<String> descripcion = new MutableLiveData<>("");
    public final MutableLiveData<Integer> progreso = new MutableLiveData<>(0);
    public final MutableLiveData<String> fechaCreacion = new MutableLiveData<>("");
    public final MutableLiveData<String> fechaObjetivo = new MutableLiveData<>("");
    public final MutableLiveData<Boolean> prioritaria = new MutableLiveData<>(false);
    public final MutableLiveData<String> urlDoc = new MutableLiveData<>(null);
    public final MutableLiveData<String> urlImg = new MutableLiveData<>(null);
    public final MutableLiveData<String> urlAud = new MutableLiveData<>(null);
    public final MutableLiveData<String> urlVid = new MutableLiveData<>(null);

    public void setTitulo(String t) { titulo.setValue(t); }
    public void setDescripcion(String d) { descripcion.setValue(d); }
    public void setProgreso(int p) { progreso.setValue(p); }
    public void setFechaCreacion(String f) { fechaCreacion.setValue(f); }
    public void setFechaObjetivo(String f) { fechaObjetivo.setValue(f); }
    public void setPrioritaria(boolean p) { prioritaria.setValue(p); }
    public void setUrlDoc(String v) { urlDoc.setValue(v); }
    public void setUrlImg(String v) { urlImg.setValue(v); }
    public void setUrlAud(String v) { urlAud.setValue(v); }
    public void setUrlVid(String v) { urlVid.setValue(v); }

    public String getTitulo() { return titulo.getValue() != null ? titulo.getValue() : ""; }
    public String getDescripcion() { return descripcion.getValue() != null ? descripcion.getValue() : ""; }
    public int getProgreso() { return progreso.getValue() != null ? progreso.getValue() : 0; }
    public String getFechaCreacion() { return fechaCreacion.getValue() != null ? fechaCreacion.getValue() : ""; }
    public String getFechaObjetivo() { return fechaObjetivo.getValue() != null ? fechaObjetivo.getValue() : ""; }
    public boolean isPrioritaria() { return prioritaria.getValue() != null && prioritaria.getValue(); }
    public String getUrlDoc() { return urlDoc.getValue(); }
    public String getUrlImg() { return urlImg.getValue(); }
    public String getUrlAud() { return urlAud.getValue(); }
    public String getUrlVid() { return urlVid.getValue(); }
}
