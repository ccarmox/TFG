/*
 * Trabajo de fin de grado: "Definición e implementación de herramienta para la navegación de peatones en entornos urbanos para el sistema operativo móvil Android"
 * Creado por: Cristian Do Carmo Rodríguez
 * Tutora: Lucía Díaz Vilariño
 * Cotutor: Jesús Balado Frías
 * Copyright (c) 2024. Todos los derechos reservados.
 */

package es.uvigo.eei.tfg.ccarmo.ui.views.navegacion;

public class Instruccion {

    private final boolean horario;
    private final double grados;
    private final long tiempo;
    private final double distancia;
    private final double azimuthOX;

    private final boolean finalizado;

    public Instruccion(Instruccion instruccion) {
        this.horario = instruccion.isHorario();
        this.grados = instruccion.getGrados();
        this.tiempo = instruccion.getTiempo();
        this.distancia = instruccion.getDistancia();
        this.azimuthOX = instruccion.getAzimuthOX();
        this.finalizado = instruccion.isFinalizado();
    }

    public Instruccion(boolean horario, double grados, double azimuthOX, double distancia, boolean finalizado) {
        this.horario = horario;
        this.grados = grados;
        this.tiempo = System.currentTimeMillis();
        this.distancia = distancia;
        this.azimuthOX = azimuthOX;
        this.finalizado = finalizado;
    }

    public InstruccionTexto getInstruccionTexto() {
        return new InstruccionTexto(this);
    }

    public boolean isHorario() {
        return horario;
    }

    public double getGrados() {
        return grados;
    }

    public long getTiempo() {
        return tiempo;
    }

    public double getDistancia() {
        return distancia;
    }

    public double getAzimuthOX() {
        return azimuthOX;
    }

    public boolean isFinalizado() {
        return finalizado;
    }

}
