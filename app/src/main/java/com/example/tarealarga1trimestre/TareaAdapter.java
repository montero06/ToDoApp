package com.example.tarealarga1trimestre;

import android.annotation.SuppressLint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.time.format.DateTimeFormatter;
import java.util.List;

public class TareaAdapter extends RecyclerView.Adapter<TareaAdapter.TareaViewHolder> {

    private List<Tarea> lista;
    private DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
    private OneditarListener editarListener;

    public TareaAdapter(List<Tarea> lista) {
        this.lista = lista;
    }

    public static class TareaViewHolder extends RecyclerView.ViewHolder {
        TextView tvTitulo, tvDescripcion, tvProgreso, tvFechaObjetivo, tvPrioritaria, tvFechaInicio;
        ProgressBar progressBar;

        @SuppressLint("WrongViewCast")
        public TareaViewHolder(@NonNull View itemView) {
            super(itemView);
            tvTitulo = itemView.findViewById(R.id.tvTitulo);
            tvDescripcion = itemView.findViewById(R.id.tvDescripcion);
            tvProgreso = itemView.findViewById(R.id.tvProgresoPorcentaje);
            tvFechaObjetivo = itemView.findViewById(R.id.tvFechaObjetivo);
            tvPrioritaria = itemView.findViewById(R.id.tvPrioritaria);
            progressBar = itemView.findViewById(R.id.progressBar);
            tvFechaInicio = itemView.findViewById(R.id.tvFechaCreacion);
        }
    }

    @NonNull
    @Override
    public TareaViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_tarea_recycler, parent, false);
        return new TareaViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull TareaViewHolder holder, int position) {
        Tarea t = lista.get(position);

        holder.tvTitulo.setText(t.getTitulo());
        holder.tvDescripcion.setText(t.getDescripcion());
        holder.tvProgreso.setText(t.getProgreso() + "%");
        holder.progressBar.setProgress(t.getProgreso());

        holder.tvFechaObjetivo.setText("Objetivo: " + t.getFechaObjetivo().format(formatter));
        holder.tvFechaInicio.setText("Creación: " + t.getFechaCreacion().format(formatter));
        holder.tvPrioritaria.setText(t.getPrioritaria() != null && t.getPrioritaria() ? "PRIORITARIA" : "Normal");

        // Long click para menú contextual
        holder.itemView.setOnLongClickListener(v -> {
            if (editarListener != null) {
                editarListener.onEdit(t, position, v);
            }
            return true;
        });
    }

    @Override
    public int getItemCount() {
        return lista.size();
    }

    // Interfaz para comunicar el long click a la Activity
    public interface OneditarListener {
        void onEdit(Tarea tarea, int position, View view);
    }

    public void setOneditarListener(OneditarListener listener) {
        this.editarListener = listener;
    }
}
