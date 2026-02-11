package com.example.tarealarga1trimestre.Data;

import com.example.tarealarga1trimestre.Manager.Tarea;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class ExternalTareaApi {

    private final String baseUrl;

    public ExternalTareaApi(String baseUrl) {
        this.baseUrl = baseUrl.endsWith("/") ? baseUrl.substring(0, baseUrl.length() - 1) : baseUrl;
    }

    public List<Tarea> getAllTareas() throws IOException {
        HttpURLConnection conn = null;
        try {
            conn = openConnection(baseUrl + "/tareas", "GET");
            int code = conn.getResponseCode();
            if (code < 200 || code >= 300) {
                throw new IOException("Error GET /tareas: HTTP " + code + " - " + readError(conn));
            }
            String response = readStream(new BufferedInputStream(conn.getInputStream()));
            return parseTareas(response);
        } finally {
            if (conn != null) conn.disconnect();
        }
    }

    public void insert(Tarea tarea) throws IOException {
        HttpURLConnection conn = null;
        try {
            conn = openConnection(baseUrl + "/tareas", "POST");
            writeJson(conn, tareaToJson(tarea).toString());
            int code = conn.getResponseCode();
            if (code < 200 || code >= 300) {
                throw new IOException("Error POST /tareas: HTTP " + code + " - " + readError(conn));
            }
        } catch (JSONException e) {
            throw new IOException("No se pudo serializar la tarea", e);
        } finally {
            if (conn != null) conn.disconnect();
        }
    }

    public void update(Tarea tarea) throws IOException {
        HttpURLConnection conn = null;
        try {
            conn = openConnection(baseUrl + "/tareas/" + tarea.getId(), "PUT");
            writeJson(conn, tareaToJson(tarea).toString());
            int code = conn.getResponseCode();
            if (code < 200 || code >= 300) {
                throw new IOException("Error PUT /tareas/{id}: HTTP " + code + " - " + readError(conn));
            }
        } catch (JSONException e) {
            throw new IOException("No se pudo serializar la tarea", e);
        } finally {
            if (conn != null) conn.disconnect();
        }
    }

    public void delete(long id) throws IOException {
        HttpURLConnection conn = null;
        try {
            conn = openConnection(baseUrl + "/tareas/" + id, "DELETE");
            int code = conn.getResponseCode();
            if (code < 200 || code >= 300) {
                throw new IOException("Error DELETE /tareas/{id}: HTTP " + code + " - " + readError(conn));
            }
        } finally {
            if (conn != null) conn.disconnect();
        }
    }

    private HttpURLConnection openConnection(String targetUrl, String method) throws IOException {
        URL url = new URL(targetUrl);
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setRequestMethod(method);
        conn.setConnectTimeout(5000);
        conn.setReadTimeout(5000);
        conn.setRequestProperty("Content-Type", "application/json; charset=UTF-8");
        conn.setRequestProperty("Accept", "application/json");
        if (!"GET".equals(method) && !"DELETE".equals(method)) {
            conn.setDoOutput(true);
        }
        return conn;
    }

    private void writeJson(HttpURLConnection conn, String jsonBody) throws IOException {
        byte[] bytes = jsonBody.getBytes(StandardCharsets.UTF_8);
        conn.setFixedLengthStreamingMode(bytes.length);
        try (OutputStream out = new BufferedOutputStream(conn.getOutputStream())) {
            out.write(bytes);
            out.flush();
        }
    }

    private List<Tarea> parseTareas(String jsonText) throws IOException {
        ArrayList<Tarea> tareas = new ArrayList<>();
        try {
            JSONArray array = new JSONArray(jsonText);
            for (int i = 0; i < array.length(); i++) {
                tareas.add(jsonToTarea(array.getJSONObject(i)));
            }
            return tareas;
        } catch (JSONException e) {
            throw new IOException("Respuesta JSON inválida", e);
        }
    }

    private JSONObject tareaToJson(Tarea t) throws JSONException {
        JSONObject json = new JSONObject();
        json.put("id", t.getId());
        json.put("titulo", t.getTitulo());
        json.put("descripcion", t.getDescripcion());
        json.put("progreso", t.getProgreso());
        json.put("fechaCreacion", t.getFechaCreacion().toString());
        json.put("fechaObjetivo", t.getFechaObjetivo().toString());
        json.put("prioritaria", t.isPrioritaria());
        json.put("urlDoc", t.getUrlDoc());
        json.put("urlImg", t.getUrlImg());
        json.put("urlAud", t.getUrlAud());
        json.put("urlVid", t.getUrlVid());
        return json;
    }

    private Tarea jsonToTarea(JSONObject json) {
        Tarea t = new Tarea();
        t.setId(json.optLong("id", 0));
        t.setTitulo(json.optString("titulo", "Sin título"));
        t.setDescripcion(json.optString("descripcion", ""));
        t.setProgreso(json.optInt("progreso", 0));

        String fechaCreacion = json.optString("fechaCreacion", LocalDate.now().toString());
        String fechaObjetivo = json.optString("fechaObjetivo", LocalDate.now().toString());
        t.setFechaCreacion(parseDateSafe(fechaCreacion));
        t.setFechaObjetivo(parseDateSafe(fechaObjetivo));

        t.setPrioritaria(json.optBoolean("prioritaria", false));
        t.setUrlDoc(json.optString("urlDoc", null));
        t.setUrlImg(json.optString("urlImg", null));
        t.setUrlAud(json.optString("urlAud", null));
        t.setUrlVid(json.optString("urlVid", null));
        return t;
    }

    private LocalDate parseDateSafe(String rawDate) {
        try {
            return LocalDate.parse(rawDate);
        } catch (Exception e) {
            return LocalDate.now();
        }
    }

    private String readError(HttpURLConnection conn) {
        try {
            InputStream error = conn.getErrorStream();
            return error == null ? "sin contenido" : readStream(error);
        } catch (Exception e) {
            return "sin detalle";
        }
    }

    private String readStream(InputStream stream) throws IOException {
        StringBuilder sb = new StringBuilder();
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(stream, StandardCharsets.UTF_8))) {
            String line;
            while ((line = reader.readLine()) != null) {
                sb.append(line);
            }
        }
        return sb.toString();
    }
}
