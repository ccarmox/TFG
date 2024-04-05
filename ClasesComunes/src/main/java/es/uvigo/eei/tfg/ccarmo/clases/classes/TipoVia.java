/*
 * Trabajo de fin de grado: "Definición e implementación de herramienta para la navegación de peatones en entornos urbanos para el sistema operativo móvil Android"
 * Creado por: Cristian Do Carmo Rodríguez
 * Tutora: Lucía Díaz Vilariño
 * Cotutor: Jesús Balado Frías
 * Copyright (c) 2024. Todos los derechos reservados.
 */

package es.uvigo.eei.tfg.ccarmo.clases.classes;

public enum TipoVia {


    PASO_DE_PEATONES(Constantes.PASO_DE_PEATONES),
    ACERA(Constantes.ACERA),
    ESCALERAS(Constantes.ESCALERAS),
    VIA_RESIDENCIAL(Constantes.VIA_RESIDENCIAL),
    VIA_SERVICIO(Constantes.VIA_SERVICIO),
    VIA_PEATONAL(Constantes.VIA_PEATONAL),
    SENDERO(Constantes.SENDERO),
    NONE(Constantes.NONE),
    POI(Constantes.POI),
    PASO_ELEVADO(Constantes.PASO_ELEVADO);

    private final int valor;

    TipoVia(final int valor) {
        this.valor = valor;
    }

    public static TipoVia parse(int valor) {
        switch (valor) {
            case Constantes.NONE:
            default:
                return NONE;
            case Constantes.ACERA:
                return ACERA;
            case Constantes.ESCALERAS:
                return ESCALERAS;
            case Constantes.VIA_RESIDENCIAL:
                return VIA_RESIDENCIAL;
            case Constantes.VIA_SERVICIO:
                return VIA_SERVICIO;
            case Constantes.VIA_PEATONAL:
                return VIA_PEATONAL;
            case Constantes.SENDERO:
                return SENDERO;
            case Constantes.PASO_DE_PEATONES:
                return PASO_DE_PEATONES;
            case Constantes.POI:
                return POI;
            case Constantes.PASO_ELEVADO:
                return PASO_ELEVADO;

        }
    }

    public int getValor() {
        return valor;
    }

    private final static class Constantes {

        public final static int NONE = 0;
        public final static int PASO_DE_PEATONES = 1;
        public final static int ACERA = 2;
        public final static int ESCALERAS = 3;
        public final static int VIA_RESIDENCIAL = 4;
        public final static int VIA_SERVICIO = 5;
        public final static int VIA_PEATONAL = 6;
        public final static int SENDERO = 7;
        public final static int POI = 8;
        public final static int PASO_ELEVADO = 9;
    }


}
