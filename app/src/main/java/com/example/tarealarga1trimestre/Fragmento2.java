package com.example.tarealarga1trimestre;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import java.util.Date;

public class Fragmento2 extends Fragment {
    private EditText edtDescripcion;

    private FormularioViewModel viewModel;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.activity_crear_fragmento_2, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        viewModel = new ViewModelProvider(requireActivity()).get(FormularioViewModel.class);
        edtDescripcion = view.findViewById(R.id.edtDescripcion);

        precargarDescripcion();

        view.findViewById(R.id.btnVolver).setOnClickListener(v -> {
            if (requireActivity() instanceof CrearTareaAtivity)
                ((CrearTareaAtivity) requireActivity()).volverPaso1();
            else if (requireActivity() instanceof EditarTareaActivity)
                ((EditarTareaActivity) requireActivity()).volverPaso1();
        });

        view.findViewById(R.id.btnGuardar).setOnClickListener(v -> guardar());
    }

    private void precargarDescripcion() {
        if (viewModel.descripcion.getValue() != null)
            edtDescripcion.setText(viewModel.descripcion.getValue());
    }
    private void guardar() {
        String titulo = viewModel.titulo.getValue();
        String descripcion = edtDescripcion.getText().toString();
        int progreso = viewModel.progreso.getValue();
        Date fechaCreacion = viewModel.fechaCreacion.getValue();
        Date fechaObjetivo = viewModel.fechaObjetivo.getValue();
        boolean prioritaria = viewModel.prioritaria.getValue();

        Tarea tarea = new Tarea(titulo, descripcion, progreso, fechaCreacion, fechaObjetivo, prioritaria);

        if (requireActivity() instanceof CrearTareaAtivity) {
            ((CrearTareaAtivity) requireActivity()).guardarTareaYSalir(tarea);
        } else if (requireActivity() instanceof EditarTareaActivity) {
            ((EditarTareaActivity) requireActivity()).guardarTareaEditada(tarea);
        }
    }



}
