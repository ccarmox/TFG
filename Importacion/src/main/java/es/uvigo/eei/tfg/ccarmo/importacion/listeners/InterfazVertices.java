/*
 * Trabajo de fin de grado: "Definición e implementación de herramienta para la navegación de peatones en entornos urbanos para el sistema operativo móvil Android"
 * Creado por: Cristian Do Carmo Rodríguez
 * Tutora: Lucía Díaz Vilariño
 * Cotutor: Jesús Balado Frías
 * Copyright (c) 2024. Todos los derechos reservados.
 */

package es.uvigo.eei.tfg.ccarmo.importacion.listeners;

import androidx.annotation.NonNull;

import es.uvigo.eei.tfg.ccarmo.clases.classes.Vertice;

public interface InterfazVertices {

    void onNuevoVertice(@NonNull Vertice vertice);

    void onFinalizado();

}
