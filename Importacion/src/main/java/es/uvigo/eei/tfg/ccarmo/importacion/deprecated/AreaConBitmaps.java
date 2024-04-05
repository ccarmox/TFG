/*
 * Trabajo de fin de grado: "Definición e implementación de herramienta para la navegación de peatones en entornos urbanos para el sistema operativo móvil Android"
 * Creado por: Cristian Do Carmo Rodríguez
 * Tutora: Lucía Díaz Vilariño
 * Cotutor: Jesús Balado Frías
 * Copyright (c) 2024. Todos los derechos reservados.
 */

package es.uvigo.eei.tfg.ccarmo.importacion.deprecated;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;

import java.util.ArrayList;
import java.util.List;

import es.uvigo.eei.tfg.ccarmo.clases.classes.TipoVia;
import es.uvigo.eei.tfg.ccarmo.clases.classes.Vertice;
import es.uvigo.eei.tfg.ccarmo.clases.utils.Baremos;
import es.uvigo.eei.tfg.ccarmo.importacion.algoritmos.osm.xml.OSMNodo;
import es.uvigo.eei.tfg.ccarmo.importacion.algoritmos.osm.xml.OSMTag;
import es.uvigo.eei.tfg.ccarmo.importacion.listeners.InterfazVertices;
import es.uvigo.eei.tfg.ccarmo.importacion.utils.Linea;

@Deprecated
public class AreaConBitmaps {

    /**
     * Transforma un poligono del mapa en una nube de nudos
     * para hacerlo se dibuja el poligono necesario y se comprueba si los pixeles solicitados
     * corresponden a un punto interior o exterior
     */
    public static void interpretarUnArea(String id, ArrayList<OSMNodo> nodes, List<OSMTag> tags, TipoVia tipoVia, int resolucion, InterfazVertices onDataParsed) {


        android.util.Log.v("Comenzando", "Area " + nodes.size() + " nodos");

        double minLatitude = nodes.get(0).getLatitud();
        double maxLatitude = nodes.get(0).getLatitud();

        double minLongitude = nodes.get(0).getLongitud();
        double maxLongitude = nodes.get(0).getLongitud();


        for (OSMNodo p : nodes) {
            if (p.getLatitud() > maxLatitude) {
                maxLatitude = p.getLatitud();
            }
            if (p.getLatitud() < minLatitude) {
                minLatitude = p.getLatitud();
            }
            if (p.getLongitud() > maxLongitude) {
                maxLongitude = p.getLongitud();
            }
            if (p.getLongitud() < minLongitude) {
                minLongitude = p.getLongitud();
            }
        }


        int width = 800;
        int height = 800;

        double distancia = Baremos.getDistanciaEnGradosArea(resolucion);

        double difLatitude = maxLatitude - minLatitude;
        double difLongitude = maxLongitude - minLongitude;

        double factorLatitude = width / difLatitude;
        double factorLongitude = height / difLongitude;


        Bitmap bitmap = Bitmap.createBitmap(width, height,
                Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);

        Path wallpath = new Path();

        wallpath.reset();

        for (int i = 0; i < nodes.size(); i++) {

            float x0 = (float) ((nodes.get(i).getLatitud() - minLatitude) * factorLatitude);
            float y0 = (float) ((nodes.get(i).getLongitud() - minLongitude) * factorLongitude);

            if (i == 0) {
                wallpath.moveTo(x0, y0);
            } else {
                wallpath.lineTo(x0, y0);
            }

        }

        Paint paint = new Paint();
        paint.setAntiAlias(true);
        paint.setStyle(Paint.Style.FILL_AND_STROKE);
        paint.setColor(Color.RED);

        canvas.drawPath(wallpath, paint);


        for (double d1 = minLatitude; d1 < maxLatitude; d1 = d1 + distancia) {
            for (double d2 = minLongitude; d2 < maxLongitude; d2 = d2 + distancia) {

                int x0 = (int) ((d1 - minLatitude) * factorLatitude);
                int y0 = (int) ((d2 - minLongitude) * factorLongitude);

                int pixel = bitmap.getPixel(x0, y0);

                if (pixel == Color.RED) {
                    onDataParsed.onNuevoVertice(new Vertice("", resolucion, d1, d2, tipoVia, Linea.getUnible(tipoVia)));
                }
            }
        }


    }
}
