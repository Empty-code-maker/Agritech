package com.agritech.services;
import java.time.LocalDateTime;
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
    // Devuelve las plantas pertenecientes a un huerto concreto
    public List<Planta> obtenerPorHuerto(int idHuerto)
            throws SQLException {

        // Comprobamos que el identificador sea válido
        if (idHuerto <= 0) {
            throw new IllegalArgumentException(
                    "El identificador del huerto no es válido.");
        }

        // Consultamos las plantas del huerto indicado
        return repositorioPlantas.obtenerPorHuerto(idHuerto);
    }



    public Planta crearPlanta(String nombre, double humedadMinima) throws SQLException {
        validar(nombre, humedadMinima);
        UmbralPlanta umbral = new UmbralPlanta(0, humedadMinima);
        return repositorioPlantas.crear(new Planta(0, nombre.trim(), umbral));
    }

    // Crea una planta con todos los datos del nuevo esquema
    public Planta crearPlanta(String nombre, double humedadMinima,
                              int idHuerto, double areaM2,
                              String especie, String ubicacionZona)
            throws SQLException {

        // Validamos el nombre y el umbral
        validar(nombre, humedadMinima);

        // Comprobamos que el huerto tenga un ID válido
        if (idHuerto <= 0) {
            throw new IllegalArgumentException(
                    "Debes seleccionar un huerto válido.");
        }

        // La superficie debe ser positiva y finita
        if (!Double.isFinite(areaM2) || areaM2 <= 0) {
            throw new IllegalArgumentException(
                    "El área de la planta debe ser mayor que cero.");
        }

        // Comprobamos que se indique la especie
        if (especie == null || especie.trim().isEmpty()) {
            throw new IllegalArgumentException(
                    "La especie de la planta es obligatoria.");
        }

        // Creamos el umbral de humedad
        UmbralPlanta umbral = new UmbralPlanta(
                0, humedadMinima);

        // Preparamos los datos de la nueva planta
        Planta planta = new Planta(
                0,
                nombre.trim(),
                umbral,
                idHuerto,
                areaM2,
                especie.trim(),
                ubicacionZona == null
                        ? null : ubicacionZona.trim(),
                LocalDateTime.now()
        );

        // Enviamos la planta al repositorio SQLite
        return repositorioPlantas.crear(planta);
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
