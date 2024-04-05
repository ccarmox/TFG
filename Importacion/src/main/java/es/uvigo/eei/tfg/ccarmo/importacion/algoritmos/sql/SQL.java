/*
 * Trabajo de fin de grado: "Definición e implementación de herramienta para la navegación de peatones en entornos urbanos para el sistema operativo móvil Android"
 * Creado por: Cristian Do Carmo Rodríguez
 * Tutora: Lucía Díaz Vilariño
 * Cotutor: Jesús Balado Frías
 * Copyright (c) 2024. Todos los derechos reservados.
 */

package es.uvigo.eei.tfg.ccarmo.importacion.algoritmos.sql;

import android.database.Cursor;

import es.uvigo.eei.tfg.ccarmo.clases.classes.TipoVia;
import es.uvigo.eei.tfg.ccarmo.clases.classes.Vertice;
import es.uvigo.eei.tfg.ccarmo.datos.almacenamiento.SQLite;
import es.uvigo.eei.tfg.ccarmo.importacion.listeners.InterfazVertices;

public class SQL {


    public static boolean parse(SQLite sql, int resolucion, TipoVia tipoVia, int unible, InterfazVertices onDataParsed) {

        try {

            //Se obtiene un cursor para todos los datos de la base de datos de origen
            Cursor cursor = sql.getCursor();

            //Se comprueba que exista contenido
            if (cursor != null) {

                //Se itera mientras siga existiendo contenido disponible
                if (cursor.getCount() > 0) {
                    do {
                        //Se obtiene el vertice actual
                        Vertice n = SQLite.getVertice(cursor);

                        //Se contextualiza con la informacion nueva y se crea como nuevo objeto
                        Vertice b = new Vertice(n.getInformacion(), resolucion, n.getLatitud(), n.getLongitud(), n.getAltitud(), tipoVia, unible);

                        //Se guarda el nuevo vertice de manera directa
                        onDataParsed.onNuevoVertice(b);
                    } while (cursor.moveToNext());
                }

                try {
                    //Se cierra el cursor de origen
                    cursor.close();
                } catch (Throwable ignored) {
                }

                return true;


            }

            try {
                //Se cierra la base de datos de origen
                sql.close();
            } catch (Throwable ignored) {
            }

        } catch (Exception ignored) {
            ignored.printStackTrace();
        }

        return false;
    }

}
