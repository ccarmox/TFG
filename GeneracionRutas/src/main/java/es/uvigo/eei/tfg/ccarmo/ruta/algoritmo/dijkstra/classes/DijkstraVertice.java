/*
 * Trabajo de fin de grado: "Definición e implementación de herramienta para la navegación de peatones en entornos urbanos para el sistema operativo móvil Android"
 * Creado por: Cristian Do Carmo Rodríguez
 * Tutora: Lucía Díaz Vilariño
 * Cotutor: Jesús Balado Frías
 * Copyright (c) 2024. Todos los derechos reservados.
 */

package es.uvigo.eei.tfg.ccarmo.ruta.algoritmo.dijkstra.classes;

import java.util.ArrayList;
import java.util.Map;

import es.uvigo.eei.tfg.ccarmo.clases.classes.Arista;
import es.uvigo.eei.tfg.ccarmo.clases.classes.Vertice;
import es.uvigo.eei.tfg.ccarmo.ruta.classes.VerticeBase;

public class DijkstraVertice extends VerticeBase {


    private final ArrayList<DijkstraArista> aristas = new ArrayList<>();
    private String[] camino;
    private Integer distanciaAOrigen = Integer.MAX_VALUE;

    public DijkstraVertice(Vertice nudo) {
        super(nudo);
    }

    public void reiniciar() {
        this.distanciaAOrigen = Integer.MAX_VALUE;
        this.camino = null;
    }

    public boolean isValido() {
        return camino != null && camino.length > 0;
    }

    public String[] getCamino() {
        return camino;
    }

    public void setCamino(String[] camino) {
        this.camino = camino;
    }

    public Integer getDistanciaAOrigen() {
        return distanciaAOrigen;
    }

    public void setDistanciaAOrigen(Integer distanciaAOrigen) {
        this.distanciaAOrigen = distanciaAOrigen;
    }

    public int getIndiceCalor() {
        if (camino == null) {
            return 0;
        }
        return camino.length;
    }

    public ArrayList<DijkstraArista> getAristas() {
        return aristas;
    }

    public void cargarUniones(Map<String, DijkstraVertice> total) {

        getVertice().ordenarAristasPorPeso();

        for (Arista union : getVertice().getAristas()) {
            DijkstraVertice l2 = total.get(union.getId());

            //Es posible que un nodo no este dentro de la base de datos, por ello hay que comprobarlo
            if (l2 != null) {
                //Si el nodo existe se añade
                aristas.add(new DijkstraArista(l2, union.getCoste()));
            }
        }
    }

}
