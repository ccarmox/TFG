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
import android.widget.RadioGroup;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;

import java.util.ArrayList;

import es.uvigo.eei.tfg.ccarmo.R;
import es.uvigo.eei.tfg.ccarmo.clases.classes.TipoVia;
import es.uvigo.eei.tfg.ccarmo.importacion.classes.Fuente;
import es.uvigo.eei.tfg.ccarmo.importacion.classes.TipoDato;

public class ActividadImportacionSeleccionarTipo extends ActividadImportacionBase {

    private final static String EXTRA_FUENTES = "Fuentes";

    public static void abrir(@NonNull ArrayList<Fuente> fuentes, @NonNull Context context) {

        boolean saltar = true;

        for (Fuente f : fuentes) {
            if (f.getTipoDato() != TipoDato.OSM) {
                saltar = false;
            }
        }

        if (saltar) {
            ActividadImportacionResolucion.abrir(fuentes, TipoVia.NONE, context);
        } else {
            Intent intent = new Intent(context, ActividadImportacionSeleccionarTipo.class);
            intent.putExtra(EXTRA_FUENTES, fuentes);
            context.startActivity(intent);
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.actividad_seleccionar_tipo_via);


        getOnBackPressedDispatcher().addCallback(this, new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                if (!isFinishing()) {

                    //Lanzo la actividad de menu
                    ActividadMenuImportacion.abrir(ActividadImportacionSeleccionarTipo.this);


                    //Termino esta actividad
                    finalizarYRetroceder();
                }
            }
        });

        findViewById(R.id.boton_continuar).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                RadioGroup group = findViewById(R.id.seleccion);
                TipoVia tipoVia = TipoVia.NONE;

                switch (group.getCheckedRadioButtonId()) {
                    case R.id.opcion_acera:
                        tipoVia = TipoVia.ACERA;
                        break;
                    case R.id.opcion_paso_peatones:
                        tipoVia = TipoVia.PASO_DE_PEATONES;
                        break;
                    case R.id.opcion_paso_elevado:
                        tipoVia = TipoVia.PASO_ELEVADO;
                        break;
                    case R.id.opcion_via_peatonal:
                        tipoVia = TipoVia.VIA_PEATONAL;
                        break;
                    case R.id.opcion_via_residencial:
                        tipoVia = TipoVia.VIA_RESIDENCIAL;
                        break;
                    case R.id.opcion_via_servicio:
                        tipoVia = TipoVia.VIA_SERVICIO;
                        break;
                    case R.id.opcion_poi:
                        tipoVia = TipoVia.POI;
                        break;
                    case R.id.opcion_escaleras:
                        tipoVia = TipoVia.ESCALERAS;
                        break;
                    case R.id.opcion_sendero:
                        tipoVia = TipoVia.SENDERO;
                        break;
                }

                //Se abre el selector de resolucion
                ActividadImportacionResolucion.abrir((ArrayList<Fuente>) getExtras2().getSerializable(EXTRA_FUENTES), tipoVia, ActividadImportacionSeleccionarTipo.this);

                //Se finaliza esta actividad
                finalizarYAvanzar();

            }
        });


    }

}