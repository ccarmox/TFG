/*
 * Trabajo de fin de grado: "Definición e implementación de herramienta para la navegación de peatones en entornos urbanos para el sistema operativo móvil Android"
 * Creado por: Cristian Do Carmo Rodríguez
 * Tutora: Lucía Díaz Vilariño
 * Cotutor: Jesús Balado Frías
 * Copyright (c) 2024. Todos los derechos reservados.
 */

package es.uvigo.eei.tfg.ccarmo.ui.views.mapa;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;

import androidx.annotation.NonNull;

import es.uvigo.eei.tfg.ccarmo.clases.classes.LatLon;
import es.uvigo.eei.tfg.ccarmo.clases.classes.Pixel;
import es.uvigo.eei.tfg.ccarmo.clases.listeners.InterfazMapa;

public class FiguraMapa {

    private TYPE tipo;
    private LatLon position;
    public FiguraMapa(TYPE tipo, LatLon position) {
        this.tipo = tipo;
        this.position = position;
    }

    public FiguraMapa clonar() {
        return new FiguraMapa(tipo, position);
    }

    public TYPE getTipo() {
        return tipo;
    }

    public void setTipo(TYPE tipo) {
        this.tipo = tipo;
    }

    public LatLon getPosition() {
        return position;
    }

    public void setPosition(LatLon position) {
        this.position = position;
    }

    public void drawOnCanvas(@NonNull Canvas canvas, @NonNull InterfazMapa mapa) {

        Pixel punto = new Pixel(getPosition(), mapa);


        int color;

        switch (tipo) {
            case PUNTERO_AZUL:
                color = Color.BLUE;
                break;
            case PUNTERO_ROJO:
            default:
                color = Color.RED;
                break;
            case PUNTERO_VERDE:
                color = Color.GREEN;
                break;
            case PUNTERO_AMARILLO:
                color = Color.YELLOW;
                break;
        }

        Paint paint = new Paint();
        paint.setAntiAlias(true);
        paint.setStyle(Paint.Style.STROKE);
        paint.setColor(color);
        paint.setStrokeWidth(10);

        canvas.drawCircle(punto.getX(), punto.getY(), 20, paint);
    }

    public enum TYPE {
        PUNTERO_ROJO,
        PUNTERO_AZUL,
        PUNTERO_VERDE,
        PUNTERO_AMARILLO
    }
}
