package com.example.tarealarga1trimestre.Activity.CrearEditar;

import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.webkit.MimeTypeMap;
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
import androidx.preference.PreferenceManager;

import com.example.tarealarga1trimestre.Activity.CrearTareaAtivity;
import com.example.tarealarga1trimestre.Activity.EditarTareaActivity;
import com.example.tarealarga1trimestre.Manager.Tarea;
import com.example.tarealarga1trimestre.R;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.util.Locale;


public class Fragmento2 extends Fragment {

    private FormularioViewModel viewModel;

    private EditText edtDescripcion;
    private Button btnVolver, btnGuardar, btnCancelar;
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
        btnCancelar = root.findViewById(R.id.btnCancelar);

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
        btnVolver.setOnClickListener(v -> {
            if (requireActivity() instanceof CrearTareaAtivity) {
                ((CrearTareaAtivity) requireActivity()).volverPaso1();
            } else {
                requireActivity().finish();
            }
        });

        btnCancelar.setOnClickListener(v -> requireActivity().finish());

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

        String value = copiarArchivoALocal(uri, tipo);
        if (value == null) return;

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

    private String copiarArchivoALocal(Uri uri, String tipo) {
        File directorioAdjuntos = obtenerDirectorioAdjuntos();
        if (directorioAdjuntos == null) {
            Toast.makeText(requireContext(), R.string.error_storage_unavailable, Toast.LENGTH_SHORT).show();
            return null;
        }

        String extension = obtenerExtension(uri);
        String nombreArchivo = tipo.toLowerCase(Locale.ROOT) + "_" + System.currentTimeMillis() + extension;
        File destino = new File(directorioAdjuntos, nombreArchivo);

        try (InputStream inputStream = requireContext().getContentResolver().openInputStream(uri);
             FileOutputStream outputStream = new FileOutputStream(destino)) {
            if (inputStream == null) return null;

            byte[] buffer = new byte[8192];
            int bytesLeidos;
            while ((bytesLeidos = inputStream.read(buffer)) != -1) {
                outputStream.write(buffer, 0, bytesLeidos);
            }

            return Uri.fromFile(destino).toString();
        } catch (Exception e) {
            return null;
        }
    }


    private File obtenerDirectorioAdjuntos() {
        boolean guardarEnSd = PreferenceManager
                .getDefaultSharedPreferences(requireContext())
                .getBoolean("sd", false);

        File baseDir;
        if (guardarEnSd) {
            baseDir = obtenerDirectorioSdExtraible();
            if (baseDir == null) {
                Toast.makeText(requireContext(), R.string.warning_sd_not_available_external_used, Toast.LENGTH_SHORT).show();
                baseDir = requireContext().getExternalFilesDir(Environment.DIRECTORY_DOCUMENTS);
            }
        } else {
            baseDir = requireContext().getExternalFilesDir(Environment.DIRECTORY_DOCUMENTS);
        }

        if (baseDir == null) {
            return null;
        }

        File directorioAdjuntos = new File(baseDir, "adjuntos");
        if (!directorioAdjuntos.exists() && !directorioAdjuntos.mkdirs()) {
            return null;
        }

        return directorioAdjuntos;
    }

    private File obtenerDirectorioSdExtraible() {
        File[] externalDirs = requireContext().getExternalFilesDirs(Environment.DIRECTORY_DOCUMENTS);
        if (externalDirs == null || externalDirs.length == 0) {
            return null;
        }

        for (File dir : externalDirs) {
            if (dir == null) continue;
            if (Environment.isExternalStorageRemovable(dir)) {
                return dir;
            }
        }

        return null;
    }

    private String obtenerExtension(Uri uri) {
        String mimeType = requireContext().getContentResolver().getType(uri);
        String extension = MimeTypeMap.getSingleton().getExtensionFromMimeType(mimeType);
        return (extension == null || extension.trim().isEmpty()) ? "" : "." + extension;
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

        LinearLayout filaArchivo = new LinearLayout(requireContext());
        filaArchivo.setLayoutParams(new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
        ));
        filaArchivo.setOrientation(LinearLayout.HORIZONTAL);

        TextView archivoView = new TextView(requireContext());
        archivoView.setText(getString(R.string.archivo_adjunto_item, tipo, uri));
        archivoView.setTextSize(14f);
        archivoView.setPadding(0, 4, 0, 4);
        archivoView.setClickable(true);
        archivoView.setLayoutParams(new LinearLayout.LayoutParams(
                0,
                LinearLayout.LayoutParams.WRAP_CONTENT,
                1f
        ));
        archivoView.setOnClickListener(v -> abrirArchivo(uri));

        Button btnEliminar = new Button(requireContext());
        btnEliminar.setText(R.string.cm_eliminar);
        btnEliminar.setOnClickListener(v -> eliminarAdjunto(tipo, uri));

        filaArchivo.addView(archivoView);
        filaArchivo.addView(btnEliminar);
        contenedorArchivos.addView(filaArchivo);
    }

    private void eliminarAdjunto(String tipo, String uri) {
        eliminarArchivoLocalSiExiste(uri);

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

        renderArchivosAdjuntos();
    }

    private void eliminarArchivoLocalSiExiste(String uriString) {
        if (uriString == null || uriString.trim().isEmpty()) return;

        try {
            Uri uri = Uri.parse(uriString);
            if (!"file".equalsIgnoreCase(uri.getScheme())) return;

            String ruta = uri.getPath();
            if (ruta == null || ruta.trim().isEmpty()) return;

            File archivo = new File(ruta);
            if (archivo.exists()) {
                archivo.delete();
            }
        } catch (Exception ignored) {
        }
    }


    private String obtenerMimeTypeParaAbrir(Uri uri) {
        String mimeType = requireContext().getContentResolver().getType(uri);
        if (mimeType != null && !mimeType.trim().isEmpty()) {
            return mimeType;
        }

        String extension = MimeTypeMap.getFileExtensionFromUrl(uri.toString());
        if (extension != null && !extension.trim().isEmpty()) {
            String inferido = MimeTypeMap.getSingleton().getMimeTypeFromExtension(extension.toLowerCase(Locale.ROOT));
            if (inferido != null && !inferido.trim().isEmpty()) {
                return inferido;
            }
        }

        return "*/*";
    }

    private void abrirArchivo(String uriString) {
        if (uriString == null || uriString.trim().isEmpty()) return;

        try {
            Uri originalUri = Uri.parse(uriString);
            Uri uriAbrir = originalUri;

            if ("file".equalsIgnoreCase(originalUri.getScheme())) {
                File archivo = new File(originalUri.getPath());
                uriAbrir = FileProvider.getUriForFile(
                        requireContext(),
                        requireContext().getPackageName() + ".fileprovider",
                        archivo
                );
            }

            String mimeType = obtenerMimeTypeParaAbrir(uriAbrir);

            Intent intent = new Intent(Intent.ACTION_VIEW);
            intent.setDataAndType(uriAbrir, mimeType);
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);

            startActivity(intent);
        } catch (ActivityNotFoundException e) {
            Toast.makeText(requireContext(), R.string.no_hay_app_para_abrir_archivo, Toast.LENGTH_SHORT).show();
        } catch (Exception e) {
            Toast.makeText(requireContext(), R.string.error_abrir_archivo, Toast.LENGTH_SHORT).show();
        }
    }
}
