/*
 * Trabajo de fin de grado: "Definición e implementación de herramienta para la navegación de peatones en entornos urbanos para el sistema operativo móvil Android"
 * Creado por: Cristian Do Carmo Rodríguez
 * Tutora: Lucía Díaz Vilariño
 * Cotutor: Jesús Balado Frías
 * Copyright (c) 2024. Todos los derechos reservados.
 */

package es.uvigo.eei.tfg.ccarmo.ui.actividades.ejemplo;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.RadioGroup;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;

import com.google.android.material.dialog.MaterialAlertDialogBuilder;

import java.util.ArrayList;

import es.uvigo.eei.tfg.ccarmo.R;
import es.uvigo.eei.tfg.ccarmo.clases.classes.TipoVia;
import es.uvigo.eei.tfg.ccarmo.importacion.classes.Fuente;
import es.uvigo.eei.tfg.ccarmo.ui.actividades.ActividadMenu;
import es.uvigo.eei.tfg.ccarmo.ui.actividades.importacion.ActividadImportacionResolucion;
import es.uvigo.eei.tfg.ccarmo.ui.actividades.utils.ActividadBase;

public class ActividadEjemplos extends ActividadBase {

    public static void abrir(@NonNull Context context) {

        Intent intent = new Intent(context, ActividadEjemplos.class);
        context.startActivity(intent);

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.actividad_ejemplos);


        getOnBackPressedDispatcher().addCallback(this, new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                if (!isFinishing()) {

                    //Lanzo la actividad de menu
                    ActividadMenu.abrir(ActividadEjemplos.this);

                    //Termino esta actividad
                    finish();
                }
            }
        });

        findViewById(R.id.boton_importar).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //Se abre el dialogo de error
                new MaterialAlertDialogBuilder(ActividadEjemplos.this)
                        .setTitle(R.string.importacion_mapas_ejemplo_titulo)
                        .setMessage(R.string.importacion_mapas_ejemplo_descripcion)
                        .setCancelable(true)
                        .setIcon(R.drawable.ic_alerta)
                        .setPositiveButton(R.string.importar, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();

                                ArrayList<Fuente> fuentes = new ArrayList<>();
                                TipoVia tipoVia = TipoVia.NONE;


                                RadioGroup radioGroup = findViewById(R.id.seleccion);

                                switch (radioGroup.getCheckedRadioButtonId()) {
                                    case R.id.opcion_1:
                                        fuentes.add(new Fuente("osm/navia_norte.osm"));
                                        tipoVia = TipoVia.NONE;
                                        break;
                                    case R.id.opcion_2:
                                        fuentes.add(new Fuente("osm/cuvi.osm"));
                                        tipoVia = TipoVia.NONE;
                                        break;
                                    case R.id.opcion_3:
                                        fuentes.add(new Fuente("gpx/ejemplo.gpx"));
                                        tipoVia = TipoVia.ACERA;
                                        break;
                                    case R.id.opcion_4:
                                        fuentes.add(new Fuente("tcx/ejemplo.tcx"));
                                        tipoVia = TipoVia.ACERA;
                                        break;
                                    case R.id.opcion_5:
                                        fuentes.add(new Fuente("geojson/ejemplo.geojson"));
                                        tipoVia = TipoVia.ACERA;
                                        break;
                                }

                                if (!fuentes.isEmpty()) {
                                    ActividadImportacionResolucion.abrir(fuentes, tipoVia, ActividadEjemplos.this);
                                    finish();
                                }

                            }
                        })
                        .setNegativeButton(R.string.cancelar, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        })
                        .create().show();


            }
        });

    }

}