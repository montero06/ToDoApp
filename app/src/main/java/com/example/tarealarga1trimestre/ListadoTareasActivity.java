package com.example.tarealarga1trimestre;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_listado_tareas);

        recycler = findViewById(R.id.recyclerTareas);
        textoVacio = findViewById(R.id.textoVacio);

        recycler.setLayoutManager(new LinearLayoutManager(this));

        // Crear tareas de ejemplo porque no hay base de datos
        listaTareas = generarTareasEjemplo();

        adapter = new TareaAdapter(listaTareas);
        recycler.setAdapter(adapter);

        isNoTareas();
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

    private ArrayList<Tarea> generarTareasEjemplo() {  ArrayList<Tarea> tareas = new ArrayList<>();

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

        // Crear tareas usando el constructor correcto
        tareas.add(new Tarea("Estudiar Android", "Repasar RecyclerView", 50, hoy, objetivo1));
        tareas.add(new Tarea("Hacer compras", "Comprar leche, pan", 100, hoy, objetivo2));
        tareas.add(new Tarea("Proyecto final", "Terminar app", 20, hoy, objetivo3));

        return tareas;
    }

}

