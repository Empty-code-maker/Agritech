package com.agritech.models;

/** Planta seleccionable con su umbral de humedad del suelo. */
public final class Planta {

    private final int id;
    private final String nombre;
    private final UmbralPlanta umbral;

    public Planta(int id, String nombre, UmbralPlanta umbral) {
        this.id = id;
        this.nombre = nombre;
        this.umbral = umbral;
    }

    public int obtenerId() {
        return id;
    }

    public String obtenerNombre() {
        return nombre;
    }

    public UmbralPlanta obtenerUmbral() {
        return umbral;
    }

    @Override
    public String toString() {
        return nombre + " — mínimo " + umbral.obtenerHumedadSueloMinima() + " %";
    }
}