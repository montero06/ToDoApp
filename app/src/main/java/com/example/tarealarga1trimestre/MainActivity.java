package com.example.tarealarga1trimestre;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.os.Bundle;
import android.widget.Button;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import java.util.Locale;

public class MainActivity extends AppCompatActivity {

    private static final String PREFS = "prefs";
    private static final String LANG = "lang";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Aplicar idioma guardado
        SharedPreferences prefs = getSharedPreferences(PREFS, MODE_PRIVATE);
        String lang = prefs.getString(LANG, "es");
        setLocale(lang);

        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        if (getSupportActionBar() != null)
            getSupportActionBar().setDisplayShowTitleEnabled(false);

        Button btnEmpezar = findViewById(R.id.btn_Empezar);
        btnEmpezar.setOnClickListener(view -> {
            startActivity(new Intent(this, ListadoTareasActivity.class));
        });

        Button btnTranslate = findViewById(R.id.btnTranslate);
        btnTranslate.setOnClickListener(view -> {
            String nuevoLang = lang.equals("es") ? "en" : "es";
            SharedPreferences.Editor editor = prefs.edit();
            editor.putString(LANG, nuevoLang);
            editor.apply();

            // Reiniciar la actividad
            recreate();
        });
    }

    private void setLocale(String lang) {
        Locale locale = new Locale(lang);
        Locale.setDefault(locale);
        Configuration config = new Configuration();
        config.setLocale(locale);
        getResources().updateConfiguration(config, getResources().getDisplayMetrics());
    }

    @Override
    protected void attachBaseContext(Context newBase) {
        SharedPreferences prefs = newBase.getSharedPreferences(PREFS, MODE_PRIVATE);
        String lang = prefs.getString(LANG, "es");
        Locale locale = new Locale(lang);
        Locale.setDefault(locale);

        Configuration config = new Configuration();
        config.setLocale(locale);
        Context context = newBase.createConfigurationContext(config);
        super.attachBaseContext(context);
    }
}
