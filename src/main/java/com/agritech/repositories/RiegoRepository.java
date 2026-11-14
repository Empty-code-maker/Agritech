package com.agritech.repositories;

import com.agritech.models.EstadoRiego;

import java.sql.SQLException;

/** Persistencia del estado actual del riego. */
public interface RiegoRepository {

    EstadoRiego obtenerEstadoActual() throws SQLException;

    void guardarEstado(EstadoRiego estado) throws SQLException;
}
