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
}
