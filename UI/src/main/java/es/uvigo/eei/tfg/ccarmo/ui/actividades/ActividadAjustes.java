/*
 * Trabajo de fin de grado: "Definición e implementación de herramienta para la navegación de peatones en entornos urbanos para el sistema operativo móvil Android"
 * Creado por: Cristian Do Carmo Rodríguez
 * Tutora: Lucía Díaz Vilariño
 * Cotutor: Jesús Balado Frías
 * Copyright (c) 2024. Todos los derechos reservados.
 */

package es.uvigo.eei.tfg.ccarmo.ui.actividades;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.RadioGroup;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;

import com.google.android.gms.oss.licenses.OssLicensesMenuActivity;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;

import es.uvigo.eei.tfg.ccarmo.R;
import es.uvigo.eei.tfg.ccarmo.datos.almacenamiento.SQLite;
import es.uvigo.eei.tfg.ccarmo.ruta.Algoritmo;
import es.uvigo.eei.tfg.ccarmo.ui.actividades.utils.ActividadBase;
import es.uvigo.eei.tfg.ccarmo.ui.actividades.visualizacion.ActividadEliminarRegion;

public class ActividadAjustes extends ActividadBase {

    public static void abrir(@NonNull Context context) {

        Intent intent = new Intent(context, ActividadAjustes.class);
        context.startActivity(intent);

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.actividad_ajustes);

        getOnBackPressedDispatcher().addCallback(this, new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                if (!isFinishing()) {

                    //Lanzo la actividad de menu
                    ActividadMenu.abrir(ActividadAjustes.this);

                    //Termino esta actividad
                    finish();
                }
            }
        });

        RadioGroup seleccionAlgoritmo = findViewById(R.id.seleccion_algoritmo);

        if (Algoritmo.isAEstrella(ActividadAjustes.this)) {
            seleccionAlgoritmo.check(R.id.algoritmo_aestrella);
        }

        if (Algoritmo.isDijkstra(ActividadAjustes.this)) {
            seleccionAlgoritmo.check(R.id.algoritmo_dijkstra);
        }

        seleccionAlgoritmo.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                if (checkedId == R.id.algoritmo_aestrella) {
                    Algoritmo.setAEstrella(ActividadAjustes.this);
                }
                if (checkedId == R.id.algoritmo_dijkstra) {
                    Algoritmo.setDijkstra(ActividadAjustes.this);
                }
            }
        });

        findViewById(R.id.boton_licencias).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(ActividadAjustes.this, OssLicensesMenuActivity.class));
            }
        });

        findViewById(R.id.boton_borrar_area).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ActividadEliminarRegion.abrir(SQLite.DEFAULT_NAME, ActividadAjustes.this);
            }
        });

        findViewById(R.id.boton_borrar_base_de_datos).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //Se abre el dialogo de error
                new MaterialAlertDialogBuilder(ActividadAjustes.this)
                        .setTitle(R.string.borrar_base_de_datos_titulo)
                        .setMessage(R.string.borrar_base_de_datos_confirmacion)
                        .setCancelable(true)
                        .setIcon(R.drawable.ic_borrar)
                        .setPositiveButton(R.string.aceptar, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();


                                try {
                                    SQLite db = SQLite.getBaseDeDatos(SQLite.DEFAULT_NAME, getApplicationContext());
                                    db.borrar();
                                    db.close();
                                } catch (Throwable ignored) {
                                }

                                //Se abren los ajustes
                                ActividadMenu.abrir(ActividadAjustes.this);

                                //Se finaliza esta actividad
                                finish();
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