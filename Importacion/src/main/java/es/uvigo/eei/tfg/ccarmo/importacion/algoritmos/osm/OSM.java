/*
 * Trabajo de fin de grado: "Definición e implementación de herramienta para la navegación de peatones en entornos urbanos para el sistema operativo móvil Android"
 * Creado por: Cristian Do Carmo Rodríguez
 * Tutora: Lucía Díaz Vilariño
 * Cotutor: Jesús Balado Frías
 * Copyright (c) 2024. Todos los derechos reservados.
 */

package es.uvigo.eei.tfg.ccarmo.importacion.algoritmos.osm;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import es.uvigo.eei.tfg.ccarmo.clases.classes.TipoVia;
import es.uvigo.eei.tfg.ccarmo.clases.classes.Vertice;
import es.uvigo.eei.tfg.ccarmo.importacion.algoritmos.osm.xml.OSMNodo;
import es.uvigo.eei.tfg.ccarmo.importacion.algoritmos.osm.xml.OSMObjeto;
import es.uvigo.eei.tfg.ccarmo.importacion.algoritmos.osm.xml.OSMReferenciaNodo;
import es.uvigo.eei.tfg.ccarmo.importacion.algoritmos.osm.xml.OSMTag;
import es.uvigo.eei.tfg.ccarmo.importacion.algoritmos.osm.xml.OSMVia;
import es.uvigo.eei.tfg.ccarmo.importacion.listeners.InterfazVertices;
import es.uvigo.eei.tfg.ccarmo.importacion.utils.Area;
import es.uvigo.eei.tfg.ccarmo.importacion.utils.Linea;

/**
 * Clase para parsear los archivos JOSM
 */
public class OSM {


    public static boolean parse(String text, int resolucion, InterfazVertices onDataParsed) {

        try {

            //Creo el parser de XML a clases
            ObjectMapper objectMapper = new XmlMapper();
            objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);

            //Leo el XML como objeto Osm
            OSMObjeto osm = objectMapper.readValue(text, OSMObjeto.class);

            //Compruebo que la lectura es correcta
            if (osm != null && osm.getVias() != null) {

                //Se crea un mapa de nodos
                Map<String, OSMNodo> valoresNodos = new HashMap<>();
                for (OSMNodo node : osm.getNodos()) {
                    valoresNodos.put(node.getId(), node);
                }

                //Itero sobre todas las vias
                for (OSMVia via : osm.getVias()) {

                    //Checkeo el tipo de via a traves de sus tags
                    TipoVia tipoVia = getTipoViaOSM(via.getTags());

                    //Si el tipo de via es valido sigue el proceso
                    if (tipoVia != TipoVia.NONE) {

                        //Construyo los nodos a traves de sus referencias
                        ArrayList<OSMNodo> nodos = new ArrayList<>();
                        OSMNodo temp = null;

                        for (OSMReferenciaNodo referencia : via.getReferenciaNodos()) {
                            temp = valoresNodos.get(referencia.getReferencia());
                            if (temp != null) {
                                nodos.add(temp);
                            }
                        }

                        //Compruebo que hay algun nodo
                        if (!nodos.isEmpty()) {

                            //Compruebo que el ultimo nodo y el primero sean el mismo si hay mas de 1 nodo
                            boolean area = nodos.size() > 1 && nodos.get(0).getId().equalsIgnoreCase(nodos.get(nodos.size() - 1).getId());

                            if (area) {

                                //Interpreto los datos como area
                                interpretarUnArea(via.getId(), nodos, via.getTags(), tipoVia, resolucion, onDataParsed);

                            } else {

                                //Interpreto los datos como via simple
                                interpretarUnaVia(via.getId(), nodos, via.getTags(), tipoVia, resolucion, onDataParsed);
                            }

                        }
                    }
                }

                return true;

            }

        } catch (Exception ignored) {
            ignored.printStackTrace();
        }

