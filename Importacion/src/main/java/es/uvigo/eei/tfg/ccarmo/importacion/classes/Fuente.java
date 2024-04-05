/*
 * Trabajo de fin de grado: "Definición e implementación de herramienta para la navegación de peatones en entornos urbanos para el sistema operativo móvil Android"
 * Creado por: Cristian Do Carmo Rodríguez
 * Tutora: Lucía Díaz Vilariño
 * Cotutor: Jesús Balado Frías
 * Copyright (c) 2024. Todos los derechos reservados.
 */

package es.uvigo.eei.tfg.ccarmo.importacion.classes;


import android.content.Context;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.io.Serializable;

import es.uvigo.eei.tfg.ccarmo.clases.classes.Vertice;
import es.uvigo.eei.tfg.ccarmo.datos.almacenamiento.SQLite;
import es.uvigo.eei.tfg.ccarmo.importacion.utils.Utils;

public class Fuente implements Serializable {

    private final TIPO tipo;
    private final String descarga;
    private final String info;
    private final int unible;
    public Fuente(@NonNull String descarga) {
        this(descarga, null, Vertice.Unible.UNIBLE_CON_TODO);
    }


    public Fuente(@NonNull String descarga, @Nullable String info) {
        this(descarga, info, Vertice.Unible.UNIBLE_CON_TODO);
    }

    public Fuente(@NonNull String descarga, @Nullable String info, int unible) {

        this.info = info == null ? descarga : info;
        this.unible = unible;

        if (descarga.startsWith("https") || descarga.startsWith("http")) {
            tipo = TIPO.URL;
        } else {
            if (descarga.startsWith("uri://")) {
                tipo = TIPO.URI;
            } else {
                if (descarga.startsWith("sql://")) {
                    tipo = TIPO.SQL_CACHE;
                } else {
                    tipo = TIPO.ASSET;
                }
            }
        }
        this.descarga = descarga;
    }

    public static Fuente getFuenteSQL(@NonNull String nombreDB) {
        return new Fuente("sql://" + nombreDB);
    }

    public static Fuente getFuenteURI(@NonNull String nombreDB) {
        return new Fuente("uri://" + nombreDB);
    }

    @NonNull
    public static TipoDato getTipoDato(@NonNull String nombreDeArchivo) {

        //Para evitar que las mayusculas puedan afectar a la deteccion del tipo de archivo
        nombreDeArchivo = nombreDeArchivo.toLowerCase();

        if (nombreDeArchivo.endsWith(".osm")) {
            return TipoDato.OSM;
        }
        if (nombreDeArchivo.endsWith(".geojson")) {
            return TipoDato.GEOJSON;
        }
        if (nombreDeArchivo.endsWith(".tcx")) {
            return TipoDato.TCX;
        }
        if (nombreDeArchivo.endsWith(".gpx")) {
            return TipoDato.GPX;
        }

        //No se ha detectado de manera directa
        //Pero puede resultar que el nombre tenga el formato "ruta.geojson (1)"
        //Es comun cuando existe un archivo con el mismo nombre y otro es descargado

        //La extension es el ultimo trozo del nombre
        String extension = nombreDeArchivo.split("[.]")[nombreDeArchivo.split("[.]").length - 1];
        if (extension.startsWith("osm")) {
            return TipoDato.OSM;
        }
        if (extension.startsWith("geojson")) {
            return TipoDato.GEOJSON;
        }
        if (extension.startsWith("tcx")) {
            return TipoDato.TCX;
        }
        if (extension.startsWith("gpx")) {
            return TipoDato.GPX;
        }

        return TipoDato.DESCONOCIDO;
    }

    public boolean isSQL() {
        return tipo == TIPO.SQL_CACHE;
    }

    public String getFile(Context context) {

        switch (tipo) {
            case URL:
                return Utils.getStringFromInternet(descarga, context);
            case ASSET:
                return Utils.getStringFromAsset(descarga, context);
            case URI:
                return Utils.getStringFromCache(descarga.substring("uri://".length()), context);

        }

        return "";
    }

    @Nullable
    public SQLite getSQLite(@NonNull Context context) {
        if (tipo == TIPO.SQL_CACHE) {
            return SQLite.getBaseDeDatos(descarga.substring("sql://".length()), context);
        }
        return null;
    }

    public String getInfo() {
        return info;
    }


    public int getUnible() {
        return unible;
    }

    public TipoDato getTipoDato() {

        if (tipo == TIPO.SQL_CACHE) {
            return TipoDato.SQL;
        }

        if (descarga.startsWith("uri://")) {
            return getTipoDato(info);
        } else {
            return getTipoDato(descarga);
        }

    }

    private enum TIPO {
        ASSET,
        URL,
        URI,

        SQL_CACHE

    }

}
