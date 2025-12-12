package com.example.tarealarga1trimestre;

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

            Calendar cal = Calendar.getInstance();
            cal.add(Calendar.DAY_OF_YEAR, -random.nextInt(30));
            Date fechaCreacion = cal.getTime();

            cal.add(Calendar.DAY_OF_YEAR, random.nextInt(30) + 1);
            Date fechaObjetivo = cal.getTime();

            lista.add(new Tarea(
                    "Tarea " + i,
                    "Descripción de la tarea número " + i,
                    (i * 5) % 100,
                    fechaCreacion,
                    fechaObjetivo,
                    i % 2 == 0
            ));
        }

        return lista;
    }
}
