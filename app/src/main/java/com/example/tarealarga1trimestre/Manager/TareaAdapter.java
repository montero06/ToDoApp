package com.example.tarealarga1trimestre.Manager;

import android.annotation.SuppressLint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.tarealarga1trimestre.R;

import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public class TareaAdapter extends RecyclerView.Adapter<TareaAdapter.TareaViewHolder> {

    private List<Tarea> lista;
    private DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");

    private OnEditarListener editarListener;
    private OnItemClickListener itemClickListener;

    // Tamaño de letra dinámico
    private float textSizeSp = 14f;

    public TareaAdapter(List<Tarea> lista) {
        if (lista != null) {
            this.lista = lista;
        } else {
            this.lista = new ArrayList<>();
        }
    }

    public void setOneditarListener(OnEditarListener listener) {
        this.editarListener = listener;
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        this.itemClickListener = listener;
    }

    // Cambiar tamaño de letra dinámicamente
    public void setTamanodeLetra(float sizeSp) {
        this.textSizeSp = sizeSp;
        notifyDataSetChanged();
    }

    // Actualizar datos de la lista y refrescar RecyclerView
    public void setDatos(List<Tarea> nuevaLista) {
        if (nuevaLista != null) {
            this.lista = nuevaLista;
        } else {
            this.lista = new ArrayList<>();
        }
        notifyDataSetChanged();
    }

    // ----------------------
    // ViewHolder
    // ----------------------
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

        if (t.getFechaObjetivo() != null)
            holder.tvFechaObjetivo.setText("Objetivo: " + t.getFechaObjetivo().format(formatter));
        if (t.getFechaCreacion() != null)
            holder.tvFechaInicio.setText("Creación: " + t.getFechaCreacion().format(formatter));

        holder.tvPrioritaria.setText(t.getPrioritaria() != null && t.getPrioritaria() ? "PRIORITARIA" : "Normal");

        // Aplicar tamaño de letra dinámico
        holder.tvTitulo.setTextSize(textSizeSp);
        holder.tvDescripcion.setTextSize(textSizeSp);
        holder.tvProgreso.setTextSize(textSizeSp);
        holder.tvFechaObjetivo.setTextSize(textSizeSp);
        holder.tvPrioritaria.setTextSize(textSizeSp);
        holder.tvFechaInicio.setTextSize(textSizeSp);

        holder.itemView.setOnClickListener(v -> {
            if (itemClickListener != null) {
                itemClickListener.onItemClick(t);
            }
        });

        // Long click para menú contextual
        holder.itemView.setOnLongClickListener(v -> {
            if (editarListener != null) {
                editarListener.onEditar(t, position, v);
            }
            return true;
        });
    }

    @Override
    public int getItemCount() {
        return lista.size();
    }

    // ----------------------
    // Interfaz para click largo
    // ----------------------
    public interface OnEditarListener {
        void onEditar(Tarea tarea, int position, View view);
    }

    public interface OnItemClickListener {
        void onItemClick(Tarea tarea);
    }
}
