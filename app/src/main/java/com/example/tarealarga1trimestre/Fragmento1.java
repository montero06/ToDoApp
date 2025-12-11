package com.example.tarealarga1trimestre;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Spinner;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

public class Fragmento1 extends Fragment {

    private EditText edt_titulo , edt_fechaCreacion, edt_fecjaObj;

    private Spinner sp_progreso;

    private CheckBox cb_prioritaria;

    private FormularioViewModel viewModel;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        return inflater.inflate(R.layout.activity_crar_fragmento_1, container, false);
    }
}
