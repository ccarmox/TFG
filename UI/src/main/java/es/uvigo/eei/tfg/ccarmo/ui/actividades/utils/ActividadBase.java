/*
 * Trabajo de fin de grado: "Definición e implementación de herramienta para la navegación de peatones en entornos urbanos para el sistema operativo móvil Android"
 * Creado por: Cristian Do Carmo Rodríguez
 * Tutora: Lucía Díaz Vilariño
 * Cotutor: Jesús Balado Frías
 * Copyright (c) 2024. Todos los derechos reservados.
 */

package es.uvigo.eei.tfg.ccarmo.ui.actividades.utils;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

public class ActividadBase extends AppCompatActivity {

    private Bundle savedInstance;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        this.savedInstance = savedInstanceState;

    }

    //Sirve para recuperar los extras si la actividad es reiniciada
    protected Bundle getExtras2() {
        if (savedInstance == null) {

            Intent intent = getIntent();

            if (intent != null) {
                if (intent.getExtras() != null) {
                    return intent.getExtras();
                }
            }

            return new Bundle();
        } else {
            return savedInstance;
        }
    }

    @Override
    protected void onSaveInstanceState(@NonNull Bundle savedInstanceState) {
        super.onSaveInstanceState(savedInstanceState);
        try {
            if (getIntent() != null && getIntent().getExtras() != null) {
                savedInstanceState.putAll(getIntent().getExtras());
            }
        } catch (Exception ignored) {
        }
    }

    //Ejecuta un runnable dentro de la UI si la actividad todavía se esta ejecutando
    protected void actualizarSiEsPosible(Runnable runnable) {
        if (!isFinishing()) {
            try {
                runOnUiThread(runnable);
            } catch (Throwable e) {
                e.printStackTrace();
            }
        }
    }
}
