package com.agritech.repositories.sqlite;

import com.agritech.models.EstadoRiego;
import com.agritech.repositories.RiegoRepository;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDateTime;

/** Persistencia SQLite del estado de riego, con una única fila para el huerto inicial. */
public class SQLiteRiegoRepository implements RiegoRepository {

    @Override
    public EstadoRiego obtenerEstadoActual() throws SQLException {
        String consulta = "SELECT activo, fecha_hora_ultimo_cambio FROM estado_riego WHERE id = 1";

        try (Connection conexion = ConexionSQLite.obtenerConexion();
             PreparedStatement sentencia = conexion.prepareStatement(consulta);
             ResultSet resultados = sentencia.executeQuery()) {
            
                if (!resultados.next()) {
                throw new SQLException("No se encontró el estado inicial del riego.");
            }

            String fechaGuardada = resultados.getString("fecha_hora_ultimo_cambio");
            LocalDateTime fechaHoraUltimoCambio = fechaGuardada == null
                    ? null
                    : LocalDateTime.parse(fechaGuardada);
            return new EstadoRiego(resultados.getInt("activo") == 1, fechaHoraUltimoCambio);
        }
    }

    @Override
    public void guardarEstado(EstadoRiego estado) throws SQLException {
        String consulta = "UPDATE estado_riego SET activo = ?, fecha_hora_ultimo_cambio = ? WHERE id = 1";

        try (Connection conexion = ConexionSQLite.obtenerConexion();
             PreparedStatement sentencia = conexion.prepareStatement(consulta)) {
            sentencia.setInt(1, estado.estaActivo() ? 1 : 0);
            LocalDateTime fecha = estado.obtenerFechaHoraUltimoCambio();
            sentencia.setString(2, fecha == null ? null : fecha.toString());
            
            if (sentencia.executeUpdate() != 1) {
                throw new SQLException("No se pudo guardar el estado del riego.");
            }
        }
    }

    // Consultar el estado de riego de un huerto
    @Override
    public EstadoRiego obtenerEstadoPorHuerto(int idHuerto)
            throws SQLException {

        if (idHuerto <= 0) {
            throw new IllegalArgumentException(
                    "El identificador del huerto debe ser positivo.");
        }

        // Buscar el estado del huerto seleccionado
        String consulta = "SELECT id, huerto_id, activo, modo, "
                + "fecha_hora_ultimo_cambio "
                + "FROM estado_riego WHERE huerto_id = ?";

        try (Connection conexion = ConexionSQLite.obtenerConexion();
             PreparedStatement sentencia = conexion.prepareStatement(consulta)) {

            sentencia.setInt(1, idHuerto);

            try (ResultSet resultados = sentencia.executeQuery()) {

                if (!resultados.next()) {
                    throw new SQLException(
                            "No existe estado de riego para este huerto.");
                }

                // La fecha puede ser nula
                String fechaGuardada = resultados.getString(
                        "fecha_hora_ultimo_cambio");

                LocalDateTime fecha = fechaGuardada == null
                        ? null
                        : LocalDateTime.parse(fechaGuardada);

                return new EstadoRiego(
                        resultados.getInt("id"),
                        resultados.getInt("huerto_id"),
                        resultados.getInt("activo") == 1,
                        resultados.getString("modo"),
                        fecha
                );
            }
        }
    }


    // Crear o actualizar el estado de riego de un huerto
    @Override
    public void guardarEstadoPorHuerto(int idHuerto,
                                       EstadoRiego estado)
            throws SQLException {

        if (idHuerto <= 0 || estado == null) {
            throw new IllegalArgumentException(
                    "El huerto y el estado deben ser válidos.");
        }

        // Si el huerto ya tiene estado, actualizarlo
        String consulta = "INSERT INTO estado_riego "
                + "(huerto_id, activo, modo, fecha_hora_ultimo_cambio) "
                + "VALUES (?, ?, ?, ?) "
                + "ON CONFLICT(huerto_id) DO UPDATE SET "
                + "activo = excluded.activo, "
                + "modo = excluded.modo, "
                + "fecha_hora_ultimo_cambio = "
                + "excluded.fecha_hora_ultimo_cambio";

        try (Connection conexion = ConexionSQLite.obtenerConexion();
             PreparedStatement sentencia = conexion.prepareStatement(consulta)) {

            sentencia.setInt(1, idHuerto);
            sentencia.setInt(2, estado.estaActivo() ? 1 : 0);
            sentencia.setString(3, estado.obtenerModo());

            // Convertir la fecha a texto si existe
            LocalDateTime fecha = estado.obtenerFechaHoraUltimoCambio();

            sentencia.setString(4,
                    fecha == null ? null : fecha.toString());

            // Ejecutar la inserción o actualización
            sentencia.executeUpdate();
        }
    }

}
