package com.example.tarealarga1trimestre.Activity;

import android.app.AlertDialog;
import android.content.ContentResolver;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.pdf.PdfRenderer;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.ParcelFileDescriptor;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.VideoView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;

import com.example.tarealarga1trimestre.Manager.LocaleHelper;
import com.example.tarealarga1trimestre.Manager.Tarea;
import com.example.tarealarga1trimestre.R;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.time.format.DateTimeFormatter;

public class DetalleTareaActivity extends AppCompatActivity {

    private final DateTimeFormatter formatterFecha = DateTimeFormatter.ofPattern("dd/MM/yyyy");
    private MediaPlayer mediaPlayer;

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

    @Override
    protected void onDestroy() {
        liberarAudio();
        super.onDestroy();
    }

    private void cargarDatos(Tarea tarea) {
        ((TextView) findViewById(R.id.tvDetalleTitulo)).setText(valorTexto(tarea.getTitulo(), getString(R.string.sin_titulo)));
        ((TextView) findViewById(R.id.tvDetalleFechaCreacion)).setText(tarea.getFechaCreacion().format(formatterFecha));
        ((TextView) findViewById(R.id.tvDetalleFechaObjetivo)).setText(tarea.getFechaObjetivo().format(formatterFecha));
        ((TextView) findViewById(R.id.tvDetalleProgreso)).setText(getString(R.string.progreso_detalle, tarea.getProgreso()));
        ((TextView) findViewById(R.id.tvDetallePrioridad)).setText(tarea.isPrioritaria() ? getString(R.string.prioridad_alta) : getString(R.string.prioridad_normal));
        ((TextView) findViewById(R.id.tvDetalleDescripcion)).setText(valorTexto(tarea.getDescripcion(), getString(R.string.sinDescripcion)));

        configurarAdjunto(R.id.tvAdjuntoDocumento, getString(R.string.documento), tarea.getUrlDoc(), "documento");
        configurarAdjunto(R.id.tvAdjuntoImagen, getString(R.string.imagen), tarea.getUrlImg(), "imagen");
        configurarAdjunto(R.id.tvAdjuntoAudio, getString(R.string.audio), tarea.getUrlAud(), "audio");
        configurarAdjunto(R.id.tvAdjuntoVideo, getString(R.string.video), tarea.getUrlVid(), "video");
    }

    private void configurarAdjunto(int viewId, String tipo, String uri, String tipoAdjunto) {
        TextView textView = findViewById(viewId);
        if (uri == null || uri.trim().isEmpty()) {
            textView.setText(getString(R.string.archivo_no_adjunto, tipo));
            textView.setEnabled(false);
            textView.setOnClickListener(null);
            return;
        }

        textView.setText(getString(R.string.archivo_adjunto_item, tipo, obtenerNombreArchivo(uri)));
        textView.setEnabled(true);
        textView.setOnClickListener(v -> abrirAdjuntoEnApp(uri, tipoAdjunto));
    }

    private void abrirAdjuntoEnApp(String uri, String tipoAdjunto) {
        Uri targetUri = obtenerUriCompartible(uri);
        if (targetUri == null) {
            Toast.makeText(this, R.string.error_abrir_archivo, Toast.LENGTH_SHORT).show();
            return;
        }

        switch (tipoAdjunto) {
            case "imagen":
                mostrarImagenEnDialogo(targetUri);
                break;
            case "audio":
                reproducirAudioEnDialogo(targetUri);
                break;
            case "video":
                reproducirVideoEnDialogo(targetUri);
                break;
            default:
                mostrarDocumentoEnDialogo(targetUri);
                break;
        }
    }

    private void mostrarImagenEnDialogo(Uri uri) {
        try {
            Bitmap bitmap;
            try (InputStream input = getContentResolver().openInputStream(uri)) {
                if (input == null) throw new IOException();
                bitmap = BitmapFactory.decodeStream(input);
            }

            if (bitmap == null) {
                Toast.makeText(this, R.string.error_abrir_archivo, Toast.LENGTH_SHORT).show();
                return;
            }

            ImageView imageView = new ImageView(this);
            imageView.setAdjustViewBounds(true);
            imageView.setImageBitmap(bitmap);

            new AlertDialog.Builder(this)
                    .setTitle(R.string.imagen)
                    .setView(imageView)
                    .setPositiveButton(android.R.string.ok, null)
                    .show();
        } catch (Exception e) {
            Toast.makeText(this, R.string.error_abrir_archivo, Toast.LENGTH_SHORT).show();
        }
    }

