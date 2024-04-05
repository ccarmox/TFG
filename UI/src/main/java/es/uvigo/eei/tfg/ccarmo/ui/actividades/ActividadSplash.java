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
import android.os.Bundle;
import android.os.Handler;

import androidx.annotation.NonNull;

import es.uvigo.eei.tfg.ccarmo.R;
import es.uvigo.eei.tfg.ccarmo.importacion.utils.Utils;
import es.uvigo.eei.tfg.ccarmo.ui.actividades.utils.ActividadBase;

public class ActividadSplash extends ActividadBase {

    public static void abrir(@NonNull Context context) {

        Intent intent = new Intent(context, ActividadSplash.class);
        context.startActivity(intent);

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.actividad_inicio);

        Utils.eliminarCache(getApplicationContext());


        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {

                if (!isFinishing()) {
                    ActividadMenu.abrir(ActividadSplash.this);

                    finish();
                }

            }
        }, 1000);


    }

}