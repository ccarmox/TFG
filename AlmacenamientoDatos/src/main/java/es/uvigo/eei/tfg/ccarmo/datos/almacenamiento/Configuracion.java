/*
 * Trabajo de fin de grado: "Definición e implementación de herramienta para la navegación de peatones en entornos urbanos para el sistema operativo móvil Android"
 * Creado por: Cristian Do Carmo Rodríguez
 * Tutora: Lucía Díaz Vilariño
 * Cotutor: Jesús Balado Frías
 * Copyright (c) 2024. Todos los derechos reservados.
 */

package es.uvigo.eei.tfg.ccarmo.datos.almacenamiento;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.ArrayList;

import es.uvigo.eei.tfg.ccarmo.clases.classes.TipoVia;


public class Configuracion {

    private final ArrayList<TipoVia> viasValidas;

    public Configuracion(@Nullable ArrayList<TipoVia> viasValidas) {
        this.viasValidas = viasValidas == null ? new ArrayList<>() : viasValidas;
    }

    @NonNull
    public ArrayList<TipoVia> getViasValidas() {
        return viasValidas;
    }

    public boolean isFiltrarTipoVia(){
        return viasValidas.size()>0;
    }

}
