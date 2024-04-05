/*
 * Trabajo de fin de grado: "Definición e implementación de herramienta para la navegación de peatones en entornos urbanos para el sistema operativo móvil Android"
 * Creado por: Cristian Do Carmo Rodríguez
 * Tutora: Lucía Díaz Vilariño
 * Cotutor: Jesús Balado Frías
 * Copyright (c) 2024. Todos los derechos reservados.
 */

package es.uvigo.eei.tfg.ccarmo.ui.views.navegacion;

import es.uvigo.eei.tfg.ccarmo.R;


public class InstruccionTexto extends Instruccion {

    public InstruccionTexto(Instruccion instruccion) {
        super(instruccion);
    }

    public InstruccionTexto(boolean horario, double grados, double azimuthOX, double distancia, boolean finalizado) {
        super(horario, grados, azimuthOX, distancia, finalizado);
    }

    public int getTexto() {

        if (isFinalizado()) {
            return R.string.instruccion_llegada_destino;
        }

        if (getGrados() < 10) {
            return R.string.instruccion_sigue_recto;
        }

        if (getGrados() < 45) {
            if (isHorario()) {
                return R.string.instruccion_gira_derecha_ligeramente;
            } else {
                return R.string.instruccion_gira_izquierda_ligeramente;
            }
        }

        if (getGrados() < 120) {
            if (isHorario()) {
                return R.string.instruccion_gira_derecha;
            } else {
                return R.string.instruccion_gira_izquierda;
            }
        }

        return R.string.instruccion_da_la_vuelta;

    }

    public int getDrawable() {

        if (isFinalizado()) {
            return R.drawable.ic_llegada_destino;
        }

        if (getGrados() < 10) {
            return R.drawable.ic_sigue_recto;
        }

        if (getGrados() < 45) {
            if (isHorario()) {
                return R.drawable.ic_girar_derecha_ligeramente;
            } else {
                return R.drawable.ic_girar_izquierda_ligeramente;
            }
        }

        if (getGrados() < 120) {
            if (isHorario()) {
                return R.drawable.ic_girar_derecha;
            } else {
                return R.drawable.ic_girar_izquierda;
            }
        }

        if (isHorario()) {
            return R.drawable.ic_girar_vuelta_derecha;
        } else {
            return R.drawable.ic_girar_vuelta_izquierda;
        }
    }

}
