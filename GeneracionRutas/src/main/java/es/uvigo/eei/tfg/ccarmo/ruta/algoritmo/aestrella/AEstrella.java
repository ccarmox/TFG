/*
 * Trabajo de fin de grado: "Definición e implementación de herramienta para la navegación de peatones en entornos urbanos para el sistema operativo móvil Android"
 * Creado por: Cristian Do Carmo Rodríguez
 * Tutora: Lucía Díaz Vilariño
 * Cotutor: Jesús Balado Frías
 * Copyright (c) 2024. Todos los derechos reservados.
 */

package es.uvigo.eei.tfg.ccarmo.ruta.algoritmo.aestrella;

import android.content.Context;

import androidx.annotation.NonNull;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;
import java.util.PriorityQueue;

import es.uvigo.eei.tfg.ccarmo.clases.classes.LatLon;
import es.uvigo.eei.tfg.ccarmo.clases.classes.Vertice;
import es.uvigo.eei.tfg.ccarmo.datos.almacenamiento.Configuracion;
import es.uvigo.eei.tfg.ccarmo.ruta.algoritmo.aestrella.classes.AEstrellaArista;
import es.uvigo.eei.tfg.ccarmo.ruta.algoritmo.aestrella.classes.AEstrellaVertice;
import es.uvigo.eei.tfg.ccarmo.ruta.classes.AlgoritmoBase;
import es.uvigo.eei.tfg.ccarmo.ruta.classes.PuntoCalor;
import es.uvigo.eei.tfg.ccarmo.ruta.classes.Ruta;

public class AEstrella extends AlgoritmoBase {

    private Map<String, AEstrellaVertice> nodes;

    public AEstrella(@NonNull String nombreDB, @NonNull Configuracion configuracion, Context context) {
        super(nombreDB, configuracion, context);

    }


    @Override
    protected void precargarNodosAlgoritmo(@NonNull LatLon inicio, @NonNull LatLon fin) {

        nodes = new HashMap<>();

        //Se carga la lista de vertices
        for (Vertice n : getVertices().values()) {

            if (n.getAristas().size() > 1) {
                //Guardo las nuevas clases
                nodes.put(n.getID(), new AEstrellaVertice(n, fin));

            }

        }

        //Se cargan las aristas de cada vertice
        for (AEstrellaVertice n2 : nodes.values()) {
            n2.cargarAristas(nodes);
        }

    }

    @Override
    protected void reiniciarNodosAlgoritmo() {
        //Se reinician todos los valores al valor inicial
        for (AEstrellaVertice n : nodes.values()) {
            n.reiniciar();
        }
    }

    @Override
    protected Ruta generarRutaDesde(@NonNull String id, @NonNull LatLon fin) {

        //Se encuentra el nodo de origen
        AEstrellaVertice origen = nodes.get(id);

        //Se comprueba que el nodo exista
        if (origen != null) {

            PriorityQueue<AEstrellaVertice> siguientes = new PriorityQueue<>(new Comparator<AEstrellaVertice>() {
                @Override
                public int compare(AEstrellaVertice o1, AEstrellaVertice o2) {
                    //Se atienden los nuevos vertices por prioridad segun su distancia a destino
                    return o1.getDistanciaOrigenMasDestinoAproximada().compareTo(o2.getDistanciaOrigenMasDestinoAproximada());
                }
            });

            //Se indica como distancia 0 al origen la del nodo origen
            origen.setDistanciaAOrigen(0);

            //Se añade el nodo a la cola de prioridad
            siguientes.add(origen);

            //Se itera sobre los valores de la cola de prioridad mientras haya elementos
            while (!siguientes.isEmpty()) {

                //Se obtiene el siguiente vertice por orden de prioridad
                //Y se borra de la lista
                AEstrellaVertice actual = siguientes.poll();

                //Se comprueba que el nodo existe
                if (actual != null) {

                    //Se itera sobre las aristas
                    for (AEstrellaArista arista : actual.getAristas()) {

                        //Se calcula la nueva distancia
                        Integer nuevoCoste = actual.getDistanciaAOrigen() + actual.getVertice().getPenalizacion() + arista.getDistancia();
                        AEstrellaVertice vecino = arista.getNudoSimple();

                        //Se comprueba que la nueva distancia es mejor
                        if (nuevoCoste < vecino.getDistanciaAOrigen()) {

                            //Se guarda el proceso que se ha realizado para llegar y el coste
                            vecino.setPadre(actual);
                            vecino.setDistanciaAOrigen(nuevoCoste);

                            //Se añade el nuevo nodo a la cola
                            siguientes.add(vecino);
                        }
                    }
                }
            }


            //Se busca el vertice mas cercano al destino
            AEstrellaVertice cercanoFinal = null;
            Integer distanciaCercana = 0;

            for (AEstrellaVertice n2 : nodes.values()) {
                //Compruebo que el nodo tiene una ruta
                if (n2.isValido()) {
                    if (cercanoFinal == null) {
                        cercanoFinal = n2;
                        distanciaCercana = n2.getDistanciaADestino();
                    } else {
                        if (distanciaCercana > n2.getDistanciaADestino()) {
                            cercanoFinal = n2;
                            distanciaCercana = n2.getDistanciaADestino();
                        }
                    }
                }
            }


            //Si se encuentra un punto cerca del destino valido se usa para recrear la ruta
            if (cercanoFinal != null) {

                //Si se ha llegado a un resultado se crea una lista con las clases que se necesitan
                //y se manda como resultado
                ArrayList<Vertice> listaFinal = new ArrayList<>();

                AEstrellaVertice a = nodes.get(cercanoFinal.getID());

                //Se rescatan los nodos anteriores al actual mientras existan
                while (a != null && a.isValido()) {
                    listaFinal.add(0, getVertices().get(a.getID()));
                    a = a.getPadre();
                }

                //Se crea el mapa de calor
                ArrayList<PuntoCalor> puntoCalors = new ArrayList<>();
                for (AEstrellaVertice node : nodes.values()) {
                    Vertice nudo = getVertices().get(node.getID());
                    if (nudo != null) {
                        int i2 = 0;
                        AEstrellaVertice a2 = node;
                        while (a2.isValido()) {
                            i2++;
                            a2 = a2.getPadre();
                        }
                        puntoCalors.add(new PuntoCalor(nudo.getLatitud(), nudo.getLongitud(), nudo.getAltitud(), i2));
                    }
                }


                return almacenarRuta(listaFinal, puntoCalors);
            }
        }

        return null;
    }

    @Override
    protected void reciclarNodosAlgoritmo() {
        nodes.clear();
    }
}
