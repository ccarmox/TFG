/*
 * Trabajo de fin de grado: "Definición e implementación de herramienta para la navegación de peatones en entornos urbanos para el sistema operativo móvil Android"
 * Creado por: Cristian Do Carmo Rodríguez
 * Tutora: Lucía Díaz Vilariño
 * Cotutor: Jesús Balado Frías
 * Copyright (c) 2024. Todos los derechos reservados.
 */

package es.uvigo.eei.tfg.ccarmo.ui.views.canvas;

import android.graphics.Paint;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;

public class CanvasHTML {


    private BufferedWriter writer;

    public CanvasHTML(int width, int heigth, File file) {

        try {

            FileOutputStream fos = new FileOutputStream(file.getAbsolutePath(), true); // Abrir el archivo en modo de añadir contenido
            OutputStreamWriter osw = new OutputStreamWriter(fos);
            writer = new BufferedWriter(osw);

            writer.write("<!DOCTYPE HTML>\n" +
                    "<html>\n" +
                    "<body>\n" +
                    "    <div id=\"div\" class=\"container\">\n" +
                    "\t\n" +
                    "<svg height=\"" + heigth + "\" width=\"" + width + "\">");

        } catch (Throwable e) {
            e.printStackTrace();
        }
    }

    public void fin() {
        try {
            writer.write("</svg>" +
                    "\n" +
                    "</body>\n" +
                    "</html>");
            writer.close(); // Cerrar el archivo
        } catch (Throwable e) {
            e.printStackTrace();
        }
    }

    public void drawLine(int x1, int y1, int x2, int y2, Paint paint) {

        drawLine(x1, y1, x2, y2, (int) paint.getStrokeWidth(), paint.getColor());

    }

    public void drawLine(int x1, int y1, int x2, int y2, int ancho, int color) {

        try {
            writer.write("\n<line x1=\"" + x1 + "\" y1=\"" + y1 + "\" x2=\"" + x2 + "\" y2=\"" + y2 + "\" style=\"stroke:" + String.format("#%06X", (0xFFFFFF & color)) + ";stroke-width:" + ancho + "\" />");
        } catch (Throwable e) {
            e.printStackTrace();
        }
    }

}
