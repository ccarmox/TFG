
/*
 * Trabajo de fin de grado: "Definición e implementación de herramienta para la navegación de peatones en entornos urbanos para el sistema operativo móvil Android"
 * Creado por: Cristian Do Carmo Rodríguez
 * Tutora: Lucía Díaz Vilariño
 * Cotutor: Jesús Balado Frías
 * Copyright (c) 2024. Todos los derechos reservados.
 */

package es.uvigo.eei.tfg.ccarmo.importacion.algoritmos.osm.xml;

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;

public class OSMMiembro {

    @JacksonXmlProperty(localName = "type", isAttribute = true)
    private String tipo; //Node o Way
    @JacksonXmlProperty(localName = "ref", isAttribute = true)
    private String referencia;
    @JacksonXmlProperty(localName = "role", isAttribute = true)
    private String rol;//Label, admin_centre o outer

    public String getTipo() {
        return tipo;
    }

    public void setTipo(String tipo) {
        this.tipo = tipo;
    }

    public String getReferencia() {
        return referencia;
    }

    public void setReferencia(String referencia) {
        this.referencia = referencia;
    }

    public String getRol() {
        return rol;
    }

    public void setRol(String rol) {
        this.rol = rol;
    }


}
