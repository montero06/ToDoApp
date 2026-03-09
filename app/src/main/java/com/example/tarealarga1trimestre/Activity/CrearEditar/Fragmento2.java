package com.example.tarealarga1trimestre.Activity.CrearEditar;

import android.Manifest;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.media.MediaRecorder;
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
import androidx.appcompat.app.AlertDialog;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.preference.PreferenceManager;

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
    private Button btnVolver, btnGuardar, btnCancelar;
    private ImageButton btnAgregarDocumento, btnAgregarImagen, btnAgregarAudio, btnAgregarVideo;
    private LinearLayout contenedorArchivos;

    private ActivityResultLauncher<String[]> archivoLauncher;
    private ActivityResultLauncher<Uri> camaraImagenLauncher;
    private ActivityResultLauncher<Uri> camaraVideoLauncher;
    private ActivityResultLauncher<String> permisoAudioLauncher;

    private String tipoArchivoSeleccionado;
    private Uri uriTemporalImagen;
    private Uri uriTemporalVideo;

    private MediaRecorder grabadoraAudio;
    private File archivoAudioTemporal;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.activity_crear_fragmento_2, container, false);

        viewModel = new ViewModelProvider(requireActivity()).get(FormularioViewModel.class);

        edtDescripcion = root.findViewById(R.id.edtDescripcion);
        btnVolver = root.findViewById(R.id.btnVolver);
        btnGuardar = root.findViewById(R.id.btnGuardar);
        btnCancelar = root.findViewById(R.id.btnCancelar);

        btnAgregarDocumento = root.findViewById(R.id.btnAgregarDocumento);
        btnAgregarImagen = root.findViewById(R.id.btnAgregarImagen);
        btnAgregarAudio = root.findViewById(R.id.btnAgregarAudio);
        btnAgregarVideo = root.findViewById(R.id.btnAgregarVideo);
        contenedorArchivos = root.findViewById(R.id.contenedorArchivos);

        if (viewModel.getDescripcion() != null) {
            edtDescripcion.setText(viewModel.getDescripcion());
        }

        btnVolver.setOnClickListener(v -> {
            if (requireActivity() instanceof CrearTareaAtivity) {
                ((CrearTareaAtivity) requireActivity()).volverPaso1();
            } else {
                requireActivity().finish();
            }
        });

        btnCancelar.setOnClickListener(v -> requireActivity().finish());

        btnGuardar.setOnClickListener(v -> {
            viewModel.setDescripcion(edtDescripcion.getText().toString());

            LocalDate fechaCreacionLD = null;
            LocalDate fechaObjetivoLD = null;
            try {
                if (viewModel.getFechaCreacion() != null && !viewModel.getFechaCreacion().isEmpty()) {
                    fechaCreacionLD = LocalDate.parse(viewModel.getFechaCreacion(), DateTimeFormatter.ofPattern("dd/MM/yyyy"));
                }
                if (viewModel.getFechaObjetivo() != null && !viewModel.getFechaObjetivo().isEmpty()) {
                    fechaObjetivoLD = LocalDate.parse(viewModel.getFechaObjetivo(), DateTimeFormatter.ofPattern("dd/MM/yyyy"));
                }
            } catch (Exception e) {
                e.printStackTrace();
            }

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

            if (requireActivity() instanceof CrearTareaAtivity) {
                ((CrearTareaAtivity) requireActivity()).guardarTareaYSalir(tarea);
            } else if (requireActivity() instanceof EditarTareaActivity) {
                ((EditarTareaActivity) requireActivity()).guardarTareaEditada(tarea);
            }
        });

        configurarLaunchers();

        btnAgregarDocumento.setOnClickListener(v -> {
            tipoArchivoSeleccionado = "documento";
            archivoLauncher.launch(new String[]{
                    "application/pdf",
                    "application/msword",
                    "application/vnd.openxmlformats-officedocument.wordprocessingml.document"
            });
        });

        btnAgregarImagen.setOnClickListener(v -> mostrarOpcionesImagen());
        btnAgregarAudio.setOnClickListener(v -> mostrarOpcionesAudio());
        btnAgregarVideo.setOnClickListener(v -> mostrarOpcionesVideo());

        renderArchivosAdjuntos();
        return root;
    }

    private void configurarLaunchers() {
        archivoLauncher = registerForActivityResult(
                new ActivityResultContracts.OpenDocument(),
                uri -> {
                    if (uri != null) {
                        guardarArchivoLocal(uri, tipoArchivoSeleccionado);
                    }
                }
        );

        camaraImagenLauncher = registerForActivityResult(
                new ActivityResultContracts.TakePicture(),
                exito -> {
                    if (Boolean.TRUE.equals(exito) && uriTemporalImagen != null) {
                        guardarRutaDirectaInterna(uriTemporalImagen, "imagen");
                    }
                }
        );

        camaraVideoLauncher = registerForActivityResult(
                new ActivityResultContracts.CaptureVideo(),
                exito -> {
                    if (Boolean.TRUE.equals(exito) && uriTemporalVideo != null) {
                        guardarRutaDirectaInterna(uriTemporalVideo, "video");
                    }
                }
        );

        permisoAudioLauncher = registerForActivityResult(
                new ActivityResultContracts.RequestPermission(),
                concedido -> {
                    if (Boolean.TRUE.equals(concedido)) {
                        mostrarDialogoGrabacionAudio();
                    } else {
                        Toast.makeText(requireContext(), "Permiso de micrófono denegado", Toast.LENGTH_SHORT).show();
                    }
                }
        );
    }

    private void mostrarOpcionesImagen() {
        new AlertDialog.Builder(requireContext())
                .setTitle(R.string.imagen)
                .setItems(new CharSequence[]{"Tomar fotografía", "Seleccionar archivo"}, (dialog, which) -> {
                    if (which == 0) {
                        lanzarCamaraImagen();
                    } else {
                        tipoArchivoSeleccionado = "imagen";
                        archivoLauncher.launch(new String[]{"image/*"});
                    }
                })
                .show();
    }

    private void mostrarOpcionesAudio() {
        new AlertDialog.Builder(requireContext())
                .setTitle(R.string.audio)
                .setItems(new CharSequence[]{"Grabar audio (Recorder)", "Seleccionar archivo"}, (dialog, which) -> {
                    if (which == 0) {
                        verificarPermisoYGrabarAudio();
                    } else {
                        tipoArchivoSeleccionado = "audio";
                        archivoLauncher.launch(new String[]{"audio/*"});
                    }
                })
                .show();
    }

    private void verificarPermisoYGrabarAudio() {
        if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.RECORD_AUDIO)
                == PackageManager.PERMISSION_GRANTED) {
            mostrarDialogoGrabacionAudio();
        } else {
            permisoAudioLauncher.launch(Manifest.permission.RECORD_AUDIO);
        }
    }

    private void mostrarDialogoGrabacionAudio() {
        if (!iniciarGrabacionConRecorder()) {
            return;
        }

        new AlertDialog.Builder(requireContext())
                .setTitle("Grabando audio")
                .setMessage("Pulsa detener para guardar la grabación.")
                .setCancelable(false)
                .setPositiveButton("Detener y guardar", (dialog, which) -> detenerYGuardarGrabacion())
                .setNegativeButton("Cancelar", (dialog, which) -> cancelarGrabacion())
                .show();
    }

    private boolean iniciarGrabacionConRecorder() {
        try {
            archivoAudioTemporal = crearArchivoInternoTemporal("audio", ".m4a");
            if (archivoAudioTemporal == null) {
                return false;
            }

            grabadoraAudio = new MediaRecorder();
            grabadoraAudio.setAudioSource(MediaRecorder.AudioSource.MIC);
            grabadoraAudio.setOutputFormat(MediaRecorder.OutputFormat.MPEG_4);
            grabadoraAudio.setAudioEncoder(MediaRecorder.AudioEncoder.AAC);
            grabadoraAudio.setOutputFile(archivoAudioTemporal.getAbsolutePath());
            grabadoraAudio.prepare();
            grabadoraAudio.start();
            return true;
        } catch (Exception e) {
            liberarGrabadora();
            Toast.makeText(requireContext(), "No se pudo iniciar la grabación", Toast.LENGTH_SHORT).show();
            return false;
        }
    }

    private void detenerYGuardarGrabacion() {
        try {
            if (grabadoraAudio != null) {
                grabadoraAudio.stop();
            }
            liberarGrabadora();

            if (archivoAudioTemporal != null && archivoAudioTemporal.exists()) {
                setUrlPorTipo("audio", Uri.fromFile(archivoAudioTemporal).toString());
                renderArchivosAdjuntos();
            }
        } catch (Exception e) {
            liberarGrabadora();
            Toast.makeText(requireContext(), "No se pudo guardar la grabación", Toast.LENGTH_SHORT).show();
        }
    }

    private void cancelarGrabacion() {
        try {
            if (grabadoraAudio != null) {
                grabadoraAudio.stop();
            }
        } catch (Exception ignored) {
        } finally {
            liberarGrabadora();
            if (archivoAudioTemporal != null && archivoAudioTemporal.exists()) {
                //noinspection ResultOfMethodCallIgnored
                archivoAudioTemporal.delete();
            }
        }
    }

    private void liberarGrabadora() {
        if (grabadoraAudio != null) {
            try {
                grabadoraAudio.release();
            } catch (Exception ignored) {
            }
            grabadoraAudio = null;
        }
    }

    private void mostrarOpcionesVideo() {
        new AlertDialog.Builder(requireContext())
                .setTitle(R.string.video)
                .setItems(new CharSequence[]{"Grabar vídeo", "Seleccionar archivo"}, (dialog, which) -> {
                    if (which == 0) {
                        lanzarCamaraVideo();
                    } else {
                        tipoArchivoSeleccionado = "video";
                        archivoLauncher.launch(new String[]{"video/*"});
                    }
                })
                .show();
    }

    private void lanzarCamaraImagen() {
        File archivo = crearArchivoInternoTemporal("imagen", ".jpg");
        if (archivo == null) return;
        uriTemporalImagen = FileProvider.getUriForFile(
                requireContext(),
                requireContext().getPackageName() + ".fileprovider",
                archivo
        );
        camaraImagenLauncher.launch(uriTemporalImagen);
    }

    private void lanzarCamaraVideo() {
        File archivo = crearArchivoInternoTemporal("video", ".mp4");
        if (archivo == null) return;
        uriTemporalVideo = FileProvider.getUriForFile(
                requireContext(),
                requireContext().getPackageName() + ".fileprovider",
                archivo
        );
        camaraVideoLauncher.launch(uriTemporalVideo);
    }

    private File crearArchivoInternoTemporal(String tipo, String extensionPorDefecto) {
        File directorio = obtenerDirectorioAdjuntosInternos();
        if (directorio == null) {
            Toast.makeText(requireContext(), R.string.error_storage_unavailable, Toast.LENGTH_SHORT).show();
            return null;
        }

        String nombre = tipo.toLowerCase(Locale.ROOT) + "_" + System.currentTimeMillis() + extensionPorDefecto;
        return new File(directorio, nombre);
    }

    private void guardarRutaDirectaInterna(Uri uri, String tipo) {
        if (uri == null || tipo == null) return;
        setUrlPorTipo(tipo, uri.toString());
        renderArchivosAdjuntos();
    }

    private void guardarArchivoLocal(Uri uri, String tipo) {
        if (tipo == null) return;

        boolean guardarEnInterna = !"documento".equalsIgnoreCase(tipo);
        String value = copiarArchivoALocal(uri, tipo, guardarEnInterna);
        if (value == null) return;

        setUrlPorTipo(tipo, value);
        renderArchivosAdjuntos();
    }

    private void setUrlPorTipo(String tipo, String value) {
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
    }

    private String copiarArchivoALocal(Uri uri, String tipo, boolean guardarEnInterna) {
        File directorioAdjuntos = guardarEnInterna ? obtenerDirectorioAdjuntosInternos() : obtenerDirectorioAdjuntosDocumentos();
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

    private File obtenerDirectorioAdjuntosInternos() {
        File baseDir = new File(requireContext().getFilesDir(), "adjuntos");
        if (!baseDir.exists() && !baseDir.mkdirs()) {
            return null;
        }
        return baseDir;
    }

    private File obtenerDirectorioAdjuntosDocumentos() {
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

        TextView archivoView = new TextView(requireContext());
        archivoView.setText(getString(R.string.archivo_adjunto_item, tipo, uri));
        archivoView.setTextSize(14f);
        archivoView.setPadding(0, 4, 0, 4);
        archivoView.setClickable(true);
        archivoView.setOnClickListener(v -> abrirArchivo(uri));
        contenedorArchivos.addView(archivoView);
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

    @Override
    public void onDestroyView() {
        cancelarGrabacion();
        super.onDestroyView();
    }
}
