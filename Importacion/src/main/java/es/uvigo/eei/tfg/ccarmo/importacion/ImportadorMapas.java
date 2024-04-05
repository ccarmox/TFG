/*
 * Trabajo de fin de grado: "Definición e implementación de herramienta para la navegación de peatones en entornos urbanos para el sistema operativo móvil Android"
 * Creado por: Cristian Do Carmo Rodríguez
 * Tutora: Lucía Díaz Vilariño
 * Cotutor: Jesús Balado Frías
 * Copyright (c) 2024. Todos los derechos reservados.
 */

package es.uvigo.eei.tfg.ccarmo.importacion;

import android.app.Activity;
import android.content.Context;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.ArrayList;
import java.util.Map;

import es.uvigo.eei.tfg.ccarmo.clases.classes.TipoVia;
import es.uvigo.eei.tfg.ccarmo.clases.classes.Vertice;
import es.uvigo.eei.tfg.ccarmo.clases.utils.Baremos;
import es.uvigo.eei.tfg.ccarmo.datos.almacenamiento.Configuracion;
import es.uvigo.eei.tfg.ccarmo.datos.almacenamiento.Margen;
import es.uvigo.eei.tfg.ccarmo.datos.almacenamiento.SQLite;
import es.uvigo.eei.tfg.ccarmo.importacion.algoritmos.geojson.GEOJSON;
import es.uvigo.eei.tfg.ccarmo.importacion.algoritmos.gpx.GPX;
import es.uvigo.eei.tfg.ccarmo.importacion.algoritmos.osm.OSM;
import es.uvigo.eei.tfg.ccarmo.importacion.algoritmos.sql.SQL;
import es.uvigo.eei.tfg.ccarmo.importacion.algoritmos.tcx.TCX;
import es.uvigo.eei.tfg.ccarmo.importacion.classes.Fuente;
import es.uvigo.eei.tfg.ccarmo.importacion.classes.ParserCache;
import es.uvigo.eei.tfg.ccarmo.importacion.listeners.InterfazVertices;

public class ImportadorMapas {


    public ImportadorMapas(@Nullable ArrayList<Fuente> fuentes, @NonNull TipoVia tipoVia, @Nullable String db, int resolucion, @NonNull Activity context, @NonNull InterfazProgreso loader) {

        if (fuentes == null || db == null || db.length() == 0) {
            loader.onError();
            return;
        }


        new Thread(new Runnable() {
            @Override
            public void run() {

                boolean exito = true;
                final ParserCache parserCache = new ParserCache();

                try {

                    if (db.equalsIgnoreCase(SQLite.CACHE_NAME)) {
                        SQLite x = SQLite.getBaseDeDatos(db, context);
                        x.borrar();
                        x.close();
                    }

                    SQLite sql = SQLite.getBaseDeDatos(db, context);

                    exito = exito && importarFuentes(parserCache, sql, fuentes, tipoVia, resolucion, context, loader);

                    exito = exito && regenerarUniones(parserCache, sql, tipoVia, resolucion, context, loader);

                    sql.close();

                } catch (Exception exception) {

                    exception.printStackTrace();
                    exito = false;

                }

                if (exito) {
                    loader.onCompletado(parserCache.getLatitudCentro(), parserCache.getLongitudCentro());
                } else {
                    loader.onError();
                }

            }
        }).start();

    }

    private static boolean importarFuentes(@NonNull ParserCache parserCache, @NonNull SQLite db, ArrayList<Fuente> fuentes, @NonNull TipoVia tipoVia, int resolucion, @NonNull Context context, @NonNull InterfazProgreso loader) {

        boolean exito = true;
        int total = fuentes.size();

        int porcentaje = 0;
        int progreso = 0;


        loader.onPorcentajeActualizado(0);

        db.iniciarEscritura();

        InterfazVertices onDataParsed = new InterfazVertices() {

            @Override
            public void onNuevoVertice(@NonNull Vertice nudo) {
                db.addVertice(nudo);
                parserCache.refresh(nudo);
            }

            @Override
            public void onFinalizado() {
                db.terminarEscritura();
            }
        };

        for (Fuente fuente : fuentes) {

            //Evaluo cada una de las fuentes de puntos
            loader.onRealizandoAccion(fuente.getInfo());


            switch (fuente.getTipoDato()) {
                case OSM:
                    exito = exito && OSM.parse(fuente.getFile(context), resolucion, onDataParsed);
                    break;
                case TCX:
                    exito = exito && TCX.parse(fuente.getFile(context), resolucion, tipoVia, fuente.getUnible(), onDataParsed);
                    break;
                case GPX:
                    exito = exito && GPX.parse(fuente.getFile(context), resolucion, tipoVia, fuente.getUnible(), onDataParsed);
                    break;
                case GEOJSON:
                    exito = exito && GEOJSON.parse(fuente.getFile(context), resolucion, tipoVia, fuente.getUnible(), onDataParsed);
                    break;
                case SQL:
                    exito = exito && SQL.parse(fuente.getSQLite(context), resolucion, tipoVia, fuente.getUnible(), onDataParsed);
                    break;
            }


            progreso++;
            int porcentaje2 = (int) ((double) (progreso * 49) / (double) total);
            if (porcentaje != porcentaje2) {
                porcentaje = porcentaje2;
                loader.onPorcentajeActualizado(porcentaje);
            }
        }

        onDataParsed.onFinalizado();

        return exito;

    }

