package com.agritech.services;

import com.agritech.models.EstadoRiego;
import com.agritech.repositories.RiegoRepository;

import java.sql.SQLException;
import java.time.LocalDateTime;

/** Coordina los cambios del estado simulado del riego. */
public class RiegoService {

    private final RiegoRepository repositorioRiego;

    public RiegoService(RiegoRepository repositorioRiego) {
        this.repositorioRiego = repositorioRiego;
    }

    public EstadoRiego obtenerEstadoActual() throws SQLException {
        return repositorioRiego.obtenerEstadoActual();
    }

    public EstadoRiego alternarEstado() throws SQLException {
        EstadoRiego estadoActual = repositorioRiego.obtenerEstadoActual();
        EstadoRiego estadoActualizado = new EstadoRiego(
                !estadoActual.estaActivo(), LocalDateTime.now());
        repositorioRiego.guardarEstado(estadoActualizado);
        return estadoActualizado;
    }

    // Consultar el estado de riego de un huerto concreto
    public EstadoRiego obtenerEstadoPorHuerto(int idHuerto)
            throws SQLException {

        if (idHuerto <= 0) {
            throw new IllegalArgumentException(
                    "El identificador del huerto debe ser positivo.");
        }

        return repositorioRiego.obtenerEstadoPorHuerto(idHuerto);
    }


    // Crear o modificar la configuración de riego de un huerto
    public EstadoRiego configurarRiegoPorHuerto(
            int idHuerto, boolean activo, String modo)
            throws SQLException {

        if (idHuerto <= 0) {
            throw new IllegalArgumentException(
                    "El identificador del huerto debe ser positivo.");
        }

        // Comprobar el modo de funcionamiento
        if (modo == null ||
                (!modo.equals("MANUAL") &&
                        !modo.equals("AUTOMATICO"))) {

            throw new IllegalArgumentException(
                    "El modo debe ser MANUAL o AUTOMATICO.");
        }

        // Preparar el nuevo estado de riego
        EstadoRiego estado = new EstadoRiego(
                0,
                idHuerto,
                activo,
                modo,
                LocalDateTime.now()
        );

        // Crear o actualizar el registro en SQLite
        repositorioRiego.guardarEstadoPorHuerto(idHuerto, estado);

        // Recuperar el registro con su identificador real
        return repositorioRiego.obtenerEstadoPorHuerto(idHuerto);
    }


    // Encender o apagar el riego de un huerto
    public EstadoRiego alternarEstadoPorHuerto(int idHuerto)
            throws SQLException {

        // Obtener el estado actual de este huerto
        EstadoRiego estadoActual =
                obtenerEstadoPorHuerto(idHuerto);

        // Invertir el estado sin cambiar el modo
        EstadoRiego estadoActualizado = new EstadoRiego(
                estadoActual.obtenerId(),
                idHuerto,
                !estadoActual.estaActivo(),
                estadoActual.obtenerModo(),
                LocalDateTime.now()
        );

        // Guardar el nuevo estado en la base de datos
        repositorioRiego.guardarEstadoPorHuerto(
                idHuerto, estadoActualizado);

        return estadoActualizado;
    }

}
