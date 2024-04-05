/*
 * Trabajo de fin de grado: "Definición e implementación de herramienta para la navegación de peatones en entornos urbanos para el sistema operativo móvil Android"
 * Creado por: Cristian Do Carmo Rodríguez
 * Tutora: Lucía Díaz Vilariño
 * Cotutor: Jesús Balado Frías
 * Copyright (c) 2024. Todos los derechos reservados.
 */

package es.uvigo.eei.tfg.ccarmo.ruta.algoritmo.dijkstra;

import android.content.Context;

import androidx.annotation.NonNull;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import es.uvigo.eei.tfg.ccarmo.clases.classes.LatLon;
import es.uvigo.eei.tfg.ccarmo.clases.classes.Vertice;
import es.uvigo.eei.tfg.ccarmo.datos.almacenamiento.Configuracion;
import es.uvigo.eei.tfg.ccarmo.ruta.algoritmo.dijkstra.classes.DijkstraArista;
import es.uvigo.eei.tfg.ccarmo.ruta.algoritmo.dijkstra.classes.DijkstraVertice;
import es.uvigo.eei.tfg.ccarmo.ruta.classes.AlgoritmoBase;
import es.uvigo.eei.tfg.ccarmo.ruta.classes.PuntoCalor;
import es.uvigo.eei.tfg.ccarmo.ruta.classes.Ruta;

public class Dijkstra extends AlgoritmoBase {

    private Map<String, DijkstraVertice> nodes;


    public Dijkstra(@NonNull String nombreDB, @NonNull Configuracion configuracion, Context context) {
        super(nombreDB, configuracion, context);
    }

    @Override
    protected Ruta generarRutaDesde(@NonNull String id, @NonNull LatLon fin) {

        DijkstraVertice origen = nodes.get(id);

        //Se comprueba que el vertice exista
        if (origen != null) {

            //Cargo las distancias desde el inicio de manera recursiva
            cargar(origen, new String[]{}, 0);


            //Busco la ruta que me deja mas cerca de mi destino
            DijkstraVertice cercanoFinal = null;
            Integer distanciaCercana = 0;

            //Se itera sobre la lista de nodos
            for (DijkstraVertice n2 : nodes.values()) {

                //Compruebo que el nodo esta conectado a algo
                if (n2.isValido()) {

                    //Si aun no tengo resultado lo uso como resultado
                    if (cercanoFinal == null) {

                        //Se guarda como resultado el nuevo nodo
                        cercanoFinal = n2;
                        distanciaCercana = n2.getVertice().getDistanciaFisica2D(fin);

                    } else {
                        //Si ya existe un resultado se comprueba si es mejor
                        Integer d = n2.getVertice().getDistanciaFisica2D(fin);
                        if (distanciaCercana > d) {

                            //Se guarda como resultado el nuevo nodo
                            cercanoFinal = n2;
                            distanciaCercana = d;

                        }
                    }
                }
            }

            //Si hay un punto cerca de mi destino devuelvo su ruta
            if (cercanoFinal != null) {

                //Si se ha llegado a un resultado creo una lista con las clases que se necesitan
                //y la mando como resultado
                ArrayList<Vertice> listaFinal = new ArrayList<>();

                for (String n : cercanoFinal.getCamino()) {
                    Vertice nudo = getVertices().get(n);
                    if (nudo != null) {
                        listaFinal.add(nudo);
                    }
                }

                //Se genera la lista de puntos de calor
                ArrayList<PuntoCalor> puntoCalors = new ArrayList<>();
                for (DijkstraVertice node : nodes.values()) {
                    puntoCalors.add(new PuntoCalor(node.getLatitud(), node.getLongitud(), node.getAltitud(), node.getIndiceCalor()));
                }

                //Se envia la ruta como resultado
                return almacenarRuta(listaFinal, puntoCalors);
            }
        }

        //Si no hay ruta se envia un valor nulo
        return null;
    }

    @Override
    protected void precargarNodosAlgoritmo(@NonNull LatLon inicio, @NonNull LatLon fin) {

        nodes = new HashMap<>();

        //Se genera una lista de los nodos junto con sus uniones precargadas
        for (Vertice n : getVertices().values()) {

            if (n.getAristas().size() > 1) {
                //Guardo las nuevas clases
                nodes.put(n.getID(), new DijkstraVertice(n));

            }

        }

        //Se añaden los vertices de cada arista
        for (DijkstraVertice n2 : nodes.values()) {
            n2.cargarUniones(nodes);
        }

    }

    @Override
    protected void reiniciarNodosAlgoritmo() {

        //Se reinician los valores de todos los nodos para aplicar de nuevo el algoritmo
        for (DijkstraVertice n : nodes.values()) {
            n.reiniciar();
        }

    }


    private void cargar(@NonNull DijkstraVertice nodo, @NonNull String[] camino, Integer distanciaAcumulada) {

        //Compruebo si existe una forma mas rapida de llegar a este nodo
        if ((distanciaAcumulada + nodo.getVertice().getPenalizacion()) < nodo.getDistanciaAOrigen()) {

            //Se guarda la ruta actual como mejor opcion
            nodo.setDistanciaAOrigen(distanciaAcumulada + nodo.getVertice().getPenalizacion());
            nodo.setCamino(camino);

            //Se añade el nodo al final del camino anterior
            String[] nuevoCamino = new String[camino.length + 1];
            System.arraycopy(camino, 0, nuevoCamino, 0, camino.length);
            nuevoCamino[nuevoCamino.length - 1] = nodo.getID();

            //Se itera sobre todas las aristas
            for (DijkstraArista arista : nodo.getAristas()) {
                cargar(arista.getNudoSimple(), nuevoCamino, distanciaAcumulada + nodo.getVertice().getPenalizacion() + arista.getDistancia());
            }
        }

    }

    @Override
    protected void reciclarNodosAlgoritmo() {
        nodes.clear();
    }
}
