package com.example.tarealarga1trimestre.Activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import com.example.tarealarga1trimestre.Activity.CrearEditar.FormularioViewModel;
import com.example.tarealarga1trimestre.Activity.CrearEditar.Fragmento1;
import com.example.tarealarga1trimestre.Activity.CrearEditar.Fragmento2;
import com.example.tarealarga1trimestre.Manager.LocaleHelper;
import com.example.tarealarga1trimestre.Manager.Tarea;
import com.example.tarealarga1trimestre.R;

public class EditarTareaActivity extends AppCompatActivity {

    private FormularioViewModel viewModel;
    private Tarea tareaOriginal;
    private int posicion;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        LocaleHelper.applyLocale(this);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_editar_fragmento_1);

        viewModel = new ViewModelProvider(this).get(FormularioViewModel.class);

        tareaOriginal = getIntent().getParcelableExtra("TAREA_EDITAR");
        posicion = getIntent().getIntExtra("POSICION", -1);

        if (tareaOriginal != null) {
            precargarDatosEnViewModel();
        }

        getSupportFragmentManager().beginTransaction()
                .replace(R.id.contenedorPaso1Editar, new Fragmento1())
                .replace(R.id.contenedorPaso2Editar, new Fragmento2())
                .commit();
    }

    private void precargarDatosEnViewModel() {
        viewModel.setTitulo(tareaOriginal.getTitulo());
        viewModel.setDescripcion(tareaOriginal.getDescripcion());
        viewModel.setProgreso(tareaOriginal.getProgreso());
        viewModel.setFechaCreacion(tareaOriginal.getFechaCreacion() != null ?
                tareaOriginal.getFechaCreacion().toString() : "");
        viewModel.setFechaObjetivo(tareaOriginal.getFechaObjetivo() != null ?
                tareaOriginal.getFechaObjetivo().toString() : "");
        viewModel.setPrioritaria(tareaOriginal.isPrioritaria());
        viewModel.setUrlDoc(tareaOriginal.getUrlDoc());
        viewModel.setUrlImg(tareaOriginal.getUrlImg());
        viewModel.setUrlAud(tareaOriginal.getUrlAud());
        viewModel.setUrlVid(tareaOriginal.getUrlVid());
    }

    public void guardarTareaEditada(Tarea modificada) {
        Intent data = new Intent();
        data.putExtra("TAREA_EDITADA", modificada);
        data.putExtra("POSICION", posicion);
        setResult(Activity.RESULT_OK, data);
        finish();
    }

    public void cargarPaso2() {}
    public void volverPaso1() {}

    @Override
    public boolean onOptionsItemSelected(@NonNull android.view.MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
