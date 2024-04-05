/*
 * Trabajo de fin de grado: "Definición e implementación de herramienta para la navegación de peatones en entornos urbanos para el sistema operativo móvil Android"
 * Creado por: Cristian Do Carmo Rodríguez
 * Tutora: Lucía Díaz Vilariño
 * Cotutor: Jesús Balado Frías
 * Copyright (c) 2024. Todos los derechos reservados.
 */

package es.uvigo.eei.tfg.ccarmo.importacion.algoritmos.tcx;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;

import java.util.ArrayList;

import es.uvigo.eei.tfg.ccarmo.clases.classes.TipoVia;
import es.uvigo.eei.tfg.ccarmo.clases.classes.Vertice;
import es.uvigo.eei.tfg.ccarmo.importacion.algoritmos.tcx.xml.TCXActividad;
import es.uvigo.eei.tfg.ccarmo.importacion.algoritmos.tcx.xml.TCXActividades;
import es.uvigo.eei.tfg.ccarmo.importacion.algoritmos.tcx.xml.TCXCamino;
import es.uvigo.eei.tfg.ccarmo.importacion.algoritmos.tcx.xml.TCXObjeto;
import es.uvigo.eei.tfg.ccarmo.importacion.algoritmos.tcx.xml.TCXPunto;
import es.uvigo.eei.tfg.ccarmo.importacion.algoritmos.tcx.xml.TCXVuelta;
import es.uvigo.eei.tfg.ccarmo.importacion.listeners.InterfazVertices;
import es.uvigo.eei.tfg.ccarmo.importacion.utils.Linea;
import es.uvigo.eei.tfg.ccarmo.importacion.utils.Utils;

public class TCX {


    public static boolean parse(String text, int resolucion, TipoVia tipoVia, int unible, InterfazVertices onDataParsed) {

        try {

            android.util.Log.v("Contenido", "*>" + text);

            ObjectMapper objectMapper = new XmlMapper();
            objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);

            TCXObjeto tcd = objectMapper.readValue(text, TCXObjeto.class);

            //Compruebo que existe contenido
            if (tcd != null && tcd.getActividades() != null) {

                //Se itera sobre la lista de actividades
                for (TCXActividades actividades : tcd.getActividades()) {

                    //Se itera sobre cada actividad
                    for (TCXActividad activity : actividades.getActividad()) {

                        //Se itera sobre cada vuelta
                        for (TCXVuelta vuelta : activity.getVuelta()) {

                            //Se itera sobre cada camino
                            for (TCXCamino camino : vuelta.getCamino()) {

                                ArrayList<Vertice> temp = new ArrayList<>();

                                //Se añaden todos los puntos del camino
                                for (TCXPunto trackpoint : camino.getPuntos()) {
                                    temp.add(new Vertice("", resolucion, trackpoint.getLatitudeDegrees(), trackpoint.getLongitudeDegrees(), trackpoint.getAltitud(), tipoVia, unible));
                                }

                                //Se eliminan los duplicados
                                //Es necesario ya que la densidad de puntos puede ser mayor a la resolucion
                                temp = Utils.getNudosSinRepetir(temp);

                                //Se importan todos los vertices añadiendo los nodos intermedios como linea
                                Linea.addNudosIntermedios(temp, resolucion, onDataParsed);
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

}
