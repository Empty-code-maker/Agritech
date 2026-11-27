
package com.agritech.repositories;

import com.agritech.models.EstadoRiego;
import java.sql.SQLException;

/** Operaciones de persistencia del sistema de riego. */
public interface RiegoRepository {

    // Métodos originales
    EstadoRiego obtenerEstadoActual() throws SQLException;

    void guardarEstado(EstadoRiego estado)
            throws SQLException;

    // Nuevos métodos para gestionar el riego por huerto
    EstadoRiego obtenerEstadoPorHuerto(int idHuerto)
            throws SQLException;

    void guardarEstadoPorHuerto(int idHuerto,
                                EstadoRiego estado)
            throws SQLException;
}
