package com.example.tarealarga1trimestre.Activity.CrearEditar;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.provider.OpenableColumns;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.FileProvider;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.example.tarealarga1trimestre.Activity.CrearTareaAtivity;
import com.example.tarealarga1trimestre.Activity.EditarTareaActivity;
import com.example.tarealarga1trimestre.Manager.Tarea;
import com.example.tarealarga1trimestre.R;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

public class Fragmento2 extends Fragment {

    private FormularioViewModel viewModel;

    private EditText edtDescripcion;
    private Button btnVolver, btnGuardar;
    private ImageButton btnAgregarDocumento, btnAgregarImagen, btnAgregarAudio, btnAgregarVideo;
    private LinearLayout contenedorArchivos;

    private ActivityResultLauncher<String[]> archivoLauncher;
    private String tipoArchivoSeleccionado;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.activity_crear_fragmento_2, container, false);

        viewModel = new ViewModelProvider(requireActivity()).get(FormularioViewModel.class);

        edtDescripcion = root.findViewById(R.id.edtDescripcion);
        btnVolver = root.findViewById(R.id.btnVolver);
        btnGuardar = root.findViewById(R.id.btnGuardar);

        btnAgregarDocumento = root.findViewById(R.id.btnAgregarDocumento);
        btnAgregarImagen = root.findViewById(R.id.btnAgregarImagen);
        btnAgregarAudio = root.findViewById(R.id.btnAgregarAudio);
        btnAgregarVideo = root.findViewById(R.id.btnAgregarVideo);
        contenedorArchivos = root.findViewById(R.id.contenedorArchivos);

        archivoLauncher = registerForActivityResult(
                new ActivityResultContracts.OpenDocument(),
                uri -> {
                    if (uri != null) {
                        guardarArchivoLocal(uri, tipoArchivoSeleccionado);
                    }
                }
        );

        btnAgregarDocumento.setOnClickListener(v -> {
            tipoArchivoSeleccionado = "documento";
            archivoLauncher.launch(new String[]{"application/pdf","application/msword","application/vnd.openxmlformats-officedocument.wordprocessingml.document"});
        });

        btnAgregarImagen.setOnClickListener(v -> {
            tipoArchivoSeleccionado = "imagen";
            archivoLauncher.launch(new String[]{"image/*"});
        });

        btnAgregarAudio.setOnClickListener(v -> {
            tipoArchivoSeleccionado = "audio";
            archivoLauncher.launch(new String[]{"audio/*"});
        });

        btnAgregarVideo.setOnClickListener(v -> {
            tipoArchivoSeleccionado = "video";
            archivoLauncher.launch(new String[]{"video/*"});
        });

        renderArchivosAdjuntos();
        return root;
    }

    /* ---------------- GUARDAR COPIA LOCAL ---------------- */
    private void guardarArchivoLocal(Uri uri, String tipo) {

        if (tipo == null) return;

        try {
            String nombre = obtenerNombreArchivo(uri);

            File carpeta = new File(requireContext().getFilesDir(), "adjuntos");
            if (!carpeta.exists()) carpeta.mkdirs();

            File archivoDestino = new File(carpeta, System.currentTimeMillis() + "_" + nombre);

            InputStream in = requireContext().getContentResolver().openInputStream(uri);
            FileOutputStream out = new FileOutputStream(archivoDestino);

            byte[] buffer = new byte[8192];
            int read;
            while ((read = in.read(buffer)) != -1) {
                out.write(buffer, 0, read);
            }

            in.close();
            out.close();

            String value = archivoDestino.getAbsolutePath();

            switch (tipo.toLowerCase(Locale.ROOT)) {
                case "documento": viewModel.setUrlDoc(value); break;
                case "imagen": viewModel.setUrlImg(value); break;
                case "audio": viewModel.setUrlAud(value); break;
                case "video": viewModel.setUrlVid(value); break;
            }

            renderArchivosAdjuntos();

        } catch (Exception e) {
            Toast.makeText(requireContext(), "Error copiando archivo", Toast.LENGTH_SHORT).show();
        }
    }

    private void abrirAdjunto(String path) {
        try {
            File file = new File(path);

            Uri uri = FileProvider.getUriForFile(
                    requireContext(),
                    requireContext().getPackageName() + ".provider",
                    file
            );

            Intent intent = new Intent(Intent.ACTION_VIEW);
            String mimeType = requireContext().getContentResolver().getType(uri);
            intent.setDataAndType(uri, mimeType);
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);

            if (intent.resolveActivity(requireContext().getPackageManager()) != null) {
                startActivity(intent);
            } else {
                Toast.makeText(requireContext(), R.string.no_hay_app_para_adjunto, Toast.LENGTH_SHORT).show();
            }

        } catch (Exception e) {
            Toast.makeText(requireContext(), R.string.error_abrir_adjunto, Toast.LENGTH_SHORT).show();
        }
    }

    /* ----------- resto de métodos SIN cambios relevantes ----------- */

    private void renderArchivosAdjuntos() {
        if (contenedorArchivos == null) return;

        contenedorArchivos.removeAllViews();
        addArchivoItem(getString(R.string.documento), viewModel.getUrlDoc());
        addArchivoItem(getString(R.string.imagen), viewModel.getUrlImg());
        addArchivoItem(getString(R.string.audio), viewModel.getUrlAud());
        addArchivoItem(getString(R.string.video), viewModel.getUrlVid());
    }

    private void addArchivoItem(String tipo, String uri) {
        if (uri == null || uri.trim().isEmpty()) return;

        LinearLayout row = new LinearLayout(requireContext());
        row.setOrientation(LinearLayout.HORIZONTAL);
        row.setGravity(Gravity.CENTER_VERTICAL);
        row.setPadding(0, dp(4), 0, dp(4));

        TextView archivoView = new TextView(requireContext());
        archivoView.setLayoutParams(new LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT, 1f));
        archivoView.setText(getString(R.string.archivo_adjunto_item, tipo, new File(uri).getName()));
        archivoView.setTextSize(14f);
        archivoView.setOnClickListener(v -> abrirAdjunto(uri));

        Button btnEliminar = new Button(requireContext());
        btnEliminar.setText(R.string.cm_eliminar);
        btnEliminar.setOnClickListener(v -> {
            eliminarAdjuntoPorTipo(tipo);
            renderArchivosAdjuntos();
        });

        row.addView(archivoView);
        row.addView(btnEliminar);
        contenedorArchivos.addView(row);
    }

    private void eliminarAdjuntoPorTipo(String tipo) {
        switch (tipo.toLowerCase(Locale.ROOT)) {
            case "documento": viewModel.setUrlDoc(null); break;
            case "imagen": viewModel.setUrlImg(null); break;
            case "audio": viewModel.setUrlAud(null); break;
            case "video": viewModel.setUrlVid(null); break;
        }
    }

    private String obtenerNombreArchivo(Uri uri) {
        try (android.database.Cursor cursor = requireContext()
                .getContentResolver()
                .query(uri, new String[]{OpenableColumns.DISPLAY_NAME}, null, null, null)) {
            if (cursor != null && cursor.moveToFirst()) {
                return cursor.getString(0);
            }
        } catch (Exception ignored) {}
        return "archivo";
    }

    private int dp(int value) {
        return (int) TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_DIP,
                value,
                getResources().getDisplayMetrics()
        );
    }
}