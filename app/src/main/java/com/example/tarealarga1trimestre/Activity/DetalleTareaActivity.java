package com.example.tarealarga1trimestre.Activity;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.provider.OpenableColumns;
import android.widget.TextView;
import android.widget.Toast;

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
            Uri parsedUri = Uri.parse(uri);
            Intent intent = new Intent(Intent.ACTION_VIEW);
            String mimeType = getContentResolver().getType(parsedUri);
            if (mimeType != null && !mimeType.trim().isEmpty()) {
                intent.setDataAndType(parsedUri, mimeType);
            } else {
                intent.setData(parsedUri);
            }
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);

            PackageManager packageManager = getPackageManager();
            if (intent.resolveActivity(packageManager) != null) {
                startActivity(intent);
            } else {
                Toast.makeText(this, R.string.no_hay_app_para_adjunto, Toast.LENGTH_SHORT).show();
            }
        } catch (Exception e) {
            Toast.makeText(this, R.string.error_abrir_adjunto, Toast.LENGTH_SHORT).show();
        }
    }

    private String obtenerNombreArchivo(String uri) {
        Uri parsed = Uri.parse(uri);

        try (android.database.Cursor cursor = getContentResolver()
                .query(parsed, new String[]{OpenableColumns.DISPLAY_NAME}, null, null, null)) {
            if (cursor != null && cursor.moveToFirst()) {
                int index = cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME);
                if (index >= 0) {
                    String displayName = cursor.getString(index);
                    if (displayName != null && !displayName.trim().isEmpty()) {
                        return displayName;
                    }
                }
            }
        } catch (Exception ignored) {
        }

        String lastSegment = parsed.getLastPathSegment();
        if (lastSegment == null || lastSegment.trim().isEmpty()) return uri;

        int idx = lastSegment.lastIndexOf('/');
        return idx >= 0 ? lastSegment.substring(idx + 1) : lastSegment;
    }

    private String valorTexto(String value, String fallback) {
        return value == null || value.trim().isEmpty() ? fallback : value;
    }
}
