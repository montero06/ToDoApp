package com.example.tarealarga1trimestre.Manager;

import android.content.Context;
import android.content.SharedPreferences;
import androidx.preference.PreferenceManager;

import com.example.tarealarga1trimestre.R;

public class utilLetra {

    // Devuelve el tamaño de letra actual según la preferencia "fuente"
    public static float getTamanoLetra(Context context) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        int fuente = Integer.parseInt(prefs.getString("fuente", "2"));

        switch (fuente) {
            case 1:
                return context.getResources().getDimension(R.dimen.tamanno_pequeño);
            case 2:
                return context.getResources().getDimension(R.dimen.tamanno_mediano);
            case 3:
                return context.getResources().getDimension(R.dimen.Tamanno_grande);
            default:
                return context.getResources().getDimension(R.dimen.tamanno_mediano);
        }
    }
}
