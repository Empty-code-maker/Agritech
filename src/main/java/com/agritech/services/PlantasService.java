package com.agritech.services;

import com.agritech.models.Planta;
import com.agritech.models.UmbralPlanta;
import com.agritech.repositories.PlantasRepository;

import java.sql.SQLException;
import java.util.List;

/** Gestiona el catálogo de plantas y sus umbrales configurables. */
public class PlantasService {

    private final PlantasRepository repositorioPlantas;

    public PlantasService(PlantasRepository repositorioPlantas) {
        this.repositorioPlantas = repositorioPlantas;
    }

    public List<Planta> obtenerPlantas() throws SQLException {
        return repositorioPlantas.obtenerTodas();
    }

    public Planta crearPlanta(String nombre, double humedadMinima) throws SQLException {
        validar(nombre, humedadMinima);
        UmbralPlanta umbral = new UmbralPlanta(0, humedadMinima);
        return repositorioPlantas.crear(new Planta(0, nombre.trim(), umbral));
    }

    public void actualizarPlanta(Planta planta, String nombre, double humedadMinima) throws SQLException {
        validar(nombre, humedadMinima);
        UmbralPlanta umbral = new UmbralPlanta(planta.obtenerId(), humedadMinima);
        repositorioPlantas.actualizar(new Planta(planta.obtenerId(), nombre.trim(), umbral));
    }

    private void validar(String nombre, double humedadMinima) {
        if (nombre == null || nombre.trim().isEmpty()) {
            throw new IllegalArgumentException("Escribe el nombre de la planta.");
        }
        if (nombre.trim().length() > 80) {
            throw new IllegalArgumentException("El nombre no puede superar los 80 caracteres.");
        }
        if (Double.isNaN(humedadMinima) || humedadMinima <= 0 || humedadMinima > 100) {
            throw new IllegalArgumentException("El umbral debe ser mayor que 0 y como máximo 100 %.");
        }
    }
}
