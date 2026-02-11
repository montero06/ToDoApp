package com.example.tarealarga1trimestre.Data;

import android.content.Context;

import androidx.lifecycle.LiveData;

import com.example.tarealarga1trimestre.Manager.Tarea;

import java.time.LocalDate;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class TareaRepository {
    private final TareaDao tareaDao;
    private final LiveData<List<Tarea>> allTareas;
    private final LiveData<List<Tarea>> prioritarias;
    private final ExecutorService executor = Executors.newSingleThreadExecutor();

    public interface StatsCallback {
        void onResult(TaskStats stats);
    }

    public TareaRepository(Context context) {
        AppDatabase db = AppDatabase.getInstance(context);
        tareaDao = db.tareaDao();
        allTareas = tareaDao.getAllTareas();
        prioritarias = tareaDao.getPrioritarias();
    }

    public LiveData<List<Tarea>> getAllTareas() {
        return allTareas;
    }

    public LiveData<List<Tarea>> getPrioritarias() {
        return prioritarias;
    }

    public void insert(Tarea tarea) {
        executor.execute(() -> {
            completarDefaults(tarea);
            tareaDao.insert(tarea);
        });
    }

    public void update(Tarea tarea) {
        executor.execute(() -> {
            completarDefaults(tarea);
            tareaDao.update(tarea);
        });
    }

    public void delete(Tarea tarea) {
        executor.execute(() -> tareaDao.delete(tarea));
    }

    public void getStats(StatsCallback callback) {
        executor.execute(() -> {
            int total = tareaDao.getTotalCount();
            Double avg = tareaDao.getAverageProgress();
            Double avgDays = tareaDao.getAverageDaysToTarget();
            TaskStats stats = new TaskStats(
                    total,
                    tareaDao.getPriorityCount(),
                    tareaDao.getCompletedCount(),
                    tareaDao.getInProgressCount(),
                    tareaDao.getNotStartedCount(),
                    avg == null ? 0 : avg,
                    avgDays == null ? 0 : avgDays
            );
            callback.onResult(stats);
        });
    }

    private void completarDefaults(Tarea tarea) {
        if (tarea.getTitulo() == null || tarea.getTitulo().trim().isEmpty()) {
            tarea.setTitulo("Sin título");
        }
        if (tarea.getFechaCreacion() == null) {
            tarea.setFechaCreacion(LocalDate.now());
        }
        if (tarea.getFechaObjetivo() == null) {
            tarea.setFechaObjetivo(LocalDate.now());
        }
        if (tarea.getProgreso() < 0 || tarea.getProgreso() > 100) {
            tarea.setProgreso(0);
        }
    }
}
