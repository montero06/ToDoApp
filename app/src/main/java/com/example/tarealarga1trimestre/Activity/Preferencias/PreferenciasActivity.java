package com.example.tarealarga1trimestre.Activity.Preferencias;

import android.content.Context;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.example.tarealarga1trimestre.Activity.Listado.ListadoTareasActivity;
import com.example.tarealarga1trimestre.R;

public class PreferenciasActivity extends AppCompatActivity {

    public static int getCriterio(Context context) {
        return context.getSharedPreferences("preferencias", Context.MODE_PRIVATE)
                .getInt("criterio", 2); // 2 = fecha creación por defecto
    }

    public static boolean getOrden(Context context) {
        return context.getSharedPreferences("preferencias", Context.MODE_PRIVATE)
                .getBoolean("orden", true); // true = ascendente por defecto
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_preferencias); // layout con Toolbar y FrameLayout

        // Toolbar con botón Home
        Toolbar toolbar = findViewById(R.id.toolbarPreferencias);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle("Preferencias");
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        // Cargar fragmento de preferencias
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.contenedorPreferencias, new AjustesFragmento())
                .commit();
    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return true;
    }
}
