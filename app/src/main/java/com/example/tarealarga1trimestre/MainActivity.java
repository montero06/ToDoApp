package com.example.tarealarga1trimestre;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Button;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class MainActivity extends AppCompatActivity {

    private static final String PREFS = "settings";
    private static final String MODE = "mode";

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

        SharedPreferences prefs = getSharedPreferences(PREFS, MODE_PRIVATE);

        //  Aplicar modo noche guardado
        boolean night = prefs.getBoolean(MODE, false);
        AppCompatDelegate.setDefaultNightMode(
                night ? AppCompatDelegate.MODE_NIGHT_YES
                        : AppCompatDelegate.MODE_NIGHT_NO
        );

        // ▶ Empezar
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

        // Modo noche / día
        findViewById(R.id.btnMode).setOnClickListener(v -> {
            boolean nuevoModo = !prefs.getBoolean(MODE, false);
            prefs.edit().putBoolean(MODE, nuevoModo).apply();

            AppCompatDelegate.setDefaultNightMode(
                    nuevoModo ? AppCompatDelegate.MODE_NIGHT_YES
                            : AppCompatDelegate.MODE_NIGHT_NO
            );
        });
    }
}
