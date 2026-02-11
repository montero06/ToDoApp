package com.example.tarealarga1trimestre.Activity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.tarealarga1trimestre.Manager.LocaleHelper;
import com.example.tarealarga1trimestre.Manager.Tarea;
import com.example.tarealarga1trimestre.R;

import java.time.format.DateTimeFormatter;

public class DetalleTareaActivity extends AppCompatActivity {

    private final DateTimeFormatter formatterFecha = DateTimeFormatter.ofPattern("dd/MM/yyyy");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        LocaleHelper.applyLocale(this);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detalle_tarea);

        Tarea tarea = getIntent().getParcelableExtra("TAREA_DETALLE");
        if (tarea == null) {
            finish();
            return;
        }

        cargarDatos(tarea);
    }

    private void cargarDatos(Tarea tarea) {
        ((TextView) findViewById(R.id.tvDetalleTitulo)).setText(valorTexto(tarea.getTitulo(), getString(R.string.sin_titulo)));
        ((TextView) findViewById(R.id.tvDetalleFechaCreacion)).setText(tarea.getFechaCreacion().format(formatterFecha));
        ((TextView) findViewById(R.id.tvDetalleFechaObjetivo)).setText(tarea.getFechaObjetivo().format(formatterFecha));
        ((TextView) findViewById(R.id.tvDetalleProgreso)).setText(getString(R.string.progreso_detalle, tarea.getProgreso()));
        ((TextView) findViewById(R.id.tvDetallePrioridad)).setText(tarea.isPrioritaria() ? getString(R.string.prioridad_alta) : getString(R.string.prioridad_normal));
        ((TextView) findViewById(R.id.tvDetalleDescripcion)).setText(valorTexto(tarea.getDescripcion(), getString(R.string.sinDescripcion)));

        configurarAdjunto(R.id.tvAdjuntoDocumento, getString(R.string.documento), tarea.getUrlDoc());
        configurarAdjunto(R.id.tvAdjuntoImagen, getString(R.string.imagen), tarea.getUrlImg());
        configurarAdjunto(R.id.tvAdjuntoAudio, getString(R.string.audio), tarea.getUrlAud());
        configurarAdjunto(R.id.tvAdjuntoVideo, getString(R.string.video), tarea.getUrlVid());
    }

    private void configurarAdjunto(int viewId, String tipo, String uri) {
        TextView textView = findViewById(viewId);
        if (uri == null || uri.trim().isEmpty()) {
            textView.setText(getString(R.string.archivo_no_adjunto, tipo));
            textView.setEnabled(false);
            textView.setOnClickListener(null);
            return;
        }

        textView.setText(getString(R.string.archivo_adjunto_item, tipo, obtenerNombreArchivo(uri)));
        textView.setEnabled(true);
        textView.setOnClickListener(v -> abrirAdjunto(uri));
    }

    private void abrirAdjunto(String uri) {
        try {
            Intent intent = new Intent(Intent.ACTION_VIEW);
            intent.setData(Uri.parse(uri));
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            startActivity(intent);
        } catch (Exception ignored) {
        }
    }

    private String obtenerNombreArchivo(String uri) {
        Uri parsed = Uri.parse(uri);
        String lastSegment = parsed.getLastPathSegment();
        if (lastSegment == null || lastSegment.trim().isEmpty()) return uri;

        int idx = lastSegment.lastIndexOf('/');
        return idx >= 0 ? lastSegment.substring(idx + 1) : lastSegment;
    }

    private String valorTexto(String value, String fallback) {
        return value == null || value.trim().isEmpty() ? fallback : value;
    }
}
