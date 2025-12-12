package com.example.tarealarga1trimestre;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Random;

public class ManagerMetodos {

    private static ManagerMetodos instance;
    private ArrayList<Tarea> datos;

    private ManagerMetodos() {
        datos = init(); // Se genera solo una vez
    }

    public static ManagerMetodos getInstance() {
        if (instance == null) {
            instance = new ManagerMetodos();
        }
        return instance;
    }

    public ArrayList<Tarea> getDatos() {
        return datos;
    }

    public void addTarea(Tarea tarea) {
        datos.add(0, tarea);
    }

    private ArrayList<Tarea> init() {
        ArrayList<Tarea> lista = new ArrayList<>();
        Random random = new Random();

        for (int i = 1; i <= 20; i++) {

            // Fecha de creación aleatoria: entre hace 0 y 30 días
            LocalDate fechaCreacion = LocalDate.now().minusDays(random.nextInt(30));

            // Fecha objetivo: entre 1 y 30 días después de la creación
            LocalDate fechaObjetivo = fechaCreacion.plusDays(random.nextInt(30) + 1);

            lista.add(new Tarea(
                    "Tarea " + i,
                    "Descripción de la tarea número " + i,
                    (i * 5) % 100,             // progreso
                    fechaCreacion,             // LocalDate
                    fechaObjetivo,             // LocalDate
                    i % 2 == 0                 // prioritaria
            ));
        }

        return lista;
    }
}
