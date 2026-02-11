package com.example.tarealarga1trimestre.Activity.Preferencias;

import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.preference.PreferenceFragmentCompat;
import androidx.preference.SwitchPreferenceCompat;
import androidx.preference.ListPreference;
import androidx.preference.CheckBoxPreference;

import com.example.tarealarga1trimestre.R;

import androidx.appcompat.app.AppCompatDelegate;

public class AjustesFragmento extends PreferenceFragmentCompat {

    @Override
    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
        setPreferencesFromResource(R.xml.activity_preferencias, rootKey);

        SharedPreferences prefs = androidx.preference.PreferenceManager.getDefaultSharedPreferences(getContext());

       // Tema
        SwitchPreferenceCompat temaPref = findPreference("tema");
        if (temaPref != null) {
            temaPref.setOnPreferenceChangeListener((preference, newValue) -> {
                boolean temaClaro = (Boolean) newValue;
                AppCompatDelegate.setDefaultNightMode(
                        temaClaro ? AppCompatDelegate.MODE_NIGHT_NO
                                : AppCompatDelegate.MODE_NIGHT_YES
                );
                return true;
            });
        }

        // Tamaño letra
        ListPreference fuentePref = findPreference("fuente");
        if (fuentePref != null) {
            fuentePref.setSummaryProvider(ListPreference.SimpleSummaryProvider.getInstance());
            fuentePref.setOnPreferenceChangeListener((preference, newValue) -> {
                if (getActivity() != null) getActivity().recreate();
                return true;
            });
        }

        // Criterio de ordenación
        ListPreference criterioPref = findPreference("criterio");
        if (criterioPref != null) {
            criterioPref.setSummaryProvider(ListPreference.SimpleSummaryProvider.getInstance());
            criterioPref.setOnPreferenceChangeListener((preference, newValue) -> true);
        }

       // ordenar ascendete y descendente
        SwitchPreferenceCompat ordenPref = findPreference("orden");
        if (ordenPref != null) {
            ordenPref.setOnPreferenceChangeListener((preference, newValue) -> true);
        }

      // almacenamietno sd
        CheckBoxPreference sdPref = findPreference("sd");
        if (sdPref != null) {
            sdPref.setOnPreferenceChangeListener((preference, newValue) -> true);
        }
    }
}
