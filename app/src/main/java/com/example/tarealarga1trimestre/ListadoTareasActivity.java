package com.example.tarealarga1trimestre;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class ListadoTareasActivity extends AppCompatActivity {

    private RecyclerView recycler;
    private TextView textoVacio;
    private ArrayList<Tarea> listaTareas;
    private TareaAdapter adapter;

    private ActivityResultLauncher<Intent> crearTareaLauncher;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_listado_tareas);

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

        // Botón para crear tarea
        FloatingActionButton btnCrearTarea = findViewById(R.id.btnAgregarTarea);
        btnCrearTarea.setOnClickListener(v -> {
            Intent intent = new Intent(this, CrearTareaAtivity.class);
            crearTareaLauncher.launch(intent);
        });
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

    // este metodo a sido creado con ia aunque he aprendido a usar la clase calendar y
    //

    private ArrayList<Tarea> generarTareasEjemplo() {
        ArrayList<Tarea> tareas = new ArrayList<>();

        Date hoy = new Date(); // fecha de creación

        // Crear fechas objetivo
        Calendar cal = Calendar.getInstance();

        cal.set(2025, Calendar.JANUARY, 30);
        Date objetivo1 = cal.getTime();

        cal = Calendar.getInstance();
        cal.add(Calendar.DAY_OF_MONTH, 0);
        Date objetivo2 = cal.getTime();

        cal = Calendar.getInstance();
        cal.add(Calendar.DAY_OF_MONTH, 5);
        Date objetivo3 = cal.getTime();

        // Crear tareas usando el nuevo constructor (con prioridad)
        tareas.add(new Tarea(
                "Estudiar Android",
                "Repasar RecyclerView",
                50,
                hoy,
                objetivo1,
                true     // tarea prioritaria
        ));

        tareas.add(new Tarea(
                "Hacer compras",
                "Comprar leche, pan",
                100,
                hoy,
                objetivo2,
                false    // no prioritaria
        ));

        tareas.add(new Tarea(
                "Proyecto final",
                "Terminar app",
                20,
                hoy,
                objetivo3,
                true     // prioritaria
        ));

        return tareas;
    }


}

