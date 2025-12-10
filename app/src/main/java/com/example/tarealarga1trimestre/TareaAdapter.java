package com.example.tarealarga1trimestre;

import android.annotation.SuppressLint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.text.SimpleDateFormat;
import java.util.List;

public class TareaAdapter extends RecyclerView.Adapter<TareaAdapter.TareaViewHolder> {

    private List<Tarea> lista;

    public TareaAdapter(List<Tarea> lista) {
        this.lista = lista;
    }

    public static class TareaViewHolder extends RecyclerView.ViewHolder {
        TextView tvTitulo, tvDescripcion, tvProgreso, tvFechaObjetivo, tvPrioritaria;
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

        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
        holder.tvFechaObjetivo.setText("Objetivo: " + sdf.format(t.getFechaObjetivo()));

        holder.tvPrioritaria.setText(t.getPrirotaria() ? "PRIORITARIA" : "Normal");
    }

    @Override
    public int getItemCount() {
        return lista.size();
    }
}
