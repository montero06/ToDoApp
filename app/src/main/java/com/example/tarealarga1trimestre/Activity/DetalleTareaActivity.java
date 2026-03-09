package com.example.tarealarga1trimestre.Activity;

import android.content.Intent;
import android.content.ActivityNotFoundException;
import android.net.Uri;
import android.os.Bundle;
import android.webkit.MimeTypeMap;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;

import com.example.tarealarga1trimestre.Manager.LocaleHelper;
import com.example.tarealarga1trimestre.Manager.Tarea;
import com.example.tarealarga1trimestre.R;

import java.io.File;
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

        configurarAdjunto(R.id.tvAdjuntoDocumento, getString(R.string.documento), tarea.getUrlDoc(), "application/pdf");
        configurarAdjunto(R.id.tvAdjuntoImagen, getString(R.string.imagen), tarea.getUrlImg(), "image/*");
        configurarAdjunto(R.id.tvAdjuntoAudio, getString(R.string.audio), tarea.getUrlAud(), "audio/*");
        configurarAdjunto(R.id.tvAdjuntoVideo, getString(R.string.video), tarea.getUrlVid(), "video/*");
    }

    private void configurarAdjunto(int viewId, String tipo, String uri, String mimeTypePreferido) {
        TextView textView = findViewById(viewId);
        if (uri == null || uri.trim().isEmpty()) {
            textView.setText(getString(R.string.archivo_no_adjunto, tipo));
            textView.setEnabled(false);
            textView.setOnClickListener(null);
            return;
        }

        textView.setText(getString(R.string.archivo_adjunto_item, tipo, obtenerNombreArchivo(uri)));
        textView.setEnabled(true);
        textView.setOnClickListener(v -> abrirAdjunto(uri, mimeTypePreferido));
    }

    private void abrirAdjunto(String uri, String mimeTypePreferido) {
        try {
            Uri targetUri = obtenerUriCompartible(uri);
            if (targetUri == null) {
                Toast.makeText(this, R.string.error_abrir_archivo, Toast.LENGTH_SHORT).show();
                return;
            }

            Intent intent = new Intent(Intent.ACTION_VIEW);
            String mimeType = getContentResolver().getType(targetUri);
            if (mimeType == null) mimeType = obtenerMimeTypeDesdeUri(targetUri);
            if (mimeType == null) mimeType = obtenerMimeTypeDesdeTextoUri(uri);
            if (mimeType == null) mimeType = mimeTypePreferido;
            intent.setDataAndType(targetUri, mimeType != null ? mimeType : "*/*");
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            startActivity(intent);
        } catch (ActivityNotFoundException e) {
            Toast.makeText(this, R.string.no_hay_app_para_abrir_archivo, Toast.LENGTH_SHORT).show();
        } catch (Exception e) {
            Toast.makeText(this, R.string.error_abrir_archivo, Toast.LENGTH_SHORT).show();
        }
    }

    private Uri obtenerUriCompartible(String value) {
        Uri parsed = Uri.parse(value);
        if ("content".equalsIgnoreCase(parsed.getScheme())) {
            return parsed;
        }

        File archivo = "file".equalsIgnoreCase(parsed.getScheme())
                ? new File(parsed.getPath())
                : new File(value);

        if (!archivo.exists()) return null;

        return FileProvider.getUriForFile(
                this,
                getPackageName() + ".fileprovider",
                archivo
        );
    }

    private String obtenerMimeTypeDesdeUri(Uri uri) {
        String extension = MimeTypeMap.getFileExtensionFromUrl(uri.toString());
        if (extension == null || extension.trim().isEmpty()) return null;
        return MimeTypeMap.getSingleton().getMimeTypeFromExtension(extension.toLowerCase());
    }

    private String obtenerMimeTypeDesdeTextoUri(String uriTexto) {
        String extension = MimeTypeMap.getFileExtensionFromUrl(uriTexto);
        if (extension == null || extension.trim().isEmpty()) return null;
        return MimeTypeMap.getSingleton().getMimeTypeFromExtension(extension.toLowerCase());
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
