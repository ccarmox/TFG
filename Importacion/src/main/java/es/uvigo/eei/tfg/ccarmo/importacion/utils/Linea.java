/*
 * Trabajo de fin de grado: "Definición e implementación de herramienta para la navegación de peatones en entornos urbanos para el sistema operativo móvil Android"
 * Creado por: Cristian Do Carmo Rodríguez
 * Tutora: Lucía Díaz Vilariño
 * Cotutor: Jesús Balado Frías
 * Copyright (c) 2024. Todos los derechos reservados.
 */

package es.uvigo.eei.tfg.ccarmo.importacion.utils;

import androidx.annotation.NonNull;

import java.util.ArrayList;

import es.uvigo.eei.tfg.ccarmo.clases.classes.TipoVia;
import es.uvigo.eei.tfg.ccarmo.clases.classes.Vertice;
import es.uvigo.eei.tfg.ccarmo.importacion.listeners.InterfazVertices;

public class Linea {


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

    /**
     * Metodo para añadir puntos intermedios a una lista de nodos dada una resolucion
     *
     * @param lista            lista de vertices
     * @param resolucion       resolucion de los vertices
     * @param interfazVertices interfaz para recuperar los nuevos valores
     */
    public static void addNudosIntermedios(@NonNull ArrayList<Vertice> lista, int resolucion, @NonNull InterfazVertices interfazVertices) {

        //Como los nudos puedes estar muy apartados entre ellos genero los puntos intermedios
        if (!lista.isEmpty()) {

            double paso = 1D / Math.pow(10, resolucion);

            //Los extremos de una lista de puntos deben ser siempre unibles independientemente del tipo
            //De otra forma no se podrian utilizar en la base de datos
            lista.get(0).setUnible(Vertice.Unible.UNIBLE_CON_TODO);
            lista.get(lista.size() - 1).setUnible(Vertice.Unible.UNIBLE_CON_TODO);

            //Se añade el primer nudo
            interfazVertices.onNuevoVertice(lista.get(0));

            //Se comprueba que la lista tiene mas de un nudo
            if (lista.size() > 1) {

                //Se itera desde el segundo nudo hasta el ultimo
                for (int i = 1; i < (lista.size() - 1); i++) {

                    //Se calculan las diferencias de coordenadas entre el vertice anterior y el actual
                    double difLatitude = lista.get(i).getLatitud() - lista.get(i - 1).getLatitud();
                    double difLongitud = lista.get(i).getLongitud() - lista.get(i - 1).getLongitud();

                    //Se comprueba la informacion del nodo actual
                    String info = lista.get(i).getInformacion();
                    TipoVia tipoVia = lista.get(i).getTipoVia();
                    int unible = getUnible(tipoVia);

                    if (tipoVia == TipoVia.POI) {
                        //Los puntos de interes no forman lineas son puntos determinados
                        interfazVertices.onNuevoVertice(lista.get(i));

                    } else {


                        double difLatitudeABS = Math.abs(difLatitude);
                        double difLongitudABS = Math.abs(difLongitud);

                        //Se busca la diferencia maxima dado a que sera usada en la iteracion
                        if (difLatitudeABS > difLongitudABS) {

                            //Se calcula el numero de iteraciones para la diferencia maxima
                            int veces = (int) Math.ceil(difLatitudeABS / paso);

                            //Se calcula el signo del avance
                            int signo = difLatitude > 0 ? 1 : -1;

                            //Se comprueba que es necesario iterar
                            if (veces > 0) {

                                //Se itera
                                for (int j = 0; j < veces; j++) {

                                    //Se crea el vertice nuevo con los datos de la iteracion
                                    double latitude = lista.get(i - 1).getLatitud() + paso * j * signo;
                                    double longitude = lista.get(i - 1).getLongitud() + ((difLongitud * ((double) j)) / ((double) veces));

                                    Vertice nudo = new Vertice(info, resolucion, latitude, longitude, tipoVia, unible);

                                    interfazVertices.onNuevoVertice(nudo);
                                }
                            }

                        } else {

                            //Se calcula el numero de iteraciones para la diferencia maxima
                            int veces = (int) Math.ceil(difLongitudABS / paso);

                            //Se calcula el signo del avance
                            int signo = difLongitud > 0 ? 1 : -1;

                            //Se comprueba que es necesario iterar
                            if (veces > 0) {

                                //Se itera
                                for (int j = 0; j < veces; j++) {

                                    //Se crea el vertice nuevo con los datos de la iteracion
                                    double latitude = lista.get(i - 1).getLatitud() + ((difLatitude * ((double) j)) / ((double) veces));
                                    double longitude = lista.get(i - 1).getLongitud() + paso * j * signo;

                                    Vertice nudo = new Vertice(info, resolucion, latitude, longitude, tipoVia, unible);


                                    interfazVertices.onNuevoVertice(nudo);
                                }
                            }
                        }

                        //Para finalizar se añade el nodo actual
                        interfazVertices.onNuevoVertice(lista.get(i));
                        //android.util.Log.v("Creacion", "ultimo" + " => lat: " + lista.get(i + 1).getLatitude() + ", long: " + lista.get(i + 1).getLongitude());
                    }
                }
            }
        }

    }

    /**
     * Filtra si un determinado tipo de via se puede unir o no con otros
     *
     * @param tipoVia
     * @return
     */
    public static int getUnible(TipoVia tipoVia) {

        //Se evita que dos vertices puedan unirse si no es el inicio de la escalera
        if (tipoVia == TipoVia.ESCALERAS) {
            return Vertice.Unible.UNIBLE_CON_TIPOS_IGUALES;
        }

        //Se evita que dos vertices puedan unirse si no es el inicio del paso elevado
        if (tipoVia == TipoVia.PASO_ELEVADO) {
            return Vertice.Unible.UNIBLE_CON_TIPOS_IGUALES;
        }

        return Vertice.Unible.UNIBLE_CON_TODO;
    }
}
