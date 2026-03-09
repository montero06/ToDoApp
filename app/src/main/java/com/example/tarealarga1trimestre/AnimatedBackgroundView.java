package com.example.tarealarga1trimestre;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.util.AttributeSet;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class AnimatedBackgroundView extends View {

    private static final int FORMAS_POR_TIPO = 3;
    private static final int TOTAL_FORMAS = FORMAS_POR_TIPO * 4;

    private final Paint pincel = new Paint(Paint.ANTI_ALIAS_FLAG);
    private final Random aleatorio = new Random();
    private final List<EstadoForma> formas = new ArrayList<>();

    private ExecutorService poolFormas;
    private volatile boolean enEjecucion = false;
    private boolean inicializado = false;

    public AnimatedBackgroundView(Context context) {
        super(context);
        inicializarPincel();
    }

    public AnimatedBackgroundView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        inicializarPincel();
    }

    public AnimatedBackgroundView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        inicializarPincel();
    }

    private void inicializarPincel() {
        pincel.setStyle(Paint.Style.FILL);
    }

    @Override
    protected void onSizeChanged(int ancho, int alto, int anchoAnterior, int altoAnterior) {
        super.onSizeChanged(ancho, alto, anchoAnterior, altoAnterior);

        if (ancho <= 0 || alto <= 0) {
            return;
        }

        if (!inicializado) {
            crearFormas(ancho, alto);
            iniciarAnimacion();
            inicializado = true;
        }
    }

    private void crearFormas(int ancho, int alto) {
        synchronized (formas) {
            formas.clear();

            agregarFormasPorTipo(TipoForma.CIRCULO, ancho, alto);
            agregarFormasPorTipo(TipoForma.CUADRADO, ancho, alto);
            agregarFormasPorTipo(TipoForma.TRIANGULO, ancho, alto);
            agregarFormasPorTipo(TipoForma.ESTRELLA, ancho, alto);
        }
    }

    private void agregarFormasPorTipo(TipoForma tipo, int ancho, int alto) {
        for (int i = 0; i < FORMAS_POR_TIPO; i++) {
            float tamano = valorAleatorio(40f, 90f);
            float posicionX = valorAleatorio(tamano, ancho - tamano);
            float posicionY = valorAleatorio(tamano, alto - tamano);

            float velocidad = valorAleatorio(2.5f, 7.5f);
            double angulo = Math.toRadians(valorAleatorio(0f, 360f));
            float desplazamientoX = (float) (Math.cos(angulo) * velocidad);
            float desplazamientoY = (float) (Math.sin(angulo) * velocidad);

            int alfa = aleatorio.nextInt(120) + 90;
            int color = Color.argb(alfa, aleatorio.nextInt(256), aleatorio.nextInt(256), aleatorio.nextInt(256));

            formas.add(new EstadoForma(tipo, posicionX, posicionY, tamano, desplazamientoX, desplazamientoY, color));
        }
    }

    private void iniciarAnimacion() {
        detenerAnimacion();
        enEjecucion = true;
        poolFormas = Executors.newFixedThreadPool(TOTAL_FORMAS);

        synchronized (formas) {
            for (EstadoForma forma : formas) {
                poolFormas.execute(() -> animarForma(forma));
            }
        }
    }

    private void animarForma(EstadoForma forma) {
        while (enEjecucion) {
            actualizarPosicionForma(forma);
            postInvalidateOnAnimation();
            try {
                Thread.sleep(16);
            } catch (InterruptedException ignored) {
                Thread.currentThread().interrupt();
                return;
            }
        }
    }

    private void actualizarPosicionForma(EstadoForma forma) {
        synchronized (forma) {
            float siguienteX = forma.x + forma.dx;
            float siguienteY = forma.y + forma.dy;

            if (siguienteX - forma.tamano < 0 || siguienteX + forma.tamano > getWidth()) {
                forma.dx *= -1;
                siguienteX = forma.x + forma.dx;
            }

            if (siguienteY - forma.tamano < 0 || siguienteY + forma.tamano > getHeight()) {
                forma.dy *= -1;
                siguienteY = forma.y + forma.dy;
            }

            forma.x = siguienteX;
            forma.y = siguienteY;
        }
    }

    private void detenerAnimacion() {
        enEjecucion = false;
        if (poolFormas != null) {
            poolFormas.shutdownNow();
            poolFormas = null;
        }
    }

    @Override
    protected void onDetachedFromWindow() {
        detenerAnimacion();
        super.onDetachedFromWindow();
    }

    @Override
    protected void onDraw(@NonNull Canvas canvas) {
        super.onDraw(canvas);

        synchronized (formas) {
            for (EstadoForma forma : formas) {
                synchronized (forma) {
                    pincel.setColor(forma.color);
                    dibujarForma(canvas, forma);
                }
            }
        }
    }

    private void dibujarForma(Canvas canvas, EstadoForma forma) {
        switch (forma.tipo) {
            case CIRCULO:
                canvas.drawCircle(forma.x, forma.y, forma.tamano, pincel);
                break;
            case CUADRADO:
                canvas.drawRect(
                        forma.x - forma.tamano,
                        forma.y - forma.tamano,
                        forma.x + forma.tamano,
                        forma.y + forma.tamano,
                        pincel
                );
                break;
            case TRIANGULO:
                dibujarTriangulo(canvas, forma.x, forma.y, forma.tamano);
                break;
            case ESTRELLA:
                dibujarEstrella(canvas, forma.x, forma.y, forma.tamano);
                break;
        }
    }

    private void dibujarTriangulo(Canvas canvas, float centroX, float centroY, float radio) {
        Path ruta = new Path();
        ruta.moveTo(centroX, centroY - radio);
        ruta.lineTo(centroX - radio, centroY + radio);
        ruta.lineTo(centroX + radio, centroY + radio);
        ruta.close();
        canvas.drawPath(ruta, pincel);
    }

    private void dibujarEstrella(Canvas canvas, float centroX, float centroY, float radio) {
        Path ruta = new Path();
        double rotacion = Math.toRadians(-18);
        float radioInterno = radio * 0.45f;

        for (int i = 0; i < 10; i++) {
            float radioActual = (i % 2 == 0) ? radio : radioInterno;
            double angulo = rotacion + i * Math.PI / 5;
            float x = centroX + (float) (Math.cos(angulo) * radioActual);
            float y = centroY + (float) (Math.sin(angulo) * radioActual);

            if (i == 0) {
                ruta.moveTo(x, y);
            } else {
                ruta.lineTo(x, y);
            }
        }

        ruta.close();
        canvas.drawPath(ruta, pincel);
    }

    private float valorAleatorio(float minimo, float maximo) {
        if (maximo <= minimo) {
            return minimo;
        }
        return minimo + aleatorio.nextFloat() * (maximo - minimo);
    }

    private enum TipoForma {
        CIRCULO,
        CUADRADO,
        TRIANGULO,
        ESTRELLA
    }

    private static class EstadoForma {
        final TipoForma tipo;
        final float tamano;
        final int color;
        float x;
        float y;
        float dx;
        float dy;

        EstadoForma(TipoForma tipo, float x, float y, float tamano, float dx, float dy, int color) {
            this.tipo = tipo;
            this.x = x;
            this.y = y;
            this.tamano = tamano;
            this.dx = dx;
            this.dy = dy;
            this.color = color;
        }
    }
}
