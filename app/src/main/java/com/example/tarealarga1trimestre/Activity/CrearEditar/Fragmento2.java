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
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.example.tarealarga1trimestre.Activity.CrearTareaAtivity;
import com.example.tarealarga1trimestre.Activity.EditarTareaActivity;
import com.example.tarealarga1trimestre.Manager.Tarea;
import com.example.tarealarga1trimestre.R;

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

        // Inicializar vistas
        edtDescripcion = root.findViewById(R.id.edtDescripcion);
        btnVolver = root.findViewById(R.id.btnVolver);
        btnGuardar = root.findViewById(R.id.btnGuardar);

        btnAgregarDocumento = root.findViewById(R.id.btnAgregarDocumento);
        btnAgregarImagen = root.findViewById(R.id.btnAgregarImagen);
        btnAgregarAudio = root.findViewById(R.id.btnAgregarAudio);
        btnAgregarVideo = root.findViewById(R.id.btnAgregarVideo);
        contenedorArchivos = root.findViewById(R.id.contenedorArchivos);

        // Cargar descripción si ya existe en ViewModel
        if (viewModel.getDescripcion() != null) {
            edtDescripcion.setText(viewModel.getDescripcion());
        }

        // -------------------------
        // Botones Volver y Guardar
        // -------------------------
        btnVolver.setOnClickListener(v -> requireActivity().finish());

        btnGuardar.setOnClickListener(v -> {
            // Guardar descripción en ViewModel
            viewModel.setDescripcion(edtDescripcion.getText().toString());

            // Convertir strings de fechas a LocalDate
            LocalDate fechaCreacionLD = null;
            LocalDate fechaObjetivoLD = null;
            try {
                if (viewModel.getFechaCreacion() != null && !viewModel.getFechaCreacion().isEmpty())
                    fechaCreacionLD = LocalDate.parse(viewModel.getFechaCreacion(), DateTimeFormatter.ofPattern("dd/MM/yyyy"));
                if (viewModel.getFechaObjetivo() != null && !viewModel.getFechaObjetivo().isEmpty())
                    fechaObjetivoLD = LocalDate.parse(viewModel.getFechaObjetivo(), DateTimeFormatter.ofPattern("dd/MM/yyyy"));
            } catch (Exception e) {
                e.printStackTrace();
            }

            // Crear objeto Tarea
            Tarea tarea = new Tarea(
                    viewModel.getTitulo(),
                    viewModel.getDescripcion(),
                    viewModel.getProgreso(),
                    fechaCreacionLD != null ? fechaCreacionLD : LocalDate.now(),
                    fechaObjetivoLD != null ? fechaObjetivoLD : LocalDate.now(),
                    viewModel.isPrioritaria(),
                    viewModel.getUrlDoc(),
                    viewModel.getUrlImg(),
                    viewModel.getUrlAud(),
                    viewModel.getUrlVid()
            );

            // Enviar a la actividad correspondiente
            if (requireActivity() instanceof CrearTareaAtivity) {
                ((CrearTareaAtivity) requireActivity()).guardarTareaYSalir(tarea);
            } else if (requireActivity() instanceof EditarTareaActivity) {
                ((EditarTareaActivity) requireActivity()).guardarTareaEditada(tarea);
            }
        });




        // -------------------------
        // Inicializar ActivityResultLauncher para archivos
        // -------------------------
        archivoLauncher = registerForActivityResult(
                new ActivityResultContracts.OpenDocument(),
                uri -> {
                    if (uri != null) {
                        guardarArchivoLocal(uri, tipoArchivoSeleccionado);
                    }
                }
        );

        // -------------------------
        // Botones de adjuntar archivos
        // -------------------------
        btnAgregarDocumento.setOnClickListener(v -> {
            tipoArchivoSeleccionado = "documento";
            archivoLauncher.launch(new String[]{
                    "application/pdf",
                    "application/msword",
                    "application/vnd.openxmlformats-officedocument.wordprocessingml.document"});
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

    // -------------------------
    // Método para guardar archivo local
    // -------------------------
    private void guardarArchivoLocal(Uri uri, String tipo) {
        String value = uri.toString();
        if (tipo == null) return;

        try {
            requireContext().getContentResolver().takePersistableUriPermission(
                    uri,
                    Intent.FLAG_GRANT_READ_URI_PERMISSION
            );
        } catch (SecurityException ignored) {
        }

        switch (tipo.toLowerCase(Locale.ROOT)) {
            case "documento":
                viewModel.setUrlDoc(value);
                break;
            case "imagen":
                viewModel.setUrlImg(value);
                break;
            case "audio":
                viewModel.setUrlAud(value);
                break;
            case "video":
                viewModel.setUrlVid(value);
                break;
        }

        renderArchivosAdjuntos();
    }

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
        archivoView.setText(getString(R.string.archivo_adjunto_item, tipo, obtenerNombreArchivo(uri)));
        archivoView.setTextSize(14f);
        archivoView.setOnClickListener(v -> abrirAdjunto(uri));
        archivoView.setClickable(true);
        archivoView.setFocusable(true);

        Button btnEliminar = new Button(requireContext());
        btnEliminar.setText(R.string.cm_eliminar);
        btnEliminar.setAllCaps(false);
        btnEliminar.setTextSize(12f);
        btnEliminar.setPadding(dp(12), dp(2), dp(12), dp(2));
        btnEliminar.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#B00020")));
        btnEliminar.setOnClickListener(v -> {
            eliminarAdjuntoPorTipo(tipo);
            renderArchivosAdjuntos();
        });

        row.addView(archivoView);
        row.addView(btnEliminar);
        contenedorArchivos.addView(row);
    }

    private void abrirAdjunto(String uri) {
        try {
            Uri parsedUri = Uri.parse(uri);
            Intent intent = new Intent(Intent.ACTION_VIEW);
            String mimeType = requireContext().getContentResolver().getType(parsedUri);
            if (mimeType != null && !mimeType.trim().isEmpty()) {
                intent.setDataAndType(parsedUri, mimeType);
            } else {
                intent.setData(parsedUri);
            }
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);

            PackageManager packageManager = requireContext().getPackageManager();
            if (intent.resolveActivity(packageManager) != null) {
                startActivity(intent);
            } else {
                Toast.makeText(requireContext(), R.string.no_hay_app_para_adjunto, Toast.LENGTH_SHORT).show();
            }
        } catch (Exception e) {
            Toast.makeText(requireContext(), R.string.error_abrir_adjunto, Toast.LENGTH_SHORT).show();
        }
    }

    private void eliminarAdjuntoPorTipo(String tipo) {
        switch (tipo.toLowerCase(Locale.ROOT)) {
            case "documento":
                viewModel.setUrlDoc(null);
                break;
            case "imagen":
                viewModel.setUrlImg(null);
                break;
            case "audio":
                viewModel.setUrlAud(null);
                break;
            case "video":
                viewModel.setUrlVid(null);
                break;
        }
    }

    private String obtenerNombreArchivo(String uri) {
        Uri parsedUri = Uri.parse(uri);

        try (android.database.Cursor cursor = requireContext()
                .getContentResolver()
                .query(parsedUri, new String[]{OpenableColumns.DISPLAY_NAME}, null, null, null)) {
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

        String lastSegment = parsedUri.getLastPathSegment();
        if (lastSegment == null || lastSegment.trim().isEmpty()) return uri;

        int idx = lastSegment.lastIndexOf('/');
        return idx >= 0 ? lastSegment.substring(idx + 1) : lastSegment;
    }

    private int dp(int value) {
        return (int) TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_DIP,
                value,
                getResources().getDisplayMetrics()
        );
    }
}
