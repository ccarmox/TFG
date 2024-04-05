/*
 * Trabajo de fin de grado: "Definición e implementación de herramienta para la navegación de peatones en entornos urbanos para el sistema operativo móvil Android"
 * Creado por: Cristian Do Carmo Rodríguez
 * Tutora: Lucía Díaz Vilariño
 * Cotutor: Jesús Balado Frías
 * Copyright (c) 2024. Todos los derechos reservados.
 */

package es.uvigo.eei.tfg.ccarmo.clases.classes;

import androidx.annotation.NonNull;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;

import es.uvigo.eei.tfg.ccarmo.clases.utils.Baremos;
import es.uvigo.eei.tfg.ccarmo.utils.Colores;


public class Vertice extends Pixel implements Serializable, Comparable<Vertice> {

    private final String informacion;
    private final TipoVia tipoVia;
    private Integer penalizacion = 0;
    private int posicion = 0;
    private int unible;
    private ArrayList<Arista> aristas = new ArrayList<>();

    public Vertice(String informacion, int resolucion, double latitude, double longitude, TipoVia tipoVia, int unible) {
        this(informacion, resolucion, latitude, longitude, 0, tipoVia, unible);
    }

    public Vertice(String informacion, int resolucion, double latitude, double longitude, TipoVia tipoVia, int unible, int posicion) {
        this(informacion, resolucion, latitude, longitude, 0, tipoVia, unible, posicion);
    }

    public Vertice(String informacion, int resolucion, double latitude, double longitude, double altitude, TipoVia tipoVia, int unible) {
        super(latitude, longitude, altitude, resolucion);
        this.informacion = informacion;
        this.tipoVia = tipoVia;
        this.unible = unible;
    }

    public Vertice(String informacion, int resolucion, double latitude, double longitude, double altitude, TipoVia tipoVia, int unible, int posicion) {
        super(latitude, longitude, altitude, resolucion);
        this.informacion = informacion;
        this.tipoVia = tipoVia;
        this.unible = unible;
        this.posicion = posicion;
    }

    public Vertice(String informacion, double latitude, double longitude, double altitude, int waytype, int unible, String near, int resolucion, int penalizacion, int posicion) {
        super(latitude, longitude, altitude, resolucion);

        this.informacion = informacion;
        this.tipoVia = TipoVia.parse(waytype);
        this.unible = unible;
        this.penalizacion = penalizacion;
        this.posicion = posicion;

        setProximo(near);
    }

    public void addProximo(Vertice nodo, Integer distance) {
        if (!isProximo(nodo)) {
            aristas.add(new Arista(nodo.getID(), distance));
        }
    }

    public void addProximo(Vertice nodo) {
        if (!isProximo(nodo)) {
            int coste = (int) getDistanciaFisicaA(nodo);
            aristas.add(new Arista(nodo.getID(), coste));
        }
    }

    public boolean isProximo(Vertice nudo) {
        for (Arista nod : getAristas()) {
            if (nod.getId().equals(nudo.getID())) {
                return true;
            }
        }
        return false;
    }

    public void addProximo(Arista union) {
        aristas.add(union);
    }

    public void addProximo(ArrayList<Arista> lista) {
        aristas.addAll(lista);
    }

    public ArrayList<Arista> getAristas() {
        return aristas;
    }

    public void ordenarAristasPorPeso() {
        Collections.sort(aristas);
    }

    public boolean isProximo(String id) {
        if (aristas != null && aristas.size() > 0) {
            for (Arista u : aristas) {
                if (u.id.equals(id)) {
                    return true;
                }
            }
        }
        return false;
    }

    public boolean isPocaDistanciaA(Vertice nodo) {
        double limite = Baremos.getDistanciaEnGradosLimiteEntrePuntos(getResolucion());
        if (Math.abs(getLatitud() - nodo.getLatitud()) > limite) {
            return false;
        }
        return !(Math.abs(getLongitud() - nodo.getLongitud()) > limite);
    }

