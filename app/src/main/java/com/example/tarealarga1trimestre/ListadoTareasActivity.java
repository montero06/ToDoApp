package com.example.tarealarga1trimestre;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.ContextMenu;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import java.time.LocalDate;
import java.util.ArrayList;

public class ListadoTareasActivity extends AppCompatActivity {

    private boolean favoritoPresionado;
    private RecyclerView recycler;
    private TextView textoVacio;
    private ArrayList<Tarea> listaTareas;
    private TareaAdapter adapter;

    private ActivityResultLauncher<Intent> crearTareaLauncher;
    private ActivityResultLauncher<Intent> editarTareaLauncher;

    // Para saber qué ítem fue pulsado en el menú contextual
    private int posicionContextual = -1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        LocaleHelper.applyLocale(this);; // cambiar de idoma
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_listado_tareas);

        // Ocultar título de la barra
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayShowTitleEnabled(false);
        }

        favoritoPresionado = false;

        // Obtener siempre la MISMA lista desde el Singleton
        listaTareas = ManagerMetodos.getInstance().getDatos();

        recycler = findViewById(R.id.recyclerTareas);
        textoVacio = findViewById(R.id.textoVacio);

        // Crear adapter usando la misma lista
        adapter = new TareaAdapter(listaTareas);
        recycler.setAdapter(adapter);
        recycler.setLayoutManager(new LinearLayoutManager(this));

        // Registrar RecyclerView para menú contextual
        registerForContextMenu(recycler);

        isNoTareas();

        // Registrar launcher para recibir la tarea creada
        crearTareaLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == Activity.RESULT_OK) {
                        Intent data = result.getData();
                        if (data != null && data.hasExtra("TAREA_NUEVA")) {
                            Tarea nueva = data.getParcelableExtra("TAREA_NUEVA");
                            if (nueva != null) {
                                ManagerMetodos.getInstance().addTarea(nueva);
                                adapter.notifyItemInserted(0);
                                recycler.scrollToPosition(0);
                                isNoTareas();
                            }
                        }
                    }
                }
        );

        // Registrar launcher para editar tareas
        editarTareaLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == Activity.RESULT_OK) {
                        Intent data = result.getData();
                        if (data != null && data.hasExtra("TAREA_EDITADA") && posicionContextual != -1) {
                            Tarea editada = data.getParcelableExtra("TAREA_EDITADA");
                            if (editada != null) {
                                listaTareas.set(posicionContextual, editada);
                                adapter.notifyItemChanged(posicionContextual);
                                isNoTareas();
                            }
                        }
                    }
                }
        );

        // Botón para crear tarea
        findViewById(R.id.btnAgregarTarea).setOnClickListener(v -> {
            Intent intent = new Intent(this, CrearTareaAtivity.class);
            crearTareaLauncher.launch(intent);
        });

        // Listener para long click en ítem (abrirá menú contextual)
        adapter.setOneditarListener((tarea, position, view) -> {
            posicionContextual = position;
            view.showContextMenu();  // Aquí se muestra el menú contextual
        });
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
            if (favoritoPresionado) {
                ArrayList<Tarea> listaTareasFavoritas = new ArrayList<>();
                for (Tarea tarea : listaTareas) {
                    if (tarea.getPrioritaria()) {
                        listaTareasFavoritas.add(tarea);
                    }
                }
                adapter = new TareaAdapter(listaTareasFavoritas);
                recycler.setAdapter(adapter);
            } else {
                adapter = new TareaAdapter(listaTareas);
                recycler.setAdapter(adapter);
            }
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

    // MENÚ CONTEXTUAL
    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        getMenuInflater().inflate(R.menu.menu_contextual, menu);
    }

    @Override
    public boolean onContextItemSelected(@NonNull MenuItem item) {
        if (posicionContextual == -1) return super.onContextItemSelected(item);

        Tarea tarea = listaTareas.get(posicionContextual);

        if (item.getItemId() == R.id.cm_editar) {
            Intent intent = new Intent(this, EditarTareaActivity.class);
            intent.putExtra("TAREA_EDITAR", tarea);
            editarTareaLauncher.launch(intent);
            return true;
        } else if (item.getItemId() == R.id.cm_eliminar) {
            listaTareas.remove(posicionContextual);
            adapter.notifyItemRemoved(posicionContextual);
            isNoTareas();
            return true;
        } else {
            return super.onContextItemSelected(item);
        }
    }

    private void isNoTareas() {
        if (listaTareas.isEmpty()) {
            recycler.setVisibility(View.GONE);
            textoVacio.setVisibility(View.VISIBLE);
        } else {
            recycler.setVisibility(View.VISIBLE);
            textoVacio.setVisibility(View.GONE);
        }
    }
}