        return false;
    }

    /**
     * Transforma un poligono del mapa en una nube de nudos
     * para hacerlo se dibuja el poligono necesario y se comprueba si los pixeles solicitados
     * corresponden a un punto interior o exterior
     */
    public static void interpretarUnArea(String id, ArrayList<OSMNodo> nodes, List<OSMTag> tags, TipoVia tipoVia, int resolucion, InterfazVertices onDataParsed) {

        //Obtengo la variable que se asociara al nodo como informacion
        String info = "";

        if (tags != null) {
            for (OSMTag tag : tags) {
                if (tag.getClave().equals("name")) {
                    info = tag.getValor();
                    break;
                }
            }
        }

        //Transformo los NodosOSM en la clase Vertice
        ArrayList<Vertice> nudos = new ArrayList<>();

        for (int i = 0; i < nodes.size(); i++) {
            nudos.add(new Vertice(info, resolucion, nodes.get(i).getLatitud(), nodes.get(i).getLongitud(), tipoVia, Vertice.Unible.UNIBLE_CON_TODO));
        }

        android.util.Log.v("AREA", "=>" + id + " => " + tipoVia);

        //Utilizo el importador generico para importar los vertices como area
        Area.interpretarUnArea(nudos, tipoVia, resolucion, onDataParsed);

    }

    /**
     * Transforma una línea del mapa en una linea de nudos
     */
    public static void interpretarUnaVia(String id, ArrayList<OSMNodo> nodes, List<OSMTag> tags, TipoVia tipoVia, int resolucion, InterfazVertices onDataParsed) {

        //Obtengo la variable que se asociara al nodo como informacion
        String info = "";

        if (tags != null) {
            for (OSMTag tag : tags) {
                if (tag.getClave().equals("name")) {
                    info = tag.getValor();
                    break;
                }
            }
        }

        //Transformo los NodosOSM en la clase Vertice
        ArrayList<Vertice> temp = new ArrayList<>();

        for (int i = 0; i < nodes.size(); i++) {
            temp.add(new Vertice(info, resolucion, nodes.get(i).getLatitud(), nodes.get(i).getLongitud(), tipoVia, Vertice.Unible.UNIBLE_CON_TODO));
        }

        //Utilizo el importador generico para importar los vertices como via
        Linea.addNudosIntermedios(temp, resolucion, onDataParsed);

    }

    @NonNull
    public static TipoVia getTipoViaOSM(@Nullable List<OSMTag> tagList) {

        if (tagList == null) {
            return TipoVia.NONE;
        }

        for (OSMTag tag : tagList) {
            if (tag.getClave().equals("highway")) {

                switch (tag.getValor()) {
                    case "residential": //Roads which serve as an access to housing, without function of connecting settlements.
                        return TipoVia.VIA_RESIDENCIAL;
                    case "service":
                        return TipoVia.VIA_SERVICIO;
                    case "pedestrian":
                        return TipoVia.VIA_PEATONAL;
                    case "track":
                        return TipoVia.SENDERO;
                    case "steps":
                        return TipoVia.ESCALERAS;
                    case "path": //A non-specific path
                        return TipoVia.SENDERO;

                    case "footway":     //Path for walkers
                    case "corridor":    //For a hallway inside of a building.
                        return TipoVia.ACERA;
                }
            }

            if (tag.getClave().equals("footway")) {

                switch (tag.getValor()) {
                    case "sidewalk":    //Sidewalk that runs typically along residential road
                        return TipoVia.ACERA;
                    case "crossing":    //Crosswalk that connects two sidewalks on the opposite side of the road
                        return TipoVia.PASO_DE_PEATONES;
                }
            }

            if (tag.getClave().equals("leisure")) {

                switch (tag.getValor()) {
                    case "park":
                        return TipoVia.SENDERO;
                }
            }

            if (tag.getClave().equals("natural")) {

                switch (tag.getValor()) {
                    case "beach":
                        return TipoVia.SENDERO;
                }
            }


        }

        return TipoVia.NONE;
    }


}