    private void reproducirVideoEnDialogo(Uri uri) {
        VideoView videoView = new VideoView(this);
        videoView.setVideoURI(uri);

        AlertDialog dialog = new AlertDialog.Builder(this)
                .setTitle(R.string.video)
                .setView(videoView)
                .setPositiveButton(android.R.string.ok, (d, which) -> videoView.stopPlayback())
                .create();

        dialog.setOnDismissListener(d -> videoView.stopPlayback());
        dialog.show();
        videoView.start();
    }

    private void reproducirAudioEnDialogo(Uri uri) {
        liberarAudio();

        LinearLayout layout = new LinearLayout(this);
        layout.setOrientation(LinearLayout.VERTICAL);
        int pad = (int) (16 * getResources().getDisplayMetrics().density);
        layout.setPadding(pad, pad, pad, pad);

        TextView estado = new TextView(this);
        estado.setText(R.string.audio);

        Button btnPlayPause = new Button(this);
        btnPlayPause.setText(R.string.play_audio);

        layout.addView(estado);
        layout.addView(btnPlayPause);

        AlertDialog dialog = new AlertDialog.Builder(this)
                .setTitle(R.string.audio)
                .setView(layout)
                .setPositiveButton(android.R.string.ok, null)
                .create();

        dialog.setOnDismissListener(d -> liberarAudio());

        btnPlayPause.setOnClickListener(v -> {
            try {
                if (mediaPlayer == null) {
                    mediaPlayer = new MediaPlayer();
                    mediaPlayer.setDataSource(this, uri);
                    mediaPlayer.setOnCompletionListener(mp -> {
                        btnPlayPause.setText(R.string.play_audio);
                        estado.setText(R.string.audio_finalizado);
                    });
                    mediaPlayer.prepare();
                    mediaPlayer.start();
                    btnPlayPause.setText(R.string.pause_audio);
                    estado.setText(R.string.audio_reproduciendo);
                } else if (mediaPlayer.isPlaying()) {
                    mediaPlayer.pause();
                    btnPlayPause.setText(R.string.play_audio);
                    estado.setText(R.string.audio_pausado);
                } else {
                    mediaPlayer.start();
                    btnPlayPause.setText(R.string.pause_audio);
                    estado.setText(R.string.audio_reproduciendo);
                }
            } catch (Exception e) {
                Toast.makeText(this, R.string.error_abrir_archivo, Toast.LENGTH_SHORT).show();
                liberarAudio();
            }
        });

        dialog.show();
    }

    private void mostrarDocumentoEnDialogo(Uri uri) {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
            Toast.makeText(this, R.string.documento_no_compatible, Toast.LENGTH_SHORT).show();
            return;
        }

        ParcelFileDescriptor descriptor = null;
        PdfRenderer renderer = null;
        PdfRenderer.Page page = null;

        try {
            ContentResolver resolver = getContentResolver();
            descriptor = resolver.openFileDescriptor(uri, "r");
            if (descriptor == null) throw new IOException();

            renderer = new PdfRenderer(descriptor);
            if (renderer.getPageCount() == 0) throw new IOException();

            page = renderer.openPage(0);
            Bitmap bitmap = Bitmap.createBitmap(page.getWidth(), page.getHeight(), Bitmap.Config.ARGB_8888);
            page.render(bitmap, null, null, PdfRenderer.Page.RENDER_MODE_FOR_DISPLAY);

            ImageView imageView = new ImageView(this);
            imageView.setAdjustViewBounds(true);
            imageView.setImageBitmap(bitmap);

            new AlertDialog.Builder(this)
                    .setTitle(R.string.documento)
                    .setView(imageView)
                    .setPositiveButton(android.R.string.ok, null)
                    .show();
        } catch (Exception e) {
            Toast.makeText(this, R.string.documento_no_compatible, Toast.LENGTH_SHORT).show();
        } finally {
            try {
                if (page != null) page.close();
                if (renderer != null) renderer.close();
                if (descriptor != null) descriptor.close();
            } catch (Exception ignored) {
            }
        }
    }

    private void liberarAudio() {
        if (mediaPlayer != null) {
            try {
                mediaPlayer.release();
            } catch (Exception ignored) {
            }
            mediaPlayer = null;
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
