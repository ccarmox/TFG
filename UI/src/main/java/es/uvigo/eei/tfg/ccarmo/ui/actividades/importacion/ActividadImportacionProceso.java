/*
 * Trabajo de fin de grado: "Definición e implementación de herramienta para la navegación de peatones en entornos urbanos para el sistema operativo móvil Android"
 * Creado por: Cristian Do Carmo Rodríguez
 * Tutora: Lucía Díaz Vilariño
 * Cotutor: Jesús Balado Frías
 * Copyright (c) 2024. Todos los derechos reservados.
 */

package es.uvigo.eei.tfg.ccarmo.ui.actividades.importacion;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.widget.TextView;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;

import com.google.android.material.progressindicator.LinearProgressIndicator;

import java.util.ArrayList;

import es.uvigo.eei.tfg.ccarmo.R;
import es.uvigo.eei.tfg.ccarmo.clases.classes.TipoVia;
import es.uvigo.eei.tfg.ccarmo.importacion.ImportadorMapas;
import es.uvigo.eei.tfg.ccarmo.importacion.classes.Fuente;
import es.uvigo.eei.tfg.ccarmo.ui.actividades.ActividadMenu;
import es.uvigo.eei.tfg.ccarmo.ui.actividades.ActividadSplash;
import es.uvigo.eei.tfg.ccarmo.ui.actividades.visualizacion.ActividadMapa;

public class ActividadImportacionProceso extends ActividadImportacionBase {

    private final static String EXTRA_FUENTES = "Fuentes";
    private final static String EXTRA_RESOLUCION = "Resolucion";
    private final static String EXTRA_DB = "DB";
    private final static String EXTRA_TIPO_VIA = "TipoVia";

    public static void abrir(@NonNull ArrayList<Fuente> fuentes, @NonNull TipoVia tipoVia, @NonNull String db, int resolucion, @NonNull Context context) {

        Intent intent = new Intent(context, ActividadImportacionProceso.class);
        intent.putExtra(EXTRA_RESOLUCION, resolucion);
        intent.putExtra(EXTRA_FUENTES, fuentes);
        intent.putExtra(EXTRA_DB, db);
        intent.putExtra(EXTRA_TIPO_VIA, tipoVia);
        context.startActivity(intent);

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.actividad_importacion_proceso);

        TextView tPorcentaje = findViewById(R.id.porcentaje);
        TextView tFileName = findViewById(R.id.file_name);
        LinearProgressIndicator progressIndicator = findViewById(R.id.progressBar2);

        ArrayList<Fuente> fuentes = (ArrayList<Fuente>) getExtras2().getSerializable(EXTRA_FUENTES);
        int resolucion = getExtras2().getInt(EXTRA_RESOLUCION);
        String nombreDB = getExtras2().getString(EXTRA_DB);
        TipoVia tipoVia = (TipoVia) getExtras2().getSerializable(EXTRA_TIPO_VIA);

        getOnBackPressedDispatcher().addCallback(this, new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                if (!isFinishing()) {

                    //Lanzo la actividad de menu
                    ActividadMenu.abrir(ActividadImportacionProceso.this);

                    //Termino esta actividad
                    finalizarYRetroceder();
                }
            }
        });

        new ImportadorMapas(fuentes, tipoVia, nombreDB, resolucion, this, new ImportadorMapas.InterfazProgreso() {

            @Override
            public void onRealizandoAccion(String file) {
                actualizarSiEsPosible(new Runnable() {
                    @Override
                    public void run() {
                        tFileName.setText(file);
                    }
                });
            }

            @Override
            public void onPorcentajeActualizado(int porcentaje) {
                actualizarSiEsPosible(new Runnable() {
                    @Override
                    public void run() {
                        tPorcentaje.setText(porcentaje + "%");
                        progressIndicator.setProgressCompat(porcentaje, true);
                    }
                });
            }


            @Override
            public void onCompletado(double latitud, double longitud) {
                actualizarSiEsPosible(new Runnable() {
                    @Override
                    public void run() {
                        tFileName.setText("");

                        new Handler(Looper.getMainLooper()).postDelayed(new Runnable() {
                            @Override
                            public void run() {

                                if (!isFinishing()) {

                                    String db = getExtras2().getString(EXTRA_DB);

                                    if (db != null && db.length() > 0) {

                                        ActividadMapa.abrir(db, latitud, longitud, ActividadImportacionProceso.this);

                                    } else {
                                        ActividadSplash.abrir(ActividadImportacionProceso.this);
                                    }

                                    finalizarYAvanzar();


                                }

                            }
                        }, 1000);
                    }
                });

            }

            @Override
            public void onError() {
                actualizarSiEsPosible(new Runnable() {
                    @Override
                    public void run() {
                        new Handler(Looper.getMainLooper()).postDelayed(new Runnable() {
                            @Override
                            public void run() {

                                if (!isFinishing()) {
                                    ActividadImportacionError.abrir(ActividadImportacionProceso.this);
                                    finalizarYAvanzar();
                                }

                            }
                        }, 2000);
                    }
                });

            }

        });

    }

}