package com.agritech.models;

import java.time.LocalDateTime;

/** Lectura simulada de humedad del suelo asociada a una planta. */
public final class Medida {

    private final long id;
    private final int idPlanta;
    private final double humedadSuelo;
    private final LocalDateTime fechaHora;

    public Medida(long id, int idPlanta,
                       double humedadSuelo, LocalDateTime fechaHora) {
        this.id = id;
        this.idPlanta = idPlanta;
        this.humedadSuelo = humedadSuelo;
        this.fechaHora = fechaHora;
    }

    public long obtenerId() {
        return id;
    }

    public int obtenerIdPlanta() {
        return idPlanta;
    }

    public double obtenerHumedadSuelo() {
        return humedadSuelo;
    }

    public LocalDateTime obtenerFechaHora() {
        return fechaHora;
    }
}