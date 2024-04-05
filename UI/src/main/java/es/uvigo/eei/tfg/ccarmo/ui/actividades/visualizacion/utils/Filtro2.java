/*
 * Trabajo de fin de grado: "Definición e implementación de herramienta para la navegación de peatones en entornos urbanos para el sistema operativo móvil Android"
 * Creado por: Cristian Do Carmo Rodríguez
 * Tutora: Lucía Díaz Vilariño
 * Cotutor: Jesús Balado Frías
 * Copyright (c) 2024. Todos los derechos reservados.
 */

package es.uvigo.eei.tfg.ccarmo.ui.actividades.visualizacion.utils;

import android.content.Context;
import android.content.res.Resources;

import androidx.annotation.NonNull;

import java.util.ArrayList;

import es.uvigo.eei.tfg.ccarmo.R;
import es.uvigo.eei.tfg.ccarmo.clases.classes.TipoVia;
import es.uvigo.eei.tfg.ccarmo.datos.almacenamiento.Configuracion;
import es.uvigo.eei.tfg.ccarmo.utils.SharedPreferences2;


public class Filtro2 {

    private final Context context;
    private final SharedPreferences2 preferencias;
    private boolean dibujar_lineas, acera, paso_peatones, paso_elevado, escaleras, via_residencial, via_peatonal, via_servicio, sendero, poi, puntosDeCalor;
    public Filtro2(@NonNull Context context) {
        this.context = context;
        this.preferencias = new SharedPreferences2(context);

        //Restauro las ultimas preferencias
        this.dibujar_lineas = preferencias.leer(PREFERENCIAS.DIBUJAR_LINEAS, true);
        this.acera = preferencias.leer(PREFERENCIAS.ACERA, true);
        this.paso_peatones = preferencias.leer(PREFERENCIAS.PASO_PEATONES, true);
        this.paso_elevado = preferencias.leer(PREFERENCIAS.PASO_ELEVADO, true);
        this.escaleras = preferencias.leer(PREFERENCIAS.ESCALERAS, true);
        this.via_residencial = preferencias.leer(PREFERENCIAS.VIA_RESIDENCIAL, true);
        this.via_peatonal = preferencias.leer(PREFERENCIAS.VIA_PEATONAL, true);
        this.via_servicio = preferencias.leer(PREFERENCIAS.VIA_SERVICIO, true);
        this.sendero = preferencias.leer(PREFERENCIAS.SENDERO, true);
        this.poi = preferencias.leer(PREFERENCIAS.POI, false);
        this.puntosDeCalor = preferencias.leer(PREFERENCIAS.PUNTOS_DE_CALOR, false);

    }

    public String[] getNombreCapas() {
        Resources r = context.getResources();
        return new String[]{
                r.getString(R.string.dibujar_lineas),
                r.getString(
                        R.string.poi),
                r.getString(
                        R.string.puntos_de_calor),
                r.getString(R.string.acera),
                r.getString(R.string.paso_peatones),
                r.getString(R.string.paso_elevado),
                r.getString(
                        R.string.escaleras),
                r.getString(
                        R.string.via_residencial),
                r.getString(
                        R.string.via_peatonal),
                r.getString(
                        R.string.via_servicio),
                r.getString(
                        R.string.sendero)
        };
    }

    public boolean[] getSeleccionados() {
        return new boolean[]{
                dibujar_lineas,
                poi,
                puntosDeCalor,
                acera,
                paso_peatones,
                paso_elevado,
                escaleras,
                via_residencial,
                via_peatonal,
                via_servicio,
                sendero
        };
    }

    public void setSeleccionado(int posicion, boolean seleccionado) {
        switch (posicion) {
            case 0:
                dibujar_lineas = seleccionado;
                break;
            case 1:
                poi = seleccionado;
                break;
            case 2:
                puntosDeCalor = seleccionado;
                break;
            case 3:
                acera = seleccionado;
                break;
            case 4:
                paso_peatones = seleccionado;
                break;
            case 5:
                paso_elevado = seleccionado;
                break;
            case 6:
                escaleras = seleccionado;
                break;
            case 7:
                via_residencial = seleccionado;
                break;
            case 8:
                via_peatonal = seleccionado;
                break;
            case 9:
                via_servicio = seleccionado;
                break;
            case 10:
                sendero = seleccionado;
                break;
        }

        //Guardo las nuevas preferencias
        preferencias.guardar(PREFERENCIAS.DIBUJAR_LINEAS, dibujar_lineas);
        preferencias.guardar(PREFERENCIAS.ACERA, acera);
        preferencias.guardar(PREFERENCIAS.PASO_PEATONES, paso_peatones);
        preferencias.guardar(PREFERENCIAS.PASO_ELEVADO, paso_elevado);
        preferencias.guardar(PREFERENCIAS.ESCALERAS, escaleras);
        preferencias.guardar(PREFERENCIAS.VIA_RESIDENCIAL, via_residencial);
        preferencias.guardar(PREFERENCIAS.VIA_PEATONAL, via_peatonal);
        preferencias.guardar(PREFERENCIAS.VIA_SERVICIO, via_servicio);
        preferencias.guardar(PREFERENCIAS.SENDERO, sendero);
        preferencias.guardar(PREFERENCIAS.POI, poi);
        preferencias.guardar(PREFERENCIAS.PUNTOS_DE_CALOR, puntosDeCalor);
    }

    public Configuracion getConfiguracion() {
        ArrayList<TipoVia> rutasValidas = new ArrayList<>();

        if (acera) {
            rutasValidas.add(TipoVia.ACERA);
        }
        if (paso_peatones) {
            rutasValidas.add(TipoVia.PASO_DE_PEATONES);
        }
        if (paso_elevado) {
            rutasValidas.add(TipoVia.PASO_ELEVADO);
        }
        if (sendero) {
            rutasValidas.add(TipoVia.SENDERO);
        }
        if (escaleras) {
            rutasValidas.add(TipoVia.ESCALERAS);
        }
        if (via_peatonal) {
            rutasValidas.add(TipoVia.VIA_PEATONAL);
        }
        if (via_residencial) {
            rutasValidas.add(TipoVia.VIA_RESIDENCIAL);
        }
        if (via_servicio) {
            rutasValidas.add(TipoVia.VIA_SERVICIO);
        }

        return new Configuracion(rutasValidas);
    }

    public boolean isPuntosDeCalor() {
        return puntosDeCalor;
    }

    public boolean isDibujarLineas() {
        return dibujar_lineas;
    }

    private final static class PREFERENCIAS {
        public static final String DIBUJAR_LINEAS = "dibujar_lineas";
        public static final String ACERA = "mostrar_acera";
        public static final String PASO_PEATONES = "mostrar_paso_peatones";
        public static final String PASO_ELEVADO = "mostrar_paso_elevado";
        public static final String ESCALERAS = "mostrar_escaleras";
        public static final String VIA_RESIDENCIAL = "mostrar_via_residencial";
        public static final String VIA_PEATONAL = "mostrar_via_peatonal";
        public static final String VIA_SERVICIO = "mostrar_via_servicio";
        public static final String SENDERO = "mostrar_sendero";
        public static final String POI = "mostrar_poi";
        public static final String PUNTOS_DE_CALOR = "mostrar_puntos_calor";

    }
}
