
package com.agritech.models;

import java.time.LocalDateTime;

/** Representa un huerto registrado en AgriTech. */
public final class Huerto {

    private final int id;
    private final int idUsuario;
    private final String nombre;
    private final String ubicacion;
    private final String tipoUbicacion;
    private final double areaTotalM2;
    private final String descripcion;
    private final LocalDateTime fechaAlta;

    // Constructor del huerto con todos sus datos
    public Huerto(int id, int idUsuario, String nombre,
                  String ubicacion, String tipoUbicacion,
                  double areaTotalM2, String descripcion,
                  LocalDateTime fechaAlta) {

        this.id = id;
        this.idUsuario = idUsuario;
        this.nombre = nombre;
        this.ubicacion = ubicacion;
        this.tipoUbicacion = tipoUbicacion;
        this.areaTotalM2 = areaTotalM2;
        this.descripcion = descripcion;
        this.fechaAlta = fechaAlta;
    }

    // Devuelve el identificador del huerto
    public int obtenerId() {
        return id;
    }

    // Devuelve el usuario propietario
    public int obtenerIdUsuario() {
        return idUsuario;
    }

    // Devuelve el nombre del huerto
    public String obtenerNombre() {
        return nombre;
    }

    // Devuelve su ubicación
    public String obtenerUbicacion() {
        return ubicacion;
    }

    // Devuelve el tipo de ubicación
    public String obtenerTipoUbicacion() {
        return tipoUbicacion;
    }

    // Devuelve la superficie total en metros cuadrados
    public double obtenerAreaTotalM2() {
        return areaTotalM2;
    }

    // Devuelve la descripción del huerto
    public String obtenerDescripcion() {
        return descripcion;
    }

    // Devuelve la fecha en la que se registró
    public LocalDateTime obtenerFechaAlta() {
        return fechaAlta;
    }
}
