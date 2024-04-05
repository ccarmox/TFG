/*
 * Trabajo de fin de grado: "Definición e implementación de herramienta para la navegación de peatones en entornos urbanos para el sistema operativo móvil Android"
 * Creado por: Cristian Do Carmo Rodríguez
 * Tutora: Lucía Díaz Vilariño
 * Cotutor: Jesús Balado Frías
 * Copyright (c) 2024. Todos los derechos reservados.
 */

package es.uvigo.eei.tfg.ccarmo.ruta.algoritmo.aestrella.classes;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.Map;

import es.uvigo.eei.tfg.ccarmo.clases.classes.Arista;
import es.uvigo.eei.tfg.ccarmo.clases.classes.LatLon;
import es.uvigo.eei.tfg.ccarmo.clases.classes.Vertice;
import es.uvigo.eei.tfg.ccarmo.ruta.classes.VerticeBase;

public class AEstrellaVertice extends VerticeBase {

    private final Integer distanciaADestino;
    private final ArrayList<AEstrellaArista> aristas = new ArrayList<>();
    private AEstrellaVertice padre;
    private Integer distanciaAOrigen = Integer.MAX_VALUE;

    public AEstrellaVertice(Vertice nudo, LatLon destino) {
        super(nudo);
        this.distanciaADestino = nudo.getDistanciaFisica2D(destino);
    }

    public void cargarAristas(Map<String, AEstrellaVertice> total) {
        for (Arista union : getVertice().getAristas()) {
            AEstrellaVertice l2 = total.get(union.getId());

            //Es posible que un nodo no este dentro de la base de datos, por ello hay que comprobarlo
            if (l2 != null) {
                //Si el nodo existe se añade
                aristas.add(new AEstrellaArista(l2, union.getCoste() + getVertice().getPenalizacion()));
            }
        }
        aristas.sort(new Comparator<AEstrellaArista>() {
            @Override
            public int compare(AEstrellaArista o1, AEstrellaArista o2) {
                return o1.getNudoSimple().distanciaADestino.compareTo(o2.getNudoSimple().distanciaADestino);
            }
        });
    }

    public void reiniciar() {
        this.distanciaAOrigen = Integer.MAX_VALUE;
        this.padre = null;
    }

    public boolean isValido() {
        return padre != null;
    }

    public AEstrellaVertice getPadre() {
        return padre;
    }

    public void setPadre(AEstrellaVertice padre) {
        this.padre = padre;
    }

    public Integer getDistanciaAOrigen() {
        return distanciaAOrigen;
    }

    public void setDistanciaAOrigen(Integer distanciaAOrigen) {
        this.distanciaAOrigen = distanciaAOrigen;
    }

    public ArrayList<AEstrellaArista> getAristas() {
        return aristas;
    }

    public Integer getDistanciaADestino() {
        return distanciaADestino;
    }

    public Integer getDistanciaOrigenMasDestinoAproximada() {
        return distanciaADestino + distanciaAOrigen;
    }

}
