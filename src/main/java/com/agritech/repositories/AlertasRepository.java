package com.agritech.repositories;

import com.agritech.models.Alerta;

import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.Optional;

/** Define la persistencia y el ciclo de vida de las alertas. */
public interface AlertasRepository {

    Optional<Alerta> obtenerActiva(int idPlanta, String tipo) throws SQLException;

    Optional<LocalDateTime> obtenerUltimoCierre(int idPlanta, String tipo) throws SQLException;

    Alerta crear(Alerta alerta) throws SQLException;

    void cerrar(long id, LocalDateTime fechaHora, String motivo) throws SQLException;
}
