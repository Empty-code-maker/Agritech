
package com.agritech.models;

import java.time.LocalDateTime;

/** Representa el estado de riego de un huerto. */
public final class EstadoRiego {

    // Identificadores
    private final int id;
    private final Integer idHuerto;

    // Estado del riego
    private final boolean activo;
    private final String modo;
    private final LocalDateTime fechaHoraUltimoCambio;

    // Constructor original para mantener compatibilidad
    public EstadoRiego(boolean activo,
                       LocalDateTime fechaHoraUltimoCambio) {

        this(1, null, activo, null, fechaHoraUltimoCambio);
    }

    // Constructor completo para el riego por huerto
    public EstadoRiego(int id, Integer idHuerto,
                       boolean activo, String modo,
                       LocalDateTime fechaHoraUltimoCambio) {

        this.id = id;
        this.idHuerto = idHuerto;
        this.activo = activo;
        this.modo = modo;
        this.fechaHoraUltimoCambio = fechaHoraUltimoCambio;
    }

    // Devuelve el identificador del estado
    public int obtenerId() {
        return id;
    }

    // Devuelve el huerto asociado
    public Integer obtenerIdHuerto() {
        return idHuerto;
    }

    // Indica si el riego está activo
    public boolean estaActivo() {
        return activo;
    }

    // Devuelve el modo de funcionamiento
    public String obtenerModo() {
        return modo;
    }

    // Devuelve la fecha del último cambio
    public LocalDateTime obtenerFechaHoraUltimoCambio() {
        return fechaHoraUltimoCambio;
    }
}
