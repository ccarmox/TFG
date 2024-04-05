/*
 * Trabajo de fin de grado: "Definición e implementación de herramienta para la navegación de peatones en entornos urbanos para el sistema operativo móvil Android"
 * Creado por: Cristian Do Carmo Rodríguez
 * Tutora: Lucía Díaz Vilariño
 * Cotutor: Jesús Balado Frías
 * Copyright (c) 2024. Todos los derechos reservados.
 */

package es.uvigo.eei.tfg.ccarmo.datos.almacenamiento;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteCantOpenDatabaseException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.GeometryFactory;
import org.locationtech.jts.geom.Point;
import org.locationtech.jts.geom.Polygon;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import es.uvigo.eei.tfg.ccarmo.clases.classes.Vertice;


public class SQLite extends SQLiteOpenHelper {


    public static final String DEFAULT_NAME = "BaseDeDatos2";
    public static final String CACHE_NAME = "BaseDeDatosTemporal";
    public static final String CACHE_RUTA = "BaseDeDatosRuta";


    private static final int FALSE = 0;
    private static final int TRUE = 1;

    private static final int DATABASE_VERSION = 1;


    private static final String TABLE_NAME = "VERTICES";


    private static final String KEY_ID = "ID";
    private static final String KEY_LATITUD = "LATITUD";
    private static final String KEY_LONGITUD = "LONGITUD";
    private static final String KEY_ALTITUD = "ALTITUD";
    private static final String KEY_INFO = "INFORMACION";
    private static final String TIPO_VIA = "TIPO_VIA";
    private static final String KEY_ARISTAS = "ARISTAS";
    private static final String KEY_UNIBLE = "UNIBLE";
    private static final String KEY_PENALIZACION = "PENALIZACION";
    private static final String KEY_POSICION_RUTA = "POSICION";
    private static final String KEY_RESOLUCION = "RESOLUCION";

    private final String db;

    public static SQLite getBaseDeDatos(@NonNull String paquete, @NonNull Context context) {
        android.util.Log.v("Base de datos", paquete);
        return new SQLite(paquete, context);
    }


    protected SQLite(@NonNull String tabla, @NonNull Context context) {
        super(context, tabla, null, DATABASE_VERSION);
        this.db = tabla;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String CREAR_TABLA = "CREATE TABLE " + TABLE_NAME + "("
                + KEY_ID + " TEXT PRIMARY KEY," + KEY_INFO + " TEXT," + KEY_LATITUD + " REAL,"
                + KEY_LONGITUD + " REAL," + KEY_ALTITUD + " REAL," + TIPO_VIA + " INTEGER," + KEY_ARISTAS + " TEXT," + KEY_UNIBLE + " INTEGER," + KEY_RESOLUCION + " INTEGER," + KEY_PENALIZACION + " INTEGER, " + KEY_POSICION_RUTA + " INTEGER" + ")";
        db.execSQL(CREAR_TABLA);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);

