package com.example.tarealarga1trimestre.Activity.CrearEditar;

import android.net.Uri;
import android.os.Bundle;
import android.webkit.MimeTypeMap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.FileProvider;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.example.tarealarga1trimestre.Activity.CrearTareaAtivity;
import com.example.tarealarga1trimestre.Activity.EditarTareaActivity;
import com.example.tarealarga1trimestre.BuildConfig;
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
        if (tipo == null) return;

        String value;
        if ("imagen".equalsIgnoreCase(tipo)) {
            value = guardarImagenEnAlmacenInterno(uri);
            if (value == null) return;
        } else {
            value = uri.toString();
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

    private String guardarImagenEnAlmacenInterno(Uri uri) {
        String extension = obtenerExtension(uri);
        String nombreArchivo = "img_" + System.currentTimeMillis() + extension;
        File destino = new File(requireContext().getFilesDir(), nombreArchivo);

        try (InputStream in = requireContext().getContentResolver().openInputStream(uri);
             FileOutputStream out = new FileOutputStream(destino)) {

            if (in == null) return null;

            byte[] buffer = new byte[8192];
            int bytesRead;
            while ((bytesRead = in.read(buffer)) != -1) {
                out.write(buffer, 0, bytesRead);
            }

            Uri localUri = FileProvider.getUriForFile(
                    requireContext(),
                    BuildConfig.APPLICATION_ID + ".fileprovider",
                    destino
            );

            return localUri.toString();
        } catch (Exception e) {
            return null;
        }
    }

    private String obtenerExtension(Uri uri) {
        String mimeType = requireContext().getContentResolver().getType(uri);
        String extension = MimeTypeMap.getSingleton().getExtensionFromMimeType(mimeType);
        if (extension != null && !extension.trim().isEmpty()) {
            return "." + extension;
        }

        String lastSegment = uri.getLastPathSegment();
        if (lastSegment != null) {
            int idx = lastSegment.lastIndexOf('.');
            if (idx >= 0 && idx < lastSegment.length() - 1) {
                return lastSegment.substring(idx);
            }
        }

        return ".jpg";
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

        TextView archivoView = new TextView(requireContext());
        archivoView.setText(getString(R.string.archivo_adjunto_item, tipo, uri));
        archivoView.setTextSize(14f);
        archivoView.setPadding(0, 4, 0, 4);
        contenedorArchivos.addView(archivoView);
    }
}
