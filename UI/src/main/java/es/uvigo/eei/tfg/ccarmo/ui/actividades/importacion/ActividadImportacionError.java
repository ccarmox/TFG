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
import android.view.View;

import androidx.annotation.NonNull;

import es.uvigo.eei.tfg.ccarmo.R;
import es.uvigo.eei.tfg.ccarmo.ui.actividades.ActividadSplash;

public class ActividadImportacionError extends ActividadImportacionBase {

    public static void abrir(@NonNull Context context) {
        Intent intent = new Intent(context, ActividadImportacionError.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.actividad_importacion_error);

        findViewById(R.id.boton_aceptar).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!isFinishing()) {
                    ActividadSplash.abrir(ActividadImportacionError.this);
                    finalizarYAvanzar();
                }
            }
        });

    }

}