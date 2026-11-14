package com.agritech.models;

/** Umbral configurable de humedad del suelo para una planta. */
public final class UmbralPlanta {

    private final int idPlanta;
    private final double humedadSueloMinima;

    public UmbralPlanta(int idPlanta, double humedadSueloMinima) {
        this.idPlanta = idPlanta;
        this.humedadSueloMinima = humedadSueloMinima;
    }

    public int obtenerIdPlanta() {
        return idPlanta;
    }

    public double obtenerHumedadSueloMinima() {
        return humedadSueloMinima;
    }
}