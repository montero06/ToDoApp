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
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

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

    private int posicionEditando = -1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
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

        // Crear adapter usando la misma lista (misma referencia)
        adapter = new TareaAdapter(listaTareas);
        recycler.setAdapter(adapter);
        recycler.setLayoutManager(new LinearLayoutManager(this));

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
                                // Añadir a la lista del Singleton (MISMA lista del adapter)
                                ManagerMetodos.getInstance().addTarea(nueva);

                                // Notificar al adapter
                                adapter.notifyItemInserted(0);

                                recycler.scrollToPosition(0);
                                isNoTareas();
                            }
                        }
                    }
                }
        );

        // EDITAR
        editarTareaLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == Activity.RESULT_OK) {
                        Intent data = result.getData();

                        if (data != null && data.hasExtra("TAREA_EDITADA") && posicionEditando != -1) {

                            Tarea editada = data.getParcelableExtra("TAREA_EDITADA");

                            if (editada != null) {
                                // Reemplazar en la lista
                                listaTareas.set(posicionEditando, editada);

                                // Notificar al adaptador
                                adapter.notifyItemChanged(posicionEditando);
                                isNoTareas();
                            }
                        }
                    }
                }
        );

        // Botón para crear tarea
        FloatingActionButton btnCrearTarea = findViewById(R.id.btnAgregarTarea);
        btnCrearTarea.setOnClickListener(v -> {
            Intent intent = new Intent(this, CrearTareaAtivity.class);
            crearTareaLauncher.launch(intent);
        });

        adapter.setOneditarListener((tarea, position, view) -> {
            posicionEditando = position;

            Intent intent = new Intent(this, EditarTareaActivity.class);
            intent.putExtra("TAREA_EDITAR", tarea);

            editarTareaLauncher.launch(intent);
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu, menu);
        menu.setGroupVisible(R.id.it_groupIconos,true);
        menu.setGroupVisible(R.id.it_groupMasOpciones,true);
        return super.onCreateOptionsMenu(menu);
    }


    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id=item.getItemId();
        if (item.getItemId() == android.R.id.home){
            finish();
            return true;
        }

        if (id == R.id.it_addTarea) {
            Intent intent = new Intent(this, CrearTareaAtivity.class);
            crearTareaLauncher.launch(intent);
        } else if (id == R.id.it_favoritos) {
            favoritoPresionado = !favoritoPresionado;
            if (favoritoPresionado == true){
                ArrayList<Tarea> listaTareasFavoritas = new ArrayList<>();

                for (Tarea tarea:listaTareas) {
                    if (tarea.getPrioritaria()){
                        listaTareasFavoritas.add(tarea);
                    }
                }

                adapter = new TareaAdapter(listaTareasFavoritas);
                recycler.setAdapter(adapter);
            }else {
                adapter = new TareaAdapter(listaTareas);
                recycler.setAdapter(adapter);
            }
        } else if (id == R.id.it_acercaDe) {
            AlertDialog.Builder alertaAcercaNosotros = new AlertDialog.Builder(this);
            alertaAcercaNosotros.setMessage("Esta aplicacion a sido creada por Juan Montero llamada Taskeitos en 2025 :)");
            alertaAcercaNosotros.setPositiveButton(R.string.ok,null);
            AlertDialog alertaAcerca = alertaAcercaNosotros.create();
            alertaAcerca.show();
        } else if (id == R.id.it_salir) {
            finishAffinity();
        }
        return super.onOptionsItemSelected(item);
    }



    private void isNoTareas() {
        if (listaTareas.isEmpty()) {
            recycler.setVisibility(View.GONE);
            textoVacio.setVisibility(TextView.VISIBLE);
        } else {
            recycler.setVisibility(View.VISIBLE);
            textoVacio.setVisibility(View.GONE);
        }
    }

    private ArrayList<Tarea> generarTareasEjemplo() {
        ArrayList<Tarea> tareas = new ArrayList<>();

        // Fecha de creación: hoy
        LocalDate hoy = LocalDate.now();

        // Fechas objetivo
        LocalDate objetivo1 = LocalDate.of(2025, 1, 30);
        LocalDate objetivo2 = LocalDate.now();              // hoy
        LocalDate objetivo3 = LocalDate.now().plusDays(5);  // +5 días

        // Crear tareas usando LocalDate y el constructor nuevo
        tareas.add(new Tarea(
                "Estudiar Android",
                "Repasar RecyclerView",
                50,
                hoy,
                objetivo1,
                true
        ));

        tareas.add(new Tarea(
                "Hacer compras",
                "Comprar leche, pan",
                100,
                hoy,
                objetivo2,
                false
        ));

        tareas.add(new Tarea(
                "Proyecto final",
                "Terminar app",
                20,
                hoy,
                objetivo3,
                true
        ));

        return tareas;
    }
}
