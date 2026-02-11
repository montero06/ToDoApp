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
import com.example.tarealarga1trimestre.Activity.EditarTareaActivity;
import com.example.tarealarga1trimestre.Activity.Preferencias.PreferenciasActivity;
import com.example.tarealarga1trimestre.Manager.LocaleHelper;
import com.example.tarealarga1trimestre.Manager.ManagerMetodos;
import com.example.tarealarga1trimestre.Manager.Tarea;
import com.example.tarealarga1trimestre.Manager.TareaAdapter;
import com.example.tarealarga1trimestre.Manager.utilLetra;
import com.example.tarealarga1trimestre.R;

import java.util.ArrayList;
import java.util.Collections;

public class ListadoTareasActivity extends AppCompatActivity {

    private boolean favoritoPresionado;
    private RecyclerView recycler;
    private TextView textoVacio;

    private ArrayList<Tarea> listaTareas;
    private ArrayList<Tarea> listaActualVisualizada;

    private TareaAdapter adapter;

    private ActivityResultLauncher<Intent> crearTareaLauncher;
    private ActivityResultLauncher<Intent> editarTareaLauncher;

    private int posicionContextual = -1;

    private final TareaAdapter.OnItemClickListener listenerDetalle = tarea -> mostrarDetallesFragmento2(tarea);

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

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) getSupportActionBar().setDisplayShowTitleEnabled(false);

        favoritoPresionado = false;
        listaTareas = ManagerMetodos.getInstance().getDatos();

        recycler = findViewById(R.id.recyclerTareas);
        recycler.setLayoutManager(new LinearLayoutManager(this));
        textoVacio = findViewById(R.id.textoVacio);

        adapter = new TareaAdapter(new ArrayList<>());
        adapter.setOneditarListener(listenerEditar);
        adapter.setOnItemClickListener(listenerDetalle);
        recycler.setAdapter(adapter);

        actualizarListaVisualizada();

        // ----------------------
        // Crear tarea
        // ----------------------
        crearTareaLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == Activity.RESULT_OK) {
                        Intent data = result.getData();
                        if (data != null && data.hasExtra("TAREA_NUEVA")) {
                            Tarea nueva = data.getParcelableExtra("TAREA_NUEVA");
                            if (nueva != null) {
                                ManagerMetodos.getInstance().addTarea(nueva);
                                actualizarListaVisualizada();
                                recycler.scrollToPosition(0);
                            }
                        }
                    }
                }
        );

        // ----------------------
        // Editar tarea
        // ----------------------
        editarTareaLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == Activity.RESULT_OK) {
                        Intent data = result.getData();
                        if (data != null && data.hasExtra("TAREA_EDITADA")) {
                            Tarea editada = data.getParcelableExtra("TAREA_EDITADA");
                            int posicion = data.getIntExtra("POSICION", -1);
                            if (editada != null && posicion != -1) {
                                // Actualizar en lista principal
                                Tarea tareaVieja = listaActualVisualizada.get(posicion);
                                int indexEnPrincipal = listaTareas.indexOf(tareaVieja);
                                if (indexEnPrincipal != -1) listaTareas.set(indexEnPrincipal, editada);

                                actualizarListaVisualizada();
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

    // ----------------------
    // Actualizar lista visualizada
    // ----------------------
    private void actualizarListaVisualizada() {
        ArrayList<Tarea> listaFiltrada = new ArrayList<>();

        // Filtrar por prioridad
        if (favoritoPresionado) {
            for (Tarea t : listaTareas) {
                if (t.getPrioritaria() != null && t.getPrioritaria()) listaFiltrada.add(t);
            }
        } else {
            listaFiltrada.addAll(listaTareas);
        }

        // Ordenar según preferencias actuales
        aplicarOrdenacion(listaFiltrada);

        // Guardar lista final
        listaActualVisualizada = listaFiltrada;

        // Actualizar adapter
        adapter.setDatos(listaActualVisualizada);

        float textSize = pxToSp(utilLetra.getTamanoLetra(this));
        adapter.setTamanodeLetra(textSize);

        textoVacio.setTextSize(textSize);
        isNoTareas();
    }

    // ----------------------
    // Aplicar ordenación según preferencias guardadas
    // ----------------------
    private void aplicarOrdenacion(ArrayList<Tarea> lista) {
        if (lista == null || lista.isEmpty()) return;

        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);

        String criterio = prefs.getString("Criterio", "2");

        boolean ascendente = prefs.getBoolean("Orden", true); // true = ascendente, false = descendente


        lista.sort((t1, t2) -> {
            int cmp = 0;
            switch (criterio) {
                case "1": cmp = t1.getTitulo().compareToIgnoreCase(t2.getTitulo()); break;
                case "2":
                    if (t1.getFechaCreacion() != null && t2.getFechaCreacion() != null)
                        cmp = t1.getFechaCreacion().compareTo(t2.getFechaCreacion());
                    break;
                case "3":
                    if (t1.getFechaObjetivo() != null && t2.getFechaObjetivo() != null)
                        cmp = t1.getFechaObjetivo().compareTo(t2.getFechaObjetivo());
                    break;
                case "4": cmp = Integer.compare(t1.getProgreso(), t2.getProgreso()); break;
            }
            return ascendente ? cmp : -cmp;
        });


        if (!ascendente) Collections.reverse(lista);
    }

    // ----------------------
    // Calcular días restantes para fecha objetivo
    // ----------------------
    private long calcularDiasRestantes(Tarea t) {
        if (t.getFechaObjetivo() == null) return Long.MAX_VALUE;
        return java.time.temporal.ChronoUnit.DAYS.between(
                java.time.LocalDate.now(), t.getFechaObjetivo());
    }

    // ----------------------
    // onResume: se actualiza lista si se cambia preferencia
    // ----------------------
    @Override
    protected void onResume() {
        super.onResume();

        // Reaplicar tamaño de letra
        float textSize = pxToSp(utilLetra.getTamanoLetra(this));
        if (adapter != null) adapter.setTamanodeLetra(textSize);

        // Actualizar lista según preferencias actuales
        actualizarListaVisualizada();

        textoVacio.setTextSize(textSize);
    }

    private float pxToSp(float px) {
        return px / getResources().getDisplayMetrics().scaledDensity;
    }

    private void mostrarDetallesFragmento2(Tarea tarea) {
        String descripcion = tarea.getDescripcion();
        if (descripcion == null || descripcion.trim().isEmpty()) {
            descripcion = getString(R.string.sinDescripcion);
        }

        new AlertDialog.Builder(this)
                .setTitle(getString(R.string.detallesFragmento2Titulo, tarea.getTitulo()))
                .setMessage(descripcion)
                .setPositiveButton(R.string.ok, null)
                .show();
    }

    // ----------------------
    // Menú superior
    // ----------------------
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
        } else if (id == R.id.it_salir) {
            finishAffinity();
        }

        return super.onOptionsItemSelected(item);
    }

    // ----------------------
    // Menú contextual
    // ----------------------
    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        getMenuInflater().inflate(R.menu.menu_contextual, menu);
    }

    @Override
    public boolean onContextItemSelected(@NonNull MenuItem item) {
        if (posicionContextual == -1) return super.onContextItemSelected(item);

        Tarea tareaSeleccionada = listaActualVisualizada.get(posicionContextual);

        if (item.getItemId() == R.id.cm_editar) {
            Intent intent = new Intent(this, EditarTareaActivity.class);
            intent.putExtra("TAREA_EDITAR", tareaSeleccionada);
            intent.putExtra("POSICION", posicionContextual);
            editarTareaLauncher.launch(intent);
            return true;
        } else if (item.getItemId() == R.id.cm_eliminar) {
            listaTareas.remove(tareaSeleccionada);
            actualizarListaVisualizada();
            return true;
        } else return super.onContextItemSelected(item);
    }

    // ----------------------
    // Mostrar mensaje si no hay tareas
    // ----------------------
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
