package com.example.tarealarga1trimestre.Activity.CrearEditar;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.ArrayAdapter;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.example.tarealarga1trimestre.Activity.CrearTareaAtivity;
import com.example.tarealarga1trimestre.R;

import java.util.Calendar;

public class Fragmento1 extends Fragment {

    private FormularioViewModel viewModel;

    private EditText edtTitulo, edtFechaCreacion, edtFechaObjetivo;
    private Spinner spinnerProgreso;
    private CheckBox cbPrioritaria;
    private Button btnSiguiente;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.activity_crar_fragmento_1, container, false);

        viewModel = new ViewModelProvider(requireActivity()).get(FormularioViewModel.class);

        edtTitulo = root.findViewById(R.id.edtTitulo);
        edtFechaCreacion = root.findViewById(R.id.edtFechaCreacion);
        edtFechaObjetivo = root.findViewById(R.id.edtFechaObjetivo);
        spinnerProgreso = root.findViewById(R.id.spinnerProgreso);
        cbPrioritaria = root.findViewById(R.id.cbPrioritaria);
        btnSiguiente = root.findViewById(R.id.btnSiguiente);

        // Spinner progreso
        spinnerProgreso.setAdapter(new ArrayAdapter<>(requireContext(),
                android.R.layout.simple_spinner_item,
                new String[]{"0%", "10%", "20%", "30%", "40%", "50%", "60%", "70%", "80%", "90%", "100%"}));

        // Cargar datos desde ViewModel
        if (viewModel.getTitulo() != null) edtTitulo.setText(viewModel.getTitulo());
        if (viewModel.getFechaCreacion() != null) edtFechaCreacion.setText(viewModel.getFechaCreacion());
        if (viewModel.getFechaObjetivo() != null) edtFechaObjetivo.setText(viewModel.getFechaObjetivo());
        spinnerProgreso.setSelection(viewModel.getProgreso() / 10);
        cbPrioritaria.setChecked(viewModel.isPrioritaria());

        // Selección de fechas
        edtFechaCreacion.setOnClickListener(v -> mostrarDatePicker(edtFechaCreacion));
        edtFechaObjetivo.setOnClickListener(v -> mostrarDatePicker(edtFechaObjetivo));

        // Botón siguiente
        btnSiguiente.setOnClickListener(v -> {
            viewModel.setTitulo(edtTitulo.getText().toString());
            viewModel.setFechaCreacion(edtFechaCreacion.getText().toString());
            viewModel.setFechaObjetivo(edtFechaObjetivo.getText().toString());
            viewModel.setProgreso(spinnerProgreso.getSelectedItemPosition() * 10);
            viewModel.setPrioritaria(cbPrioritaria.isChecked());

            // Si es edición, no hay pasos
            if (requireActivity() instanceof CrearTareaAtivity) {
                ((CrearTareaAtivity) requireActivity()).cargarPaso2();
            }
        });

        return root;
    }

    private void mostrarDatePicker(EditText editText) {
        final Calendar calendario = Calendar.getInstance();
        int año = calendario.get(Calendar.YEAR);
        int mes = calendario.get(Calendar.MONTH);
        int dia = calendario.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog picker = new DatePickerDialog(requireContext(),
                (view, year, month, dayOfMonth) -> {
                    String fecha = String.format("%02d/%02d/%04d", dayOfMonth, month + 1, year);
                    editText.setText(fecha);
                }, año, mes, dia);
        picker.show();
    }
}
