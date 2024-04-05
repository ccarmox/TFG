/*
 * Trabajo de fin de grado: "Definición e implementación de herramienta para la navegación de peatones en entornos urbanos para el sistema operativo móvil Android"
 * Creado por: Cristian Do Carmo Rodríguez
 * Tutora: Lucía Díaz Vilariño
 * Cotutor: Jesús Balado Frías
 * Copyright (c) 2024. Todos los derechos reservados.
 */

package es.uvigo.eei.tfg.ccarmo.ui.actividades;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;

import androidx.annotation.NonNull;

import es.uvigo.eei.tfg.ccarmo.R;
import es.uvigo.eei.tfg.ccarmo.datos.almacenamiento.SQLite;
import es.uvigo.eei.tfg.ccarmo.ui.actividades.ejemplo.ActividadEjemplos;
import es.uvigo.eei.tfg.ccarmo.ui.actividades.generacion.ActividadGeneracion;
import es.uvigo.eei.tfg.ccarmo.ui.actividades.importacion.ActividadMenuImportacion;
import es.uvigo.eei.tfg.ccarmo.ui.actividades.utils.ActividadBase;
import es.uvigo.eei.tfg.ccarmo.ui.actividades.visualizacion.ActividadMapa;

public class ActividadMenu extends ActividadBase {

    public static void abrir(@NonNull Context context) {

        Intent intent = new Intent(context, ActividadMenu.class);
        context.startActivity(intent);

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.actividad_menu);

        findViewById(R.id.boton_escuela).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String url = "https://eei.uvigo.es";
                Intent i = new Intent(Intent.ACTION_VIEW);
                i.setData(Uri.parse(url));
                startActivity(i);
            }
        });

        findViewById(R.id.boton_menu_mapa).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //Se abre el mapa
                ActividadMapa.abrir(SQLite.DEFAULT_NAME, ActividadMenu.this);

                //Se finaliza esta actividad
                finish();

            }
        });

        findViewById(R.id.boton_menu_generacion).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //Se abre el generador
                ActividadGeneracion.abrir(ActividadMenu.this);

                //Se finaliza esta actividad
                finish();

            }
        });

        findViewById(R.id.boton_menu_importar).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //Se abre el nuevo menu
                ActividadMenuImportacion.abrir(ActividadMenu.this);

                //Se finaliza esta actividad
                finish();

            }
        });

        findViewById(R.id.boton_menu_ajustes).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //Se abren los ajustes
                ActividadAjustes.abrir(ActividadMenu.this);

                //Se finaliza esta actividad
                finish();

            }
        });

        findViewById(R.id.boton_menu_debug).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //Se abren los ajustes de importacion
                ActividadEjemplos.abrir(ActividadMenu.this);

                //Se finaliza esta actividad
                finish();

            }
        });

    }

}