/*
 * Trabajo de fin de grado: "Definición e implementación de herramienta para la navegación de peatones en entornos urbanos para el sistema operativo móvil Android"
 * Creado por: Cristian Do Carmo Rodríguez
 * Tutora: Lucía Díaz Vilariño
 * Cotutor: Jesús Balado Frías
 * Copyright (c) 2024. Todos los derechos reservados.
 */

package es.uvigo.eei.tfg.ccarmo.ruta.classes;

import android.graphics.Color;

import es.uvigo.eei.tfg.ccarmo.clases.classes.Pixel;

/*
    Clase para representar visualmente cada punto en el plano
 */
public class PuntoCalor extends Pixel {

    private final int valor;


    public PuntoCalor(double latitude, double longitude, double altitude, int valor) {
        super(latitude, longitude, altitude);
        this.valor = valor;
    }

    public PuntoCalor(double latitude, double longitude, double altitude, int resolucion, int valor) {
        super(latitude, longitude, altitude, resolucion);
        this.valor = valor;
    }

    public int getColor(int maximo) {

        if (valor == 0) {
            return Color.BLACK;
        }

        // Defino los colores inicial y final del gradiente
        int startColor = Color.GREEN; // Color inicial
        int endColor = Color.RED; // Color final

        int value = Math.min(maximo, Math.max(0, valor));

        // Calculo la proporcion del valor en relación con el rango total
        double ratio = (double) value / maximo;

        // Calculo los componentes de color interpolados
        int red = (int) (Color.red(startColor) + Math.round(ratio * (double) (Color.red(endColor) - Color.red(startColor))));
        int green = (int) (Color.green(startColor) + Math.round(ratio * (double) (Color.green(endColor) - Color.green(startColor))));
        int blue = (int) (Color.blue(startColor) + Math.round(ratio * (double) (Color.blue(endColor) - Color.blue(startColor))));

        // Retorno el color resultante
        return Color.rgb(red, green, blue);
    }


    public int getColor() {

        if (valor == 0) {
            return Color.BLACK;
        }

        if (true) {

            // Defino los colores inicial y final del gradiente
            int startColor = Color.WHITE; // Color inicial
            int endColor = Color.BLACK; // Color final

            int value = Math.min(300, Math.max(0, valor));

            // Calcula la proporción del valor en relación con el rango total (0-300)
            double ratio = (double) value / 300D;

            // Calcula los componentes de color interpolados
            int red = (int) (Color.red(startColor) + Math.round(ratio * (double) (Color.red(endColor) - Color.red(startColor))));
            int green = (int) (Color.green(startColor) + Math.round(ratio * (double) (Color.green(endColor) - Color.green(startColor))));
            int blue = (int) (Color.blue(startColor) + Math.round(ratio * (double) (Color.blue(endColor) - Color.blue(startColor))));

            // Retorna el color resultante
            return Color.rgb(red, green, blue);
        }

        if (true) {

            // Establecemos la saturación y luminosidad constantes para obtener colores brillantes
            float saturacion = 1.0f;
            float luminosidad = 0.5f;

            //Como el valor puede ser superior a 360 utilizo el resto de la division valor / 360
            //Para potenciar los cambios de color multiplico el valor por 4
            return Color.HSVToColor(new float[]{(valor * 4) % 360, saturacion, luminosidad});

        } else {

            return Color.argb(255, valor * 5, valor * 5, valor * 5);

        }

    }

}
