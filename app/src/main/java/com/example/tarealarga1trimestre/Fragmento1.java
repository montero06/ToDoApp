package com.example.tarealarga1trimestre;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Calendar;

public class Fragmento1 extends Fragment {

    private EditText edt_titulo, edt_fechaCreacion, edt_fechaObj;
    private Spinner sp_progreso;
    private CheckBox cb_prioritaria;
    private Button btnSiguiente;

    private FormularioViewModel viewModel;
    private boolean modoEdicion = false;

    private final DateTimeFormatter formatter =
            DateTimeFormatter.ofPattern("dd/MM/yyyy");

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.activity_crar_fragmento_1, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view,
                              @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        viewModel = new ViewModelProvider(requireActivity())
                .get(FormularioViewModel.class);

        edt_titulo = view.findViewById(R.id.edtTitulo);
        edt_fechaCreacion = view.findViewById(R.id.edtFechaCreacion);
        edt_fechaObj = view.findViewById(R.id.edtFechaObjetivo);
        sp_progreso = view.findViewById(R.id.spinnerProgreso);
        cb_prioritaria = view.findViewById(R.id.cbPrioritaria);
        btnSiguiente = view.findViewById(R.id.btnSiguiente);

        if (requireActivity() instanceof EditarTareaActivity) {
            modoEdicion = true;
            btnSiguiente.setText("Guardar");
        }

        // Spinner progreso
        ArrayAdapter<String> adapter = new ArrayAdapter<>(
                requireContext(),
                android.R.layout.simple_spinner_item,
                new String[]{
                        "No iniciada",
                        "Iniciada",
                        "Avanzada",
                        "Casi finalizada",
                        "Finalizada"
                }
        );
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        sp_progreso.setAdapter(adapter);

        precargarDatos();

        // DatePickers
        edt_fechaCreacion.setOnClickListener(v ->
                mostrarDatePicker(true)
        );

        edt_fechaObj.setOnClickListener(v ->
                mostrarDatePicker(false)
        );

        btnSiguiente.setOnClickListener(v -> guardarONavegar());
    }

  
    private void mostrarDatePicker(boolean esFechaCreacion) {
        Calendar c = Calendar.getInstance();

        new DatePickerDialog(
                requireContext(),
                (view, year, month, dayOfMonth) -> {
                    LocalDate fecha = LocalDate.of(
                            year,
                            month + 1,
                            dayOfMonth
                    );

                    if (esFechaCreacion) {
                        viewModel.fechaCreacion.setValue(fecha);
                        edt_fechaCreacion.setText(fecha.format(formatter));
                    } else {
                        viewModel.fechaObjetivo.setValue(fecha);
                        edt_fechaObj.setText(fecha.format(formatter));
                    }
                },
                c.get(Calendar.YEAR),
                c.get(Calendar.MONTH),
                c.get(Calendar.DAY_OF_MONTH)
        ).show();
    }


    private void guardarONavegar() {
        if (edt_titulo.getText().toString().trim().isEmpty()) {
            Toast.makeText(
                    getContext(),
                    "Escribe un título",
                    Toast.LENGTH_SHORT
            ).show();
            return;
        }

        viewModel.titulo.setValue(edt_titulo.getText().toString());
        viewModel.prioritaria.setValue(cb_prioritaria.isChecked());

        int[] valores = {0, 25, 50, 75, 100};
        viewModel.progreso.setValue(
                valores[sp_progreso.getSelectedItemPosition()]
        );

        if (modoEdicion && requireActivity() instanceof EditarTareaActivity) {

            LocalDate fechaCreacion =
                    viewModel.fechaCreacion.getValue() != null
                            ? viewModel.fechaCreacion.getValue()
                            : LocalDate.now();

            LocalDate fechaObjetivo =
                    viewModel.fechaObjetivo.getValue() != null
                            ? viewModel.fechaObjetivo.getValue()
                            : LocalDate.now();

            String descripcion =
                    viewModel.descripcion.getValue() != null
                            ? viewModel.descripcion.getValue()
                            : "";

            Tarea tareaEditada = new Tarea(
                    viewModel.titulo.getValue(),
                    descripcion,
                    viewModel.progreso.getValue(),
                    fechaCreacion,
                    fechaObjetivo,
                    viewModel.prioritaria.getValue()
            );

            ((EditarTareaActivity) requireActivity())
                    .guardarTareaEditada(tareaEditada);

        } else if (requireActivity() instanceof CrearTareaAtivity) {
            ((CrearTareaAtivity) requireActivity()).cargarPaso2();
        }
    }

    // =======================
    // PRECARGAR DATOS
    // =======================
    private void precargarDatos() {

        if (viewModel.titulo.getValue() != null)
            edt_titulo.setText(viewModel.titulo.getValue());

        if (viewModel.fechaCreacion.getValue() != null)
            edt_fechaCreacion.setText(
                    viewModel.fechaCreacion.getValue().format(formatter)
            );

        if (viewModel.fechaObjetivo.getValue() != null)
            edt_fechaObj.setText(
                    viewModel.fechaObjetivo.getValue().format(formatter)
            );

        if (viewModel.prioritaria.getValue() != null)
            cb_prioritaria.setChecked(viewModel.prioritaria.getValue());

        if (viewModel.progreso.getValue() != null) {
            int pos = viewModel.progreso.getValue() / 25;
            sp_progreso.setSelection(pos);
        }
    }
}
