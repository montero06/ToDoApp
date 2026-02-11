package com.example.tarealarga1trimestre.Activity.CrearEditar;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class FormularioViewModel extends ViewModel {
    private Map<String, List<String>> archivosPorTipo = new HashMap<>();

    public void agregarArchivo(String tipo, String ruta) {
        if (!archivosPorTipo.containsKey(tipo)) {
            archivosPorTipo.put(tipo, new ArrayList<>());
        }
        archivosPorTipo.get(tipo).add(ruta);
    }

    public Map<String, List<String>> getArchivos() {
        return archivosPorTipo;
    }


    public final MutableLiveData<String> titulo = new MutableLiveData<>("");
    public final MutableLiveData<String> descripcion = new MutableLiveData<>("");
    public final MutableLiveData<Integer> progreso = new MutableLiveData<>(0);
    public final MutableLiveData<String> fechaCreacion = new MutableLiveData<>("");
    public final MutableLiveData<String> fechaObjetivo = new MutableLiveData<>("");
    public final MutableLiveData<Boolean> prioritaria = new MutableLiveData<>(false);

    // Opcional: setters para usar en fragmentos
    public void setTitulo(String t) { titulo.setValue(t); }
    public void setDescripcion(String d) { descripcion.setValue(d); }
    public void setProgreso(int p) { progreso.setValue(p); }
    public void setFechaCreacion(String f) { fechaCreacion.setValue(f); }
    public void setFechaObjetivo(String f) { fechaObjetivo.setValue(f); }
    public void setPrioritaria(boolean p) { prioritaria.setValue(p); }

    // Opcional: getters
    public String getTitulo() { return titulo.getValue() != null ? titulo.getValue() : ""; }
    public String getDescripcion() { return descripcion.getValue() != null ? descripcion.getValue() : ""; }
    public int getProgreso() { return progreso.getValue() != null ? progreso.getValue() : 0; }
    public String getFechaCreacion() { return fechaCreacion.getValue() != null ? fechaCreacion.getValue() : ""; }
    public String getFechaObjetivo() { return fechaObjetivo.getValue() != null ? fechaObjetivo.getValue() : ""; }
    public boolean isPrioritaria() { return prioritaria.getValue() != null && prioritaria.getValue(); }
}
