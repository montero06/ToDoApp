package com.example.tarealarga1trimestre.Data;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Transformations;
import androidx.preference.PreferenceManager;

import com.example.tarealarga1trimestre.Manager.Tarea;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class TareaRepository {
    private static final String TAG = "TareaRepository";

    private final TareaDao tareaDao;
    private final LiveData<List<Tarea>> allTareas;
    private final LiveData<List<Tarea>> prioritarias;
    private final ExecutorService executor = Executors.newSingleThreadExecutor();

    private final boolean usarBdExterna;
    private final ExternalTareaApi externalApi;
    private final MutableLiveData<List<Tarea>> remoteTareas = new MutableLiveData<>(new ArrayList<>());

    public interface StatsCallback {
        void onResult(TaskStats stats);
    }

    public TareaRepository(Context context) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        usarBdExterna = prefs.getBoolean("usar_bd_externa", false);

        if (usarBdExterna) {
            String baseUrl = prefs.getString("url_api_externa", "http://10.0.2.2:8080/api");
            externalApi = new ExternalTareaApi(baseUrl == null ? "http://10.0.2.2:8080/api" : baseUrl);
            tareaDao = null;
            allTareas = remoteTareas;
            prioritarias = Transformations.map(remoteTareas, this::filtrarPrioritarias);
            recargarDesdeApi();
        } else {
            AppDatabase db = AppDatabase.getInstance(context);
            tareaDao = db.tareaDao();
            externalApi = null;
            allTareas = tareaDao.getAllTareas();
            prioritarias = tareaDao.getPrioritarias();
        }
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
            if (usarBdExterna) {
                try {
                    externalApi.insert(tarea);
                    recargarDesdeApi();
                } catch (Exception e) {
                    Log.e(TAG, "Error insertando tarea en API externa", e);
                }
            } else {
                tareaDao.insert(tarea);
            }
        });
    }

    public void update(Tarea tarea) {
        executor.execute(() -> {
            completarDefaults(tarea);
            if (usarBdExterna) {
                try {
                    externalApi.update(tarea);
                    recargarDesdeApi();
                } catch (Exception e) {
                    Log.e(TAG, "Error actualizando tarea en API externa", e);
                }
            } else {
                tareaDao.update(tarea);
            }
        });
    }

    public void delete(Tarea tarea) {
        executor.execute(() -> {
            if (usarBdExterna) {
                try {
                    externalApi.delete(tarea.getId());
                    recargarDesdeApi();
                } catch (Exception e) {
                    Log.e(TAG, "Error eliminando tarea en API externa", e);
                }
            } else {
                tareaDao.delete(tarea);
            }
        });
    }

    public void getStats(StatsCallback callback) {
        executor.execute(() -> {
            if (usarBdExterna) {
                List<Tarea> tareas = remoteTareas.getValue();
                callback.onResult(calcularStatsDesdeLista(tareas == null ? new ArrayList<>() : tareas));
            } else {
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
            }
        });
    }

    private void recargarDesdeApi() {
        executor.execute(() -> {
            try {
                List<Tarea> tareas = externalApi.getAllTareas();
                remoteTareas.postValue(tareas);
            } catch (Exception e) {
                Log.e(TAG, "No se pudieron descargar tareas de la API externa", e);
            }
        });
    }

    private List<Tarea> filtrarPrioritarias(List<Tarea> tareas) {
        ArrayList<Tarea> resultado = new ArrayList<>();
        if (tareas == null) return resultado;
        for (Tarea t : tareas) {
            if (t.isPrioritaria()) resultado.add(t);
        }
        return resultado;
    }

    private TaskStats calcularStatsDesdeLista(List<Tarea> tareas) {
        int total = tareas.size();
        int prioritariasCount = 0;
        int completadas = 0;
        int enProgreso = 0;
        int noIniciadas = 0;
        double sumaProgreso = 0;
        double sumaDias = 0;

        for (Tarea t : tareas) {
            if (t.isPrioritaria()) prioritariasCount++;
            if (t.getProgreso() == 100) completadas++;
            else if (t.getProgreso() > 0) enProgreso++;
            else noIniciadas++;

            sumaProgreso += t.getProgreso();
            sumaDias += t.getFechaObjetivo().toEpochDay() - t.getFechaCreacion().toEpochDay();
        }

        double progresoPromedio = total == 0 ? 0 : sumaProgreso / total;
        double diasPromedio = total == 0 ? 0 : sumaDias / total;

        return new TaskStats(total, prioritariasCount, completadas, enProgreso, noIniciadas,
                progresoPromedio, diasPromedio);
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
