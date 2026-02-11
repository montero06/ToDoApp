package com.example.tarealarga1trimestre.Data;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import com.example.tarealarga1trimestre.Manager.Tarea;

import java.util.List;

@Dao
public interface TareaDao {

    @Query("SELECT * FROM tareas")
    LiveData<List<Tarea>> getAllTareas();

    @Query("SELECT * FROM tareas WHERE prioritaria = 1")
    LiveData<List<Tarea>> getPrioritarias();

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    long insert(Tarea tarea);

    @Update
    void update(Tarea tarea);

    @Delete
    void delete(Tarea tarea);

    @Query("SELECT COUNT(*) FROM tareas")
    int getTotalCount();

    @Query("SELECT AVG(progreso) FROM tareas")
    Double getAverageProgress();

    @Query("SELECT COUNT(*) FROM tareas WHERE prioritaria = 1")
    int getPriorityCount();

    @Query("SELECT COUNT(*) FROM tareas WHERE progreso = 100")
    int getCompletedCount();

    @Query("SELECT COUNT(*) FROM tareas WHERE progreso > 0 AND progreso < 100")
    int getInProgressCount();

    @Query("SELECT COUNT(*) FROM tareas WHERE progreso = 0")
    int getNotStartedCount();

    @Query("SELECT AVG(julianday(fechaObjetivo) - julianday(fechaCreacion)) FROM tareas")
    Double getAverageDaysToTarget();
}
