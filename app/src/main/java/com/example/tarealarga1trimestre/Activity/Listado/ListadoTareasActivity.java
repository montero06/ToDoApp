package com.example.tarealarga1trimestre.Activity.Listado;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.preference.PreferenceManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.ContextMenu;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.example.tarealarga1trimestre.Activity.CrearTareaAtivity;
import com.example.tarealarga1trimestre.Activity.DetalleTareaActivity;
import com.example.tarealarga1trimestre.Activity.EditarTareaActivity;
import com.example.tarealarga1trimestre.Activity.Preferencias.PreferenciasActivity;
import com.example.tarealarga1trimestre.Data.TaskStats;
import com.example.tarealarga1trimestre.Data.TareaRepository;
import com.example.tarealarga1trimestre.Manager.LocaleHelper;
import com.example.tarealarga1trimestre.Manager.Tarea;
import com.example.tarealarga1trimestre.Manager.TareaAdapter;
import com.example.tarealarga1trimestre.Manager.utilLetra;
import com.example.tarealarga1trimestre.R;

import java.text.Collator;
import java.util.Locale;
import java.util.ArrayList;

public class ListadoTareasActivity extends AppCompatActivity {

    private boolean favoritoPresionado;
    private RecyclerView recycler;
    private TextView textoVacio;

    private final ArrayList<Tarea> listaTareas = new ArrayList<>();
    private ArrayList<Tarea> listaActualVisualizada = new ArrayList<>();

    private TareaAdapter adapter;
    private TareaRepository repository;

    private ActivityResultLauncher<Intent> crearTareaLauncher;
    private ActivityResultLauncher<Intent> editarTareaLauncher;

    private int posicionContextual = -1;

    private final TareaAdapter.OnItemClickListener listenerDetalle = this::abrirDetalleTarea;

    private final TareaAdapter.OnEditarListener listenerEditar = (tarea, position, view) -> {
        posicionContextual = position;
        registerForContextMenu(view);
        view.showContextMenu();
        unregisterForContextMenu(view);
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        LocaleHelper.applyLocale(this);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_listado_tareas);

