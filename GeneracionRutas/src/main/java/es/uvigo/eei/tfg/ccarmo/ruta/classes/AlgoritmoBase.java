/*
 * Trabajo de fin de grado: "Definición e implementación de herramienta para la navegación de peatones en entornos urbanos para el sistema operativo móvil Android"
 * Creado por: Cristian Do Carmo Rodríguez
 * Tutora: Lucía Díaz Vilariño
 * Cotutor: Jesús Balado Frías
 * Copyright (c) 2024. Todos los derechos reservados.
 */

package es.uvigo.eei.tfg.ccarmo.ruta.classes;


import android.content.Context;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Map;

import es.uvigo.eei.tfg.ccarmo.clases.classes.LatLon;
import es.uvigo.eei.tfg.ccarmo.clases.classes.Vertice;
import es.uvigo.eei.tfg.ccarmo.datos.almacenamiento.Configuracion;
import es.uvigo.eei.tfg.ccarmo.datos.almacenamiento.Margen;
import es.uvigo.eei.tfg.ccarmo.datos.almacenamiento.SQLite;

public abstract class AlgoritmoBase {

    private final SQLite db;
    private final Configuracion configuracion;
    private final Context context;
    private Map<String, Vertice> quick;
    private Ruta ruta;


    public AlgoritmoBase(@NonNull String nombreDB, @NonNull Configuracion configuracion, Context context) {

        this.configuracion = configuracion;
        this.context = context;
        this.db = SQLite.getBaseDeDatos(nombreDB, context);

    }

    @NonNull
    public Map<String, Vertice> getVertices() {
        return quick;
    }

    public Ruta getRuta() {
        return ruta;
    }

    public boolean isRuta() {
        return ruta != null;
    }

    public void setRuta(@Nullable Ruta ruta) {
        this.ruta = ruta;
    }


    private void cargarBaseDeDatos(@NonNull LatLon inicio, @NonNull LatLon fin) {

        //Se cargan los valores desde la base de datos delimitando el posible recorrido
        double latitude1;
        double longitude1;
        double longitude2;
        double latitude2;

        if (inicio.getLatitud() > fin.getLatitud()) {
            latitude2 = inicio.getLatitud();
            latitude1 = fin.getLatitud();
        } else {
            latitude1 = inicio.getLatitud();
            latitude2 = fin.getLatitud();
        }

        if (inicio.getLongitud() > fin.getLongitud()) {
            longitude2 = inicio.getLongitud();
            longitude1 = fin.getLongitud();
        } else {
            longitude1 = inicio.getLongitud();
            longitude2 = fin.getLongitud();
        }

        quick = db.getVerticesMap(Margen.MARGEN_RUTA, latitude1, latitude2, longitude1, longitude2, configuracion);

    }

    public AlgoritmoBase buscarRuta(@NonNull LatLon inicio, @NonNull LatLon fin) {

        //Borro cualquier ruta guardada
        this.ruta = null;

        //Se cargan los vertices necesarios
        cargarBaseDeDatos(inicio, fin);

        //Se precargan los nodos del algoritmo
        precargarNodosAlgoritmo(inicio, fin);

        //Se buscan los nodos mas cercanos al inicio
        ArrayList<NodoOrdenacion> ordenacion = new ArrayList<>();

        //Genero una lista de los nodos junto con sus uniones precargadas
        for (Vertice n : getVertices().values()) {

            //Se comprueba que el nodo esta conectado a algo antes de considerarlo
            if (n.getAristas().size() > 1) {
                ordenacion.add(new NodoOrdenacion(n.getID(), n.getDistanciaFisica2D(inicio)));
            }
        }

        Collections.sort(ordenacion);

        for (NodoOrdenacion prioritario : ordenacion) {

            //Se busca una ruta desde el nodo mas cercano al origen hasta encontrar una
            this.ruta = generarRutaDesde(prioritario.id, fin);

            //Se termina si se encuentra una ruta o si se encuentra demasiado lejos del origen
            if (ruta != null || prioritario.distanciaEstimadaAOrigen > 400) {
                return this;
            }

            //Se preparan los nodos para la nueva iteracion
            reiniciarNodosAlgoritmo();

        }

        return this;

    }

    public Context getContext() {
        return context;
    }

    /**
     * Genera las rutas usando como id un parametro valido
     *
     * @param id
     * @return
     */
    @Nullable
    protected abstract Ruta generarRutaDesde(@NonNull String id, @NonNull LatLon fin);

    protected abstract void precargarNodosAlgoritmo(@NonNull LatLon inicio, @NonNull LatLon fin);

    //Reinicia los nodos para volver a aplicar el algoritmo
    protected abstract void reiniciarNodosAlgoritmo();

    protected abstract void reciclarNodosAlgoritmo();

    public AlgoritmoBase terminar() {

        db.close();

        reciclarNodosAlgoritmo();

        return this;
    }

    protected Ruta almacenarRuta(ArrayList<Vertice> vertices, ArrayList<PuntoCalor> puntosCalor) {

        //Se guarda el orden de la ruta
        for (int i2 = 0; i2 < vertices.size(); i2++) {
            vertices.get(i2).setPosicion(i2);
        }

        //Se guarda el itinerario en la base de datos
        String nombreBaseDeDatos = SQLite.CACHE_RUTA;

        SQLite db = SQLite.getBaseDeDatos(nombreBaseDeDatos, getContext());
        db.borrar();
        db.addVertices(vertices);
        db.close();

        //Se calcula la distancia total
        double distanciaRuta = 0;
        if (vertices.size() > 1) {
            for (int x = 1; x < vertices.size(); x++) {
                distanciaRuta = distanciaRuta + LatLon.getDistanciaFisica(vertices.get(x - 1), vertices.get(x));
            }
        }

        return new Ruta(nombreBaseDeDatos, distanciaRuta, puntosCalor);
    }

}
