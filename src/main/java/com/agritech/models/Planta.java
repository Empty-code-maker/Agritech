
package com.agritech.models;

import java.time.LocalDateTime;

/** Representa una planta registrada en AgriTech. */
public final class Planta {

    private final int id;
    private final String nombre;
    private final UmbralPlanta umbral;

    // Relación con el huerto
    private final Integer idHuerto;
    private final Double areaM2;

    // Información adicional de la planta
    private final String especie;
    private final String ubicacionZona;
    private final LocalDateTime fechaAlta;

    // Constructor original para mantener compatibilidad
    public Planta(int id, String nombre, UmbralPlanta umbral) {
        this(id, nombre, umbral, null, null, null, null, null);
    }

    // Constructor que ya utiliza el repositorio
    public Planta(int id, String nombre, UmbralPlanta umbral,
                  Integer idHuerto, Double areaM2) {

        this(id, nombre, umbral, idHuerto, areaM2,
                null, null, null);
    }

    // Constructor completo según el nuevo esquema
    public Planta(int id, String nombre, UmbralPlanta umbral,
                  Integer idHuerto, Double areaM2,
                  String especie, String ubicacionZona,
                  LocalDateTime fechaAlta) {

        this.id = id;
        this.nombre = nombre;
        this.umbral = umbral;
        this.idHuerto = idHuerto;
        this.areaM2 = areaM2;
        this.especie = especie;
        this.ubicacionZona = ubicacionZona;
        this.fechaAlta = fechaAlta;
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

    public Integer obtenerIdHuerto() {
        return idHuerto;
    }

    public Double obtenerAreaM2() {
        return areaM2;
    }

    public String obtenerEspecie() {
        return especie;
    }

    public String obtenerUbicacionZona() {
        return ubicacionZona;
    }

    public LocalDateTime obtenerFechaAlta() {
        return fechaAlta;
    }

    @Override
    public String toString() {
        return nombre + " — mínimo "
                + umbral.obtenerHumedadSueloMinima() + " %";
    }
}
