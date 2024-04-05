/*
 * Trabajo de fin de grado: "Definición e implementación de herramienta para la navegación de peatones en entornos urbanos para el sistema operativo móvil Android"
 * Creado por: Cristian Do Carmo Rodríguez
 * Tutora: Lucía Díaz Vilariño
 * Cotutor: Jesús Balado Frías
 * Copyright (c) 2024. Todos los derechos reservados.
 */

package es.uvigo.eei.tfg.ccarmo.ui.actividades.importacion;

import es.uvigo.eei.tfg.ccarmo.R;
import es.uvigo.eei.tfg.ccarmo.ui.actividades.utils.ActividadBase;

public class ActividadImportacionBase extends ActividadBase {

    protected void finalizarYAvanzar() {
        finish();
        overridePendingTransition(R.anim.animacion_1, R.anim.animacion_2);
    }

    protected void finalizarYRetroceder() {
        finish();
        overridePendingTransition(R.anim.animacion_3, R.anim.animacion_4);
    }

}