        //Crear tabla de nuevo
        onCreate(db);
    }


    // Adding new contact
    public void addVertices(Vertice... vertices) {
        try {
            SQLiteDatabase db = this.getWritableDatabase();

            for (Vertice vertice : vertices) {

                //Insertar vertice
                db.insert(TABLE_NAME, null, getContentValues(vertice));

            }

            db.close(); // Closing database connection

        } catch (Throwable e) {
            e.printStackTrace();
        }
    }

    public void addVertices(@NonNull ArrayList<Vertice> vertices) {

        try {

            SQLiteDatabase db = this.getWritableDatabase();

            for (Vertice vertice : vertices) {

                // Insertar o actualizar vertice
                db.insertWithOnConflict(TABLE_NAME, null, getContentValues(vertice), SQLiteDatabase.CONFLICT_REPLACE);

            }

            db.close(); // Cerrar la base de datos


        } catch (Throwable e) {
            e.printStackTrace();
        }
    }

    SQLiteDatabase escribir;

    public void iniciarEscritura(){
        escribir = this.getWritableDatabase();
    }

    public void addVertice(Vertice vertice){
        escribir.insertWithOnConflict(TABLE_NAME, null, getContentValues(vertice), SQLiteDatabase.CONFLICT_REPLACE);
    }

    public void terminarEscritura(){
        escribir.close();
    }

    @NonNull
    private static ContentValues getContentValues(Vertice vertice) {
        ContentValues values = new ContentValues();

        values.put(KEY_ID, vertice.getID());
        values.put(KEY_INFO, vertice.getInformacion());
        values.put(KEY_LATITUD, vertice.getLatitud());
        values.put(KEY_LONGITUD, vertice.getLongitud());
        values.put(KEY_ALTITUD, vertice.getAltitud());
        values.put(TIPO_VIA, vertice.getValorTipoVia());
        values.put(KEY_ARISTAS, vertice.getProximo());
        values.put(KEY_UNIBLE, vertice.getUnible());
        values.put(KEY_RESOLUCION, vertice.getResolucion());
        values.put(KEY_PENALIZACION, vertice.getPenalizacion());
        values.put(KEY_POSICION_RUTA, vertice.getPosicion());
        return values;
    }

    public static Vertice getVertice(Cursor cursor) {

        String nid = ((cursor.getString(0)));
        String info = ((cursor.getString(1)));
        double latitude = (cursor.getDouble(2));
        double longitude = (cursor.getDouble(3));
        double altitude = (cursor.getDouble(4));
        int way = (cursor.getInt(5));
        String near = (cursor.getString(6));
        int unible = (cursor.getInt(7));
        int resolucion = cursor.getInt(8);
        int penalizacion = (cursor.getInt(9));
        int posicion = cursor.getInt(10);

        return new Vertice(info, latitude, longitude, altitude, way, unible, near, resolucion, penalizacion, posicion);

    }

    public Cursor getCursorRuta() {

        String order = " ORDER BY " + KEY_POSICION_RUTA + " ASC";

        String selectQuery = "SELECT  * FROM " + TABLE_NAME + order;

        try {

            SQLiteDatabase db = this.getReadableDatabase();
            return db.rawQuery(selectQuery, null);

        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }

    public List<Vertice> getVerticeListArray(Margen margen, double latitude1, double latitude2, double longitude1, double longitude2) {
        return new ArrayList<>(getVerticesMap(margen, latitude1, latitude2, longitude1, longitude2, null).values());
    }

    public Map<String, Vertice> getVerticesMap(Margen margen, double latitude1, double latitude2, double longitude1, double longitude2) {
        return getVerticesMap(margen, latitude1, latitude2, longitude1, longitude2, null);
    }

    public Map<String, Vertice> getVerticesMap(Margen margen, double latitude1, double latitude2, double longitude1, double longitude2, @Nullable Configuracion configuracion) {


        if (latitude1 > latitude2) {
            double latitude3 = latitude2;
            latitude2 = latitude1;
            latitude1 = latitude3;
        }

        if (longitude1 > longitude2) {
            double longitude3 = longitude2;
            longitude2 = longitude1;
            longitude1 = longitude3;
        }

        double latDiff = 0;
        double lonDiff = 0;

        if (margen == Margen.MARGEN_RUTA) {
            latDiff = Math.abs(latitude2 - latitude1);
            lonDiff = Math.abs(longitude2 - longitude1);

            latDiff = Math.max(latDiff, lonDiff);
            lonDiff = latDiff;
        }

        if (margen == Margen.MARGEN_VISTA) {
            latDiff = 0.00001D;
            lonDiff = 0.00001D;
        }


        double latitude1b = latitude1 - latDiff;
        double latitude2b = latitude2 + latDiff;

        double longitude1b = longitude1 - lonDiff;
        double longitude2b = longitude2 + lonDiff;


        String filtro = "";

        if (configuracion != null && configuracion.isFiltrarTipoVia()) {

            if (!configuracion.getViasValidas().isEmpty()) {

                for (int i = 0; i < configuracion.getViasValidas().size(); i++) {
                    if (i == 0) {
                        filtro = " AND " + TIPO_VIA + " = '" + configuracion.getViasValidas().get(i).getValor() + "'";
                    } else {
                        filtro = filtro + " OR " + TIPO_VIA + " = '" + configuracion.getViasValidas().get(i).getValor() + "'";
                    }
                }

            } else {

                return new HashMap<>();

            }
        }

        DecimalFormat df2 = new DecimalFormat("#.######");


        String selectQuery = "SELECT  * FROM " + TABLE_NAME + " WHERE " + KEY_LATITUD + " BETWEEN " + df2.format(latitude1b).replace(",", ".") + " AND " + df2.format(latitude2b).replace(",", ".") + " AND " + KEY_LONGITUD + " BETWEEN " + df2.format(longitude1b).replace(",", ".") + " AND " + df2.format(longitude2b).replace(",", ".") + filtro;

        //android.util.Log.v("Query "+margin,"=>"+selectQuery);

        try {

            SQLiteDatabase db = this.getReadableDatabase();

            Cursor c = db.rawQuery(selectQuery, null);

            c.moveToFirst();

            Map<String, Vertice> quick = new HashMap<>();

            if(c.getCount()>0) {
                do {
                    Vertice n = SQLite.getVertice(c);
                    quick.put(n.getID(), n);
                } while (c.moveToNext());
            }

            try {
                c.close();
                db.close();
            } catch (Throwable ignored) {
            }

            //android.util.Log.v("Modelo","Size="+c.getCount()+" vs "+quick.size());

            return quick;

        } catch (Exception e) {
            e.printStackTrace();
        }

        return new HashMap<>();
    }

    public Map<String, Vertice> getVerticesMap(@Nullable String texto) {
        return getVerticesMap(texto, 0);
    }
        public Map<String, Vertice> getVerticesMap(@Nullable String texto, int limite) {


        String selectQuery;

        if (texto != null && !texto.isEmpty()) {
            selectQuery = "SELECT * FROM " + TABLE_NAME + " WHERE " + KEY_INFO + " LIKE '%" + texto + "%' GROUP BY "+KEY_INFO+" ORDER BY "+KEY_INFO+" COLLATE NOCASE";
        } else {
            selectQuery = "SELECT * FROM " + TABLE_NAME;
        }

        if(limite > 0){
            selectQuery = selectQuery+" LIMIT "+limite;
        }

        try {

            SQLiteDatabase db = this.getReadableDatabase();


            Cursor c = db.rawQuery(selectQuery, null);


            c.moveToFirst();

            Map<String, Vertice> quick = new HashMap<>();


            if(c.getCount()>0) {

                do {
                    Vertice n = SQLite.getVertice(c);
                    quick.put(n.getID(), n);
                } while (c.moveToNext());
            }

            try {
                c.close();
                db.close();
            } catch (Throwable ignored) {
            }

            return quick;

        } catch (Exception e) {
            e.printStackTrace();
        }

        return new HashMap<>();
    }

    @Nullable
    public Cursor getCursor() {


        String selectQuery = "SELECT  * FROM " + TABLE_NAME;

        try {

            SQLiteDatabase db = this.getReadableDatabase();

            Cursor c = db.rawQuery(selectQuery, null);

            c.moveToFirst();

            return c;

        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }

    public void borrar() {
        try {
            SQLiteDatabase db = this.getWritableDatabase();
            db.delete(TABLE_NAME, null, null);
            db.close();
        } catch (SQLiteCantOpenDatabaseException e) {
            e.printStackTrace();
        }
    }

    public void borrar(@NonNull String id) {
        try {
            SQLiteDatabase db = this.getWritableDatabase();
            db.delete(TABLE_NAME, KEY_ID + " = ?", new String[]{id});
            db.close();
        } catch (SQLiteCantOpenDatabaseException e) {
            e.printStackTrace();
        }
    }

    public void borrarArea(@NonNull List<Vertice> vertices){

        try{

            if(vertices.size()<3){
                return;
            }

            //Se buscan las coordenadas maximas y minimas
            double minLatitude = vertices.get(0).getLatitud();
            double maxLatitude = vertices.get(0).getLatitud();

            double minLongitude = vertices.get(0).getLongitud();
            double maxLongitude = vertices.get(0).getLongitud();


            for (Vertice p : vertices) {
                if (p.getLatitud() > maxLatitude) {
                    maxLatitude = p.getLatitud();
                }
                if (p.getLatitud() < minLatitude) {
                    minLatitude = p.getLatitud();
                }
                if (p.getLongitud() > maxLongitude) {
                    maxLongitude = p.getLongitud();
                }
                if (p.getLongitud() < minLongitude) {
                    minLongitude = p.getLongitud();
                }
            }

            vertices.add(vertices.get(0));

            //Se transforman los vertices en coordenadas de la biblioteca JTS
            Coordinate[] puntosPoligono = new Coordinate[vertices.size()];
            for (int i = 0; i < vertices.size(); i++) {
                puntosPoligono[i] = new Coordinate(vertices.get(i).getLatitud(), vertices.get(i).getLongitud());
            }

            //Se crea el poligono con las coordenadas
            GeometryFactory geometryFactory = new GeometryFactory();
            Polygon jtsPolygon = geometryFactory.createPolygon(puntosPoligono);

            List<Vertice> nudos = getVerticeListArray(Margen.SIN_MARGEN, minLatitude, maxLatitude, minLongitude, maxLongitude);

            for (Vertice nudo : nudos) {
                //Se crea un punto con las coordenadas de la iteracion
                Point jtsPoint = geometryFactory.createPoint(new Coordinate(nudo.getLatitud(), nudo.getLongitud()));

                //Se comprueba que el punto esta dentro del poligono
                if (jtsPolygon.contains(jtsPoint)) {
                    borrar(nudo.getID());
                }
            }

            close();

        }catch (Throwable e){
            e.printStackTrace();
        }
    }


}
