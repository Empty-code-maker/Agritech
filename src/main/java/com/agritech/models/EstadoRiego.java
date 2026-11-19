package com.agritech.models;

import java.time.LocalDateTime;

/** Estado persistente del sistema de riego simulado. */
public final class EstadoRiego {

    private final boolean activo;
    private final LocalDateTime fechaHoraUltimoCambio;

    /**
     * @param activo indica si el riego está marcado como activo
     * @param fechaHoraUltimoCambio instante del último cambio; es nulo antes del primer cambio
     */
    public EstadoRiego(boolean activo, LocalDateTime fechaHoraUltimoCambio) {
        this.activo = activo;
        this.fechaHoraUltimoCambio = fechaHoraUltimoCambio;
    }

    public boolean estaActivo() {
        return activo;
    }

    public LocalDateTime obtenerFechaHoraUltimoCambio() {
        return fechaHoraUltimoCambio;
    }
}