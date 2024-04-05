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
import android.widget.TextView;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;

import com.google.android.material.slider.Slider;

import java.util.ArrayList;

import es.uvigo.eei.tfg.ccarmo.R;
import es.uvigo.eei.tfg.ccarmo.clases.classes.TipoVia;
import es.uvigo.eei.tfg.ccarmo.importacion.classes.Fuente;
import es.uvigo.eei.tfg.ccarmo.ui.actividades.ActividadMenu;
import es.uvigo.eei.tfg.ccarmo.ui.actividades.importacion.documentacion.GenerarEscalasReferencia;

public class ActividadImportacionResolucion extends ActividadImportacionBase {

    private final static String EXTRA_FUENTES = "Fuentes";
    private final static String EXTRA_TIPO_VIA = "TipoVia";
    ArrayList<Fuente> fuentes;
    TipoVia tipoVia;
    private int resolucion = 4;

    public static void abrir(@NonNull ArrayList<Fuente> fuentes, @NonNull TipoVia tipoVia, @NonNull Context context) {

        Intent intent = new Intent(context, ActividadImportacionResolucion.class);
        intent.putExtra(EXTRA_FUENTES, fuentes);
        intent.putExtra(EXTRA_TIPO_VIA, tipoVia);
        context.startActivity(intent);

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.actividad_importacion_resolucion);

        fuentes = (ArrayList<Fuente>) getExtras2().getSerializable(EXTRA_FUENTES);
        tipoVia = (TipoVia) getExtras2().getSerializable(EXTRA_TIPO_VIA);


        getOnBackPressedDispatcher().addCallback(this, new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                if (!isFinishing()) {

                    if (tipoVia == TipoVia.NONE) {
                        //Lanzo la actividad de menu
                        ActividadMenu.abrir(ActividadImportacionResolucion.this);
                    } else {
                        ActividadImportacionSeleccionarTipo.abrir(fuentes, ActividadImportacionResolucion.this);
                    }

                    //Termino esta actividad
                    finalizarYRetroceder();
                }
            }
        });


        TextView texto = findViewById(R.id.slider_text);
        Slider slider = findViewById(R.id.slider);

        slider.addOnChangeListener(new Slider.OnChangeListener() {
            @Override
            public void onValueChange(@NonNull Slider slider, float value, boolean fromUser) {
                resolucion = Math.round(value);
                texto.setText(String.valueOf(resolucion));
            }
        });

        slider.setValue(4.0F);
        texto.setText(String.valueOf(resolucion));

        int[] views = new int[]{
                R.id.valor_resolucion_1,
                R.id.valor_resolucion_2,
                R.id.valor_resolucion_3,
                R.id.valor_resolucion_4,
                R.id.valor_resolucion_5,
                R.id.valor_resolucion_6,
                R.id.valor_resolucion_7
        };

        GenerarEscalasReferencia.DatosResolucion[] resoluciones = GenerarEscalasReferencia.getTablaResoluciones(7);
        for (int i = 0; i < views.length; i++) {
            TextView t = findViewById(views[i]);
            t.setText(resoluciones[i].getDistanciaMaxima());
        }

        if (fuentes == null || tipoVia == null) {
            ActividadMenu.abrir(ActividadImportacionResolucion.this);
            finish();
        }

        findViewById(R.id.boton_continuar).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //Se abre el importador
                ActividadImportacionDestino.abrir(fuentes, tipoVia, resolucion, ActividadImportacionResolucion.this);

                //Se finaliza esta actividad
                finalizarYAvanzar();

            }
        });


    }

}