package com.example.tarealarga1trimestre;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

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

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        viewModel = new ViewModelProvider(requireActivity()).get(FormularioViewModel.class);

        edt_titulo = view.findViewById(R.id.edtTitulo);
        edt_fechaCreacion = view.findViewById(R.id.edtFechaCreacion);
        edt_fecjaObj = view.findViewById(R.id.edtFechaObjetivo);
        sp_progreso = view.findViewById(R.id.spinnerProgreso);
        cb_prioritaria = view.findViewById(R.id.cbPrioritaria);

        // Configurar Spinner
        ArrayAdapter<String> adapter = new ArrayAdapter<>(
                requireContext(),
                android.R.layout.simple_spinner_item,
                new String[]{"No iniciada", "Iniciada", "Avanzada", "Casi finalizada", "Finalizada"}
        );
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        sp_progreso.setAdapter(adapter);

        // Precargar datos si existen
        precargarDatos();

        // DatePickers
        edt_fechaCreacion.setOnClickListener(v -> mostrarDatePicker(edt_fechaCreacion));
        edt_fecjaObj.setOnClickListener(v -> mostrarDatePicker(edt_fecjaObj));

        // Botón siguiente
        view.findViewById(R.id.btnSiguiente).setOnClickListener(v -> {
            if (edt_titulo.getText().toString().isEmpty()) {
                Toast.makeText(getContext(), "Escribe un título", Toast.LENGTH_SHORT).show();
                return;
            }

            viewModel.titulo.setValue(edt_titulo.getText().toString());
            viewModel.prioritaria.setValue(cb_prioritaria.isChecked());

            int[] valores = {0, 25, 50, 75, 100};
            viewModel.progreso.setValue(valores[sp_progreso.getSelectedItemPosition()]);

            if (requireActivity() instanceof CrearTareaAtivity) {
                ((CrearTareaAtivity) requireActivity()).cargarPaso2();
            } else if (requireActivity() instanceof EditarTareaActivity) {
                ((EditarTareaActivity) requireActivity()).cargarPaso2();
            }
        });
    }

    private void mostrarDatePicker(EditText campo) {
        DatePickerFragment picker = new DatePickerFragment(date -> {
            campo.setText(date.toString());
            if (campo == edt_fechaCreacion) viewModel.fechaCreacion.setValue(date);
            if (campo == edt_fechaCreacion) viewModel.fechaObjetivo.setValue(date);
        });
        picker.show(getParentFragmentManager(), "datePicker");
    }
    private void precargarDatos() {
        if (viewModel.titulo.getValue() != null)
            edt_titulo.setText(viewModel.titulo.getValue());

        if (viewModel.fechaCreacion.getValue() != null)
            edt_fechaCreacion.setText(viewModel.fechaCreacion.getValue().toString());

        if (viewModel.fechaObjetivo.getValue() != null)
            edt_fecjaObj.setText(viewModel.fechaObjetivo.getValue().toString());

        if (viewModel.prioritaria.getValue() != null)
            cb_prioritaria.setChecked(viewModel.prioritaria.getValue());

        if (viewModel.progreso.getValue() != null) {
            int progreso = viewModel.progreso.getValue();
            int pos = progreso / 25; // porque usamos {0,25,50,75,100}
            sp_progreso.setSelection(pos);
        }
    }
}
