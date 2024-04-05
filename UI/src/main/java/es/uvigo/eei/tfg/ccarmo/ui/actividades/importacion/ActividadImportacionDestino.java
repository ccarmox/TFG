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

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;

import java.util.ArrayList;

import es.uvigo.eei.tfg.ccarmo.R;
import es.uvigo.eei.tfg.ccarmo.clases.classes.TipoVia;
import es.uvigo.eei.tfg.ccarmo.datos.almacenamiento.SQLite;
import es.uvigo.eei.tfg.ccarmo.importacion.classes.Fuente;
import es.uvigo.eei.tfg.ccarmo.ui.actividades.ActividadMenu;

public class ActividadImportacionDestino extends ActividadImportacionBase {

    private final static String EXTRA_FUENTES = "Fuentes";
    private final static String EXTRA_TIPO_VIA = "TipoVia";
    private final static String EXTRA_RESOLUCION = "Resolucion";
    ArrayList<Fuente> fuentes;
    TipoVia tipoVia;
    int resolucion;

    public static void abrir(@NonNull ArrayList<Fuente> fuentes, @NonNull TipoVia tipoVia, int resolucion, @NonNull Context context) {

        Intent intent = new Intent(context, ActividadImportacionDestino.class);
        intent.putExtra(EXTRA_FUENTES, fuentes);
        intent.putExtra(EXTRA_TIPO_VIA, tipoVia);
        intent.putExtra(EXTRA_RESOLUCION, resolucion);
        context.startActivity(intent);

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.actividad_importacion_destino);

        fuentes = (ArrayList<Fuente>) getExtras2().getSerializable(EXTRA_FUENTES);
        tipoVia = (TipoVia) getExtras2().getSerializable(EXTRA_TIPO_VIA);
        resolucion = getExtras2().getInt(EXTRA_RESOLUCION);


        getOnBackPressedDispatcher().addCallback(this, new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                if (!isFinishing()) {

                    if (tipoVia == TipoVia.NONE) {
                        //Lanzo la actividad de menu
                        ActividadMenu.abrir(ActividadImportacionDestino.this);
                    } else {
                        ActividadImportacionResolucion.abrir(fuentes, tipoVia, ActividadImportacionDestino.this);
                    }

                    //Termino esta actividad
                    finalizarYRetroceder();
                }
            }
        });

        if (fuentes == null || tipoVia == null) {
            ActividadMenu.abrir(ActividadImportacionDestino.this);
            finish();
        }

        findViewById(R.id.boton_importar).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //Se abre el importador
                ActividadImportacionProceso.abrir(fuentes, tipoVia, SQLite.DEFAULT_NAME, resolucion, ActividadImportacionDestino.this);

                //Se finaliza esta actividad
                finalizarYAvanzar();

            }
        });

        findViewById(R.id.boton_visualizar).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //Se abre el importador
                ActividadImportacionProceso.abrir(fuentes, tipoVia, SQLite.CACHE_NAME, resolucion, ActividadImportacionDestino.this);

                //Se finaliza esta actividad
                finalizarYAvanzar();

            }
        });

    }

}