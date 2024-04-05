/*
 * Trabajo de fin de grado: "Definición e implementación de herramienta para la navegación de peatones en entornos urbanos para el sistema operativo móvil Android"
 * Creado por: Cristian Do Carmo Rodríguez
 * Tutora: Lucía Díaz Vilariño
 * Cotutor: Jesús Balado Frías
 * Copyright (c) 2024. Todos los derechos reservados.
 */

package es.uvigo.eei.tfg.ccarmo.ui.actividades.importacion.documentacion;

import es.uvigo.eei.tfg.ccarmo.clases.classes.LatLon;

public class GenerarEscalasReferencia {


    public static DatosResolucion[] getTablaResoluciones(int resoluciones) {

        DatosResolucion[] lista = new DatosResolucion[resoluciones];

        LatLon latLon = new LatLon(0, 0, 0);

        for (int i = 1; i < (resoluciones + 1); i++) {

            double a = 1D / (Math.pow(10, i));
            //Distancia máxima para puntos cercanos al ecuador
            LatLon dos = new LatLon(a, a, 0);
            lista[i - 1] = new DatosResolucion(i, LatLon.getDistanciaFisica(latLon, dos));

        }

        return lista;
    }

    public static class DatosResolucion {
        private final String resolucion;
        private final String distanciaMaxima;

        public DatosResolucion(int resolucion, double distancia) {
            this.resolucion = String.valueOf(resolucion);
            //String formato = "%." + resolucion + "f"; // Crear el formato de cadena con el número deseado de decimales
            this.distanciaMaxima = convertirDistancia(distancia);//String.format(formato, distancia); // Formatear el número
        }

        public static String convertirDistancia(double distanciaMetros) {
            if (distanciaMetros >= 1000) {
                double distanciaKm = distanciaMetros / 1000;
                return String.format("%.2f Km.", distanciaKm);
            } else if (distanciaMetros >= 1) {
                return String.format("%.2f m.", distanciaMetros);
            } else if (distanciaMetros >= 0.01D) {
                double distanciaCm = distanciaMetros * 100;
                return String.format("%.2f cm.", distanciaCm);
            } else if (distanciaMetros >= 0.001D) {
                double distanciaMm = distanciaMetros * 1000;
                return String.format("%.2f mm.", distanciaMm);
            } else {
                double distanciaMm = distanciaMetros * 1000;
                return String.format("%.7f mm.", distanciaMm);
            }
        }

        public String getResolucion() {
            return resolucion;
        }

        public String getDistanciaMaxima() {
            return distanciaMaxima;
        }
    }


}
