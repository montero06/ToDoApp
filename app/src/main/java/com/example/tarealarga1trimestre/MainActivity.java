package com.example.tarealarga1trimestre;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.tarealarga1trimestre.Activity.Listado.ListadoTareasActivity;
import com.example.tarealarga1trimestre.Manager.LocaleHelper;

public class MainActivity extends AppCompatActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {

        //  Aplicar idioma guardado
        LocaleHelper.applyLocale(this);

        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        if (getSupportActionBar() != null)
            getSupportActionBar().setDisplayShowTitleEnabled(false);



        //  Empezar
        findViewById(R.id.btn_Empezar).setOnClickListener(v ->
                startActivity(new Intent(this, ListadoTareasActivity.class))
        );

        // Cambiar idioma (ES / EN)
        findViewById(R.id.btnTranslate).setOnClickListener(v -> {
            String nuevo = getResources().getConfiguration().getLocales()
                    .get(0).getLanguage().equals("es") ? "en" : "es";

            LocaleHelper.setLocale(this, nuevo);
            recreate();
        });

        SharedPreferences prefs =
                androidx.preference.PreferenceManager.getDefaultSharedPreferences(this);

        boolean temaClaro = prefs.getBoolean("tema", true);

        AppCompatDelegate.setDefaultNightMode(
                temaClaro ? AppCompatDelegate.MODE_NIGHT_NO
                        : AppCompatDelegate.MODE_NIGHT_YES
        );

    }
}
