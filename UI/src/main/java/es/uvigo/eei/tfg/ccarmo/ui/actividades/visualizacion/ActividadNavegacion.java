/*
 * Trabajo de fin de grado: "Definición e implementación de herramienta para la navegación de peatones en entornos urbanos para el sistema operativo móvil Android"
 * Creado por: Cristian Do Carmo Rodríguez
 * Tutora: Lucía Díaz Vilariño
 * Cotutor: Jesús Balado Frías
 * Copyright (c) 2024. Todos los derechos reservados.
 */

package es.uvigo.eei.tfg.ccarmo.ui.actividades.visualizacion;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;

import es.uvigo.eei.tfg.ccarmo.R;
import es.uvigo.eei.tfg.ccarmo.clases.classes.LatLon;
import es.uvigo.eei.tfg.ccarmo.ui.actividades.ActividadMenu;
import es.uvigo.eei.tfg.ccarmo.ui.actividades.visualizacion.utils.GPS;
import es.uvigo.eei.tfg.ccarmo.ui.views.navegacion.NavegacionView;

public class ActividadNavegacion extends GPS {

    private final static String EXTRA_DB = "DB";
    private final static String EXTRA_RUTA = "RUTA";
    private NavegacionView mapa2;

    public static void abrir(@NonNull String db, @NonNull String ruta, @NonNull Activity activity) {
        Intent intent = new Intent(activity, ActividadNavegacion.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        intent.putExtra(EXTRA_RUTA, ruta);
        intent.putExtra(EXTRA_DB, db);
        activity.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.actividad_navegacion);

        getOnBackPressedDispatcher().addCallback(this, new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                if (!isFinishing()) {

                    //Lanzo la actividad de menu
                    ActividadMenu.abrir(ActividadNavegacion.this);

                    //Termino esta actividad
                    finish();
                }
            }
        });


        mapa2 = findViewById(R.id.navegacion);


        mapa2.setRuta(getExtras2().getString(EXTRA_DB), getExtras2().getString(EXTRA_RUTA));


        new Thread(new Runnable() {
            @Override
            public void run() {

                try {
                    Thread.sleep(1000);
                } catch (Exception ignored) {
                }

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {

                        setLocationListener(new CustomLocationListener() {
                            @Override
                            public void onLocation(LatLon latLon) {
                                android.util.Log.v("Localizacion", "=>" + latLon.toString());
                                mapa2.update(latLon);
                            }
                        });


                        setBrujulaListener(new CustomBrujulaListener() {
                            @Override
                            public void onData(float azimuth) {
                                mapa2.update(azimuth);
                            }
                        });


                    }
                });

            }
        }).start();

    }


}