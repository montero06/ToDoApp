package com.example.tarealarga1trimestre.Activity;

import android.os.Bundle;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import com.example.tarealarga1trimestre.Activity.CrearEditar.FormularioViewModel;
import com.example.tarealarga1trimestre.Activity.CrearEditar.Fragmento1;
import com.example.tarealarga1trimestre.Activity.CrearEditar.Fragmento2;
import com.example.tarealarga1trimestre.Manager.LocaleHelper;
import com.example.tarealarga1trimestre.Manager.Tarea;
import com.example.tarealarga1trimestre.Manager.utilLetra;
import com.example.tarealarga1trimestre.R;

public class CrearTareaAtivity extends AppCompatActivity {

    private FormularioViewModel viewModel;
    private TextView tvTitulo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        LocaleHelper.applyLocale(this);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.actvity_crear_tarea);

        viewModel = new ViewModelProvider(this).get(FormularioViewModel.class);

        tvTitulo = findViewById(R.id.tvTituloActividad);
        aplicarTamanoLetra();

        getSupportFragmentManager().beginTransaction()
                .replace(R.id.contenedorFragments, new Fragmento1())
                .commit();
    }

    private void aplicarTamanoLetra() {
        float tamanoPx = utilLetra.getTamanoLetra(this);
        float tamanoSp = pxAsp(tamanoPx);
        if (tvTitulo != null) tvTitulo.setTextSize(tamanoSp);
    }

    private float pxAsp(float px) {
        return px / getResources().getDisplayMetrics().scaledDensity;
    }

    // Navegación fragmentos
    public void volverPaso1() {
        getSupportFragmentManager().popBackStack();
    }

    public void cargarPaso2() {
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.contenedorFragments, new Fragmento2())
                .addToBackStack(null)
                .commit();
    }

    // Guardar tarea nueva
    public void guardarTareaYSalir(Tarea nueva) {
        setResult(RESULT_OK, new android.content.Intent().putExtra("TAREA_NUEVA", nueva));
        finish();
    }
}
