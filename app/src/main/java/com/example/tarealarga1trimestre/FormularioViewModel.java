package com.example.tarealarga1trimestre;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import java.time.LocalDate;

public class FormularioViewModel extends ViewModel {

    // esto de LiveData  es una movida de que hace que cuando un dato esta en secundaria se actualice
    // automaticamente sin necesidad de pasar el dato (Creo que solo sirve para fragmentos!!!!)

    public MutableLiveData<String> titulo = new MutableLiveData<>();
    public MutableLiveData<LocalDate> fechaCreacion = new MutableLiveData<>();
    public MutableLiveData<LocalDate> fechaObjetivo = new MutableLiveData<>();
    public MutableLiveData<Integer> progreso = new MutableLiveData<>();
    public MutableLiveData<Boolean> prioritaria = new MutableLiveData<>();
    public MutableLiveData<String> descripcion = new MutableLiveData<>();
}