    public Integer getDistanciaFisica2D(LatLon latLon) {
        return (int) Math.ceil(getDistanciaFisicaA(latLon));
    }

    public boolean isContieneProximo(Vertice nudo) {
        return isContieneProximo(nudo.getID());
    }

    public boolean isContieneProximo(String referencia) {
        for (Arista union : aristas) {
            if (union.getId().equals(referencia)) {
                return true;
            }
        }
        return false;
    }

    public void eliminarProximos() {
        aristas.clear();
    }

    public LatLon getLatLon() {
        return new LatLon(this);
    }

    public int getColor() {

        switch (tipoVia) {
            case POI:
                return Colores.GREEN_400;
            case VIA_PEATONAL:
                return Colores.RED_400;
            case VIA_SERVICIO:
                return Colores.BLUE_400;
            case VIA_RESIDENCIAL:
                return Colores.TEAL_400;
            case SENDERO:
                return Colores.BROWN_400;
            case PASO_DE_PEATONES:
                return Colores.DARK_PURPLE_400;
            case PASO_ELEVADO:
                return Colores.DARK_PURPLE_A200;
            case ESCALERAS:
                return Colores.CYAN_400;
            case ACERA:
                return Colores.GREY_400;
            case NONE:
            default:
                return Colores.BLACK;
        }

    }

    public String getInformacion() {
        return informacion;
    }

    public int getValorTipoVia() {
        return tipoVia.getValor();
    }

    public String getProximo() {
        String near = "";

        for (Arista union : aristas) {
            near = near + "<" + union.getId() + "x" + union.getCoste() + ">";
        }

        return near;
    }

    public void setProximo(String near) {
        aristas = new ArrayList<>();

        String[] sp = near.split("<");

        if (sp.length > 1) {
            for (int i = 1; i < sp.length; i++) {
                String nid = sp[i].split("x")[0];
                Integer distance = Integer.parseInt(sp[i].split("x")[1].split(">")[0]);
                aristas.add(new Arista(nid, distance));
            }
        }

    }

    public String getJSON() {
        return "{\n" +
                "      \"type\": \"Feature\",\n" +
                "      \"properties\": {\n" +
                "        \"nombre\": \"" + getID() + "\"\n" +
                "      },\n" +
                "      \"geometry\": {\n" +
                "        \"type\": \"Point\",\n" +
                "        \"coordinates\": [\n" +
                "          " + getLongitud() + ",\n" +
                "          " + getLatitud() + "\n" +
                "        ]\n" +
                "      }\n" +
                "    },";
    }

    public int getPosicion() {
        return posicion;
    }

    public void setPosicion(int posicion) {
        this.posicion = posicion;
    }

    public TipoVia getTipoVia() {
        return tipoVia;
    }

    public boolean isUnible(Vertice b) {

        if (unible == Unible.UNIBLE_CON_TODO) {
            return true;
        }

        if (unible == Unible.UNIBLE_CON_TIPOS_IGUALES) {
            return getValorTipoVia() == b.getValorTipoVia();
        }

        return false;
    }

    public int getUnible() {
        return unible;
    }

    public void setUnible(int unible) {
        this.unible = unible;
    }

    public Integer getPenalizacion() {
        return penalizacion;
    }

    public void setPenalizacion(int penalizacion) {
        this.penalizacion = penalizacion;
    }

    @Override
    public int compareTo(Vertice o) {
        return posicion - o.posicion;
    }

    @NonNull
    @Override
    public String toString() {
        return "Nudo{" +
                "\nid='" + getID() + '\'' +
                "\nlatitude='" + getLatitud() + '\'' +
                "\nlongitude='" + getLongitud() + '\'' +
                "\ninfo='" + informacion + '\'' +
                ", \ntipoVia=" + tipoVia +
                ", \npenalizacion=" + penalizacion +
                ", \nunible=" + unible +
                ", \nproximos=" + aristas +
                "\n}";
    }

    public static final class Unible {
        public static final int UNIBLE_CON_TIPOS_IGUALES = 0;
        public static final int UNIBLE_CON_TODO = 1;
    }
}