        repository = new TareaRepository(this);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) getSupportActionBar().setDisplayShowTitleEnabled(false);

        favoritoPresionado = false;

        recycler = findViewById(R.id.recyclerTareas);
        recycler.setLayoutManager(new LinearLayoutManager(this));
        textoVacio = findViewById(R.id.textoVacio);

        adapter = new TareaAdapter(new ArrayList<>());
        adapter.setOneditarListener(listenerEditar);
        adapter.setOnItemClickListener(listenerDetalle);
        recycler.setAdapter(adapter);

        observarTareas();

        crearTareaLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == Activity.RESULT_OK) {
                        Intent data = result.getData();
                        if (data != null && data.hasExtra("TAREA_NUEVA")) {
                            Tarea nueva = data.getParcelableExtra("TAREA_NUEVA");
                            if (nueva != null) {
                                repository.insert(nueva);
                            }
                        }
                    }
                }
        );

        editarTareaLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == Activity.RESULT_OK) {
                        Intent data = result.getData();
                        if (data != null && data.hasExtra("TAREA_EDITADA")) {
                            Tarea editada = data.getParcelableExtra("TAREA_EDITADA");
                            int posicion = data.getIntExtra("POSICION", -1);
                            if (editada != null && posicion != -1 && posicion < listaActualVisualizada.size()) {
                                Tarea tareaVieja = listaActualVisualizada.get(posicion);
                                editada.setId(tareaVieja.getId());
                                repository.update(editada);
                            }
                        }
                    }
                }
        );

        findViewById(R.id.btnAgregarTarea).setOnClickListener(v -> {
            Intent intent = new Intent(this, CrearTareaAtivity.class);
            crearTareaLauncher.launch(intent);
        });
    }

    private void observarTareas() {
        repository.getAllTareas().observe(this, tareas -> {
            listaTareas.clear();
            if (tareas != null) {
                listaTareas.addAll(tareas);
            }
            actualizarListaVisualizada();
        });
    }

    private void actualizarListaVisualizada() {
        ArrayList<Tarea> listaFiltrada = new ArrayList<>();

        if (favoritoPresionado) {
            for (Tarea t : listaTareas) {
                if (t.isPrioritaria()) listaFiltrada.add(t);
            }
        } else {
            listaFiltrada.addAll(listaTareas);
        }

        aplicarOrdenacion(listaFiltrada);

        listaActualVisualizada = listaFiltrada;
        adapter.setDatos(listaActualVisualizada);

        float textSize = pxToSp(utilLetra.getTamanoLetra(this));
        adapter.setTamanodeLetra(textSize);

        textoVacio.setTextSize(textSize);
        isNoTareas();
    }

    private void aplicarOrdenacion(ArrayList<Tarea> lista) {
        if (lista.isEmpty()) return;

        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);

        String criterio = prefs.getString("criterio", "2");
        boolean ascendente = prefs.getBoolean("orden", true);

        Collator collator = Collator.getInstance(new Locale("es", "ES"));
        collator.setStrength(Collator.PRIMARY);

        lista.sort((t1, t2) -> {
            int cmp;
            switch (criterio) {
                case "1":
                    String titulo1 = normalizarTitulo(t1.getTitulo());
                    String titulo2 = normalizarTitulo(t2.getTitulo());
                    cmp = collator.compare(titulo1, titulo2);
                    break;
                case "2":
                    cmp = t1.getFechaCreacion().compareTo(t2.getFechaCreacion());
                    break;
                case "3":
                    cmp = t1.getFechaObjetivo().compareTo(t2.getFechaObjetivo());
                    break;
                case "4":
                    cmp = Integer.compare(t1.getProgreso(), t2.getProgreso());
                    break;
                default:
                    cmp = 0;
            }
            if (cmp == 0) {
                cmp = Long.compare(t1.getId(), t2.getId());
            }
            return ascendente ? cmp : -cmp;
        });
    }

    @Override
    protected void onResume() {
        super.onResume();

        float textSize = pxToSp(utilLetra.getTamanoLetra(this));
        if (adapter != null) adapter.setTamanodeLetra(textSize);

        actualizarListaVisualizada();

        textoVacio.setTextSize(textSize);
    }

    private float pxToSp(float px) {
        return px / getResources().getDisplayMetrics().scaledDensity;
    }

    private void abrirDetalleTarea(Tarea tarea) {
        Intent intent = new Intent(this, DetalleTareaActivity.class);
        intent.putExtra("TAREA_DETALLE", tarea);
        startActivity(intent);
    }

    private String normalizarTitulo(String titulo) {
        if (titulo == null) return "";
        return titulo.trim();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu, menu);
        menu.setGroupVisible(R.id.it_groupIconos, true);
        menu.setGroupVisible(R.id.it_groupMasOpciones, true);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.it_addTarea) {
            Intent intent = new Intent(this, CrearTareaAtivity.class);
            crearTareaLauncher.launch(intent);
        } else if (id == R.id.it_favoritos) {
            favoritoPresionado = !favoritoPresionado;
            actualizarListaVisualizada();
        } else if (id == R.id.it_preferencias) {
            startActivity(new Intent(this, PreferenciasActivity.class));
        } else if (id == R.id.it_acercaDe) {
            new AlertDialog.Builder(this)
                    .setMessage("Esta aplicación ha sido creada por Juan Montero llamada Taskeitos en 2025 :)")
                    .setPositiveButton(R.string.ok, null)
                    .show();
        } else if (id == R.id.it_estadisticas) {
            mostrarEstadisticas();
        } else if (id == R.id.it_salir) {
            finishAffinity();
        }

        return super.onOptionsItemSelected(item);
    }

    private void mostrarEstadisticas() {
        repository.getStats((TaskStats stats) -> runOnUiThread(() -> {
            String detalle = getString(R.string.estadisticas_detalle,
                    stats.getTotal(),
                    stats.getPrioritarias(),
                    stats.getNoIniciadas(),
                    stats.getEnProgreso(),
                    stats.getCompletadas(),
                    stats.getProgresoPromedio(),
                    stats.getDiasPromedioObjetivo());

            new AlertDialog.Builder(this)
                    .setTitle(R.string.estadisticas)
                    .setMessage(detalle)
                    .setPositiveButton(R.string.ok, null)
                    .show();
        }));
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        getMenuInflater().inflate(R.menu.menu_contextual, menu);
    }

    @Override
    public boolean onContextItemSelected(@NonNull MenuItem item) {
        if (posicionContextual == -1 || posicionContextual >= listaActualVisualizada.size()) {
            return super.onContextItemSelected(item);
        }

        Tarea tareaSeleccionada = listaActualVisualizada.get(posicionContextual);

        if (item.getItemId() == R.id.cm_editar) {
            Intent intent = new Intent(this, EditarTareaActivity.class);
            intent.putExtra("TAREA_EDITAR", tareaSeleccionada);
            intent.putExtra("POSICION", posicionContextual);
            editarTareaLauncher.launch(intent);
            return true;
        } else if (item.getItemId() == R.id.cm_eliminar) {
            repository.delete(tareaSeleccionada);
            return true;
        } else return super.onContextItemSelected(item);
    }

    private void isNoTareas() {
        if (listaActualVisualizada.isEmpty()) {
            recycler.setVisibility(View.GONE);
            textoVacio.setVisibility(View.VISIBLE);
        } else {
            recycler.setVisibility(View.VISIBLE);
            textoVacio.setVisibility(View.GONE);
        }
    }
}
