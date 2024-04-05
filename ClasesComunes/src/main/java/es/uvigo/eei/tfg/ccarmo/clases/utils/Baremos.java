/*
 * Trabajo de fin de grado: "Definición e implementación de herramienta para la navegación de peatones en entornos urbanos para el sistema operativo móvil Android"
 * Creado por: Cristian Do Carmo Rodríguez
 * Tutora: Lucía Díaz Vilariño
 * Cotutor: Jesús Balado Frías
 * Copyright (c) 2024. Todos los derechos reservados.
 */

package es.uvigo.eei.tfg.ccarmo.clases.utils;

public class Baremos {


    /*

    Diferencias entre una unica coordenada


      1    |   11119.492664455875 metros   |   0.1 grados
      2    |   1111.9492664455875 metros   |   0.01 grados
      3    |   111.19492664455873 metros   |   0.001 grados
      4    |   11.119492664455876 metros   |   0.0001 grados
      5    |   1.1119492664455877 metros   |   0.00001 grados
      6    |   0.1111949266445587 metros   |   0.000001 grados
      7    |   0.0111194926644558 metros   |   0.0000001 grados
      8    |   0.0011119492664455 metros   |   0.00000001 grados
      9    |   0.0001111949266445 metros   |   0.000000001 grados
      10   |   0.0000111194926644 metros   |   0.0000000001 grados
      11   |   0.0000011119492664 metros   |   0.00000000001 grados

     */

    /*

    escala |  distancia minima representable   |  grados de diferencia
      1    |   11119.492664455875 metros       |   0.1 grados
      2    |    1111.9492664455875 metros      |   0.01 grados
      3    |     111.19492664455873 metros     |   0.001 grados
      4    |      11.119492664455876 metros    |   0.0001 grados
      5    |       1.1119492664455877 metros   |   0.00001 grados
      6    |       0.1111949266445587 metros   |   0.000001 grados
      7    |       0.0111194926644558 metros   |   0.0000001 grados
      8    |       0.0011119492664455 metros   |   0.00000001 grados
      9    |       0.0001111949266445 metros   |   0.000000001 grados
      10   |       0.0000111194926644 metros   |   0.0000000001 grados
      11   |       0.0000011119492664 metros   |   0.00000000001 grados

      Para obtener los valores de 2 coordenadas seria necesario multiplicar cada distancia por raiz de 2

     */

    public static double getMargenParaLeerLaBaseDeDatos(int resolucion) {

        double indice = 10;

        switch (resolucion) {
            case 1:
            default:
                return 0.1 * indice;
            case 2:
                return 0.01 * indice;
            case 3:
                return 0.001 * indice;
            case 4:
                return 0.0001 * indice;
            case 5:
                return 0.00001 * indice;
            case 6:
                return 0.000001 * indice;
            case 7:
                return 0.0000001 * indice;
            case 8:
                return 0.00000001 * indice;
        }

    }

    public static double getDistanciaEnGradosLimiteEntrePuntos(int resolucion) {

        double indice = 3;

        switch (resolucion) {
            case 1:
            default:
                return 0.1 * indice;
            case 2:
                return 0.01 * indice;
            case 3:
                return 0.001 * indice;
            case 4:
                return 0.0001 * indice;
            case 5:
                return 0.00001 * indice;
            case 6:
                return 0.000001 * indice;
            case 7:
                return 0.0000001 * indice;
            case 8:
                return 0.00000001 * indice;
        }

    }

    public static double getDistanciaEnGradosArea(int resolucion) {


        double indice = 1;

        switch (resolucion) {
            case 1:
            default:
                return 0.1 * indice;
            case 2:
                return 0.01 * indice;
            case 3:
                return 0.001 * indice;
            case 4:
                return 0.0001 * indice;
            case 5:
                return 0.00001 * indice;
            case 6:
                return 0.000001 * indice;
            case 7:
                return 0.0000001 * indice;
            case 8:
                return 0.00000001 * indice;
        }

    }


    //Distancia en metros destinada a considerar si dos puntos estan cerca
    public static double getDistanciaEnMetrosEntrePuntos(int resolucion) {
        double indice = 3;

        switch (resolucion) {
            case 1:
            default:
                return 10000 * indice;
            case 2:
                return 1000 * indice;
            case 3:
                return 100 * indice;
            case 4:
                return 10 * indice;
            case 5:
                return 1 * indice;
            case 6:
                return 0.1 * indice;
            case 7:
                return 0.01 * indice;
            case 8:
                return 0.001 * indice;
        }

    }


}
