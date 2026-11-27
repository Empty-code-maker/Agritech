package com.agritech.repositories;

import com.agritech.models.Planta;

import java.sql.SQLException;
import java.util.List;

/** Define las operaciones de persistencia del catálogo de plantas y sus umbrales. */
public interface PlantasRepository {

    List<Planta> obtenerTodas() throws SQLException;

    // Devuelve las plantas que pertenecen a un huerto
    List<Planta> obtenerPorHuerto(int idHuerto) throws SQLException;


    Planta crear(Planta planta) throws SQLException;

    void actualizar(Planta planta) throws SQLException;
}
