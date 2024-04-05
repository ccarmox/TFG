/*
 * Trabajo de fin de grado: "Definición e implementación de herramienta para la navegación de peatones en entornos urbanos para el sistema operativo móvil Android"
 * Creado por: Cristian Do Carmo Rodríguez
 * Tutora: Lucía Díaz Vilariño
 * Cotutor: Jesús Balado Frías
 * Copyright (c) 2024. Todos los derechos reservados.
 */

package es.uvigo.eei.tfg.ccarmo.importacion.algoritmos.gpx;

import androidx.annotation.NonNull;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;

import java.util.ArrayList;

import es.uvigo.eei.tfg.ccarmo.clases.classes.TipoVia;
import es.uvigo.eei.tfg.ccarmo.clases.classes.Vertice;
import es.uvigo.eei.tfg.ccarmo.importacion.algoritmos.gpx.xml.GPXObjeto;
import es.uvigo.eei.tfg.ccarmo.importacion.algoritmos.gpx.xml.GPXPunto;
import es.uvigo.eei.tfg.ccarmo.importacion.algoritmos.gpx.xml.GPXSegmento;
import es.uvigo.eei.tfg.ccarmo.importacion.algoritmos.gpx.xml.GPXVuelta;
import es.uvigo.eei.tfg.ccarmo.importacion.listeners.InterfazVertices;
import es.uvigo.eei.tfg.ccarmo.importacion.utils.Linea;
import es.uvigo.eei.tfg.ccarmo.importacion.utils.Utils;

public class GPX {


    public static boolean parse(String text, int resolucion, @NonNull TipoVia tipoVia, int unible, @NonNull InterfazVertices onDataParsed) {

        try {


            ObjectMapper objectMapper = new XmlMapper();
            objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);

            //Se lee el XML como objeto GPX
            GPXObjeto gpx = objectMapper.readValue(text, GPXObjeto.class);

            //Compruebo que existe contenido
            if (gpx != null && gpx.getVueltas() != null) {

                //Se itera sobre la lista de vueltas
                for (GPXVuelta vuelta : gpx.getVueltas()) {

                    //Se comprueba que la vuelta actual tenga algun segmento
                    if (vuelta != null && vuelta.getSegmentos() != null && !vuelta.getSegmentos().isEmpty()) {

                        //Se itera sobre la lista de segmentos
                        for (GPXSegmento segmento : vuelta.getSegmentos()) {

                            //Se añaden todos los vertices del segmento
                            ArrayList<Vertice> temp = new ArrayList<>();

                            for (GPXPunto trkpt : segmento.getPuntos()) {

                                temp.add(new Vertice("", resolucion, trkpt.getLatitud(), trkpt.getLongitud(), trkpt.getAltitud(), tipoVia, unible));

                            }

                            //Se eliminan vertices duplicados
                            temp = Utils.getNudosSinRepetir(temp);

                            //Se añaden junto con los nudos entre vertices si es preciso
                            Linea.addNudosIntermedios(temp, resolucion, onDataParsed);

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