    private boolean regenerarUniones(@NonNull ParserCache parserCache, @NonNull SQLite db, TipoVia tipoVia, int resolucion, @NonNull Context context, @NonNull InterfazProgreso loader) {

        loader.onRealizandoAccion("Regenerando uniones");

        try {

            android.util.Log.v("Parametros", "Latitud [" + parserCache.getMinLatitude() + ", " + parserCache.getMaxLatitude() + "]");
            android.util.Log.v("Parametros", "Longitu [" + parserCache.getMinLongitude() + ", " + parserCache.getMaxLongitude() + "]");

            double margen = Baremos.getMargenParaLeerLaBaseDeDatos(resolucion);
            double paso = margen;

            double latitudLimite = parserCache.getMaxLatitude() + margen;
            double longitudLimite = parserCache.getMaxLongitude() + margen;

            double latitudInicial = parserCache.getMinLatitude() - margen;
            double longitudInicial = parserCache.getMinLongitude() - margen;

            double maximaDistanciaValida = Baremos.getDistanciaEnMetrosEntrePuntos(resolucion);


            boolean aContieneB;
            boolean bContieneA;

            //Calculo el numero total de iteraciones para actualizar el progreso
            int bucles = ((int) Math.round((latitudLimite - latitudInicial) / paso) + 1) * ((int) Math.round((longitudLimite - longitudInicial) / paso) + 1);
            int bucle = 0;

            android.util.Log.v("BUCLES", "total: " + bucles);

            if (bucles < 0) {
                //Existe un error
                loader.onError();
            }

            int porcentaje = 0;

            loader.onPorcentajeActualizado(porcentaje);

            //Divido el problema en fragmentos para acelerar el proceso
            //En lugar de regenerar toda la gráfica lo divido en cuadros
            for (double latitude = latitudInicial; latitude <= latitudLimite; latitude = latitude + paso) {

                for (double longitude = longitudInicial; longitude <= longitudLimite; longitude = longitude + paso) {

                    //Actualizo visualmente el porcentaje aumentado del progreso
                    bucle++;
                    int porcentaje2 = (int) ((double) (bucle * 50) / (double) bucles) + 50;
                    if (porcentaje2 != porcentaje) {
                        porcentaje = porcentaje2;
                        loader.onPorcentajeActualizado(porcentaje);
                    }

                    Map<String, Vertice> nudos = db.getVerticesMap(Margen.SIN_MARGEN, latitude - margen, latitude + paso + margen, longitude - margen, longitude + paso + margen, new Configuracion(null));


                    if (!nudos.isEmpty()) {

                        for (Map.Entry<String, Vertice> nodoA : nudos.entrySet()) {
                            for (Map.Entry<String, Vertice> nodoB : nudos.entrySet()) {

                                if (nodoA != nodoB) {

                                    if (nodoA.getValue().isUnible(nodoB.getValue()) && nodoB.getValue().isUnible(nodoA.getValue())) {

                                        aContieneB = nodoA.getValue().isContieneProximo(nodoB.getValue());
                                        bContieneA = nodoB.getValue().isContieneProximo(nodoA.getValue());

                                        if (!aContieneB || !bContieneA) {
                                            if (nodoA.getValue().isPocaDistanciaA(nodoB.getValue())) {

                                                //Calculo de la distancia entre puntos con una biblioteca
                                                double distancias = nodoA.getValue().getDistanciaFisicaA(nodoB.getValue());

                                                //android.util.Log.v("Distancia","=> " + distancias[0]);

                                                if (distancias < maximaDistanciaValida) {
                                                    if (!aContieneB) {
                                                        nodoA.getValue().addProximo(nodoB.getValue(), (int) Math.round(distancias));
                                                    }
                                                    if (!bContieneA) {
                                                        nodoB.getValue().addProximo(nodoA.getValue(), (int) Math.round(distancias));
                                                    }
                                                }
                                            }
                                        }

                                        //android.util.Log.v("Distancia","=> Ya existe una union fuerte");
                                        //android.util.Log.v("Distancia","A = " +nodoA.getValue().toString());
                                        //android.util.Log.v("Distancia","B = "+nodoB.getValue().toString());

                                    }
                                }
                            }
                        }

                        //Como la base de datos usa como ID un valor unico se puede sobreescribir cada dato con los mejores valores disponibles
                        //sin necesidad de comprobar si previamente existian o no
                        db.addVertices(new ArrayList<>(nudos.values()));

                    } else {
                        longitude = longitude + paso;
                    }
                }
            }

            return true;

        } catch (Throwable ignored) {
            ignored.printStackTrace();
        }

        return false;

    }

    public interface InterfazProgreso {
        void onCompletado(double latitud, double longitud);

        void onRealizandoAccion(String file);

        void onPorcentajeActualizado(int porcentaje);

        void onError();
    }


}
