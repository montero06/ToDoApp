package com.example.tarealarga1trimestre;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
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

import java.util.ArrayList;

public class ListadoTareasActivity extends AppCompatActivity {

    private boolean favoritoPresionado;
    private RecyclerView recycler;
    private TextView textoVacio;

    // Lista total (Base de datos en memoria)
    private ArrayList<Tarea> listaTareas;
    // Lista que se está viendo actualmente (puede ser la total o solo favoritas)
    private ArrayList<Tarea> listaActualVisualizada;

    private TareaAdapter adapter;

    private ActivityResultLauncher<Intent> crearTareaLauncher;
    private ActivityResultLauncher<Intent> editarTareaLauncher;

    // Para saber qué ítem fue pulsado en el menú contextual
    private int posicionContextual = -1;

    // Listener definido como variable para poder reutilizarlo al cambiar el adapter
    private final TareaAdapter.OnEditarListener listenerEditar = (tarea, position, view) -> {
        posicionContextual = position;
        // 1. Registramos temporalmente esta vista concreta para el menú
        registerForContextMenu(view);
        // 2. Abrimos el menú
        view.showContextMenu();
        // 3. Desregistramos para limpiar
        unregisterForContextMenu(view);
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        LocaleHelper.applyLocale(this); // cambiar de idioma
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_listado_tareas);

        // 1. BUSCAR Y ACTIVAR LA TOOLBAR
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // Ocultar título de la barra
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayShowTitleEnabled(false);
        }

        favoritoPresionado = false;

        // Obtener siempre la MISMA lista desde el Singleton
        listaTareas = ManagerMetodos.getInstance().getDatos();

        recycler = findViewById(R.id.recyclerTareas);
        recycler.setLayoutManager(new LinearLayoutManager(this));
        textoVacio = findViewById(R.id.textoVacio);

        // Inicializar el adapter con la lista completa
        actualizarAdapter(listaTareas);

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
                                // Si estábamos viendo favoritos y la nueva no es favorita,
                                // refrescamos para evitar incongruencias, o simplemente volvemos a cargar todo.
                                if (favoritoPresionado) {
                                    // Si estamos en favoritos, recargamos según la lógica
                                    if(nueva.getPrioritaria()) {
                                        listaActualVisualizada.add(0, nueva);
                                        adapter.notifyItemInserted(0);
                                    }
                                    // Siempre se añade a la principal en el Singleton,
                                    // pero visualmente depende del filtro.
                                } else {
                                    adapter.notifyItemInserted(0);
                                }
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

                                // 1. Actualizar en la lista PRINCIPAL
                                // Obtenemos el objeto viejo que se estaba visualizando
                                Tarea tareaVieja = listaActualVisualizada.get(posicionContextual);

                                // Buscamos su índice en la lista maestra y actualizamos
                                int indexEnPrincipal = listaTareas.indexOf(tareaVieja);
                                if (indexEnPrincipal != -1) {
                                    listaTareas.set(indexEnPrincipal, editada);
                                }

                                // 2. Actualizar en la lista VISUALIZADA
                                listaActualVisualizada.set(posicionContextual, editada);
                                adapter.notifyItemChanged(posicionContextual);

                                // Si editamos una tarea y le quitamos "Prioritaria" mientras vemos favoritos,
                                // debería desaparecer de la lista.
                                if (favoritoPresionado && !editada.getPrioritaria()) {
                                    listaActualVisualizada.remove(posicionContextual);
                                    adapter.notifyItemRemoved(posicionContextual);
                                }

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
    }

    // Método Helper para cambiar de lista y mantener el listener funcionando
    private void actualizarAdapter(ArrayList<Tarea> datos) {
        listaActualVisualizada = datos;
        adapter = new TareaAdapter(datos);
        // ASIGNAMOS EL LISTENER CADA VEZ QUE CREAMOS EL ADAPTER
        adapter.setOneditarListener(listenerEditar);
        recycler.setAdapter(adapter);
        isNoTareas();
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
                // FILTRAR
                ArrayList<Tarea> listaTareasFavoritas = new ArrayList<>();
                for (Tarea tarea : listaTareas) {
                    if (tarea.getPrioritaria()) {
                        listaTareasFavoritas.add(tarea);
                    }
                }
                // Usamos el helper
                actualizarAdapter(listaTareasFavoritas);
            } else {
                // MOSTRAR TODAS
                actualizarAdapter(listaTareas);
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

        // Obtenemos la tarea de la lista QUE SE ESTÁ VIENDO
        Tarea tareaSeleccionada = listaActualVisualizada.get(posicionContextual);

        if (item.getItemId() == R.id.cm_editar) {
            Intent intent = new Intent(this, EditarTareaActivity.class);
            intent.putExtra("TAREA_EDITAR", tareaSeleccionada);
            editarTareaLauncher.launch(intent);
            return true;
        } else if (item.getItemId() == R.id.cm_eliminar) {
            // 1. Borrar de la lista visual
            listaActualVisualizada.remove(posicionContextual);
            adapter.notifyItemRemoved(posicionContextual);

            // 2. Si estamos en favoritos (la lista visual no es la principal),
            // hay que borrar también de la lista principal.
            if (listaActualVisualizada != listaTareas) {
                listaTareas.remove(tareaSeleccionada);
            }

            isNoTareas();
            return true;
        } else {
            return super.onContextItemSelected(item);
        }
    }

    private void isNoTareas() {
        // Comprobamos si la lista actual (sea filtrada o completa) está vacía
        if (listaActualVisualizada.isEmpty()) {
            recycler.setVisibility(View.GONE);
            textoVacio.setVisibility(View.VISIBLE);
        } else {
            recycler.setVisibility(View.VISIBLE);
            textoVacio.setVisibility(View.GONE);
        }
    }
}
