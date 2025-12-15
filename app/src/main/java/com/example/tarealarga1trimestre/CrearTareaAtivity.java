package com.example.tarealarga1trimestre;

import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModelProvider;

public class CrearTareaAtivity extends AppCompatActivity {

    FormularioViewModel viewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        LocaleHelper.applyLocale(this); // cambiar de idoma
        super.onCreate(savedInstanceState);
        setContentView(R.layout.actvity_crear_tarea);

        viewModel = new ViewModelProvider(this).get(FormularioViewModel.class);

        // Cargar primer fragmento
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.contenedorFragments, new Fragmento1())
                .commit();
    }

    public void volverPaso1() {
        getSupportFragmentManager().popBackStack();
    }

    public void cargarPaso2() {
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.contenedorFragments, new Fragmento2())
                .addToBackStack(null)
                .commit();
    }

    public void guardarTareaYSalir(Tarea nueva) {
        Intent data = new Intent();
        data.putExtra("TAREA_NUEVA", nueva);
        setResult(RESULT_OK, data);
        finish();
    }
}
