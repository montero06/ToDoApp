package com.example.tarealarga1trimestre;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModelProvider;

import java.util.Date;
public class CrearTareaAtivity extends AppCompatActivity {

    FormularioViewModel viewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.actvity_crear_tarea);

        viewModel = new ViewModelProvider(this).get(FormularioViewModel.class);

        // Cargar primer fragmento
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.contenedorFragmentos, new FragmentoPasoUno())
                .commit();
    }


}


