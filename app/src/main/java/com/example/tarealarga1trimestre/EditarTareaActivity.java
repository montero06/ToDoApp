package com.example.tarealarga1trimestre;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

public class EditarTareaActivity extends AppCompatActivity {

    private FormularioViewModel viewModel;
    private Tarea tareaOriginal;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        LocaleHelper.applyLocale(this); // cambiar de idoma
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_editar_fragmento_1);

        viewModel = new ViewModelProvider(this).get(FormularioViewModel.class);

        // Obtener la tarea pasada desde el Intent
        tareaOriginal = getIntent().getParcelableExtra("TAREA_EDITAR");

        if (tareaOriginal != null) {
            precargarDatosEnViewModel();
        }

        // Cargar los 2 fragmentos a la vez (modo edición)
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.contenedorPaso1Editar, new Fragmento1())
                .replace(R.id.contenedorPaso2Editar, new Fragmento2())
                .commit();
    }

    private void precargarDatosEnViewModel() {
        viewModel.titulo.setValue(tareaOriginal.getTitulo());
        viewModel.descripcion.setValue(tareaOriginal.getDescripcion());
        viewModel.progreso.setValue(tareaOriginal.getProgreso());
        viewModel.fechaCreacion.setValue(tareaOriginal.getFechaCreacion());
        viewModel.fechaObjetivo.setValue(tareaOriginal.getFechaObjetivo());
        viewModel.prioritaria.setValue(tareaOriginal.getPrioritaria());
    }

    public void guardarTareaEditada(Tarea modificada) {
        Intent data = new Intent();
        data.putExtra("TAREA_EDITADA", modificada); // LocalDate ya convertido dentro de Tarea Parcelable
        setResult(Activity.RESULT_OK, data);
        finish();
    }

    // Métodos añadidos para evitar errores en los fragmentos
    public void cargarPaso2() {
        // En edición no hay navegación por pasos → no hacer nada
    }

    public void volverPaso1() {
        // En edición no hay navegación por pasos → no hacer nada
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull android.view.MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
